import React, { useState } from 'react';
import { Link } from 'react-router-dom'; // Можно заменить обычными кнопками или ссылками

import SigninPage from './auth/SigninPage';
import ProfilePage from './ProfilePage';
import CreateLotPage from './CreateLot';

const MainPage = () => {
  const [page, setPage] = useState<'home' | 'profile' | 'create-lot' | 'signin'>('home');

  const renderPage = () => {
    switch (page) {
      case 'profile':
        return <ProfilePage/>;
      case 'create-lot':
        return <CreateLotPage/>;
      case 'signin':
        return <SigninPage />;
    }
  };

  return (
    <>
      <header style={{ padding: '1rem', background: '#eee', display: 'flex', gap: '1rem' }}>
        <button onClick={() => setPage('profile')}>Профиль</button>
        <button onClick={() => setPage('create-lot')}>Создать лот</button>
      </header>

      <main style={{ padding: '1rem' }}>
        {renderPage()}
      </main>
    </>
  );
};

export default MainPage;
