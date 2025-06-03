<template>
  <div class="goals">
    <div class="chat-header">
      <div class="header-left">
        <div class="burger-menu">
          <button @click="toggleSidebar">☰</button>
        </div>
        <h1>Цели</h1>
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

    <div class="goals-content">
      <div class="goals-tabs">
        <button @click="loadGoals('personal')" :class="{ active: activeTab === 'personal' }">Личные</button>
        <button @click="loadGoals('shared')" :class="{ active: activeTab === 'shared' }" :disabled="!hasPartner">Общие</button>
      </div>

      <button class="add-goal-btn" @click="openModal">Добавить цель</button>

      <div v-for="goal in goals" :key="goal.id" class="goal-card">
        <div class="goal-header">
          <h3>{{ goal.name }}</h3>
          <div class="goal-actions">
            <button @click="editGoal(goal)">✎</button>
            <button @click="deleteGoal(goal.id)">✖</button>
          </div>
        </div>
        <p>{{ goal.description }}</p>
        <div class="goal-dates">
          <span>С: {{ formatDate(goal.startDate) }}</span>
          <span>По: {{ formatDate(goal.endDate) }}</span>
        </div>
        <div class="progress-container">
          <div class="progress-bar" :style="{ width: calculateProgress(goal) + '%' }"></div>
          <div class="progress-text">{{ calculateProgress(goal) }}%</div>
        </div>
        <ul class="stages-list">
          <li v-for="stage in goal.stages" :key="stage.id">
            <input type="checkbox" :checked="stage.completed" @change="toggleStage(goal.id, stage.id, !stage.completed)" />
            <span :class="{ completed: stage.completed }">{{ stage.name }}</span>
          </li>
        </ul>
      </div>
    </div>

    <div class="modal-overlay" v-if="showModal">
      <div class="modal-content">
        <h3>{{ editingGoal ? 'Редактировать цель' : 'Добавить цель' }}</h3>
        <div class="form-group">
          <label>Название</label>
          <input v-model="goalForm.name" type="text" />
        </div>
        <div class="form-group">
          <label>Описание</label>
          <textarea v-model="goalForm.description"></textarea>
        </div>
        <div class="form-group">
          <label>Дата начала</label>
          <input v-model="goalForm.startDate" type="date" />
        </div>
        <div class="form-group">
          <label>Дата окончания</label>
          <input v-model="goalForm.endDate" type="date" />
        </div>
        <div class="form-group">
          <label>Этапы</label>
          <div v-for="(stage, index) in goalForm.stages" :key="index" class="form-group">
            <input v-model="stage.name" placeholder="Название этапа" />
            <button @click="removeStage(index)" style="margin-left: 10px">Удалить</button>
          </div>
          <button @click="addStage">Добавить этап</button>
        </div>
        <div class="form-actions">
          <button @click="closeModal">Отмена</button>
          <button @click="saveGoal">Сохранить</button>
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
      showModal: false,
      editingGoal: null,
      activeTab: 'personal',
      goals: [],
      hasPartner: false, // Load this from user state
      userAvatar: '/avatar.jpg', // Replace with actual
      username: 'Пользователь', // Replace with actual
      goalForm: {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        stages: []
      }
    };
  },
  mounted() {
    this.loadGoals('personal');
    this.getUserInfo(); // <--- Добавлено
  },
  methods: {
    getGoalTypeEnum() {
  return this.activeTab.toUpperCase();
},
     getUserInfo() {
    axios.get('/api/users/')
      .then(response => {
        const user = response.data;
        this.username = user.username;
        this.userAvatar = user.avatarUrl || '/default-avatar.jpg';
        this.hasPartner = !!user.partnerId; // true, если у пользователя есть партнер
      })
      .catch(error => {
        console.error('Ошибка при получении данных пользователя:', error);
      });
  },
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
    },
    loadGoals(type) {
      this.activeTab = type;
      axios.get(`/api/goals/${type}`)
        .then(response => {
          this.goals = response.data;
        });
    },
    openModal() {
      this.resetForm();
      this.showModal = true;
    },
    closeModal() {
      this.showModal = false;
      this.editingGoal = null;
    },
    resetForm() {
      this.goalForm = {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        stages: []
      };
    },
    addStage() {
      this.goalForm.stages.push({ name: '', completed: false });
    },
    removeStage(index) {
      this.goalForm.stages.splice(index, 1);
    },
    saveGoal() {
  const method = this.editingGoal ? 'put' : 'post';
  const url = this.editingGoal
  ? `/api/goals/${this.activeTab.toUpperCase()}/${this.editingGoal.id}`
  : `/api/goals/${this.activeTab.toUpperCase()}`;
  // Преобразование данных для отправки (можно валидировать перед этим)
  const goalData = {
    name: this.goalForm.name,
    description: this.goalForm.description,
    startDate: this.goalForm.startDate,
    endDate: this.goalForm.endDate,
    stages: this.goalForm.stages.map(stage => ({
      name: stage.name,
      completed: stage.completed || false
    }))
  };

  axios[method](url, goalData)
    .then(() => {
      this.loadGoals(this.activeTab);
      this.closeModal();
    })
    .catch(error => {
      console.error('Ошибка при сохранении цели:', error);
      alert('Не удалось сохранить цель.');
    });
},
    editGoal(goal) {
      this.editingGoal = goal;
      this.goalForm = JSON.parse(JSON.stringify(goal));
      this.showModal = true;
    },
    deleteGoal(goalId) {
  if (!confirm('Вы уверены, что хотите удалить эту цель?')) return;
  axios.delete(`/api/goals/${this.getGoalTypeEnum()}/${goalId}`)
    .then(() => {
      this.loadGoals(this.activeTab);
    })
    .catch(error => {
      console.error('Ошибка при удалении цели:', error);
      alert('Не удалось удалить цель.');
    });
},
    toggleStage(goalId, stageId, completed) {
      axios.put(`/api/goals/${this.getGoalTypeEnum()}/${goalId}/stages/${stageId}?completed=${completed}`)
        .then(() => {
          this.loadGoals(this.activeTab);
        });
    },
    calculateProgress(goal) {
      if (!goal.stages || goal.stages.length === 0) return 0;
      const completed = goal.stages.filter(s => s.completed).length;
      return Math.round((completed / goal.stages.length) * 100);
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleDateString();
    }
  }
 
};
</script>

