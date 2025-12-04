import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import Cookies from 'js-cookie';

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

interface AuthContextType {
  isAuthenticated: boolean;
  login: (token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = Cookies.get('auction_jwt_token');
    setIsAuthenticated(!!token);
  }, []);

  const login = (token: string) => {
    Cookies.set('auction_jwt_token', token, { 
      expires: new Date(new Date().getTime() + 60 * 60 * 1000), // 7 дней
      secure: true, 
      sameSite: 'strict' 
    });
    setIsAuthenticated(true);
  };

  const logout = () => {
    Cookies.remove('auction_jwt_token');
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
