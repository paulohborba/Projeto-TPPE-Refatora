// src/components/common/ListItemCard.jsx
import React from 'react';
import Button from './Button'; // Importa o componente Button
import '../../styles/card.css'; // Reusa estilos de card, ou crie um ListItemCard.css se preferir

function ListItemCard({ title, description, info, onEdit, onDelete, onDetails, hasActions = true }) {
    return (
        <div className="list-item-card">
            <div className="list-item-content">
                <h3>{title}</h3>
                {description && <p>{description}</p>}
                {info && <span className="list-item-info">{info}</span>}
            </div>
            {hasActions && (
                <div className="list-item-actions">
                    {onDetails && <Button variant="secondary" onClick={onDetails}>Detalhes</Button>}
                    {onEdit && <Button variant="secondary" onClick={onEdit}>Editar</Button>}
                    {onDelete && <Button variant="danger" onClick={onDelete}>Apagar</Button>}
                </div>
            )}
        </div>
    );
}

export default ListItemCard;