import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CloseApi from '../api/CloseApi';

const CommentWritePage: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const [sellerName, setSellerName] = useState('Продавец');
  const [rating, setRating] = useState(5);
  const [review, setReview] = useState('');
  const [loading, setLoading] = useState(false);
  const [submitStatus, setSubmitStatus] = useState<'idle' | 'success' | 'error'>('idle');

  // Загрузка данных продавца
  useEffect(() => {
    if (userId) {
      CloseApi.get(`/users/${userId}`)
        .then(res => {
          setSellerName(res.data.name || res.data.email || 'Продавец');
        })
        .catch(() => {
          setSellerName('Продавец #' + userId);
        });
    }
  }, [userId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!userId || review.length < 10) {
      alert('Отзыв должен содержать минимум 10 символов');
      return;
    }

    setLoading(true);
    try {
      await CloseApi.post('/comments/write', {
        addresseeId: parseInt(userId || '0'),
        rating,
        review
      });
      setSubmitStatus('success');
      setTimeout(() => navigate(-1), 2000);  // назад через 2 сек
    } catch (error: any) {
      setSubmitStatus('error');
      alert(error.response?.data?.message || 'Ошибка отправки отзыва');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4">
      <div className="max-w-2xl mx-auto bg-white rounded-2xl shadow-2xl p-8 max-h-screen overflow-y-auto">
        {/* Заголовок */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent mb-4">
            Написать отзыв
          </h1>
          <div className="flex items-center justify-center space-x-3 text-gray-600 mb-2">
            <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white font-bold text-lg shadow-lg">
              {sellerName[0]?.toUpperCase()}
            </div>
            <span className="text-xl font-semibold">О {sellerName}</span>
          </div>
        </div>

        {/* Форма */}
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Рейтинг звездами */}
          <div className="flex flex-col items-center gap-2">
            <label className="text-sm font-semibold text-gray-700">Оценка</label>
            <div className="flex items-center space-x-1 p-2 bg-gray-50 rounded-lg">
                {[5,4,3,2,1].map((star) => (
                <button
                    key={star}
                    type="button"
                    className={`p-0 border-0 bg-transparent cursor-pointer transition-all hover:scale-125 ${
                    star <= rating ? 'text-yellow-400' : 'text-gray-300'
                    }`}
                    onClick={() => setRating(star)}
                >
                    <svg
                    className="w-5 h-5"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                    >
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
                    </svg>
                </button>
                ))}
            </div>
            <p className="text-center text-sm text-gray-500 mt-2">Вы выбрали: {rating} звёзд</p>
          </div>

          {/* Текст отзыва */}
          <div>
            <label className="block text-lg font-semibold text-gray-700 mb-3">
              Ваш отзыв (минимум 10 символов)
            </label>
            <textarea
              value={review}
              onChange={(e) => setReview(e.target.value)}
              rows={8}
              className="w-full p-5 border-2 border-gray-200 rounded-2xl focus:ring-4 focus:ring-blue-500 focus:border-transparent resize-vertical text-lg placeholder-gray-400 transition-all"
              placeholder="Расскажите о вашем опыте работы с этим продавцом. Быстрая доставка? Хорошая коммуникация? Всё ли соответствует описанию?"
              maxLength={1000}
            />
            <div className="flex justify-between text-sm text-gray-500 mt-2">
              <span>{review.length}/1000 символов</span>
              <span className={review.length < 10 ? 'text-red-500' : 'text-green-500'}>
                {review.length < 10 ? 'Минимум 10 символов' : 'Готово'}
              </span>
            </div>
          </div>

          {/* Кнопка отправки */}
          <button
            type="submit"
            disabled={loading || review.length < 10 || !userId}
            className="w-full bg-gradient-to-r from-emerald-500 to-emerald-600 text-white py-5 px-8 rounded-2xl font-bold text-xl shadow-2xl hover:from-emerald-600 hover:to-emerald-700 hover:shadow-3xl transition-all disabled:opacity-50 disabled:cursor-not-allowed transform hover:-translate-y-1 flex items-center justify-center space-x-3"
          >
            {loading ? (
              <>
                <svg className="animate-spin -ml-1 mr-3 h-6 w-6 text-white" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                </svg>
                Отправляем отзыв...
              </>
            ) : (
              '🚀 Опубликовать отзыв'
            )}
          </button>
        </form>

        {/* Статусы */}
        {submitStatus === 'success' && (
          <div className="mt-8 p-6 bg-gradient-to-r from-green-400 to-green-500 text-white rounded-2xl text-center font-bold text-lg shadow-2xl animate-bounce">
            ✅ Спасибо за отзыв! Он поможет другим покупателям!
          </div>
        )}
        {submitStatus === 'error' && (
          <div className="mt-6 p-4 bg-red-100 border-2 border-red-400 text-red-800 rounded-xl text-center font-semibold">
            ❌ Ошибка отправки. Проверьте подключение и попробуйте еще раз.
          </div>
        )}

        {/* Кнопка назад */}
        <button
          onClick={() => navigate(-1)}
          className="w-full mt-6 bg-gray-100 hover:bg-gray-200 text-gray-800 py-3 px-6 rounded-xl font-semibold transition-all border border-gray-300"
        >
          ← Вернуться назад
        </button>
      </div>
    </div>
  );
};

export default CommentWritePage;
