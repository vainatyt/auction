import React, { useState } from 'react';
import axios from 'axios'
import { Link } from 'react-router-dom';
import Cookies from 'js-cookie';

import CloseApi from '../../api/CloseApi'

const handleTestJwt = async (e: React.FormEvent) => {
  try {
    const response = await CloseApi.get('/api/testjwt');

  console.log('Данные:', response.data);
  } catch (error) {
    console.error('Ошибка запроса:', error);
  }
}

const SingupPage: React.FC = () => {

  return (
    <div>
      <p>Добра пожаловать на наш сайт на данный момент это все. :(</p>
      <button onClick={handleTestJwt}>проверить jwt token</button>
    </div>
  );
};

export default SingupPage;
