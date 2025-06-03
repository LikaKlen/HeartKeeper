<script >
export default{
  name:'Home',
  data(){
    return {
      showTapText: false,
    };
  },
  mounted() {
    setTimeout(() => {
      this.showTapText = true;
    }, 2000); // Надпись "Нажмите на экран" появляется через 2 секунды
  },
  methods:{
    goToLogin() {
      this.$router.push("/signin");
    },
  },
 
}

</script>


<template>
  <div class="welcome-screen" @click="goToLogin">
    <div class="content">
      <img src="C:/Users/Admin/Downloads/HeattKeeper/frontend/src/assets/logo.png" alt="App Icon" class="app-icon" />
      <h1 class="app-name">HeartKeeper</h1>
      <p class="welcome-text">Добро пожаловать</p>
      <p class="tap-text" v-if="showTapText">Нажмите на экран</p>
    </div>
  </div>
</template>


<style scoped>
.welcome-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(#EFD4E1, #99CAF3);
  text-align: center;
  cursor: pointer;
}

.app-icon {
  width: 120px;
  height: 120px;
}

.app-name {
  font-size: 24px;
  font-weight: bold;
  margin-top: 10px;
   color: rgb(255, 252, 252);
  -webkit-text-stroke: 1px black;
}

.welcome-text {
  font-size: 20px;
  margin-top: 20px;
}

.tap-text {
  font-size: 16px;
  margin-top: 10px;
  opacity: 0;
  animation: fadeIn 1s forwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
