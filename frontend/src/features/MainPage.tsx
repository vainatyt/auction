import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';

import SigninPage from './auth/SigninPage';
import ProfilePage from './ProfilePage';
import CreateLotPage from './CreateLot';
import LotsPage from './LotsPage';
import TrackedLotsPage from './TrackedLotsPage';
import MailsPage from './MailsPage';

const MainPage = () => {
  const [page, setPage] = useState<'track' | 'home' | 'profile' | 'create-lot' | 'signin' | 'emails'>('home');

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
      case 'emails':  
        return <MailsPage />;
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
        <button onClick={() => setPage('emails')}>Письма</button>
      </header>

      <main style={{ padding: '1rem' }}>
        {renderPage()}
      </main>
    </>
  );
};

export default MainPage;
