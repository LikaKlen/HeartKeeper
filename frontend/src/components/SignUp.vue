<script>
import api from "./api/api.js";

export default {
  data() {
    return {
      username: '',
      email: '',
      userPassword: '',
      repeatedPassword: '',
      errors: {
        username: '',
        email: '',
        password: '',
        passwordMatch: '',
        general: ''
      },
      emailPattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      isLoading: false
    };
  },
  methods: {
   async signupUser() {
  if (!this.validateForm()) return;
  
  this.isLoading = true;
  try {
    const response = await api.post('/auth/signup', {
      username: this.username,
      email: this.email,
      password: this.userPassword
    }, {
      validateStatus: function (status) {
        return status >= 200 && status < 500; // Resolve only if status code is less than 500
      }
    });

    console.log('Full response:', response); // Log full response
    
    if (response.status === 200) {
      console.log('Registration successful:', response.data);
      this.$router.push('/signin');
    } else {
      // Handle specific status codes
      if (response.status === 409) {
        this.errors.email = 'Пользователь с таким email уже существует';
      } else {
        this.errors.general = response.data || 'Ошибка регистрации';
      }
    }
  } catch (error) {
    console.error('Network error:', error);
    if (error.code === 'ECONNABORTED') {
      this.errors.general = 'Превышено время ожидания сервера';
    } else if (error.response) {
      // The request was made and the server responded with a status code
      this.errors.general = error.response.data || 'Ошибка сервера';
    } else if (error.request) {
      // The request was made but no response was received
      this.errors.general = 'Нет ответа от сервера. Проверьте подключение.';
    } else {
      // Something happened in setting up the request
      this.errors.general = 'Ошибка при отправке запроса';
    }
  } finally {
    this.isLoading = false;
  }
},
    
    validateForm() {
      this.resetErrors();
      let isValid = true;

      // Username validation
      if (!this.username.trim()) {
        this.errors.username = 'Введите имя пользователя';
        isValid = false;
      }

      // Email validation
      if (!this.email.trim()) {
        this.errors.email = 'Введите электронную почту';
        isValid = false;
      } else if (!this.emailPattern.test(this.email)) {
        this.errors.email = 'Неверный формат электронной почты';
        isValid = false;
      }

      // Password validation
      if (!this.userPassword) {
        this.errors.password = 'Введите пароль';
        isValid = false;
      } else if (this.userPassword.length < 8) {
        this.errors.password = 'Пароль должен содержать не менее 8 символов';
        isValid = false;
      }

      // Password match validation
      if (this.userPassword !== this.repeatedPassword) {
        this.errors.passwordMatch = 'Пароли не совпадают';
        isValid = false;
      }

      return isValid;
    },
    
    resetErrors() {
      this.errors = {
        username: '',
        email: '',
        password: '',
        passwordMatch: '',
        general: ''
      };
    },
    
    handleApiError(error) {
      console.error('Registration error:', error);
      
      if (error.response) {
        switch (error.response.status) {
          case 409:
            this.errors.email = 'Пользователь с таким email уже существует';
            break;
          case 400:
            this.errors.general = 'Неверные данные для регистрации';
            break;
          default:
            this.errors.general = 'Ошибка сервера. Пожалуйста, попробуйте позже.';
        }
      } else {
        this.errors.general = 'Ошибка соединения. Проверьте интернет-соединение.';
      }
    }
  }
};
</script>

<template>
  <div class="register-screen">
    <div class="register-box">
      <h1 class="app-title">HeartKeeper</h1>
      <p class="register-text">Создать учетную запись</p>
      
      <div class="form-group">
        <input
          type="text"
          v-model="username"
          placeholder="Имя пользователя"
          class="input-field"
          :class="{ 'error': errors.username }"
          @keyup.enter="signupUser"
        />
        <span class="error-message" v-if="errors.username">{{ errors.username }}</span>
      </div>
      
      <div class="form-group">
        <input
          type="email"
          v-model="email"
          placeholder="Почта"
          class="input-field"
          :class="{ 'error': errors.email }"
          @keyup.enter="signupUser"
        />
        <span class="error-message" v-if="errors.email">{{ errors.email }}</span>
      </div>
      
      <div class="form-group">
        <input
          type="password"
          v-model="userPassword"
          placeholder="Пароль"
          class="input-field"
          :class="{ 'error': errors.password }"
          @keyup.enter="signupUser"
        />
        <span class="error-message" v-if="errors.password">{{ errors.password }}</span>
      </div>
      
      <div class="form-group">
        <input
          type="password"
          v-model="repeatedPassword"
          placeholder="Повторите пароль"
          class="input-field"
          :class="{ 'error': errors.passwordMatch }"
          @keyup.enter="signupUser"
        />
        <span class="error-message" v-if="errors.passwordMatch">{{ errors.passwordMatch }}</span>
      </div>
      
      <button 
        class="register-button" 
        @click="signupUser"
        :disabled="isLoading"
      >
        <span v-if="!isLoading">Зарегистрироваться</span>
        <span v-else>Обработка...</span>
      </button>
      
      <div class="error-message general-error" v-if="errors.general">
        {{ errors.general }}
      </div>
      
      <div class="divider"></div>
      
      <p class="login-text">
        Уже есть аккаунт? <router-link to="/signin" class="login-link">Войти</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.register-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(#EFD4E1, #99CAF3);
  padding: 20px;
}

.register-box {
  background: white;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.app-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.register-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 25px;
}

.form-grid {
  display: grid;
  gap: 18px;
  margin-bottom: 10px;
}

.form-group {
  position: relative;
}

.input-field {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.input-field:focus {
  border-color: #5ca43b;
  outline: none;
  box-shadow: 0 0 0 2px rgba(92, 164, 59, 0.2);
}

.input-field.error {
  border-color: #e74c3c;
  background-color: #fff9f9;
}

.error-message {
  display: block;
  color: #e74c3c;
  font-size: 13px;
  margin-top: 6px;
  padding-left: 4px;
}

.error-message.general-error {
  text-align: center;
  margin: 15px 0;
  font-size: 14px;
}

.register-button {
  width: 100%;
  padding: 14px;
  background-color: #5ca43b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.register-button:hover {
  background-color: #4a8a2f;
  transform: translateY(-1px);
}

.register-button:active {
  transform: translateY(0);
}

.register-button:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  display: inline-flex;
  align-items: center;
}

.divider {
  height: 1px;
  background-color: #99CAF3;
  margin: 25px 0;
  position: relative;
}

.divider::before {
  content: "или";
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 0 12px;
  color: #999;
  font-size: 13px;
}

.login-text {
  font-size: 15px;
  color: #666;
  margin-top: 5px;
}

.login-link {
  color: #5ca43b;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.login-link:hover {
  color: #4a8a2f;
  text-decoration: underline;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-spinner::after {
  content: "";
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
  margin-left: 8px;
}
</style>