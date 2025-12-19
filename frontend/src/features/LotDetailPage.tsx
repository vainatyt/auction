import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import CloseApi from '../api/CloseApi';
import { Lot } from '../types/Lot';
import Pagination from '../components/Pagination';

interface Comment {
  id: {
    commentatorId: number;
    addresseeId: number;
  };
  rating: number;
  review: string;
  date: string;
}

interface CommentsPage {
  content: Comment[];
  number: number;
  totalPages: number;
  totalElements: number;
  size: number;
}

const LotDetailPage: React.FC = () => {
  const { lotId } = useParams<{ lotId: string }>();
  const [lot, setLot] = useState<Lot | null>(null);
  const [loading, setLoading] = useState(true);
  const [imageLoading, setImageLoading] = useState(false);
  const [pinned, setPinned] = useState(false);
  const [bidAmount, setBidAmount] = useState('');
  const [minBid, setMinBid] = useState(0);
  const [buyLoading, setBuyLoading] = useState(false);
  const [auctionEnded, setAuctionEnded] = useState(false);
  
  const [commentsPage, setCommentsPage] = useState<CommentsPage | null>(null);
  const [commentsCurrentPage, setCommentsCurrentPage] = useState(0);

  const imgRef = useRef<HTMLImageElement>(null);

  useEffect(() => {
    if (lotId) {
      CloseApi.get(`/lots/${lotId}`)
        .then((response) => {
          const data = response.data;
          setLot(data);
          const min = (data.currentCost || 0) + (data.rateStep || 0);
          setMinBid(min);
          setBidAmount(min.toString());
          setAuctionEnded(new Date(data.endAuction || 0) < new Date());
        })
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [lotId]);

  useEffect(() => {
    if (!lot?.uuid || !imgRef.current) return;
    setImageLoading(true);
    const img = imgRef.current;
    CloseApi.get(`/users_lots_photo/${lot.uuid}`, { responseType: 'blob' })
      .then(res => {
        const url = URL.createObjectURL(res.data);
        img.src = url;
        img.style.display = 'block';
      })
      .catch(err => {
        console.error('Фото ошибка:', err);
        img.style.display = 'none';
      })
      .finally(() => setImageLoading(false));
  }, [lot?.uuid]);

  useEffect(() => {
    if (lot?.ownerId) {
      CloseApi.get(`/comments/getmy?page=${commentsCurrentPage}&size=5&userId=${lot.ownerId}`)
        .then((response) => {
          setCommentsPage(response.data);
        })
        .catch(console.error);
    }
  }, [lot?.ownerId, commentsCurrentPage]);

  const handleCommentsPageChange = (page: number) => {
    setCommentsCurrentPage(page);
  };

  const handlePin = async () => {
    try {
      if (!pinned) {
        await CloseApi.post(`/track/add/${lotId}`);
      } else {
        await CloseApi.delete(`/track/remove/${lotId}`);
      }
      setPinned(!pinned);
    } catch (error) {
      console.error('Ошибка закрепления:', error);
    }
  };

  const handleBuy = async () => {
    if (!lotId || !bidAmount) return;
    const amount = parseFloat(bidAmount);
    if (isNaN(amount) || amount < minBid) {
      alert(`Минимальная ставка: ₽${minBid.toLocaleString('ru-RU')}`);
      return;
    }
    if (auctionEnded) {
      alert('Аукцион завершён!');
      return;
    }
    setBuyLoading(true);
    try {
      await CloseApi.post(`/lots/buy`, { lotId, reqCost: amount });
      alert(`✅ Ставка ${amount.toLocaleString('ru-RU')}₽ принята!`);
      window.location.reload();
    } catch (error: any) {
      if (error.response?.data?.error === 'AUCTION_EXPIRED') {
        alert('❌ Аукцион завершён!');
        setAuctionEnded(true);
      } else if (error.response?.data?.error === 'INVALID_BID_AMOUNT') {
        alert(`❌ Неверная сумма: ${error.response.data.message}`);
      } else {
        alert('❌ Ошибка при размещении ставки');
      }
    } finally {
      setBuyLoading(false);
    }
  };

  if (loading) return <div className="flex items-center justify-center h-screen">Загрузка лота...</div>;
  if (!lot) return <div className="flex items-center justify-center h-screen">Лот не найден</div>;

  return (
    <div className="lot-detail max-w-4xl mx-auto p-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Изображение */}
        <div className="lot-image relative">
          {imageLoading && (
            <div className="absolute inset-0 bg-gray-100 flex items-center justify-center rounded-lg z-10 backdrop-blur-sm">
              <div className="text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-2"></div>
                <p className="text-sm text-gray-500">⏳ Загружается...</p>
              </div>
            </div>
          )}
          <img 
            ref={imgRef}
            alt={lot.name} 
            className="w-full h-32 md:h-80 object-cover rounded-lg shadow-xl"
            style={{ width: '200px', height: '200px' }}
            onLoad={() => setImageLoading(false)}
            onError={() => setImageLoading(false)}
          />
          {(!lot.uuid && !imageLoading) && (
            <div className="absolute inset-0 bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center rounded-lg">
              <div className="text-center text-gray-500 p-8">
                <div className="text-4xl mb-2">📷</div>
                <p>Фото отсутствует</p>
              </div>
            </div>
          )}
        </div>

        {/* Информация */}
        <div className="lot-info space-y-6">
          <h1 className="text-3xl font-bold text-gray-900">{lot.name}</h1>
          
          <div className="stats grid grid-cols-2 gap-4 text-sm">
            <div className="stat">
              <span className="text-gray-500">Текущая ставка</span>
              <div className="text-2xl font-bold text-green-600">
                ₽{(lot.currentCost || 0).toLocaleString('ru-RU')}
              </div>
            </div>
            <div className="stat">
              <span className="text-gray-500">Конец аукциона</span>
              <div className={`text-xl font-semibold ${auctionEnded ? 'text-red-500' : 'text-blue-600'}`}>
                {lot.endAuction ? new Date(lot.endAuction).toLocaleString('ru-RU') : 'Не указана'}
              </div>
            </div>
          </div>

          <div className="description p-4 bg-gray-50 rounded-lg">
            <h3 className="font-semibold mb-2">Описание</h3>
            <p>{lot.description || 'Описание отсутствует'}</p>
          </div>

          {/* Форма ставки */}
          <div className="bid-form bg-gradient-to-r from-blue-50 to-indigo-50 p-6 rounded-xl border">
            <h3 className="text-lg font-semibold mb-4 flex items-center">
              <span className="w-2 h-2 bg-green-400 rounded-full mr-2"></span>
              Сделать ставку
            </h3>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Сумма ставки (₽)</label>
                <input
                  type="number"
                  value={bidAmount}
                  onChange={(e) => setBidAmount(e.target.value)}
                  min={minBid}
                  step="0.01"
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder={minBid.toLocaleString('ru-RU')}
                  disabled={auctionEnded || buyLoading}
                />
                {bidAmount && parseFloat(bidAmount) < minBid && (
                  <p className="text-red-500 text-xs mt-1">Минимум: ₽{minBid.toLocaleString('ru-RU')}</p>
                )}
              </div>
              <div className="text-xs text-gray-500">
                Текущая цена: ₽{(lot.currentCost || 0).toLocaleString('ru-RU')} + шаг ₽{(lot.rateStep || 0).toLocaleString('ru-RU')}
              </div>
            </div>
          </div>

          {/* Кнопки */}
          <div className="actions flex flex-col sm:flex-row gap-4 pt-4">
            <button 
              onClick={handlePin}
              className={`px-6 py-3 rounded-lg font-medium transition-all ${
                pinned ? 'bg-yellow-500 text-white shadow-lg' : 'bg-gray-200 hover:bg-gray-300'
              }`}
              disabled={buyLoading}
            >
              {pinned ? '✅ Закреплено' : '📌 Закрепить'}
            </button>
            <button 
              onClick={handleBuy}
              disabled={auctionEnded || buyLoading || parseFloat(bidAmount || '0') < minBid}
              className="flex-1 bg-gradient-to-r from-blue-600 to-blue-700 text-white py-3 px-6 rounded-lg font-semibold hover:from-blue-700 hover:to-blue-800 shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
            >
              {buyLoading ? (
                <>
                  <svg className="animate-spin -ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                  </svg>
                  Размещаем...
                </>
              ) : auctionEnded ? (
                '❌ Аукцион завершён'
              ) : (
                `💰 Сделать ставку ₽${parseFloat(bidAmount || '0').toLocaleString('ru-RU')}`
              )}
            </button>
          </div>
        </div>
      </div>

      {/* ✅ ОТЗЫВЫ С ПАГИНАЦИЕЙ */}
      <div className="mt-12">
        <h3 className="text-2xl font-bold mb-8 text-center text-gray-800">
          Отзывы об владельце
        </h3>
        
        {commentsPage ? (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8 max-h-96 overflow-y-auto p-4">
              {commentsPage.content.map((comment, index) => (
                <div key={`${comment.id.commentatorId}-${index}`} className="comment bg-white p-6 rounded-xl shadow-lg border border-gray-100 hover:shadow-xl transition-all">
                  <div className="flex items-start space-x-4 mb-3">
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between mb-2">
                        <span className="font-semibold text-gray-900 truncate">
                          Пользователь #{comment.id.commentatorId}
                        </span>
                        <span className="text-xs text-gray-400">
                          {comment.date ? new Date(comment.date).toLocaleDateString('ru-RU') : 'Недавно'}
                        </span>
                      </div>
                      <div className="flex flex-col items-start gap-2"> 
                      <div className="flex flex-col items-start gap-2">
                        <label className="text-sm font-semibold text-gray-700">Оценка</label>
                        <div className="flex items-center gap-2 p-2 bg-gray-50 rounded-lg">
                          <span className="text-lg font-bold text-yellow-500">{comment.rating}/5</span>
                        </div>
                      </div>
                    </div>
                    </div>
                  </div>
                  <p className="text-gray-700 leading-relaxed">{comment.review || '—'}</p>
                </div>
              ))}
            </div>

            {/* ✅ ТВОЯ ПАГИНАЦИЯ */}
            {commentsPage.totalPages > 1 && (
              <Pagination
                currentPage={commentsCurrentPage}
                totalPages={commentsPage.totalPages}
                totalElements={commentsPage.totalElements}
                pageSize={commentsPage.size}
                onPageChange={handleCommentsPageChange}
                lotsPage={commentsPage}
              />
            )}
          </>
        ) : (
          <div className="text-center py-12 text-gray-500">
            <div className="text-4xl mb-4">💭</div>
            <p>Отзывов пока нет</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default LotDetailPage;
