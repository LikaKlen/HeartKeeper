<template>
  <div class="calendar-notes">
    <div class="calendar-header">
      <div class="header-left">
        <button @click="toggleSidebar">☰</button>
        <h1>Календарь</h1>
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
        <li><a href="#" @click.prevent="logout">Выход</a></li>
      </ul>
    </div>

   <div class="calendar-content">
      <h2>Календарь</h2>
      <vue-cal
        style="height: 600px;"
        :events="calendarEvents"
        default-view="month"
        @cell-click="handleCellClick"
        time="24"
        locale="ru"
      />

      <h2>Заметки</h2>
      <button @click="openNoteModal(null)" class="add-note-btn">➕ Новая заметка</button>

      <div v-for="note in notes" :key="note.id" class="note-card">
        <div class="note-header">
          <h3>{{ note.title }}</h3>
          <div class="note-actions">
            <button @click="openNoteModal(note)">✏️</button>
            <button @click="deleteNote(note.id)">🗑️</button>
            <button @click="toggleLock(note)">{{ note.locked ? '🔓' : '🔒' }}</button>
          </div>
        </div>
        <p>{{ note.description }}</p>
        <div class="note-meta">
          <p><strong>Дата:</strong> {{ formatDate(note.dateTime) }}</p>
          <p><strong>Место:</strong> {{ note.location || 'не указано' }}</p>
          <p><strong>Повтор:</strong> {{ note.reminderRepeatDays > 0 ? note.reminderRepeatDays + ' дн.' : 'нет' }}</p>
          <p><strong>Напоминание:</strong> {{ note.reminderEnabled ? 'Да' : 'Нет' }}</p>
        </div>
      </div>

      <div v-if="showNoteModal" class="modal-overlay" @click.self="closeNoteModal">
        <div class="modal-content">
          <h3>{{ editingNote ? 'Редактировать заметку' : 'Новая заметка' }}</h3>
          <form @submit.prevent="saveNote">
            <input v-model="currentNote.title" placeholder="Название" required />
            <textarea v-model="currentNote.description" placeholder="Описание" />
            <input type="datetime-local" v-model="currentNote.dateTime" required />
            <input v-model="currentNote.location" placeholder="Место" />
            <label>
              <input type="checkbox" v-model="currentNote.reminderEnabled" /> Напоминание
            </label>
            <input type="number" v-model.number="currentNote.reminderRepeatDays" placeholder="Повтор каждые X дней" />
            <button type="submit">Сохранить</button>
            <button type="button" @click="closeNoteModal">Отмена</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import VueCal from 'vue-cal';
import 'vue-cal/dist/vuecal.css';

export default {
  components: {
    VueCal
  },
  data() {
    return {
      showSidebar: false,
      notes: [],
      calendarEvents: [],
      showNoteModal: false,
      editingNote: null,
      currentNote: {
        title: '',
        description: '',
        dateTime: '',
        location: '',
        reminderEnabled: false,
        reminderRepeatDays: 0,
        locked: false,
      },
      userId: null,
      username: '',
      userAvatar: '',
      hasPartner: false
    };
  },
  methods: {
    async fetchUserData() {
      try {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (!userData) {
          console.warn('User data not found in localStorage');
          return;
        }
        this.userId = userData.id;
        this.username = userData.username || 'Пользователь';
        this.userAvatar = userData.avatar || 'https://via.placeholder.com/150';
        this.hasPartner = !!localStorage.getItem('partnerId');
        await this.fetchNotes();
      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    },
    async fetchNotes() {
      try {
        const response = await axios.get(`/api/calendar/notes/${this.userId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        
        this.notes = response.data;
        this.updateCalendarEvents();
      } catch (error) {
        if (error.response && error.response.status === 401) {
          await this.handleUnauthorized();
        } else {
          console.error('Error fetching notes:', error);
        }
      }
    },
    updateCalendarEvents() {
      this.calendarEvents = this.notes.map(note => ({
        title: note.title,
        start: note.dateTime,
        end: note.dateTime,
        content: note.description
      }));
    },
    async saveNote() {
      try {
        await axios.post(`/api/calendar/note?userId=${this.userId}`, this.currentNote, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        await this.fetchNotes();
        this.closeNoteModal();
      } catch (error) {
        if (error.response && error.response.status === 401) {
          await this.handleUnauthorized();
        } else {
          console.error('Error saving note:', error);
        }
      }
    },
    async deleteNote(noteId) {
      try {
        await axios.delete(`/api/calendar/note/${noteId}?userId=${this.userId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        await this.fetchNotes();
      } catch (error) {
        if (error.response && error.response.status === 401) {
          await this.handleUnauthorized();
        } else {
          console.error('Error deleting note:', error);
        }
      }
    },
    async toggleLock(note) {
      try {
        await axios.patch(
          `/api/calendar/note/${note.id}/lock?userId=${this.userId}&locked=${!note.locked}`,
          {},
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('accessToken')}`
            }
          }
        );
        await this.fetchNotes();
      } catch (error) {
        if (error.response && error.response.status === 401) {
          await this.handleUnauthorized();
        } else {
          console.error('Error toggling note lock:', error);
        }
      }
    },
    openNoteModal(note) {
      this.editingNote = !!note;
      this.currentNote = note
        ? { ...note }
        : {
            title: '',
            description: '',
            dateTime: new Date().toISOString().slice(0, 16),
            location: '',
            reminderEnabled: false,
            reminderRepeatDays: 0,
            locked: false
          };
      this.showNoteModal = true;
    },
    closeNoteModal() {
      this.showNoteModal = false;
    },
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
    },
    formatDate(datetime) {
      return new Date(datetime).toLocaleString();
    },
    handleCellClick({ date }) {
      const isoString = new Date(date).toISOString().slice(0, 16);
      this.openNoteModal({
        title: '',
        description: '',
        dateTime: isoString,
        location: '',
        reminderEnabled: false,
        reminderRepeatDays: 0,
        locked: false
      });
    },
    async logout() {
  try {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      await axios.post('http://localhost:8080/logout', {}, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      });
    }
  } catch (error) {
    console.error('Logout error:', error);
  } finally {
    this.clearUserData();
    // Add a small delay before redirecting to ensure data is cleared
    setTimeout(() => {
      this.$router.push('/login');
    }, 100);
  }},
    clearUserData() {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      localStorage.removeItem('partnerId');
    },
    async handleUnauthorized() {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      console.warn('No refresh token available');
      return;
    }

    const response = await axios.post('http://localhost:8080/refresh', {
      refreshToken
    });

    localStorage.setItem('accessToken', response.data.accessToken);
    await this.fetchNotes();
  } catch (error) {
    console.error('Refresh token failed:', error);
    // Instead of automatically logging out, show a message to the user
    this.$notify({
      title: 'Session Expired',
      message: 'Your session has expired. Please log in again.',
      type: 'warning'
    });}
  }
}
    // Only clear data when user explicitly tries to access protected routes
  
};
</script>

<style scoped>
.calendar-notes {
  padding: 1rem;
  background: linear-gradient(#EFD4E1, #99CAF3);
}
.note-card {
  background: #f8f8f8;
  margin: 1rem 0;
  padding: 1rem;
  border-radius: 8px;
}
.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.note-actions button {
  margin-left: 0.5rem;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  padding: 1rem;
  border-radius: 8px;
  width: 300px;
}
</style>
