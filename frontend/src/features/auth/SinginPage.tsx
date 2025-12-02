import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'
import Cookies from 'js-cookie';
import { Link } from 'react-router-dom';

import Modal from '../../components/modals/Modal';
import FreeApi from '../../api/FreeApi'

const SinginPage: React.FC = () => {
  const [username, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = React.useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = React.useState(false);

  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await FreeApi.post('/api/auth/signin', {
      username,
      email,
      password
    });
    console.log('Успешный вход:', response.data);
    Cookies.set("jwt_token", response.data.token, {
      expires: new Date(new Date().getTime() + 60 * 60 * 1000), // сохраняем на час
      secure: true, // только по https
      sameSite: "strict", // политика SameSite для защиты CSRF
    });
    navigate('/main');
    } catch (error: unknown) { 
      let errorMessage = 'Unknown error';
      if (axios.isAxiosError(error)) {
        if (error.response) {
          if (error.response.status === 401) {
            errorMessage = 'Неверный логин или пароль';
          } else if (error.response.data && typeof error.response.data.message === 'string') {
            errorMessage = error.response.data.message;
          } else {
            errorMessage = `Ошибка сервера: ${error.response.status}`;
          }
        } else {
          errorMessage = error.message;
        }
      } else if (error instanceof Error) {
        errorMessage = error.message;
      }

      setError(errorMessage);
      setIsModalOpen(true);
    }
  };
  const To_Main_Page =()=>{
    navigate('/main');
  }
  return (
    <div>
      <h2>Вход в систему</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Имя:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Пароль:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          
        </div>
        <button type="submit" onClick={To_Main_Page}>Войти</button>
      </form>
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <h2>Ошибка</h2>
        <p>{error}</p>
        <button onClick={() => setIsModalOpen(false)}>Закрыть</button>
      </Modal>
      <p>Еще нет акаунта? 
      <Link to="/signup">Зарегистрироваться</Link>
      </p>
    </div>
  );
};

export default SinginPage;
