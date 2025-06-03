<template>
  <div class="partner">
    <div class="chat-header">
      <div class="header-left">
        <div class="burger-menu" :class="{ visible: showSidebar }">
          <button @click="toggleSidebar">☰</button>
        </div>
        <p>{{ hasPartner ? 'Партнер' : 'Добавление партнера' }}</p>
      </div>
      <div class="partner-status" v-if="hasPartner">
        <span>Партнер подключен</span>
        <img :src="partnerInfo.partnerDto.profilePhotoUrl || 'https://via.placeholder.com/100'" class="avatar" />
      </div>
      <div class="partner-status" v-else>
        <span>Партнер не подключен</span>
      </div>
    </div>

    <div class="sidebar-overlay" v-if="showSidebar" @click="toggleSidebar"></div>
    <div class="sidebar" :class="{ 'sidebar-open': showSidebar }">
      <div class="block">
        <img :src="userAvatar" class="avatar-large" />
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

    <div class="partner-container">
      <div v-if="!hasPartner" class="partner-connect">
        <h3>Ваш код приглашения</h3>
        <div class="code-box">
          <span>{{ partnerInfo.invitationCode }}</span>
          <button @click="copyCode" class="copy-btn">Копировать</button>
        </div>

        <h3>Подключиться к партнеру</h3>
        <div class="connect-form">
          <input v-model="inputCode" placeholder="Введите код партнера" />
          <button @click="connectPartner" class="connect-btn">Подключиться</button>
        </div>

        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      </div>

      <div v-else class="partner-info">
        <h3>Информация о партнере</h3>
        <div class="partner-details">
          <img :src="partnerInfo.partnerDto.profilePhotoUrl || 'https://via.placeholder.com/100'" class="partner-avatar" />
          <div class="partner-meta">
            <p><strong>Имя:</strong> {{ partnerInfo.partnerDto.username }}</p>
            <p><strong>Email:</strong> {{ partnerInfo.partnerDto.email }}</p>
            <p v-if="partnerBirthDate"><strong>Дата рождения:</strong> {{ partnerBirthDate }}</p>
            <p v-if="partnerAbout"><strong>О себе:</strong> {{ partnerAbout }}</p>
          </div>
        </div>
        <button @click="showDeleteConfirm = true" class="disconnect-btn">Удалить партнера</button>
        
        <div v-if="showDeleteConfirm" class="delete-confirm">
          <p>Вы уверены, что хотите удалить партнера?</p>
          <div class="confirm-buttons">
            <button @click="disconnect" class="confirm-delete">Удалить</button>
            <button @click="showDeleteConfirm = false" class="cancel-delete">Отмена</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      showSidebar: false,
      partnerInfo: {
        hasPartner: false,
        invitationCode: '',
        partnerDto: {
          username: '',
          email: '',
          profilePhotoUrl: ''
        }
      },
      username: '',
      userAvatar: 'https://via.placeholder.com/100',
      inputCode: '',
      errorMsg: '',
      showDeleteConfirm: false,
      partnerBirthDate: '',  // These would come from your backend
      partnerAbout: ''       // You'll need to fetch these separately
    };
  },
  computed: {
    hasPartner() {
      return this.partnerInfo.hasPartner;
    }
  },
  methods: {
    async loadPartnerInfo() {
      try {
        const response = await axios.get('/api/partner/info');
        this.partnerInfo = response.data;
        
        if (this.hasPartner) {
          // Fetch additional partner info if needed
          await this.loadAdditionalPartnerInfo();
        }
      } catch (error) {
        console.error("Error loading partner info:", error);
      }
    },
    async loadAdditionalPartnerInfo() {
      try {
        // You'll need to implement these endpoints in your backend
        const [birthRes, aboutRes] = await Promise.all([
          axios.get(`/api/user/${this.partnerInfo.partnerDto.email}/birthdate`),
          axios.get(`/api/user/${this.partnerInfo.partnerDto.email}/about`)
        ]);
        
        this.partnerBirthDate = birthRes.data;
        this.partnerAbout = aboutRes.data;
      } catch (error) {
        console.error("Error loading additional partner info:", error);
      }
    },
    async copyCode() {
      try {
        await navigator.clipboard.writeText(this.partnerInfo.invitationCode);
        alert('Код скопирован в буфер обмена');
      } catch (err) {
        console.error('Не удалось скопировать код:', err);
      }
    },
    async connectPartner() {
      if (!this.inputCode.trim()) {
        this.errorMsg = 'Пожалуйста, введите код партнера';
        return;
      }
      
      try {
        await axios.post('/api/partner/connect', { code: this.inputCode });
        this.errorMsg = '';
        await this.loadPartnerInfo();
        this.inputCode = '';
      } catch (error) {
        this.errorMsg = error.response?.data?.message || 'Ошибка при подключении партнера';
      }
    },
    async disconnect() {
      try {
        await axios.delete('/api/partner/disconnect');
        this.showDeleteConfirm = false;
        await this.loadPartnerInfo();
      } catch (error) {
        console.error("Error disconnecting partner:", error);
      }
    },
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
    }
  },
  async created() {
    await this.loadPartnerInfo();
  }
};
</script>

<style scoped>
.partner {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 100%;
  background: linear-gradient(#EFD4E1, #99CAF3);
  font-family: 'Helvetica Neue', sans-serif;
  position: relative;
  color: white;
}

.partner-container {
  padding: 20px;
  margin-top: 60px;
  max-width: 600px;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
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


sidebar-overlay {
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

/* Partner specific styles */
.partner-connect, .partner-info {
  background-color: rgba(255, 255, 255, 0.2);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.code-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  color: #333;
  padding: 10px 15px;
  border-radius: 4px;
  margin: 15px 0;
}

.copy-btn, .connect-btn, .disconnect-btn {
  background-color: rgba(79, 149, 255, 1);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.copy-btn:hover, .connect-btn:hover, .disconnect-btn:hover {
  background-color: rgba(60, 130, 235, 1);
}

.connect-form {
  display: flex;
  gap: 10px;
  margin: 15px 0;
}

.connect-form input {
  flex: 1;
  padding: 8px 12px;
  border-radius: 4px;
  border: none;
  background-color: white;
  color: #333;
}

.partner-details {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 20px;
}

.partner-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2px solid white;
}

.partner-meta {
  flex: 1;
}

.partner-meta p {
  margin: 8px 0;
}

.delete-confirm {
  background-color: rgba(255, 255, 255, 0.2);
  padding: 15px;
  border-radius: 4px;
  margin-top: 15px;
}

.confirm-buttons {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.confirm-delete {
  background-color: #f44336;
}

.cancel-delete {
  background-color: #ccc;
  color: #333;
}

.error {
  color: #ffeb3b;
  margin-top: 10px;
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

  .partner-container {
    padding: 15px;
  }

  .partner-details {
    flex-direction: column;
    text-align: center;
  }
}
</style>




