import axios from 'axios';

// Создаем экземпляр axios
const OpenApi = axios.create({
  baseURL: 'http://localhost:8080',
});

export default OpenApi;
