import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import SinginPage from './features/auth/SinginPage';
import SingupPage from './features/auth/SingupPage';
import MainPage from './features/auth/MainPage';
import Test from './features/auth/Test';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/signup" replace />} />
        <Route path="/signin" element={<SinginPage />} />
        <Route path="/signup" element={<SingupPage />} />
        <Route path="/main" element={<MainPage />} />
      </Routes>
    </Router>
  );
}

export default App
