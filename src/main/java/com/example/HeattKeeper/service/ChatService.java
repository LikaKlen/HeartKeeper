package com.example.HeattKeeper.service;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.MessageDTO;

import com.example.HeattKeeper.models.Chat;
import com.example.HeattKeeper.models.Message;

import com.example.HeattKeeper.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.HeattKeeper.dto.AIResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final DataAccessLayer dataAccessLayer;
    private final RelationshipAIService relationshipAIService;

    @Autowired
    public ChatService(DataAccessLayer dataAccessLayer,
                       RelationshipAIService relationshipAIService) {
        this.dataAccessLayer = dataAccessLayer;
        this.relationshipAIService = relationshipAIService;
    }

    public Chat createChat(User user1, User user2) {
        Optional<Chat> chatOpt = dataAccessLayer.findChatByUsers(user1, user2);
        if (chatOpt.isPresent()) {
            return chatOpt.get();
        }

        chatOpt = dataAccessLayer.findChatByUsers(user2, user1);
        if (chatOpt.isPresent()) {
            return chatOpt.get();
        }

        Chat newChat = new Chat(user1, user2);
        newChat.setCreatedAt(LocalDateTime.now());
        return dataAccessLayer.save(newChat);
    }

    public Chat getChat(String email) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User partner = user.getPartner();

        if (partner == null) {
            throw new RuntimeException("User has no partner");
        }

        return createChat(user, partner);
    }

    public MessageDTO sendMessage(String senderUsername, String target, String text, String mediaUrl) {
        User sender = dataAccessLayer.getUserFromDatabaseByEmail(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver;
        if ("AI".equalsIgnoreCase(target)) {
            // Create a virtual AI user
            receiver = new User();
            receiver.setEmail("AI");
            receiver.setUsername("AI");
        } else {
            receiver = dataAccessLayer.getUserFromDatabaseByEmail(target)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));
        }

        Chat chat = createChat(sender, receiver);
        Message message = new Message(text, mediaUrl, sender, chat);
        dataAccessLayer.save(message);

        // If message is sent to AI, get and send AI response
        if ("AI".equalsIgnoreCase(target)) {
            AIResponse aiResponse = relationshipAIService.getAIResponse(sender.getId(), text, "chat");
            Message aiMessage = new Message(aiResponse.getResponse(), null, receiver, chat);
            dataAccessLayer.save(aiMessage);
            return toDTO(aiMessage);
        }

        return toDTO(message);
    }

    public List<MessageDTO> getMessages(String email, String target) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User otherUser;
        if ("AI".equalsIgnoreCase(target)) {
            otherUser = new User();
            otherUser.setEmail("AI");
            otherUser.setUsername("AI");
        } else {
            otherUser = dataAccessLayer.getUserFromDatabaseByEmail(target)
                    .orElseThrow(() -> new RuntimeException("Target user not found"));
        }

        Chat chat = createChat(user, otherUser);

        return dataAccessLayer.findMessagesByChat(chat).stream()
                .filter(message -> message.isVisibleFor(user))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteMessage(String email, Long messageId, boolean forBoth) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = dataAccessLayer.findMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getChat().getUser1().equals(user) && !message.getChat().getUser2().equals(user)) {
            throw new RuntimeException("User not authorized to delete this message");
        }

        if (message.getSender().equals(user)) {
            if (forBoth) {
                message.setDeletedForSender(true);
                message.setDeletedForReceiver(true);
            } else {
                message.setDeletedForSender(true);
            }
        } else {
            message.setDeletedForReceiver(true);
        }

        dataAccessLayer.save(message);
    }

    private MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getEmail(),
                message.getText(),
                message.getMediaUrl(),
                message.getTimestamp(),
                message.isDeletedForSender()
        );
    }
}
