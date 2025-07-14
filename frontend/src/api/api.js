import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api', // Adapte para a URL da sua API backend
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;