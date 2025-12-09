import React, { useState, useEffect } from 'react';
import { Lot } from '../types/Lot';

export interface RenderLotProps {
  lot: Lot;
  onUnpin?: (lotId: number) => void;  // Для TrackedLotsPage
}

const RenderLot: React.FC<RenderLotProps> = ({ lot, onUnpin }) => {
  const [hoveredLotId, setHoveredLotId] = useState<number | null>(null);
  const isHovered = hoveredLotId === lot.id;

  const openLotDetails = () => {
    window.open(`/lot/${lot.id}`, '_blank', 'noopener,noreferrer');
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

      {/* Кнопка для TrackedLotsPage */}
      {onUnpin && (
        <button
          onClick={(e) => {
            e.stopPropagation();  // Отменяем открытие деталей
            onUnpin(lot.id);
          }}
          style={{
            position: 'absolute',
            top: '1rem',
            right: '1rem',  
            padding: '0.75rem',
            background: '#e74c3c',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            fontWeight: '500',
            cursor: 'pointer',
            marginTop: '1rem'
          }}
        >
          Удалить из отслеживания
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
export default RenderLot