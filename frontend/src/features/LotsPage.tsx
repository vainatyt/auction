import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import CloseApi from '../api/CloseApi';
import { useNavigate } from 'react-router-dom';

import { Lot } from '../types/Lot';
import { Page } from '../types/Page'
import RenderLot from '../components/LotRender';

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

  const handleUnpin = async (lotId: number) => {
    try {
      await CloseApi.delete(`/track/remove/${lotId}`);
    } catch (error) {
      console.error('Ошибка удаления:', error);
    }
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
        gridTemplateColumns: 'repeat(auto-fill, minmax(400px, 1fr))', 
        gap: '1.5rem', 
        marginBottom: '2rem' 
      }}>
        {lotsPage.content.map((lot) => (
          <RenderLot 
            key={lot.id}
            lot={lot}
            onUnpin={handleUnpin}
            />
        ))}
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
