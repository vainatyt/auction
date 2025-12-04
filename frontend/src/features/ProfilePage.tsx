import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import CloseApi from '../api/CloseApi'; 

interface UserData {
  email: string;
  username: string;
  avatar?: string;
}

const ProfilePage: React.FC = () => {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [user, setUser] = useState<UserData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/signin');
      return;
    }

    const fetchUserData = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await CloseApi.get('/profile');
        console.log('Полученные данные:', response); // ← ДЛЯ ОТЛАДКИ
        setUser(response.data);
      } catch (error) {
        console.error('Ошибка загрузки профиля:', error);
        setError('Не удалось загрузить профиль');
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [isAuthenticated, navigate]);

  if (loading) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center', color: '#666' }}>
        Загрузка профиля...
      </div>
    );
  }

  if (error || !user) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center', color: '#dc3545' }}>
        {error || 'Данные пользователя не найдены'}
        <br />
        <button onClick={() => window.location.reload()}>Повторить</button>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
      <div style={{ 
        display: 'flex', 
        gap: '2rem', 
        marginBottom: '2rem',
        padding: '1.5rem',
        background: '#f8f9fa',
        borderRadius: '12px'
      }}>
        
        <div style={{ flex: 1 }}>
          <h1 style={{ margin: '0 0 1rem 0', color: '#333' }}>
            {user.username || 'Пользователь'}
          </h1>
          <p style={{ margin: '0.5rem 0', fontSize: '1.1em', color: '#666' }}>
            <strong>Email:</strong> {user.email}
          </p>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
