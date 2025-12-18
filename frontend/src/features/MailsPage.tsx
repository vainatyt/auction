import React, { useEffect, useState } from 'react';
import CloseApi from '../api/CloseApi'; // путь подправь под свой проект

interface Mail {
  id: number;
  message: string;
  title: string;
  userId: string;
}

interface MailPage {
  content: Mail[];
  totalPages: number;
  totalElements: number;
  number: number; // текущая страница (0-based)
  size: number;
}

const MailsPage: React.FC = () => {
  const [page, setPage] = useState<MailPage | null>(null);
  const [loading, setLoading] = useState(false);
  const [pageNumber, setPageNumber] = useState(0); // 0-based
  const [pageSize] = useState(10);

  const loadMails = async (pageNum = 0) => {
    try {
      setLoading(true);
      const response = await CloseApi.get(`/mails`, {
        params: { page: pageNum, size: pageSize },
      });
      setPage(response.data);
    } catch (e) {
      console.error('Ошибка загрузки писем:', e);
      setPage(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMails(pageNumber);
  }, [pageNumber]);

  const nextPage = () => {
    if (page && page.number < page.totalPages - 1) {
      setPageNumber(page.number + 1);
    }
  };

  const prevPage = () => {
    if (page && page.number > 0) {
      setPageNumber(page.number - 1);
    }
  };

  if (!page || !Array.isArray(page.content)) {
    return (
      <div>
        <button
          onClick={() => loadMails(pageNumber)}
          disabled={loading}
        >
          {loading ? 'Загрузка...' : 'Обновить'}
        </button>
        <p>Ошибка загрузки писем. Нажмите "Обновить".</p>
      </div>
    );
  }

  const mails = page.content;

  return (
    <div>
      <div
        style={{
          display: 'flex',
          gap: '1rem',
          marginBottom: '1rem',
          alignItems: 'center',
        }}
      >
        <button
          onClick={() => loadMails(pageNumber)}
          disabled={loading}
          style={{
            padding: '0.5rem 1rem',
            background: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
          }}
        >
          {loading ? 'Загрузка...' : 'Обновить'}
        </button>

        <span style={{ fontWeight: 'bold' }}>
          Писем на странице: {mails.length} | всего: {page.totalElements}
        </span>

        <div style={{ marginLeft: 'auto', display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={prevPage}
            disabled={loading || page.number === 0}
          >
            Назад
          </button>
          <span>
            Стр. {page.number + 1} / {page.totalPages}
          </span>
          <button
            onClick={nextPage}
            disabled={
              loading || page.number >= page.totalPages - 1
            }
          >
            Вперед
          </button>
        </div>
      </div>

      {mails.length === 0 ? (
        <div
          style={{
            textAlign: 'center',
            padding: '2rem',
            color: '#666',
          }}
        >
          <p>📭 Писем нет</p>
          <p>Отправьте тестовое письмо для проверки</p>
        </div>
      ) : (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '1rem',
          }}
        >
          {mails.map((mail) => (
            <div
              key={mail.id}
              style={{
                border: '1px solid #ddd',
                padding: '1.5rem',
                borderRadius: '8px',
                background: '#f8f9fa',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
              }}
            >
              <h3
                style={{
                  margin: '0 0 1rem 0',
                  color: '#333',
                }}
              >
                📧 {mail.title}
              </h3>

              <div
                style={{
                  marginBottom: '1rem',
                  fontSize: '0.9rem',
                  color: '#555',
                }}
              >
                <p>
                  <strong>Пользователь ID:</strong> {mail.userId}
                </p>
              </div>

              <div
                style={{
                  background: 'white',
                  border: '1px solid #eee',
                  borderRadius: '4px',
                  padding: '1rem',
                  whiteSpace: 'pre-wrap',
                  fontSize: '0.9em',
                  lineHeight: '1.5',
                }}
              >
                {mail.message}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MailsPage;
