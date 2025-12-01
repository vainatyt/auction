import React, { ReactNode, MouseEvent, useEffect, useState } from 'react';
import ReactDOM from 'react-dom';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
}

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, children }) => {

  const handleContentClick = (e: MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
  }; 

  if (!isOpen) return null;

  return ReactDOM.createPortal(
    <div>
      <div>
        {children}
      </div>
      <div onClick={onClose}/>
    </div>,
    document.body
  );
};

export default Modal;
