import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SinginPage from './features/auth/SinginPage';
import SingupPage from './features/auth/SingupPage';
import Test from './features/auth/Test';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/signin" element={<SinginPage />} />
        <Route path="/signup" element={<SingupPage />} />
      </Routes>
    </Router>
  );
}

export default App
