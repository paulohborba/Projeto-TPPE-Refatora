// src/components/common/InputGroup.jsx
import React from 'react';
import '../../styles/inputGroup.css';

function InputGroup({ label, id, type = 'text', value, onChange, placeholder, required = false, readOnly = false, step = null, maxLength = null }) {
    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            <input
                type={type}
                id={id}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                required={required}
                readOnly={readOnly}
                step={step}
                maxLength={maxLength}
            />
        </div>
    );
}

export default InputGroup;
