import api from './api';

export const getAllEventos = async () => {
    try {
        const response = await api.get('/eventos');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.eventos)) {
            return data.eventos;
        }
        console.warn('API response structure unexpected for eventos:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todos os eventos da API:", error);
        if (error.response) {
            console.error('Response error:', error.response.status, error.response.data);
        } else if (error.request) {
            console.error('Network error:', error.request);
        } else {
            console.error('Error:', error.message);
        }
        throw error;
    }
};

export const getEventoById = async (id) => {
    try {
        const response = await api.get(`/eventos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar evento com ID ${id} da API:`, error);
        if (error.response) {
            console.error('Response error:', error.response.status, error.response.data);
        } else if (error.request) {
            console.error('Network error:', error.request);
        } else {
            console.error('Error:', error.message);
        }
        throw error;
    }
};

export const createEvento = async (eventoData) => {
    try {
        const response = await api.post('/eventos', eventoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar evento na API:", error);
        if (error.response) {
            console.error('Response error:', error.response.status, error.response.data);
        } else if (error.request) {
            console.error('Network error:', error.request);
        } else {
            console.error('Error:', error.message);
        }
        throw error;
    }
};

export const updateEvento = async (id, eventoData) => {
    try {
        const response = await api.put(`/eventos/${id}`, eventoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar evento com ID ${id} na API:`, error);
        if (error.response) {
            console.error('Response error:', error.response.status, error.response.data);
        } else if (error.request) {
            console.error('Network error:', error.request);
        } else {
            console.error('Error:', error.message);
        }
        throw error;
    }
};

export const deleteEvento = async (id) => {
    try {
        const response = await api.delete(`/eventos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar evento com ID ${id} da API:`, error);
        if (error.response) {
            console.error('Response error:', error.response.status, error.response.data);
        } else if (error.request) {
            console.error('Network error:', error.request);
        } else {
            console.error('Error:', error.message);
        }
        throw error;
    }
};