import React from 'react';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  lotsPage: any;  // Page<Lot>
}

const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  lotsPage
}) => {
  if (totalPages <= 1) return null;  // Не показывать если 1 страница

  const visiblePages = Array.from({ length: lotsPage.totalPages }, (_, i) => i + 1)
    .slice(Math.max(0, currentPage - 1), Math.min(lotsPage.totalPages, currentPage + 3));

  return (
    <>
      {/* КНОПКИ ПАГИНАЦИИ */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        gap: '0.5rem', 
        flexWrap: 'wrap',
        padding: '2rem 0'
      }}>
        {/* Предыдущая */}
        <button
          disabled={currentPage === 0}
          onClick={() => onPageChange(currentPage - 1)}
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

        {/* НУМЕРАЦИЯ СТРАНИЦ */}
        {visiblePages.map((page) => (
          <button
            key={page}
            onClick={() => onPageChange(page - 1)}
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

        {/* Следующая */}
        <button
          disabled={lotsPage.number === lotsPage.totalPages - 1}
          onClick={() => onPageChange(currentPage + 1)}
          style={{ 
            padding: '0.75rem 1.5rem', 
            border: '1px solid #dee2e6', 
            background: lotsPage.number === lotsPage.totalPages - 1 ? '#f8f9fa' : 'white',
            borderRadius: '6px',
            cursor: lotsPage.number === lotsPage.totalPages - 1 ? 'not-allowed' : 'pointer',
            color: lotsPage.number === lotsPage.totalPages - 1 ? '#6c757d' : '333',
            transition: 'all 0.2s ease'
          }}
        >
          Следующая →
        </button>
      </div>

      {/* ИНФО */}
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
    </>
  );
};

export default Pagination;
