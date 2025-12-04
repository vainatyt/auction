import React, { ReactNode, MouseEvent, useEffect, useState } from 'react';
import ReactDOM from 'react-dom';

import '../../styles/Modal.css';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
}

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, children }) => {
  if (!isOpen) return null;

  return (
    <>
      <div
        className='background' 
        onClick={onClose}
      />

      <div className='window'>
        {children}
        <button onClick={onClose} style={{ marginTop: '1rem' }}>Закрыть</button>
      </div>
    </>
  );
};

export default Modal;
