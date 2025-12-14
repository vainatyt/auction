import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import CloseApi from '../api/CloseApi';
import { Lot } from '../types/Lot';

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

  const imgRef = useRef<HTMLImageElement>(null);

  useEffect(() => {
    if (lotId) {
      CloseApi.get(`/lots/${lotId}`)
        .then((response) => {
          const data = response.data;
          setLot(data);
          
          const min = data.currentCost + data.rateStep;
          setMinBid(min);
          setBidAmount(min.toString());
          
          setAuctionEnded(new Date(data.endAuction) < new Date());
        })
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [lotId]);

  useEffect(() => {
    if (!lot?.uuid || !imgRef.current) return;
    
    setImageLoading(true);
    const img = imgRef.current;
    
    CloseApi.get(`/users_lots_photo/${lot.uuid}`, {
      responseType: 'blob'
    })
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
    if (!lot || !bidAmount) return;
    
    const amount = parseFloat(bidAmount);
    
    if (isNaN(amount) || amount < minBid) {
      alert(`Минимальная ставка: ₽${minBid}`);
      return;
    }
    
    if (auctionEnded) {
      alert('Аукцион завершён!');
      return;
    }

    setBuyLoading(true);
    try {
      const response = await CloseApi.post(`/lots/buy`, { 
        lotId,
        reqCost: amount 
      });
      
      setLot(response.data);
      
      const newMin = response.data.currentCost + response.data.rateStep;
      setMinBid(newMin);
      setBidAmount(newMin.toString());
      
      alert(`✅ Ставка ${amount}₽ принята! Новая цена: ₽${response.data.currentCost}`);
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
                ₽{lot.currentCost.toLocaleString()}
              </div>
            </div>
            <div className="stat">
              <span className="text-gray-500">Конец аукциона</span>
              <div className={`text-xl font-semibold ${
                auctionEnded ? 'text-red-500' : 'text-blue-600'
              }`}>
                {new Date(lot.endAuction).toLocaleString()}
              </div>
            </div>
          </div>

          <div className="description p-4 bg-gray-50 rounded-lg">
            <h3 className="font-semibold mb-2">Описание</h3>
            <p>{lot.description}</p>
          </div>

          {/* Форма ставки */}
          <div className="bid-form bg-gradient-to-r from-blue-50 to-indigo-50 p-6 rounded-xl border">
            <h3 className="text-lg font-semibold mb-4 flex items-center">
              <span className="w-2 h-2 bg-green-400 rounded-full mr-2"></span>
              Сделать ставку
            </h3>
            
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Сумма ставки (₽)
                </label>
                <input
                  type="number"
                  value={bidAmount}
                  onChange={(e) => setBidAmount(e.target.value)}
                  min={minBid}
                  step="0.01"
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder={minBid.toString()}
                  disabled={auctionEnded || buyLoading}
                />
                {bidAmount && parseFloat(bidAmount) < minBid && (
                  <p className="text-red-500 text-xs mt-1">
                    Минимум: ₽{minBid.toLocaleString()}
                  </p>
                )}
              </div>
              
              <div className="text-xs text-gray-500">
                Текущая цена: ₽{lot.currentCost.toLocaleString()} + шаг ₽{lot.rateStep.toLocaleString()}
              </div>
            </div>
          </div>

          {/* Кнопки */}
          <div className="actions flex flex-col sm:flex-row gap-4 pt-4">
            <button 
              onClick={handlePin}
              className={`px-6 py-3 rounded-lg font-medium transition-all ${
                pinned 
                  ? 'bg-yellow-500 text-white shadow-lg' 
                  : 'bg-gray-200 hover:bg-gray-300'
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
                `💰 Сделать ставку ₽${parseFloat(bidAmount || '0').toLocaleString()}`
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LotDetailPage;
