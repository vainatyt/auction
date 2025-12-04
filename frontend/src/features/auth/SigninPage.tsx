import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'
import { Link } from 'react-router-dom';

import Modal from '../../components/modals/Modal';
import FreeApi from '../../api/FreeApi'
import { useAuth } from '../../context/AuthContext'

const SinginPage: React.FC = () => {
  const [username, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = React.useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = React.useState(false);

  const { login } = useAuth();

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
    login(response.data.token);
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
    <div className="auth-container">
      <h2 className="auth-title">Вход</h2>
      <form className="auth-form" onSubmit={handleSubmit}>
        <input className="auth-input"
          type="text"
          placeholder="имя"
          value={username}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <input className="auth-input"
          type="email"
          placeholder="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input className="auth-input"
          type="password"
          placeholder="Пароль"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" className='auth-button'>Войти</button>
      </form>
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <h2>Ошибка</h2>
        <p>{error}</p>
      </Modal>
        <div className="auth-footer">
          <label>
            Нет акаунта? 
          </label>
          <Link to="/signup">Зарегистрироваться</Link>
        </div>
    </div>
  );
};

export default SinginPage;
