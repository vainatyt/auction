import axios from 'axios';
import Cookies from 'js-cookie';

// Создаем экземпляр axios
const CloseApi = axios.create({
  baseURL: 'http://localhost:8080',
});

// Добавляем интерцептор запросов
CloseApi.interceptors.request.use((config) => {
  const token = Cookies.get('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default CloseApi;
