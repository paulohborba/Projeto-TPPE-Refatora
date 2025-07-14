// src/components/common/Button.jsx
import React from 'react';
import '../../styles/button.css';

function Button({ children, onClick, variant = 'primary', type = 'button', disabled = false }) {
    // A classe CSS será construída dinamicamente, e.g., 'button button-primary' ou 'button button-danger'
    const classes = `button button-${variant}`;

    return (
        <button
            type={type}
            className={classes}
            onClick={onClick}
            disabled={disabled}
        >
            {children}
        </button>
    );
}

export default Button;