<style>
  .goal-card {
    border: 1px solid #ccc;
    border-radius: 10px;
    padding: 12px;
    margin-bottom: 16px;
  }


 
.goals{
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 100%;
  background: linear-gradient(#EFD4E1, #99CAF3);
  font-family: 'Helvetica Neue', sans-serif;
  position: relative;
}
.goals-content {
  padding: 80px 20px 20px;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.goals-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #ccc;
}

.goals-tabs button {
  padding: 10px 20px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  cursor: pointer;
  font-size: 16px;
}

.goals-tabs button.active {
  border-bottom-color: #4f95ff;
  font-weight: bold;
}

.goals-tabs button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.add-goal-btn {
  background-color: #4f95ff;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  margin-bottom: 20px;
  cursor: pointer;
}

.add-goal-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.goal-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  color: #333;
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.goal-actions button {
  background: none;
  border: none;
  cursor: pointer;
  margin-left: 10px;
  font-size: 16px;
}

.goal-dates {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
  font-size: 14px;
  color: #666;
}

.progress-container {
  margin: 15px 0;
  position: relative;
}

.progress-bar {
  height: 20px;
  background-color: #4f95ff;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.progress-text {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  text-align: center;
  line-height: 20px;
  color: white;
  font-size: 12px;
}

.stages-list {
  list-style: none;
  padding: 0;
  margin-top: 15px;
}

.stages-list li {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
}

.stages-list input[type="checkbox"] {
  margin-right: 10px;
}

.stages-list .completed {
  text-decoration: line-through;
  color: #888;
}

/* Modal styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.form-group textarea {
  min-height: 80px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.form-actions button {
  padding: 8px 15px;
  margin-left: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.form-actions button:first-child {
  background-color: #ccc;
}

.form-actions button:last-child {
  background-color: #4f95ff;
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


  