import { createRouter, createWebHistory } from 'vue-router';
import Home from '../components/Home.vue'
import SignIn from "../components/SignIn.vue";
import SignUp from "../components/SignUp.vue";
import Main from "../components/Main.vue";
import PartnerView from "../components/PartnerView.vue";
import SettingsView from "../components/SettingsView.vue";
import SharedGoals from "../components/SharedGoals.vue";
import ProfileView from "../components/ProfileView.vue";
import ChatComponent from "../components/ChatComponent.vue";
import Calendar from "../components/Calendar.vue";


export default  createRouter({
  history:createWebHistory(),
  routes:[
    
      {path:"/home",component:Home, alias:'/'},
      {path:"/signup",component:SignUp},
      {path:"/signin",component:SignIn},
      {path:"/auth/main",component:Main},//аменить на чат
      {path:"/partnerView",component:PartnerView},
      {path:"/settingsView",component:SettingsView},
      {path:"/sharedGoals",component:SharedGoals},
      {path:"/profileView",component:ProfileView},
      {path:"/chatComponent",component:ChatComponent},
      {path:"/calendar",component:Calendar},
      {path:"/logout"},
    

  ]
 
})