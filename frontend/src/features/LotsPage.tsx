import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import CloseApi from '../api/CloseApi';
import { useNavigate } from 'react-router-dom';

interface Lot {
  id: number;
  name: string;
  description: string;
  currentCost: number;
  rateStep: number;
  startAuction: string;
  endAuction: string;
  buyerId?: number;
}

interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

const LotsPage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [lotsPage, setLotsPage] = useState<Page<Lot> | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);
  const [loading, setLoading] = useState(false);
  const [hoveredLotId, setHoveredLotId] = useState<number | null>(null); // ✅ Hover состояние

  const navigate = useNavigate();

  const fetchLots = async (page: number) => {
    setLoading(true);
    try {
      const response = await CloseApi.get<Page<Lot>>(`/lots/getall?page=${page}&size=${pageSize}`);
      setLotsPage(response.data);
    } catch (error) {
      console.error('Ошибка загрузки лотов:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/signin');
      return;
    }
    fetchLots(currentPage);
  }, [currentPage, isAuthenticated, navigate]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  // ✅ Открытие лота в новой вкладке
  const openLotDetails = (lot: Lot) => {
    const lotUrl = `/lot/${lot.id}`;
    window.open(lotUrl, '_blank', 'noopener,noreferrer');
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '4rem' }}>Загрузка лотов...</div>;
  if (!lotsPage?.content.length) return <div style={{ textAlign: 'center', padding: '4rem' }}>Лотов пока нет</div>;

  return (
    <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
      <h1 style={{ color: '#333', marginBottom: '2rem', textAlign: 'center' }}>
        Все лоты ({lotsPage.totalElements})
      </h1>

      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))', 
        gap: '1.5rem', 
        marginBottom: '2rem' 
      }}>
        {lotsPage.content.map((lot) => {
          const isHovered = hoveredLotId === lot.id;
          
          return (
            <div 
              key={lot.id}
              onClick={() => openLotDetails(lot)}
              onMouseEnter={() => setHoveredLotId(lot.id)}
              onMouseLeave={() => setHoveredLotId(null)}
              style={{ 
                border: '1px solid #ddd', 
                borderRadius: '12px', 
                padding: '1.5rem', 
                boxShadow: isHovered 
                  ? '0 12px 40px rgba(0,123,255,0.15)' 
                  : '0 4px 12px rgba(0,0,0,0.08)',
                transform: isHovered ? 'translateY(-4px)' : 'translateY(0)',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                cursor: 'pointer',
                background: 'white',
                position: 'relative',
                overflow: 'hidden'
              }}
            >
              {/* ✅ Иконка "подробнее" */}
              <div style={{
                position: 'absolute',
                top: '1rem',
                right: '1rem',
                background: isHovered ? 'rgba(0,123,255,0.1)' : 'rgba(0,123,255,0.05)',
                color: '#007bff',
                padding: '0.25rem 0.75rem',
                borderRadius: '20px',
                fontSize: '0.8em',
                fontWeight: '500',
                transition: 'all 0.2s ease'
              }}>
                👁️ Подробнее
              </div>
              
              <h3 style={{ 
                margin: '0 0 1rem 0', 
                color: '#2c5aa0',
                fontSize: '1.4em'
              }}>
                {lot.name}
              </h3>
              
              <div style={{ marginBottom: '1rem' }}>
                <strong style={{ color: '#666' }}>Текущая цена:</strong>{' '}
                <span style={{ 
                  color: '#e74c3c', 
                  fontSize: '1.8em',
                  fontWeight: 'bold'
                }}>
                  {lot.currentCost.toLocaleString()} ₽
                </span>
              </div>
              
              <div style={{ marginBottom: '0.75rem', fontSize: '0.95em' }}>
                <strong>Шаг ставки:</strong> {lot.rateStep.toLocaleString()} ₽
              </div>
              
              <div style={{ marginBottom: '0.75rem', fontSize: '0.9em', color: '#666' }}>
                <strong>Начало:</strong> {new Date(lot.startAuction).toLocaleDateString('ru-RU')}{' '}
                {new Date(lot.startAuction).toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'})}
              </div>
              
              <div style={{ marginBottom: '1.25rem', fontSize: '0.9em', color: '#666' }}>
                <strong>Конец:</strong> {new Date(lot.endAuction).toLocaleDateString('ru-RU')}{' '}
                {new Date(lot.endAuction).toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'})}
              </div>
              
              <p style={{ 
                marginBottom: '1.25rem', 
                lineHeight: '1.6',
                color: '#555',
                minHeight: '3em'
              }}>
                {lot.description || 'Описание отсутствует'}
              </p>
              
              {lot.buyerId && (
                <div style={{ 
                  background: '#d4edda', 
                  color: '#155724', 
                  padding: '0.75rem', 
                  borderRadius: '6px', 
                  marginBottom: '1.25rem',
                  fontSize: '0.95em',
                  fontWeight: '500'
                }}>
                  🏆 Лидер аукциона: Пользователь #{lot.buyerId}
                </div>
              )}
              
              {/* ✅ Нижняя панель действия */}
              <div style={{ 
                borderTop: '1px solid #e9ecef', 
                paddingTop: '1.25rem', 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center'
              }}>
                <span style={{ 
                  color: '#666',
                  fontSize: '0.95em'
                }}>
                  Кликни для участия в торгах
                </span>
                <div style={{ 
                  color: isHovered ? '#007bff' : '#007bff',
                  fontSize: '1.2em',
                  fontWeight: 'bold',
                  transition: 'all 0.2s ease'
                }}>
                  →
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Пагинация */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        gap: '0.5rem', 
        flexWrap: 'wrap',
        padding: '2rem 0'
      }}>
        <button
          disabled={currentPage === 0}
          onClick={() => handlePageChange(currentPage - 1)}
          style={{ 
            padding: '0.75rem 1.5rem', 
            border: '1px solid #dee2e6', 
            background: currentPage === 0 ? '#f8f9fa' : 'white',
            borderRadius: '6px',
            cursor: currentPage === 0 ? 'not-allowed' : 'pointer',
            color: currentPage === 0 ? '#6c757d' : '#333',
            transition: 'all 0.2s ease'
          }}
        >
          ← Предыдущая
        </button>

        {Array.from({ length: lotsPage.totalPages }, (_, i) => i + 1).slice(
          Math.max(0, currentPage - 2), 
          Math.min(lotsPage.totalPages, currentPage + 3)
        ).map((page) => (
          <button
            key={page}
            onClick={() => handlePageChange(page - 1)}
            style={{
              padding: '0.75rem 1.25rem',
              border: '1px solid #dee2e6',
              background: currentPage === page - 1 ? '#007bff' : 'white',
              color: currentPage === page - 1 ? 'white' : '#333',
              borderRadius: '6px',
              cursor: currentPage === page - 1 ? 'default' : 'pointer',
              fontWeight: currentPage === page - 1 ? 'bold' : 'normal',
              transition: 'all 0.2s ease'
            }}
          >
            {page}
          </button>
        ))}

        <button
          disabled={lotsPage.number === lotsPage.totalPages - 1}
          onClick={() => handlePageChange(currentPage + 1)}
          style={{ 
            padding: '0.75rem 1.5rem', 
            border: '1px solid #dee2e6', 
            background: lotsPage.number === lotsPage.totalPages - 1 ? '#f8f9fa' : 'white',
            borderRadius: '6px',
            cursor: lotsPage.number === lotsPage.totalPages - 1 ? 'not-allowed' : 'pointer',
            color: lotsPage.number === lotsPage.totalPages - 1 ? '#6c757d' : '#333',
            transition: 'all 0.2s ease'
          }}
        >
          Следующая →
        </button>
      </div>

      <div style={{ 
        textAlign: 'center', 
        marginTop: '1.5rem', 
        color: '#6c757d', 
        fontSize: '0.95em',
        padding: '1rem',
        background: '#f8f9fa',
        borderRadius: '6px'
      }}>
        Страница {lotsPage.number + 1} из {lotsPage.totalPages} • Всего {lotsPage.totalElements} лотов
      </div>
    </div>
  );
};

export default LotsPage;
