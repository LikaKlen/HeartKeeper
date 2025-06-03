<template>
  <div class="settings">
    <div class="chat-header">
      <div class="header-left">
        <div class="burger-menu">
          <button @click="toggleSidebar">☰</button>
        </div>
        <h1>Настройки</h1>
      </div>
      <div class="partner-status" v-if="hasPartner">
        <img :src="partnerAvatar" class="avatar" alt="Partner avatar" />
      </div>
    </div>
    
    <div class="sidebar-overlay" v-if="showSidebar" @click="toggleSidebar"></div>
    <div class="sidebar" :class="{ 'sidebar-open': showSidebar }">
      <div class="block">
        <img :src="userAvatar" class="avatar-large" alt="User avatar" />
        <p class="username">{{ username }}</p>
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
    
    <div class="settings-content">
      <div class="setting-group">
        <label>Тема:</label>
        <select v-model="settings.theme">
          <option value="light">Светлая</option>
          <option value="dark">Тёмная</option>
        </select>
      </div>
      <div class="setting-group">
        <label>Язык:</label>
        <select v-model="settings.language">
          <option value="ru">Русский</option>
          <option value="en">English</option>
        </select>
      </div>
      <button class="save-btn" @click="saveSettings">Сохранить настройки</button>
      
      <h2 class="support-title">Обратиться в поддержку</h2>
      <input 
        v-model="supportRequest.email" 
        placeholder="Ваш Email" 
        class="support-input"
        :class="{ 'input-error': emailError }"
      />
      <span class="error-message" v-if="emailError">{{ emailError }}</span>
      <textarea 
        v-model="supportRequest.message" 
        placeholder="Ваше сообщение" 
        class="support-textarea"
        :class="{ 'input-error': messageError }"
      ></textarea>
      <span class="error-message" v-if="messageError">{{ messageError }}</span>
      <button class="support-btn" @click="sendSupport">Отправить</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showSidebar: false,
      hasPartner: false,
      username: 'Пользователь',
      userAvatar: 'https://via.placeholder.com/100',
      partnerAvatar: 'https://via.placeholder.com/100',
      userId: 1,
      settings: {
        theme: 'light',
        language: 'ru'
      },
      supportRequest: {
        email: '',
        message: ''
      },
      emailError: '',
      messageError: ''
    };
  },
  watch: {
    'settings.theme'(newTheme) {
      this.applyTheme(newTheme);
    },
    'supportRequest.email'(newEmail) {
      if (newEmail) {
        this.emailError = '';
      }
    },
    'supportRequest.message'(newMessage) {
      if (newMessage) {
        this.messageError = '';
      }
    }
  },
  async mounted() {
  try {
    const res = await fetch('/api/users/user/theme');
    if (res.ok) {
      const data = await res.json();
      this.settings.theme = data.theme.toLowerCase(); // "LIGHT" → "light"
      this.applyTheme(this.settings.theme);
    }
  } catch (e) {
    console.error('Ошибка загрузки темы пользователя', e);
  }},
  methods: {
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
      document.body.style.overflow = this.showSidebar ? 'hidden' : 'auto';
    },
    applyTheme(theme) {
      document.documentElement.setAttribute('data-theme', theme);
    },
    saveSettings() {
      console.log('Settings saved:', this.settings);
      alert('Настройки сохранены');
    },
    validateEmail(email) {
      const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return re.test(email);
    },
    async saveSettings() {
  try {
    const response = await fetch('/api/users/theme', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ theme: this.settings.theme })
    });

    if (!response.ok) {
      throw new Error('Не удалось сохранить настройки');
    }

    alert('Настройки сохранены');
  } catch (error) {
    console.error('Ошибка при сохранении настроек:', error);
    alert('Ошибка при сохранении настроек');
  }
},
    async sendSupport() {
      // Reset errors
      this.emailError = '';
      this.messageError = '';
      
      // Validate fields
      if (!this.supportRequest.email) {
        this.emailError = 'Пожалуйста, введите email';
        return;
      }
      
      if (!this.validateEmail(this.supportRequest.email)) {
        this.emailError = 'Пожалуйста, введите корректный email';
        return;
      }
      
      if (!this.supportRequest.message) {
        this.messageError = 'Пожалуйста, введите сообщение';
        return;
      }
      
      try {
        // Here you would normally send the data to your backend
        // For demonstration, we'll simulate sending to the email
        const response = await this.sendEmailToSupport(
          this.supportRequest.email,
          this.supportRequest.message
        );
        
        console.log('Support request sent:', response);
        alert('Сообщение отправлено в поддержку');
        this.supportRequest = { email: '', message: '' };
      } catch (error) {
        console.error('Error sending support request:', error);
        alert('Произошла ошибка при отправке сообщения');
      }
    },
    // This is a mock function - in a real app you would call your backend API
    sendEmailToSupport(fromEmail, message) {
      const supportEmail = 'motidzuki.yasu@gmail.com';
      console.log(`Sending email from ${fromEmail} to ${supportEmail} with message: ${message}`);
      
      // Simulate API call
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({ status: 'success' });
        }, 1000);
      });
    }
  }
};
</script>


<style>
.settings {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 100%;
  background: linear-gradient(#EFD4E1, #99CAF3);
  font-family: 'Helvetica Neue', sans-serif;
  position: relative;
  color: white;
}

.settings-content {
  padding: 20px;
  margin-top: 60px;
  max-width: 600px;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
}

.setting-group {
  margin-bottom: 20px;
}

.setting-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.setting-group select {
  width: 100%;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: white;
  color: black;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(79, 149, 255, 1);
  color: white;
  padding: 12px 16px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
}

.block {
  display: flex;
  width: 100%;
  background-color: rgba(19, 111, 189, 1);
  border: 7px;
  box-sizing: border-box;
  text-align: center;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.avatar-large {
  margin-left: auto;
  margin-right: auto;
  border: 2px solid rgb(255, 255, 255);
  border-radius: 50%;
  width: 80px;
  height: 80px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-header h1 {
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

/* Sidebar + burger menu */
.burger-menu {
  z-index: 1001; /* Higher than sidebar and overlay */
  position: relative; /* Ensure it stays above other elements */
}

.burger-menu button {
  background: none;
  border: none;
  font-size: 24px;
  color: white;
  cursor: pointer;
  padding: 5px;
  outline: none; /* Remove focus outline */
}

/* Enhanced sidebar styles */
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  z-index: 1000; /* Below burger menu but above content */
  display: block;
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
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 15px;
  transition: left 0.3s ease;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
}

.sidebar-open {
  left: 0;
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

/* Form elements */
.save-btn,
.support-btn {
  background-color: rgba(79, 149, 255, 1);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
  font-weight: bold;
}

.save-btn:hover,
.support-btn:hover {
  background-color: rgba(60, 130, 235, 1);
}

.support-title {
  margin-top: 30px;
  margin-bottom: 15px;
}

.support-input,
.support-textarea {
  width: 100%;
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: white;
  color: black;
}

.support-textarea {
  min-height: 100px;
  resize: vertical;
}

/* Mobile support */
@media (max-width: 600px) {
  .chat-header h1 {
    font-size: 16px;
  }

  .sidebar {
    width: 80%;
    left: -80%;
  }

  .settings-content {
    padding: 15px;
    margin-top: 60px;
  }
}
</style>

