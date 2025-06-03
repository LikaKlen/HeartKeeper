<template>
  <div class="profile">
    <div class="chat-header">
      <div class="header-left">
        <div class="burger-menu">
          <button @click="toggleSidebar">☰</button>
        </div>
        <p>{{ hasPartner ? 'Партнер' : 'Добавление партнера' }}</p>
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

    <!-- Main Content -->
    <div class="profile-content">
      <h1 class="text-2xl font-bold mb-4">Your Profile</h1>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-1">Profile Photo</label>
        <img
          v-if="profile.profilePhotoUrl"
          :src="profile.profilePhotoUrl"
          alt="Profile"
          class="w-32 h-32 rounded-full object-cover mb-2"
        />
        <input type="file" @change="onPhotoChange" />
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-1">Name</label>
        <input
          v-model="profile.username"
          type="text"
          class="w-full px-3 py-2 border rounded"
        />
      </div>

      <button
        @click="saveProfile"
        class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
      >
        Save Changes
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      showSidebar: false,
      hasPartner: false,
      username: '',
      userAvatar: 'https://via.placeholder.com/100',
      partnerAvatar: 'https://via.placeholder.com/100',
      profile: {
        username: '',
        email: '',
        profilePhotoUrl: '',
        backgroundUrl: ''
      }
    };
  },
  methods: {
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
    },
    async fetchProfile() {
      try {
        const response = await axios.get('/api/profile');
        this.profile = response.data;
      } catch (error) {
        console.error('Failed to fetch profile:', error);
      }
    },
    async saveProfile() {
      try {
        await axios.put('/api/profile', this.profile);
        alert('Profile updated!');
      } catch (error) {
        console.error('Failed to update profile:', error);
      }
    },
    async onPhotoChange(event) {
      const file = event.target.files[0];
      if (!file) return;
      const formData = new FormData();
      formData.append('file', file);
      try {
        const response = await axios.post('/api/profile/photo', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        this.profile.profilePhotoUrl = response.data.photoUrl;
      } catch (error) {
        console.error('Failed to upload photo:', error);
      }
    }
  },
  mounted() {
    this.fetchProfile();
  }
};
</script>

<style>
.profile {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 100%;
  background: linear-gradient(#EFD4E1, #99CAF3);
  font-family: 'Helvetica Neue', sans-serif;
  position: relative;
  color: white;
}

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
  border: 2px solid rgb(255, 255, 255);
  border-radius: 50%;
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

.profile-content {
  padding: 24px;
  max-width: 600px;
  margin: 80px auto 0;
  width: 100%;
}

.profile-content input[type="text"],
.profile-content input[type="file"] {
  background: white;
  color: black;
}

/* Mobile support */
@media (max-width: 600px) {
  .chat-header h2 {
    font-size: 16px;
  }

  .sidebar {
    width: 80%;
    left: -80%;
  }

  .profile-content {
    padding: 16px;
    margin-top: 60px;
  }
}
</style>