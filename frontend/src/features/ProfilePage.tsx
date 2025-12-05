import React, { useState, useEffect } from 'react';  // ✅ useCallback!
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import CloseApi from '../api/CloseApi';

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
  goodId: number;
}

interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

const ProfilePage: React.FC = () => {
  const { isAuthenticated } = useAuth();
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
  const loadLots = async (pageNum = 0) => {
    if (!user?.id) {
      console.log('Нет user.id');
      return;
    }
    try {
      console.log(`Загрузка лотов user ${user.id}, page ${pageNum}`);
      const response = await CloseApi.get<Page<Lot>>(`/lots/getmy/?page=${pageNum}&size=10`);
      console.log('Лоты:', response.data);
      setLots(response.data);
      setPage(pageNum);
    } catch (error) {
      console.error('Лоты ошибка:', error);
    }
  };

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
    if (user?.id) {
      loadLots(0);
    }
  }, [user?.id, loadLots]);

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
                  <div
                    key={lot.id}
                    style={{
                      padding: '1.5rem',
                      border: '1px solid #e0e0e0',
                      borderRadius: '12px',
                      background: 'white',
                      boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                    }}
                  >
                    <div
                      style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        marginBottom: '1rem',
                      }}
                    >
                      <h3 style={{ margin: 0, color: '#2c3e50' }}>Лот #{lot.id}</h3>
                      <span
                        style={{
                          fontSize: '1.4em',
                          fontWeight: 'bold',
                          color: '#27ae60',
                        }}
                      >
                        {lot.currentCost} ₽
                      </span>
                    </div>
                    <div
                      style={{
                        display: 'grid',
                        gridTemplateColumns: '1fr 1fr',
                        gap: '1rem',
                        fontSize: '0.95em',
                      }}
                    >
                      <div>
                        <strong>Старт:</strong> {new Date(lot.startAuction).toLocaleString('ru-RU')}
                      </div>
                      <div>
                        <strong>Конец:</strong> {new Date(lot.endAuction).toLocaleString('ru-RU')}
                      </div>
                      <div>
                        <strong>Шаг ставки:</strong> {lot.rateStep} ₽
                      </div>
                      <div>
                        <strong>Товар ID:</strong> {lot.goodId}
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              <div
                style={{
                  display: 'flex',
                  justifyContent: 'center',
                  gap: '0.5rem',
                  padding: '1.5rem',
                  background: '#f8f9fa',
                  borderRadius: '12px',
                }}
              >
                <button
                  onClick={() => loadLots(page - 1)}
                  disabled={page === 0}
                  style={{
                    padding: '0.75rem 1.5rem',
                    border: '1px solid #ddd',
                    background: page === 0 ? '#f5f5f5' : 'white',
                    borderRadius: '8px',
                    cursor: page === 0 ? 'not-allowed' : 'pointer',
                  }}
                >
                  ← Предыдущая
                </button>
                <span style={{ padding: '0.75rem 1rem', fontWeight: '500' }}>
                  Страница {page + 1} из {lots.totalPages || 1}
                </span>
                <button
                  onClick={() => loadLots(page + 1)}
                  disabled={!lots.hasNext}
                  style={{
                    padding: '0.75rem 1.5rem',
                    border: '1px solid #ddd',
                    background: !lots.hasNext ? '#f5f5f5' : 'white',
                    borderRadius: '8px',
                    cursor: !lots.hasNext ? 'not-allowed' : 'pointer',
                  }}
                >
                  Следующая →
                </button>
              </div>
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
