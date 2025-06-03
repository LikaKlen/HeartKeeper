package com.example.HeattKeeper.dal;

import com.example.HeattKeeper.models.*;
import jakarta.persistence.TypedQuery;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import org.springframework.security.crypto.bcrypt.BCrypt;
import com.example.HeattKeeper.models.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
public class DataAccessLayer {

    private final SessionFactory sessionFactory;

    public DataAccessLayer(SessionFactory session) {
        this.sessionFactory = session;
    }
    Session session = null;
    public String newUserToDatabase(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Query query = session
                    .createQuery("FROM User WHERE username = :username")
                    .setParameter("username", user.getUsername());

            if (query.uniqueResult() != null) {
                return "Выберите другое имя";
            }

            query = session
                    .createQuery("FROM User WHERE email = :email")
                    .setParameter("email", user.getEmail());

            if (query.uniqueResult() != null) {
                return "Выберите другую почту";
            }

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);

            session.persist(user);
            session.getTransaction().commit();
            return "Победа)";
        } catch (Exception e) {
            e.printStackTrace(); // Логируем ошибку
            return "Ошибка при сохранении пользователя";
        }
    }
    public Optional<User> getUserFromDatabaseByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.uniqueResult());
        }
    }
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        }
    }
    public List<User> getUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }
    public boolean updateUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteUserById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
            }
            session.getTransaction().commit();
        }
    }
    public void updateUserAvatarByEmail(String email, String avatarUrl) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            if (user != null) {
                user.setProfilePhotoUrl(avatarUrl);
                session.update(user);
            }
            tx.commit();
        }
    }
    public User getUserId(Long id){
        session=sessionFactory.openSession();
        session.beginTransaction();
        User user=session.get(User.class,id);
        session.getTransaction().commit();
        if(session!=null){
            session.close();
        }
        return user;
    }
    public Optional<User> findByInvitationCode(String code) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE invitationCode = :code", User.class);
            query.setParameter("code", code);
            return Optional.ofNullable(query.uniqueResult());
        }
    }
    public void save(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Optional<Chat> findChatByUsers(User user1, User user2) {
        try (Session session = sessionFactory.openSession()) {
            Query<Chat> query = session.createQuery(
                    "FROM Chat WHERE (user1 = :user1 AND user2 = :user2)",
                    Chat.class
            );
            query.setParameter("user1", user1);
            query.setParameter("user2", user2);
            return Optional.ofNullable(query.uniqueResult());
        }
    }
    public Chat save(Chat chat) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(chat);
            session.getTransaction().commit();
            return chat;
        }
    }
    public Message save(Message message) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(message);
            session.getTransaction().commit();
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save message", e);
        }
    }

    public Optional<Message> findMessageById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Message.class, id));
        }
    }

    public List<Message> findMessagesByChat(Chat chat) {
        try (Session session = sessionFactory.openSession()) {
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE chat = :chat ORDER BY timestamp", Message.class);
            query.setParameter("chat", chat);
            return query.getResultList();
        }
    }
//    public List<Message> findMessagesBySenderOrReceiver(String senderEmail, String receiverEmail) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Message> query = session.createQuery(
//                    "FROM Message WHERE (sender.email = :sender AND receiver.email = :receiver) " +
//                            "OR (sender.email = :receiver AND receiver.email = :sender) " +
//                            "ORDER BY timestamp", Message.class);
//            query.setParameter("sender", senderEmail);
//            query.setParameter("receiver", receiverEmail);
//            return query.getResultList();
//        }
//    }
//
//    public List<Message> findMessagesBetweenUsers(User user1, User user2) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Message> query = session.createQuery(
//                    "FROM Message WHERE (sender = :user1 AND receiver = :user2) " +
//                            "OR (sender = :user2 AND receiver = :user1) " +
//                            "ORDER BY timestamp", Message.class);
//            query.setParameter("user1", user1);
//            query.setParameter("user2", user2);
//            return query.getResultList();
//        }
//    }
//
//    public Chat findOrCreateChat(User user1, User user2) {
//        Optional<Chat> chat = findChatByUsers(user1, user2);
//        if (chat.isPresent()) {
//            return chat.get();
//        }
//        chat = findChatByUsers(user2, user1);
//        if (chat.isPresent()) {
//            return chat.get();
//        }
//
//        // Create new chat
//        Chat newChat = new Chat(user1, user2);
//        newChat.setCreatedAt(LocalDateTime.now());
//        return save(newChat);
//    }

