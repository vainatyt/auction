import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';

import SigninPage from './auth/SigninPage';
import ProfilePage from './ProfilePage';
import CreateLotPage from './CreateLot';
import LotsPage from './LotsPage';
import TrackedLotsPage from './TrackedLotsPage';
// ← Добавьте эти строки
import EmailsPage from './EmailsPage';

const MainPage = () => {
  const [page, setPage] = useState<'track' | 'home' | 'profile' | 'create-lot' | 'signin' | 'emails'>('home');

  // ← Добавьте состояние для писем
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(false);

  // ← Функция загрузки писем
  const fetchEmails = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/emails/list');
      const data = await response.json();
      setEmails(data);
    } catch (error) {
      console.error('Ошибка загрузки писем:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  const renderPage = () => {
    switch (page) {
      case 'profile':
        return <ProfilePage/>;
      case 'create-lot':
        return <CreateLotPage/>;
      case 'signin':
        return <SigninPage />;
      case 'home':
        return <LotsPage/>;
      case 'track':
        return <TrackedLotsPage/>;
      case 'emails':  // ← Добавьте кейс
        return <EmailsPage emails={emails} loading={loading} onRefresh={fetchEmails} />;
      default:
        return <LotsPage/>;
    }
  };

  return (
    <>
      <header style={{ padding: '1rem', background: '#eee', display: 'flex', gap: '1rem' }}>
        <button onClick={() => setPage('home')}>Главная</button>
        <button onClick={() => setPage('profile')}>Профиль</button>
        <button onClick={() => setPage('create-lot')}>Создать лот</button>
        <button onClick={() => setPage('track')}>Отслеживаемые лоты</button>
        {/* ← Добавьте кнопку */}
        <button onClick={() => { setPage('emails'); fetchEmails(); }}>
          Письма ({emails.length})
        </button>
      </header>

      <main style={{ padding: '1rem' }}>
        {renderPage()}
      </main>
    </>
  );
};

export default MainPage;
