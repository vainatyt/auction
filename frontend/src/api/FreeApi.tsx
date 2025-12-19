import axios from 'axios';

// Создаем экземпляр axios
const OpenApi = axios.create({
  baseURL: 'https://localhost:8443',
});

export default OpenApi;
