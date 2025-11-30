import React, { useState } from 'react';
import axios from 'axios'

const SinginPage: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
        const response = await axios.post('http://localhost:8080/api/auth/signin', {
      name,
      email,
      password
    },
    {
      withCredentials: true 
    });
    console.log('Успешный вход:', response.data);
    // Перенаправление на /login или главную
  } catch (error) {
    console.error('Ошибка входа:', error);
  }
  };

  return (
    <div>
      <h2>Вход в систему</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Имя:</label>
          <input
            type="text"
            value={name}
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
        <button type="submit">Войти</button>
      </form>
    </div>
  );
};

export default SinginPage;
