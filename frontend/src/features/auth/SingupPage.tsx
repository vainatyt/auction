import React, { useState } from 'react';
import axios from 'axios'

import Modal from '../../components/modals/Modal'

const SingupPage: React.FC = () => {
  const [username, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = React.useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = React.useState(false);


  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
        const response = await axios.post('http://localhost:8080/api/auth/signup', {
      username,
      email,
      password
    });
    console.log('Успешная регистрация:', response.data);
    // Перенаправление на /login или главную
  } catch (error) {
    console.error('Ошибка регистрации:', error);
    const errorMessage = error instanceof Error ? error.message: String(error);
    setError(errorMessage || 'Unknown error');
    setIsModalOpen(true);
  }
  };

  return (
    <div>
      <h2>Регистрация</h2>
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
        <button type="submit">Зарегистрироваться</button>
      </form>
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <h2>Ошибка</h2>
        <p>{error}</p>
        <button onClick={() => setIsModalOpen(false)}>Закрыть</button>
      </Modal>
    </div>
  );
};

export default SingupPage;
