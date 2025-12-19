import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import SigninPage from './features/auth/SigninPage';
import SignupPage from './features/auth/SignupPage';
import MainPage from './features/MainPage';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import LotDetailPage from './features/LotDetailPage';
import CommentWritePage from './features/CommentWritePage';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Публичные маршруты — без защиты */}
          <Route path="/" element={<Navigate to="/signup" replace />} />
          <Route path="/signin" element={<SigninPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/comment/write/:userId" element={<CommentWritePage />} />
          
          {/* Защищенный маршрут */}
          <Route 
            path="/main" 
            element={
              <ProtectedRoute>
                <MainPage />
              </ProtectedRoute>
            } 
          />
          <Route path="/lot/:lotId" element={<LotDetailPage />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}


export default App
