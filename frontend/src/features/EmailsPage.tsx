import React from 'react';

interface Email {
  to: string;
  subject: string;
  text: string;
  timestamp: string;
  ownerId?: number;
  ownerName?: string;
}

interface EmailsPageProps {
  emails: Email[];
  loading: boolean;
  onRefresh: () => void;
}

const EmailsPage: React.FC<EmailsPageProps> = ({ emails, loading, onRefresh }) => {
  // Извлечение userId из текста письма
  const extractUserId = (text: string): string | null => {
    const match = text.match(/userId=(\d+)/);
    return match ? match[1] : null;
  };

  const clearEmails = async () => {
    try {
      await fetch('http://localhost:8080/api/emails/clear', { method: 'DELETE' });
      onRefresh();
    } catch (error) {
      console.error('Ошибка очистки:', error);
    }
  };

  // Проверка на массив
  if (!Array.isArray(emails)) {
    return (
      <div>
        <button onClick={onRefresh} disabled={loading}>
          {loading ? 'Загрузка...' : 'Обновить'}
        </button>
        <p>Ошибка загрузки писем. Нажмите "Обновить".</p>
      </div>
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem', alignItems: 'center' }}>
        <button 
          onClick={onRefresh} 
          disabled={loading}
          style={{
            padding: '0.5rem 1rem',
            background: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Загрузка...' : 'Обновить'}
        </button>
        <button 
          onClick={clearEmails}
          style={{
            padding: '0.5rem 1rem',
            background: '#dc3545',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Очистить
        </button>
        <span style={{ fontWeight: 'bold' }}>Писем: {emails.length}</span>
      </div>

      {emails.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
          <p>📭 Писем нет</p>
          <p>Отправьте тестовое письмо для проверки</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {emails.map((email, index) => {
            const userId = extractUserId(email.text);
            return (
              <div 
                key={index} 
                style={{
                  border: '1px solid #ddd', 
                  padding: '1.5rem', 
                  borderRadius: '8px',
                  background: '#f8f9fa',
                  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                }}
              >
                <h3 style={{ margin: '0 0 1rem 0', color: '#333' }}>
                  📧 {email.subject}
                </h3>
                
                <div style={{ marginBottom: '1rem' }}>
                  <p><strong>Кому:</strong> {email.to}</p>
                  <p><strong>Время:</strong> {new Date(email.timestamp).toLocaleString('ru-RU')}</p>
                </div>

                {/* Кнопка перейти к создателю */}
                {userId && (
                  <div style={{ marginBottom: '1rem' }}>
                    <button 
                      onClick={() => {
                        window.location.hash = `profile?userId=${userId}`;
                      }}
                      style={{ 
                        background: '#28a745', 
                        color: 'white', 
                        border: 'none', 
                        padding: '0.75rem 1.5rem', 
                        borderRadius: '6px',
                        cursor: 'pointer',
                        fontSize: '1rem',
                        fontWeight: 'bold'
                      }}
                    >
                      👤 Написать создателю лота
                    </button>
                  </div>
                )}

                {/* Текст письма */}
                <div style={{ 
                  background: 'white', 
                  border: '1px solid #eee', 
                  borderRadius: '4px',
                  padding: '1rem',
                  whiteSpace: 'pre-wrap', 
                  fontSize: '0.9em',
                  lineHeight: '1.5'
                }}>
                  {email.text}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default EmailsPage;
