// src/pages/NotFoundPage.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import Card from '../components/common/Card'; // Reutiliza o Card para um layout agradável
import Button from '../components/common/Button'; // Reutiliza o Button

function NotFoundPage() {
    return (
        <Card title="Página Não Encontrada">
            <p>Parece que a página que você está procurando não existe.</p>
            <p>Por favor, verifique o endereço ou retorne à página inicial.</p>
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <Link to="/">
                    <Button variant="primary">Voltar para a Dashboard</Button>
                </Link>
            </div>
        </Card>
    );
}

export default NotFoundPage;