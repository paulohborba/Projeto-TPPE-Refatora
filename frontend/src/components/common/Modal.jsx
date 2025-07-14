// src/components/common/Modal.jsx
import React from 'react';
import '../../styles/modal.css'; // Importa os estilos do modal

function Modal({ show, onClose, title, children, actions }) {
    if (!show) {
        return null; // Não renderiza nada se 'show' for falso
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>{title}</h2>
                    <button className="modal-close-button" onClick={onClose}>&times;</button>
                </div>
                <div className="modal-body">
                    {children} {/* Conteúdo do modal (sua mensagem de confirmação) */}
                </div>
                <div className="modal-footer">
                    {actions} {/* Botões de ação (Confirmar, Cancelar) */}
                </div>
            </div>
        </div>
    );
}

export default Modal;