<script >

import api from "./api/api.js";

export default{
  
  data(){
    
    return {
      email: "",
      userPassword: "",
      errorMessage: "",
      emailError: false,
      passwordError: false,
      emailPattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      googleAuth: null,
    };
  },
  mounted() {
    this.loadGoogleScript();
  },
  methods: {
    loadGoogleScript() {
      const script = document.createElement('script');
      script.src = 'https://accounts.google.com/gsi/client';
      script.async = true;
      script.defer = true;
      script.onload = this.initGoogleAuth;
      document.head.appendChild(script);
    },
    initGoogleAuth() {
      window.google.accounts.id.initialize({
        client_id: 'YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com',
        callback: this.handleGoogleSignIn,
      });
      window.google.accounts.id.renderButton(
        document.getElementById('googleSignInButton'),
        { theme: 'outline', size: 'large' }
      );
    },
    async handleGoogleSignIn(response) {
      try {
        const googleAuthResponse = await api.post("/auth/google", {
          credential: response.credential
        });
        
        // Store tokens and user data
        localStorage.setItem("accessToken", googleAuthResponse.data.accessToken);
        localStorage.setItem("refreshToken", googleAuthResponse.data.refreshToken);
        localStorage.setItem("userId", googleAuthResponse.data.user.id);
        localStorage.setItem("username", googleAuthResponse.data.user.username);
        localStorage.setItem("avatar", googleAuthResponse.data.user.profilePhotoUrl);
        
        this.$router.push("/partnerView");
      } catch (error) {
        console.error("Google login error:", error);
        this.errorMessage = "Google login failed";
      }
    },
    async signInUser() {
      this.errorMessage = ""; // Сброс ошибки перед новой попыткой входа
      this.emailError = false;
      this.passwordError = false;
      
      // Проверка email
      if (!this.email) {
        this.emailError = true;
        this.errorMessage = "Введите электронную почту";
        return;
      }
      if (!this.emailPattern.test(this.email)) {
        this.emailError = true;
        this.errorMessage = "Неверный формат электронной почты";
        return;
      }

      // Проверка пароля
      if (!this.userPassword) {
        this.passwordError = true;
        this.errorMessage = "Введите пароль";
        return;
      }

      const user = {
        email: this.email,
        password: this.userPassword,
      };

      try {
        const response = await api.post("/auth/signin", user);
        console.log("Успешно авторизован:", response.data);
        this.$router.push("/partnerView"); // Перенаправление
      } catch (error) {
        console.error("Ошибка авторизации:", error.response?.data || error.message);
        this.errorMessage = "Неверный логин или пароль";
      }
    },
  },
  
}
</script>

<template>
  <div class="login-screen">
    <div class="login-box">
      <h1 class="app-title">HeartKeeper</h1>
      <p class="login-text">Войти в учетную запись</p>
      <input type="email" v-model="email" placeholder="Почта" class="input-field" />
      <!-- <span class="error-message" v-if="loginError">Неправильный логин</span> -->
      <input type="password" v-model="userPassword" placeholder="Пароль" class="input-field" />
      <!-- <span class="error-message" v-if="passwordError">Неправильный пароль</span> -->
      
      <button class="login-button" @click="signInUser()">Войти</button>
      
      <div class="divider"></div>
      <p class="alt-login-text">Другие способы входа</p>
      <div id="googleSignInButton">Войти через Google</div>
      <!--<button class="alt-login-button">Войти через Google</button> -->
      
      <p class="register-text">
        Нет аккаунта? <router-link to="/signup" class="register-link">Регистрация</router-link>
      </p>
    </div>
  </div>
</template>;
<style scoped>
.login-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(#EFD4E1, #99CAF3);
}

.login-box {
  background: white;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 320px;
}

.app-title {
  font-size: 24px;
  font-weight: bold;
  color: rgb(255, 252, 252);
  -webkit-text-stroke: 1px black;
  margin-bottom: 20px;
}

.login-text {
  font-size: 16px;
  margin-bottom: 20px;
  color: #555;
}

.input-group {
  margin-bottom: 15px;
  text-align: left;
}

.input-field {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.input-field:focus {
  outline: none;
  border-color: #99CAF3;
  box-shadow: 0 0 0 2px rgba(153, 202, 243, 0.2);
}

.login-button {
  width: 100%;
  padding: 12px;
  background: #5ca43b;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  margin-top: 5px;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #4a8a2f;
}

.divider {
  height: 1px;
  background: #99CAF3;
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
  padding: 0 10px;
  color: #777;
  font-size: 14px;
}

.alt-login-text {
  font-size: 14px;
  color: #777;
  margin-bottom: 15px;
}



.register-text {
  margin-top: 15px;
  font-size: 14px;
  color: #555;
}

.register-link {
  color: #5ca43b;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.register-link:hover {
  color: #4a8a2f;
}

.error-message {
  display: block;
  color: #e74c3c;
  font-size: 12px;
  margin-top: 5px;
  text-align: left;
}
</style>