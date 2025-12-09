import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CloseApi from '../api/CloseApi'; // твой API клиент

interface Lot {
  id: number;
  name: string;
  description: string;
  currentCost: number;
  rateStep: number;
  startAuction: string;
  endAuction: string;
  buyerId?: number;
  imageUrl?: string;
}

const LotDetailPage: React.FC = () => {
  const { lotId } = useParams<{ lotId: string }>();
  const [lot, setLot] = useState<Lot | null>(null);
  const [loading, setLoading] = useState(true);
  const [pinned, setPinned] = useState(false);

  useEffect(() => {
    if (lotId) {
      CloseApi.get(`/lots/${lotId}`)
        .then((response) => setLot(response.data))
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [lotId]);

  const handlePin = () => {
    if(!pinned){
        CloseApi.post(`/track/add/${lotId}`).catch(console.error)
    }
    else{
        CloseApi.delete(`/track/remove/${lotId}`).catch(console.error)
    }
    setPinned(!pinned);
  };

  const handleBuy = () => {
    // TODO: POST /lots/{id}/buy 
    CloseApi.post(`/lots/${lotId}/buy`)
      .then(() => alert('Покупка оформлена!'))
      .catch(console.error);
  };

  if (loading) return <div>Загрузка лота...</div>;
  if (!lot) return <div>Лот не найден</div>;

  return (
    <div className="lot-detail max-w-4xl mx-auto p-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Изображение */}
        <div className="lot-image">
          <img src={lot.imageUrl || '/placeholder.jpg'} alt={lot.name} 
               className="w-full h-96 object-cover rounded-lg shadow-xl" />
        </div>

        {/* Информация */}
        <div className="lot-info space-y-6">
          <h1 className="text-3xl font-bold text-gray-900">{lot.name}</h1>
          
          <div className="stats grid grid-cols-2 gap-4 text-sm">
            <div className="stat">
              <span className="text-gray-500">Текущая ставка</span>
              <div className="text-2xl font-bold text-green-600">₽{lot.currentCost}</div>
            </div>
            <div className="stat">
              <span className="text-gray-500">Конец аукциона</span>
              <div className="text-xl">{new Date(lot.endAuction).toLocaleString()}</div>
            </div>
          </div>

          <div className="description p-4 bg-gray-50 rounded-lg">
            <h3 className="font-semibold mb-2">Описание</h3>
            <p>{lot.description}</p>
          </div>

          {/* Кнопки */}
          <div className="actions flex gap-4 pt-4">
            <button 
              onClick={handlePin}
              className={`px-6 py-3 rounded-lg font-medium transition-all ${
                pinned 
                  ? 'bg-yellow-500 text-white shadow-lg' 
                  : 'bg-gray-200 hover:bg-gray-300'
              }`}
            >
              {pinned ? '✅ Закреплено' : '📌 Закрепить'}
            </button>
            
            <button 
              onClick={handleBuy}
              className="flex-1 bg-blue-600 text-white py-3 px-6 rounded-lg font-medium hover:bg-blue-700 shadow-lg transition-all"
              disabled={new Date(lot.endAuction) < new Date()}
            >
              {new Date(lot.endAuction) < new Date() ? 'Аукцион завершен' : '💰 Купить сейчас'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LotDetailPage;
