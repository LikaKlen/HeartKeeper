import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:5555',
  
  timeout: 10000, // Add timeout
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

api.interceptors.request.use(config => {
  console.log('Request:', config);
  return config;
}, error => {
  console.error('Request error:', error);
  return Promise.reject(error);
});

// Add response interceptor
api.interceptors.response.use(response => {
  console.log('Response:', response);
  return response;
}, error => {
  console.error('Response error:', error);
  return Promise.reject(error);
});

// Auth endpoints
export const signup = (userData) => api.post('/auth/signup', userData);
export const signin = (credentials) => api.post('/auth/signin', credentials);

export const getProfile = () => api.get('/api/profile');
export const logout = () => api.get('/logout');

export const updateProfile = (data) => api.put('/api/profile', data);

export const getPartnerCode = () => api.get('/api/partner/code');

export const connectPartner = (code) =>
  api.post('/api/partner/connect', null, { params: { code } });

export const getPartner = () => api.get('/api/partner');

export const removePartner = () => api.delete('/api/partner');

export const getMessages = () => api.get('/api/chat');

export const sendMessage = (text, mediaUrl = null) =>
  api.post('/api/chat', null, { params: { text, mediaUrl } });




export const getUserData = () => axios.get('/api/user/profile')
export const getPartnerInfo = () => axios.get('/api/partner/info')



//  Функции настроек и поддержки пользователя
export const getSettings = (userId) => api.get(`/api/settings?userId=${userId}`);

export const updateSettings = (userId, settings) =>
  api.put(`/api/settings?userId=${userId}`, settings);

export const sendSupportRequest = (userId, supportRequest) =>
  api.post(`/api/settings/support?userId=${userId}`, supportRequest);



//  Экспорт по умолчанию: сам экземпляр axios
// export default {
//   getSettings(userId) {
//     return Promise.resolve({ data: { theme: 'light', language: 'ru' } });
//   },
//   updateSettings(userId, settings) {
//     return Promise.resolve();
//   },
//   // sendSupportRequest(request) {
//   //   return Promise.resolve();
//   // }
// };
export default api;