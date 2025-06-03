<template>
  <div class="chat-container">
    <div class="chat-header">
      <div class="header-left">
        <div class="burger-menu">
          <button @click="toggleSidebar">☰</button>
        </div>
        <h2>Помощник по взаимоотношениям</h2>
      </div>
      <div class="partner-status" v-if="hasPartner">
        <span>Партнер подключен</span>
        <img :src="partnerAvatar" class="avatar" />
      </div>
      <div class="partner-status" v-else>
        <span>Партнер не подключен</span>
      </div>
    </div>

    <div class="sidebar-overlay" v-if="showSidebar" @click="toggleSidebar"></div>
    <div class="sidebar" :class="{ 'sidebar-open': showSidebar }">
      <div class="block">
      <img :src="userAvatar" class="avatar-large" />
      <p>{{ username }}</p>
      </div>
      <ul>
        <li><router-link to="/profileView">Мой профиль</router-link></li>
        <li><router-link to="/chatComponent">Чат</router-link></li>
        <li><router-link to="/partnerView">{{ hasPartner ? 'Партнер' : 'Добавление партнера' }}</router-link></li>
        <li><router-link to="/calendar">Календарь</router-link></li>
        <li><router-link to="/sharedGoals">Цели</router-link></li>
        <li><router-link to="/settingsView">Настройки</router-link></li>
        <li><router-link to="/logout">Выход</router-link></li>
      </ul>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index" 
           :class="['message', 
                   { 'user-message': message.sender === 'user',
                     'ai-message': message.sender === 'AI',
                     'partner-message': message.sender === 'partner' }]">
        <div class="message-avatar" v-if="message.sender !== 'user'">
          <img :src="message.sender === 'AI' ? aiAvatar : partnerAvatar" class="avatar" />
        </div>
        <div class="message-content">
          <div class="message-text">{{ message.text }}</div>
          <img v-if="message.mediaUrl" :src="message.mediaUrl" class="message-media" />
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <div class="input-group">
        <input v-model="newMessage" 
               @keyup.enter="sendMessage" 
               placeholder="Тип сообщения..." 
               :disabled="isLoading" />
        <button @click="sendMessage" :disabled="isLoading || !newMessage.trim()">
          <span v-if="!isLoading">Send</span>
          <span v-else class="loading-dots">Загрузка ответа...</span>
        </button>
      </div>
      <div class="input-options">
        <button @click="toggleQuickReplies" class="option-button">
          <i class="fas fa-lightbulb"></i> Быстрые ответы
        </button>
        <button @click="showRecommendations" class="option-button">
          <i class="fas fa-gift"></i> Получить рекомендацию
        </button>
        <input type="file" id="media-upload" @change="handleMediaUpload" accept="image/*" hidden />
        <label for="media-upload" class="option-button">
          <i class="fas fa-image"></i> Добавить медиа
        </label>
      </div>
      
      <div class="quick-replies" v-if="showQuickRepliesPanel">
        <button v-for="(reply, index) in quickReplies" 
                :key="index" 
                @click="selectQuickReply(reply)"
                class="quick-reply">
          {{ reply }}
        </button>
      </div>
    </div>

    <!-- Recommendations Modal -->
    <div class="modal" v-if="showRecommendationsModal" @click.self="closeRecommendations">
      <div class="modal-content">
        <h3>Recommendations</h3>
        <div class="recommendation-types">
          <button v-for="type in recommendationTypes" 
                  :key="type.value" 
                  @click="getRecommendation(type.value)"
                  class="recommendation-button">
            {{ type.label }}
          </button>
        </div>
        <div class="recommendation-result" v-if="currentRecommendation">
          <h4>{{ currentRecommendation.title }}</h4>
          <p>{{ currentRecommendation.content }}</p>
        </div>
        <button @click="closeRecommendations" class="close-button">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  name: 'ChatComponent',
  setup() {
    const router = useRouter();
    const messages = ref([]);
    const newMessage = ref('');
    const isLoading = ref(false);
    const hasPartner = ref(false);
    const messagesContainer = ref(null);
    const showSidebar = ref(false);
    const username = ref('User');
    const userAvatar = ref('');
    const aiAvatar = ref('/default-ai-avatar.png');
    const partnerAvatar = ref('');
    const showQuickRepliesPanel = ref(false);
    const quickReplies = ref([
      "Как улучшить наше общение?",
      "Что делать при конфликте?",
      "Как выразить благодарность?"
    ]);
    const showRecommendationsModal = ref(false);
    const recommendationTypes = ref([
      { value: 'date', label: 'Идеи для свидания' },
      { value: 'communication', label: 'Советы по общению' },
      { value: 'gift', label: 'Идеи подарков' }
    ]);
    const currentRecommendation = ref(null);

    // Base URL for API requests
    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:5000';

    const getUserId = () => {
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        return user?.id || null;
      } catch (e) {
        console.error('Error parsing user data:', e);
        return null;
      }
    };

    const initializeChat = () => {
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user) {
          // router.push('/login');
          return;
        }
        username.value = user.name || 'User';
        userAvatar.value = user.avatar || '';
        
        if (messages.value.length === 0) {
          messages.value = [{
            sender: 'AI',
            text: 'Привет! Я ваш помощник по взаимоотношениям. Чем могу помочь?',
            timestamp: new Date().toISOString()
          }];
        }
      } catch (e) {
        console.error('Initialization error:', e);
      }
    };

    const toggleSidebar = () => showSidebar.value = !showSidebar.value;
    const toggleQuickReplies = () => showQuickRepliesPanel.value = !showQuickRepliesPanel.value;

    const showRecommendations = () => showRecommendationsModal.value = true;
    const closeRecommendations = () => {
      showRecommendationsModal.value = false;
      currentRecommendation.value = null;
    };

    const getRecommendation = async (type) => {
      try {
        const response = await axios.get(`${API_BASE_URL}/recommendations/${type}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        currentRecommendation.value = response.data;
      } catch (error) {
        console.error('Error getting recommendation:', error);
        throw error;
      }
    };

    const selectQuickReply = (reply) => {
      newMessage.value = reply;
      showQuickRepliesPanel.value = false;
    };

    const formatTime = (timestamp) => {
      return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };

    const handleMediaUpload = (event) => {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          messages.value.push({
            sender: 'user',
            text: '',
            mediaUrl: e.target.result,
            timestamp: new Date().toISOString()
          });
          scrollToBottom();
        };
        reader.readAsDataURL(file);
      }
    };

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
      });
    };

    const sendMessage = async () => {
      const messageText = newMessage.value.trim();
      if (!messageText || isLoading.value) return;

      try {
        // Add user message immediately
        const userMsg = {
          sender: 'user',
          text: messageText,
          timestamp: new Date().toISOString()
        };
        messages.value.push(userMsg);
        newMessage.value = '';
        scrollToBottom();
        
        isLoading.value = true;
        
        const requestData = {
          userId: getUserId(),
          message: messageText,
          context: messages.value.slice(-5).filter(m => m.sender === 'user').map(m => m.text)
        };

        // Enhanced API call with better error handling
        const response = await axios.post(`${API_BASE_URL}/ai/chat`, requestData, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
            'Content-Type': 'application/json'
          },
          timeout: 15000 // Increased timeout
        });

        // Handle different response formats
        const aiResponse = response.data?.response || 
                          response.data?.message || 
                          response.data?.answer || 
                          "Извините, не могу обработать запрос";

        messages.value.push({
          sender: 'AI',
          text: aiResponse,
          timestamp: new Date().toISOString()
        });
      } catch (error) {
        console.error('Error sending message:', error);
        let errorMessage = 'Ошибка при получении ответа. Пожалуйста, попробуйте позже.';
        
        if (error.response) {
          if (error.response.status === 401) {
            errorMessage = 'Требуется авторизация';
          } else if (error.response.status === 429) {
            errorMessage = 'Слишком много запросов. Подождите немного.';
          }
        } else if (error.message.includes('timeout')) {
          errorMessage = 'Превышено время ожидания ответа';
        } else if (error.message.includes('Network Error')) {
          errorMessage = 'Проблемы с соединением';
        }

        messages.value.push({
          sender: 'system',
          text: errorMessage,
          timestamp: new Date().toISOString()
        });
      } finally {
        isLoading.value = false;
        scrollToBottom();
      }
    };

    const loadMessages = async () => {
      try {
        const userId = getUserId();
        if (!userId) return;

        const response = await axios.get(`${API_BASE_URL}/ai/messages`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          },
          params: { userId },
          timeout: 10000
        });

        if (response.data?.messages?.length > 0) {
          messages.value = response.data.messages;
        } else {
          messages.value = [{
            sender: 'AI',
            text: 'Привет! Я ваш помощник по взаимоотношениям. Чем могу помочь?',
            timestamp: new Date().toISOString()
          }];
        }
      } catch (error) {
        console.error('Error loading messages:', error);
        messages.value = [{
          sender: 'AI',
          text: 'Привет! Я ваш помощник по взаимоотношениям. Чем могу помочь?',
          timestamp: new Date().toISOString()
        }];
      } finally {
        scrollToBottom();
      }
    };

    onMounted(() => {
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        if (user) {
          username.value = user.name || 'User';
          userAvatar.value = user.avatar || '';
          loadMessages();
        } else {
          messages.value = [{
            sender: 'AI',
            text: 'Привет! Я ваш помощник по взаимоотношениям. Чем могу помочь?',
            timestamp: new Date().toISOString()
          }];
        }
      } catch (e) {
        console.error('Mount error:', e);
      }
    });

    return {
      messages,
      newMessage,
      isLoading,
      hasPartner,
      messagesContainer,
      sendMessage,
      showSidebar,
      toggleSidebar,
      username,
      userAvatar,
      aiAvatar,
      partnerAvatar,
      showQuickRepliesPanel,
      quickReplies,
      toggleQuickReplies,
      selectQuickReply,
      showRecommendationsModal,
      showRecommendations,
      closeRecommendations,
      recommendationTypes,
      currentRecommendation,
      getRecommendation,
      formatTime,
      handleMediaUpload
    };
  }
};
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 100%;
  background-color: #e5ddd5;
  font-family: 'Helvetica Neue', sans-serif;
  position: relative;
}

/* Header */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(79, 149, 255, 1);
  color: white;
  padding: 12px 16px;
  position: relative;
  z-index: 10;
}
.block{
  display: flex;
  width: 100%;
  background-color: rgba(19, 111, 189, 1);
  border: 7px ;
  box-sizing: border-box;
  text-align: center;
}
.avatar-large{
 
   margin-left: auto;
   margin-right: auto;
   border: 2px solid rgb(255, 255, 255) ;

}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-header h2 {
  font-size: 18px;
  margin: 0;
}

.partner-status {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.partner-status img.avatar {
  width: 32px;
  height: 32px;
  margin-left: 8px;
  border-radius: 50%;
  border: 2px solid white;
}

/* Message Area */
.chat-messages {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
  background-color: #fff;
  display: flex;
  flex-direction: column;
}

/* Message Styles */
.message {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-end;
}

.message-content {
  max-width: 70%;
  background-color: #f1f0f0;
  padding: 8px 12px;
  border-radius: 10px;
  position: relative;
  word-wrap: break-word;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.user-message .message-content {
  background-color: #dcf8c6;
  border-bottom-right-radius: 0;
}

.partner-message .message-content,
.ai-message .message-content {
  background-color: #ffffff;
  border-bottom-left-radius: 0;
}

.message-avatar {
  margin-right: 8px;
}

.message-avatar img.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
}

.message-time {
  font-size: 10px;
  color: gray;
  margin-top: 4px;
}

/* Media in message */
.message-media {
  margin-top: 5px;
  max-width: 100%;
  border-radius: 8px;
}

/* Input */
.chat-input {
  padding: 10px;
  background-color: #f0f0f0;
  border-top: 1px solid #ddd;
  display: flex;
  flex-direction: column;
}

.input-group {
  display: flex;
  gap: 8px;
}

.input-group input {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 20px;
  outline: none;
}

.input-group button {
  background-color: #0088cc;
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 20px;
  cursor: pointer;
}

/* Options */
.input-options {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
}

.option-button {
  background: none;
  border: none;
  color: #0088cc;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

/* Quick Replies */
.quick-replies {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.quick-reply {
  background-color: #e1f5fe;
  border: 1px solid #81d4fa;
  border-radius: 16px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
}

/* Modal */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.modal-content {
  background-color: #fff;
  padding: 20px;
  width: 90%;
  max-width: 400px;
  border-radius: 12px;
}

.recommendation-types {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.recommendation-button {
  flex: 1 1 40%;
  background-color: #0088cc;
  color: white;
  border: none;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
}

.close-button {
  margin-top: 10px;
  background-color: #d32f2f;
  color: white;
  border: none;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
}

/* Sidebar + burger menu */
.burger-menu {
  z-index: 20;
}

.burger-menu button {
  background: none;
  border: none;
  font-size: 24px;
  color: white;
  cursor: pointer;
  padding: 5px;
}

.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  z-index: 15;
}

.sidebar {
  position: fixed;
  top: 0;
  left: -260px;
  width: 260px;
  height: 100%;
  background-color: #0088cc;
  padding: 20px;
  color: white;
  z-index: 20;
  display: flex;
  flex-direction: column;
  gap: 15px;
  transition: left 0.3s ease;
}

.sidebar-open {
  left: 0;
}

.sidebar img.avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 10px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar li {
  padding: 10px 0;
}

.sidebar a {
  color: white;
  text-decoration: none;
}

.sidebar a.router-link-active {
  font-weight: bold;
}

/* Mobile support */
@media (max-width: 600px) {
  .chat-header h2 {
    font-size: 16px;
  }

  .message-content {
    max-width: 85%;
  }

  .sidebar {
    width: 80%;
    left: -80%;
  }

  .modal-content {
    width: 95%;
  }

  .input-options {
    flex-direction: column;
    gap: 5px;
  }
}
</style>