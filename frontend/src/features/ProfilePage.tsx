import React, { useState, useEffect, useCallback } from 'react';  // ✅ useCallback!
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import CloseApi from '../api/CloseApi';
import RenderLot from '../components/LotRender';
import Pagination from '../components/Pagination';

interface UserData {
  id: number;
  email: string;
  username: string;
  avatar?: string;
}

interface Lot {
  id: number;
  startAuction: string;
  endAuction: string;
  currentCost: number;
  rateStep: number;
  buyerId: number;
  ownerId: number;
  name: string;
  description: string;
}

interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

const ProfilePage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [currentPage, setCurrentPage] = useState(0);
  const navigate = useNavigate();
  const [user, setUser] = useState<UserData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lots, setLots] = useState<Page<Lot>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    hasNext: false,
  });
  const [page, setPage] = useState(0);

  // ✅ Правильный loadLots с useCallback
  const loadLots = useCallback(async (pageNum: number = 0) => {
    setCurrentPage(page);
    try {
      const response = await CloseApi.get<Page<Lot>>(`/lots/getmy?page=${pageNum}&size=10`);
      console.log('Лоты:', response.data);  // ✅ Лог ответа API
      setLots(response.data);
      setPage(pageNum);
    } catch (error) {
      console.error('Лоты ошибка:', error);  // ✅ Лог ошибки
    }
  }, [user?.id]);

  // ✅ Загрузка пользователя
  useEffect(() => {
    console.log('ProfilePage рендер, isAuthenticated:', isAuthenticated);
    
    if (!isAuthenticated) {
      navigate('/signin');
      return;
    }

    const fetchUserData = async () => {  // Правильное имя
      try {
        setLoading(true);
        setError(null);
        const response = await CloseApi.get<UserData>('/profile');
        console.log('User data:', response.data);
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

  //Загрузка лотов после получения user
  useEffect(() => {
    loadLots(0);
  }, [user?.id]);

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center', color: '#666' }}>Загрузка профиля...</div>;
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
    <div>
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
        <div
          style={{
            display: 'flex',
            gap: '2rem',
            marginBottom: '2rem',
            padding: '1.5rem',
            background: '#f8f9fa',
            borderRadius: '12px',
          }}
        >
          <div style={{ flex: 1 }}>
            <h1 style={{ margin: '0 0 1rem 0', color: '#333' }}>{user.username || 'Пользователь'}</h1>
            <p style={{ margin: '0.5rem 0', fontSize: '1.1em', color: '#666' }}>
              <strong>Email:</strong> {user.email}
            </p>
          </div>
        </div>

        <div style={{ marginTop: '2rem' }}>
          <h2 style={{ margin: '0 0 1.5rem 0', color: '#333' }}>Мои лоты ({lots.totalElements || 0})</h2>

          {lots.content && lots.content.length > 0 ? (
            <>
              <div style={{ display: 'grid', gap: '1.5rem', marginBottom: '2rem' }}>
                {lots.content.map((lot) => (
                        <RenderLot 
                        key={lot.id}
                        lot={lot} 
                        />
                      ))}
              </div>
              <Pagination
                currentPage={currentPage}
                totalPages={lots.totalPages}
                totalElements={lots.totalElements}
                pageSize={9}
                lotsPage={lots}
                onPageChange={loadLots}
                />
            </>
          ) : (
            <div
              style={{
                textAlign: 'center',
                padding: '3rem',
                color: '#888',
                fontSize: '1.1em',
              }}
            >
              У вас пока нет активных лотов
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
