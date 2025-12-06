import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import CloseApi from '../api/CloseApi';

interface LotFormData {
  startAuction: string;
  endAuction: string;
  currentCost: number;
  rateStep: number;
  goodsName: string;
  goodsDescription: string;
}

const CreateLotPage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<LotFormData>({
    startAuction: '',
    endAuction: '',
    currentCost: 0,
    rateStep: 0,
    goodsName: '',
    goodsDescription: '',
  });
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/signin');
    }
  }, [isAuthenticated, navigate]);

  const validateForm = (): boolean => {
    const newErrors: { [key: string]: string } = {};

    // Проверка обязательных полей
    if (!formData.goodsName.trim()) {
      newErrors.goodsName = 'Название товара обязательно';
    }
    if (!formData.goodsDescription.trim()) {
      newErrors.goodsDescription = 'Описание товара обязательно';
    }

    // Проверка дат
    const startDate = new Date(formData.startAuction);
    const endDate = new Date(formData.endAuction);
    const now = new Date();
    
    if (!formData.startAuction || startDate <= now) {
      newErrors.startAuction = 'Дата начала должна быть в будущем';
    }
    if (!formData.endAuction || endDate <= startDate) {
      newErrors.endAuction = 'Дата окончания должна быть позже начала';
    }

    // Проверка сумм
    if (formData.currentCost <= 0) {
      newErrors.currentCost = 'Начальная цена должна быть больше 0';
    }
    if (formData.rateStep <= 0) {
      newErrors.rateStep = 'Шаг ставки должен быть больше 0';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    try {
      const lotData = {
        goodsName: formData.goodsName.trim(),
        goodsDescription: formData.goodsDescription.trim(),
        currentCost:formData.currentCost.toString(),
        rateStep: formData.rateStep.toString(),
        startAuction: formData.startAuction,
        endAuction: formData.endAuction,
      };

      await CloseApi.post('/lots/create', lotData);
      setSuccess(true);
      
      
    } catch (error) {
      console.error('Ошибка создания лота:', error);
      setErrors({ submit: 'Ошибка при создании лота. Попробуйте снова.' });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    
    // Для number полей
    const parsedValue = (name === 'currentCost' || name === 'rateStep')
      ? (value === '' ? 0 : parseFloat(value) || 0)
      : value;
      
    setFormData(prev => ({ 
      ...prev, 
      [name as keyof LotFormData]: parsedValue 
    }));
    
    // Очистка ошибки
    setErrors(prev => {
      const newErrors = { ...prev };
      delete newErrors[name];
      return newErrors;
    });
};

  if (success) {
    return (
      <div style={{ 
        maxWidth: '500px', 
        margin: '0 auto', 
        padding: '3rem 2rem', 
        textAlign: 'center',
        background: '#d4edda',
        borderRadius: '12px',
        color: '#155724'
      }}>
        ✅ Лот успешно создан!
      </div>
    );
  }

  return (
    <div style={{ 
      maxWidth: '600px', 
      margin: '0 auto', 
      padding: '2rem',
    }}>
      <div style={{ 
        padding: '2rem', 
        borderRadius: '12px', 
        background: 'white', 
        boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
        marginBottom: '2rem'
      }}>
        <h1 style={{ 
          margin: '0 0 2rem 0', 
          textAlign: 'center', 
          color: '#2c3e50',
          fontSize: '2rem'
        }}>
          Создать новый лот
        </h1>

        <form onSubmit={handleSubmit}>
          {/* Название товара */}
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', color: '#333' }}>
              Название товара
            </label>
            <input
              type="text"
              name="goodsName"
              value={formData.goodsName}
              onChange={handleChange}
              style={{
                width: '100%', padding: '0.75rem',
                border: errors.goodsName ? '2px solid #dc3545' : '1px solid #ddd',
                borderRadius: '8px', fontSize: '1rem'
              }}
              placeholder="Введите название товара"
            />
            {errors.goodsName && (
              <span style={{ color: '#dc3545', fontSize: '0.875rem', marginTop: '0.25rem', display: 'block' }}>
                {errors.goodsName}
              </span>
            )}
          </div>

          {/* Описание товара */}
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', color: '#333' }}>
              Описание товара
            </label>
            <textarea
              name="goodsDescription"
              rows={4}
              value={formData.goodsDescription}
              onChange={handleChange}
              style={{
                width: '100%', padding: '0.75rem',
                border: errors.goodsDescription ? '2px solid #dc3545' : '1px solid #ddd',
                borderRadius: '8px', fontSize: '1rem', resize: 'vertical'
              }}
              placeholder="Подробное описание товара"
            />
            {errors.goodsDescription && (
              <span style={{ color: '#dc3545', fontSize: '0.875rem', marginTop: '0.25rem', display: 'block' }}>
                {errors.goodsDescription}
              </span>
            )}
          </div>
          {/* Начальная цена */}
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', color: '#333' }}>
              Начальная цена (₽)
            </label>
            <input
              type="number"
              name="currentCost"
              step="0.01"
              min="0.01"
              value={formData.currentCost}
              onChange={handleChange}
              style={{
                width: '100%',
                padding: '0.75rem',
                border: errors.currentCost ? '2px solid #dc3545' : '1px solid #ddd',
                borderRadius: '8px',
                fontSize: '1rem',
              }}
              placeholder="1000.00"
            />
            {errors.currentCost && (
              <span style={{ color: '#dc3545', fontSize: '0.875rem', marginTop: '0.25rem', display: 'block' }}>
                {errors.currentCost}
              </span>
            )}
          </div>

          {/* Шаг ставки */}
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', color: '#333' }}>
              Шаг ставки (₽)
            </label>
            <input
              type="number"
              name="rateStep"
              step="0.01"
              min="0.01"
              value={formData.rateStep}
              onChange={handleChange}
              style={{
                width: '100%',
                padding: '0.75rem',
                border: errors.rateStep ? '2px solid #dc3545' : '1px solid #ddd',
                borderRadius: '8px',
                fontSize: '1rem',
              }}
              placeholder="100.00"
            />
            {errors.rateStep && (
              <span style={{ color: '#dc3545', fontSize: '0.875rem', marginTop: '0.25rem', display: 'block' }}>
                {errors.rateStep}
              </span>
            )}
          </div>

          {/* Дата начала */}
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', color: '#333' }}>
              Дата начала аукциона
            </label>
            <input
              type="datetime-local"
              name="startAuction"
              value={formData.startAuction}
              onChange={handleChange}
              style={{
                width: '100%',
                padding: '0.75rem',
                border: errors.startAuction ? '2px solid #dc3545' : '1px solid #ddd',
                borderRadius: '8px',
                fontSize: '1rem',
              }}
            />
            {errors.startAuction && (
              <span style={{ color: '#dc3545', fontSize: '0.875rem', marginTop: '0.25rem', display: 'block' }}>
                {errors.startAuction}
              </span>
            )}
          </div>

          {/* Дата окончания */}
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', color: '#333' }}>
              Дата окончания аукциона
            </label>
            <input
              type="datetime-local"
              name="endAuction"
              value={formData.endAuction}
              onChange={handleChange}
              style={{
                width: '100%',
                padding: '0.75rem',
                border: errors.endAuction ? '2px solid #dc3545' : '1px solid #ddd',
                borderRadius: '8px',
                fontSize: '1rem',
              }}
            />
            {errors.endAuction && (
              <span style={{ color: '#dc3545', fontSize: '0.875rem', marginTop: '0.25rem', display: 'block' }}>
                {errors.endAuction}
              </span>
            )}
          </div>
          {errors.submit && (
            <div style={{ 
              padding: '1rem', 
              background: '#f8d7da', 
              borderRadius: '8px', 
              marginBottom: '1rem',
              color: '#721c24'
            }}>
              {errors.submit}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            style={{
              width: '100%',
              padding: '1rem',
              background: loading ? '#6c757d' : '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '12px',
              fontSize: '1.1rem',
              fontWeight: '600',
              cursor: loading ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s',
            }}
          >
            {loading ? 'Создание...' : 'Создать лот'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateLotPage;
