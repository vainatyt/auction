import React, { useEffect, useState } from 'react';
import CloseApi from '../api/CloseApi';

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
  number: number;
  size: number;
}

const MailsPage: React.FC = () => {
  const [page, setPage] = useState<MailPage | null>(null);
  const [loading, setLoading] = useState(false);
  const [deleting, setDeleting] = useState<Set<number>>(new Set());
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);
  const [pageNumber, setPageNumber] = useState(0);
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

  const deleteMail = async (mailId: number) => {
    setDeleteConfirm(mailId);
  };

  const confirmDelete = async () => {
    if (!deleteConfirm) return;
    
    try {
      setDeleting(prev => new Set(prev).add(deleteConfirm));
      await CloseApi.delete(`/mail/delete/${deleteConfirm}`);
      await loadMails(pageNumber);
    } catch (e) {
      console.error('Ошибка удаления письма:', e);
      alert('Ошибка при удалении письма');
    } finally {
      setDeleting(prev => {
        const newSet = new Set(prev);
        newSet.delete(deleteConfirm);
        return newSet;
      });
      setDeleteConfirm(null);
    }
  };

  const cancelDelete = () => {
    setDeleteConfirm(null);
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
        <p>Ошибка загрузки писем. Нажмите "Обновить".</p>
      </div>
    );
  }

  const mails = page.content;

  return (
    <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
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
            style={{
              padding: '0.5rem 1rem',
              background: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
            }}
          >
            Назад
          </button>
          <span>
            Стр. {page.number + 1} / {page.totalPages}
          </span>
          <button
            onClick={nextPage}
            disabled={loading || page.number >= page.totalPages - 1}
            style={{
              padding: '0.5rem 1rem',
              background: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
            }}
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

              <button
                onClick={() => deleteMail(mail.id)}
                style={{
                  marginTop: '1rem',
                  padding: '0.5rem 1rem',
                  background: '#dc3545',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  float: 'right',
                }}
              >
                Удалить
              </button>
            </div>
          ))}
        </div>
      )}

      {/* ✅ Модальное окно подтверждения удаления */}
      {deleteConfirm && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
          }}
        >
          <div
            style={{
              background: 'white',
              padding: '2rem',
              borderRadius: '8px',
              maxWidth: '400px',
              boxShadow: '0 4px 20px rgba(0,0,0,0.3)',
            }}
          >
            <h3 style={{ margin: '0 0 1rem 0', color: '#333' }}>Удалить письмо?</h3>
            <p>Вы уверены, что хотите удалить это письмо?</p>
            <div
              style={{
                display: 'flex',
                gap: '1rem',
                justifyContent: 'flex-end',
                marginTop: '1.5rem',
              }}
            >
              <button
                onClick={cancelDelete}
                style={{
                  padding: '0.5rem 1rem',
                  background: '#6c757d',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                Отмена
              </button>
              <button
                onClick={confirmDelete}
                disabled={deleting.has(deleteConfirm)}
                style={{
                  padding: '0.5rem 1rem',
                  background: '#dc3545',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: deleting.has(deleteConfirm) ? 'not-allowed' : 'pointer',
                  opacity: deleting.has(deleteConfirm) ? 0.7 : 1,
                }}
              >
                {deleting.has(deleteConfirm) ? 'Удаляется...' : 'Удалить'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MailsPage;
