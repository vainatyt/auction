import React, { useState } from 'react';
import axios from 'axios'
import { Link } from 'react-router-dom';

const SingupPage: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  return (
      <div>
      <p>Добра пожаловать на наш сайт на данный момент это все. :(</p>

      </div>
  );
};

export default SingupPage;