//    public List<CommonGoal> getPersonalGoalsByUser(User user) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM CommonGoal WHERE owner = :user AND partner IS NULL", CommonGoal.class)
//                    .setParameter("user", user)
//                    .getResultList();
//        }
//    }
//
//    public List<CommonGoal> getSharedGoalsByUsers(User user1, User user2) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM CommonGoal WHERE (owner = :user1 AND partner = :user2) OR (owner = :user2 AND partner = :user1)", CommonGoal.class)
//                    .setParameter("user1", user1)
//                    .setParameter("user2", user2)
//                    .getResultList();
//        }
//    }
//
//    public CommonGoal saveGoal(CommonGoal goal) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction tx = session.beginTransaction();
//            session.saveOrUpdate(goal);
//            tx.commit();
//            return goal;
//        }
//    }
//
//    public GoalStage saveGoalStage(GoalStage stage) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction tx = session.beginTransaction();
//            session.saveOrUpdate(stage);
//            tx.commit();
//            return stage;
//        }
//    }
//
//    public Optional<CommonGoal> getGoalById(Long id) {
//        try (Session session = sessionFactory.openSession()) {
//            return Optional.ofNullable(session.get(CommonGoal.class, id));
//        }
//    }
//
//    public Optional<GoalStage> getGoalStageById(Long id) {
//        try (Session session = sessionFactory.openSession()) {
//            return Optional.ofNullable(session.get(GoalStage.class, id));
//        }
//    }
//
//    public void deleteGoal(CommonGoal goal) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction tx = session.beginTransaction();
//            goal = session.merge(goal); // Ensure attached
//            session.delete(goal);
//            tx.commit();
//        }
//    }
    public CommonGoal createGoal(CommonGoal goal) {
        try (Session session = sessionFactory.openSession()) {
        Transaction tx = session.beginTransaction();
        session.persist(goal);
        tx.commit();
        return goal;
        }
    }

    public Optional<CommonGoal> getGoalById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(CommonGoal.class, id));
        }
    }

    public List<CommonGoal> getGoalsByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM CommonGoal WHERE owner = :user OR partner = :user", CommonGoal.class)
                    .setParameter("user", user)
                    .getResultList();
        }
    }

    public List<CommonGoal> getPersonalGoals(String userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM CommonGoal WHERE owner.id = :userId AND partner IS NULL", CommonGoal.class)
                    .setParameter("userId", Long.parseLong(userId))
                    .getResultList();
        }
    }

    public List<CommonGoal> getSharedGoals(String userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM CommonGoal WHERE partner IS NOT NULL AND (owner.id = :userId OR partner.id = :userId)", CommonGoal.class)
                    .setParameter("userId", Long.parseLong(userId))
                    .getResultList();
        }
    }

    public CommonGoal updateGoal(CommonGoal goal) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(goal); // merge attaches the detached entity
            tx.commit();
            return goal;
        }
    }

    public boolean deleteGoal(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            CommonGoal goal = session.get(CommonGoal.class, id);
            if (goal != null) {
                session.remove(goal);
                tx.commit();
                return true;
            }
            return false;
        }
    }


    // Создание или обновление заметки
    public CalendarNote saveNote(CalendarNote note) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(note);
            session.getTransaction().commit();
            return note;
        }
    }

    // Получение заметок по пользователю
    public List<CalendarNote> getNotesForUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<CalendarNote> query = session.createQuery(
                    "FROM CalendarNote WHERE createdBy = :user OR partner = :user", CalendarNote.class);
            query.setParameter("user", user);
            return query.list();
        }
    }

    // Получить заметку по ID
    public Optional<CalendarNote> getNoteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(CalendarNote.class, id));
        }
    }

    // Удаление заметки, если пользователь является создателем
    public boolean deleteNoteIfOwner(Long noteId, Long userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CalendarNote note = session.get(CalendarNote.class, noteId);
            if (note != null && note.getCreatedBy().getId().equals(userId)) {
                session.remove(note);
                session.getTransaction().commit();
                return true;
            }
            return false;
        }
    }

    // Обновление блокировки
    public boolean updateLockStatus(Long noteId, boolean locked, Long userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CalendarNote note = session.get(CalendarNote.class, noteId);
            if (note != null && note.getCreatedBy().getId().equals(userId)) {
                note.setLocked(locked);
                session.update(note);
                session.getTransaction().commit();
                return true;
            }
            return false;
        }
    }
}
