import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import CloseApi from '../api/CloseApi';
import { useNavigate } from 'react-router-dom';

import { Lot } from '../types/Lot';
import { Page } from '../types/Page'
import RenderLot from '../components/LotRender';
import Pagination from '../components/Pagination';

const LotsPage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [lotsPage, setLotsPage] = useState<Page<Lot> | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);
  const [loading, setLoading] = useState(false);
  const [hoveredLotId, setHoveredLotId] = useState<number | null>(null); // ✅ Hover состояние

  const navigate = useNavigate();

  const fetchLots = async (page: number) => {
    setCurrentPage(page);
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
    <div>
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
            />
        ))}
      </div>
      <Pagination
                currentPage={currentPage}
                totalPages={lotsPage.totalPages}
                totalElements={lotsPage.totalElements}
                pageSize={9}
                lotsPage={lotsPage}
                onPageChange={fetchLots}
                />
        </div>
    </div>
  );
};

export default LotsPage;
