import axios from 'axios';
import Cookies from 'js-cookie';

// Создаем экземпляр axios
const CloseApi = axios.create({
  baseURL: 'https://localhost:8443',
});

// Добавляем интерцептор запросов
CloseApi.interceptors.request.use((config) => {
  const token = Cookies.get('auction_jwt_token');
  console.log('Токен отправляется:', !!token)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default CloseApi;
