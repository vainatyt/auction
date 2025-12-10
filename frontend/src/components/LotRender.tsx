import React, { useState, useEffect, useCallback } from 'react';
import { Lot } from '../types/Lot';
import CloseApi from '../api/CloseApi';

export interface RenderLotProps {
  lot: Lot;
}

const RenderLot: React.FC<RenderLotProps> = ({ lot }) => {
  const [hoveredLotId, setHoveredLotId] = useState<number | null>(null);
  const isHovered = hoveredLotId === lot.id;

  // Состояния для отслеживания
  const [isTracked, setIsTracked] = useState<boolean | null>(null);
  const [loadingTracked, setLoadingTracked] = useState<boolean>(false);
  const [actionLoading, setActionLoading] = useState<boolean>(false);

  const openLotDetails = () => {
    window.open(`/lot/${lot.id}`, '_blank', 'noopener,noreferrer');
  };

  // Стандартная функция проверки статуса отслеживания
  const fetchIsTracked = useCallback(async (id: number): Promise<boolean> => {
  try {
    setLoadingTracked(true);
    console.log('Запрос к:', `/track/tracked/${id}`); // увидишь точный URL
    const res = await CloseApi(`/track/tracked/${id}`);
    if (!res || typeof res.status !== 'number' || res.status < 200 || res.status >= 300) {
      console.error('fetchIsTracked: некорректный статус', res?.status);
      throw new Error(`HTTP ${res?.status}`);
    }

    const payload = res.data;
    const tracked = typeof payload === 'boolean' ? payload : Boolean(payload?.data ?? payload?.body ?? payload);

    setIsTracked(tracked);
    console.log(`Статус отслеживания для ${id}:`, tracked);
    return tracked;
  } catch (e) {
    console.error(`Ошибка проверки статуса для ${id}:`, e);
    setIsTracked(false);
    return false;
  } finally {
    setLoadingTracked(false);
  }
},[]);

  // Стандартная функция добавления в отслеживание
const pinLot = useCallback(async (id: number): Promise<boolean> => {
  try {
    setActionLoading(true);

    const res = await CloseApi.post(`/track/add/${id}`)

    // Проверяем HTTP-статус
    if (!res || typeof res.status !== 'number' || res.status < 200 || res.status >= 300) {
      console.error('pinLot: некорректный статус', res?.status);
      throw new Error(`HTTP ${res?.status}`);
    }
    const payload = res.data;

    setIsTracked(true);
    return true;
  } catch (e) {
    console.error('Ошибка добавления в отслеживание:', e);
    return false;
  } finally {
    setActionLoading(false);
  }
}, []);

  // Стандартная функция удаления из отслеживания 
const unpinLot = useCallback(async (id: number): Promise<boolean> => {
  try {
    setActionLoading(true);

    await CloseApi.delete(`/track/remove/${id}`);
    setIsTracked(false); // если success true => setIsTracked(false)
    return false;
  } catch (e) {
    // console.error('Ошибка удаления из отслеживания:', e);
    return false;
  } finally {
    setActionLoading(false);
  }
}, []);

  // Инициализация статуса при монтировании/изменении лота
  useEffect(() => {
    if (lot?.id != null) {
      fetchIsTracked(lot.id);
    }
  }, [lot?.id, fetchIsTracked]);

  // Обработчик клика по кнопке отслеживания
  const handleTrackClick = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (actionLoading || isTracked === null) return;

    if (isTracked) {
      await unpinLot(lot.id);
    } else {
      await pinLot(lot.id);
    }
  };

  return (
    <div>
      <div 
        onClick={openLotDetails}
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
            color: '#155724', 
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

        {/* ✅ Кнопка отслеживания с использованием стандартных функций */}
        {lot.id != null && (
          <button
            onClick={handleTrackClick}
            disabled={loadingTracked || actionLoading}
            style={{
              position: 'absolute',
              top: '1rem',
              right: '1rem',  
              padding: '0.75rem',
              background: isTracked ? '#e74c3c' : '#27ae60',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontWeight: '500',
              cursor: (loadingTracked || actionLoading) ? 'wait' : 'pointer',
              boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
              transition: 'all 0.2s ease',
              minWidth: '200px'
            }}
            title={
              loadingTracked 
                ? 'Проверка статуса...' 
                : isTracked 
                  ? 'Удалить из отслеживания' 
                  : 'Добавить в отслеживание'
            }
          >
            {loadingTracked ? '⏳' : actionLoading ? '...' : 
              (isTracked ? '❌ Удалить из отслеживания' : '✅ Добавить в отслеживание')
            }
          </button>
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
    </div>
  );
};

export default RenderLot;
