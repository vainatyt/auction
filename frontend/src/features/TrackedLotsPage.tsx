import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import CloseApi from '../api/CloseApi';
import { Lot } from '../types/Lot'
import RenderLot from '../components/LotRender';
import { Page } from '../types/Page'
import Pagination from '../components/Pagination';

const TrackedLotsPage: React.FC = () => {
  const [trackedLots, setTrackedLots] = useState<Page<Lot>>({ 
    content: [], totalElements: 0, totalPages: 0, number: 0, size: 9 
  });
  const [currentPage, setCurrentPage] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchTrackedLots = async (pageNum: number) => {
    setCurrentPage(pageNum);
    setLoading(true);
    try {
      const response = await CloseApi.get<Page<Lot>>(`/track/getmy?page=${pageNum}&size=10`);
      setTrackedLots(response.data);
      setPage(pageNum);
    } catch (error) {
      console.error('Ошибка загрузки отслеживаемых лотов:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTrackedLots(0);
  }, []);

  const handleUnpin = async (lotId: number) => {
    try {
      await CloseApi.delete(`/track/remove/${lotId}`);
      fetchTrackedLots(page); // Обновляем текущую страницу
    } catch (error) {
      console.error('Ошибка удаления:', error);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '3rem', color: '#888' }}>
        Загрузка отслеживаемых лотов...
      </div>
    );
  }

  return (
    <div>
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
        {/* Заголовок */}
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
            <h1 style={{ margin: '0 0 1rem 0', color: '#333' }}>
              📌 Отслеживаемые лоты ({trackedLots.totalElements || 0})
            </h1>
          </div>
        </div>

        {/* Лоты */}
        <div style={{ marginTop: '2rem' }}>
          <h2 style={{ margin: '0 0 1.5rem 0', color: '#333' }}>
            Отслеживаемые лоты ({trackedLots.totalElements || 0})
          </h2>

          {trackedLots.content && trackedLots.content.length > 0 ? (
            <>
              <div style={{ display: 'grid', gap: '1.5rem', marginBottom: '2rem' }}>
                <div style={{ display: 'grid', gap: '1.5rem', marginBottom: '2rem' }}>
                    {trackedLots.content.map((lot) => (
                        <RenderLot 
                        key={lot.id}
                        lot={lot} 
                        onUnpin={handleUnpin}  // Показывает кнопку "Удалить"
                        />
                    ))}
                    </div>
              </div>

              <Pagination
                currentPage={currentPage}
                totalPages={trackedLots.totalPages}
                totalElements={trackedLots.totalElements}
                pageSize={9}
                lotsPage={trackedLots}
                onPageChange={fetchTrackedLots}
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
              <p>У вас пока нет отслеживаемых лотов</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default TrackedLotsPage;
