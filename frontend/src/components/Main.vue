<script>
import axios from 'axios';

export default {
  name: 'Main',
  data() {
    return {
      messages: [],
      newMessage: '',
      UserId: '1', 
      chatId: null,
      isMenuOpen: true,
    };
  },
  methods: {
    async loadChat() {
      try {
        const chatRes = await axios.get(`/chat/${this.UserId}`);
        this.chatId = chatRes.data.id;
        await this.loadMessages();
      } catch (error) {
        console.error('Ошибка при загрузке чата:', error);
      }
    },
    async loadMessages() {
      try {
        const res = await axios.get(`/messages/chat/${this.chatId}`);
        this.messages = res.data;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error('Ошибка при загрузке сообщений:', error);
      }
    },
    async sendMessage() {
      if (!this.newMessage.trim()) return;

      try {
        const payload = {
          chatId: this.chatId,
          senderId: this.currentUserId,
          content: this.newMessage,
        };

        const res = await axios.post('/messages/send', payload);
        this.messages.push(res.data);
        this.newMessage = '';
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error('Ошибка при отправке сообщения:', error);
      }
    },
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
    },
    scrollToBottom() {
      const container = this.$refs.messageContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    }
  },
  mounted() {
    this.loadChat();
  }
};
</script>

<template>
  <div class="flex h-screen">
    <!-- Left sidebar -->
    <div v-if="isMenuOpen" class="w-1/3 bg-[#D5F2BC] flex flex-col">
      <!-- Top bar with burger menu -->
      <div class="bg-[#6E9E54] p-4 text-white font-bold">
        <button @click="toggleMenu">&#9776;</button>
      </div>

      <!-- Chat with partner preview -->
      <div class="p-4 hover:bg-green-100 cursor-pointer border-b">
        <div class="flex items-center space-x-4">
          <img src="" alt="Partner Icon" class="w-10 h-10 rounded-full" />
          <div>
            <div class="font-semibold">Имя партнера</div>
            <div class="text-sm text-gray-600">Последнее сообщение...</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Right main chat area -->
    <div class="w-full md:w-2/3 flex flex-col">
      <!-- Top partner bar -->
      <div class="bg-[#6E9E54] p-4 flex justify-between items-center text-white">
        <div class="flex items-center space-x-4">
          <img src="" alt="Partner Icon" class="w-10 h-10 rounded-full" />
          <span class="font-semibold text-lg">Имя партнера</span>
        </div>
        <div class="flex space-x-4">
          <button><i class="fas fa-phone"></i></button>
          <button><i class="fas fa-video"></i></button>
          <button><i class="fas fa-ellipsis-v"></i></button>
        </div>
      </div>

      <!-- Chat messages -->
      <div ref="messageContainer" class="flex-1 overflow-y-auto p-4 space-y-2 bg-gray-50">
        <div v-if="messages.length === 0" class="text-center text-gray-400">Сообщения появятся здесь</div>
        <div v-else>
          <div
            v-for="message in messages"
            :key="message.id"
            :class="[
              'p-2 rounded max-w-xs',
              message.senderId === currentUserId ? 'bg-green-200 ml-auto text-right' : 'bg-white mr-auto text-left'
            ]"
          >
            <span class="text-xs text-gray-500 block">
              {{ message.senderId === currentUserId ? 'Вы' : 'Партнёр' }}
            </span>
            <div class="font-medium">{{ message.content }}</div>
          </div>
        </div>
      </div>

      <!-- Message input -->
      <div class="p-4 border-t bg-white flex items-center space-x-4">
        <button><i class="far fa-smile"></i></button>
        <button><i class="fas fa-paperclip"></i></button>
        <input
          v-model="newMessage"
          @keyup.enter="sendMessage"
          type="text"
          placeholder="Введите сообщение..."
          class="flex-1 border rounded-full px-4 py-2 focus:outline-none"
        />
        <button
          @click="sendMessage"
          class="bg-[#6E9E54] text-white px-4 py-2 rounded-full"
        >
          Отправить
        </button>
      </div>
    </div>
  </div>
</template>