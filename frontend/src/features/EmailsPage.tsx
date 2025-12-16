import React from 'react';

interface Email {
  to: string;
  subject: string;
  text: string;
  timestamp: string;
}

interface EmailsPageProps {
  emails: Email[];
  loading: boolean;
  onRefresh: () => void;
}

const EmailsPage: React.FC<EmailsPageProps> = ({ emails, loading, onRefresh }) => {
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
        <p>Пока писем нет. Нажмите "Обновить".</p>
      </div>
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
        <button onClick={onRefresh} disabled={loading}>
          {loading ? 'Загрузка...' : 'Обновить'}
        </button>
        <button onClick={clearEmails}>Очистить</button>
        <span>Писем: {emails.length}</span>
      </div>

      {emails.length === 0 ? (
        <p>Писем нет</p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {emails.map((email, index) => (
            <div key={index} style={{
              border: '1px solid #ccc', 
              padding: '1rem', 
              borderRadius: '4px',
              background: '#f9f9f9'
            }}>
              <h3>{email.subject}</h3>
              <p><strong>Кому:</strong> {email.to}</p>
              <p><strong>Время:</strong> {new Date(email.timestamp).toLocaleString('ru-RU')}</p>
              <pre style={{ whiteSpace: 'pre-wrap', margin: '0.5rem 0', fontSize: '0.9em' }}>
                {email.text}
              </pre>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default EmailsPage;  // ← Обязательно default export!
