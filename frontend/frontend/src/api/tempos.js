import api from './api';

export const getAllTempos = async () => {
    try {
        const response = await api.get('/tempos');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.tempos)) {
            return data.tempos;
        }
        console.warn('API response structure unexpected for tempos:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todos os tempos da API:", error);
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

export const createTempo = async (tempoData) => {
    try {
        const response = await api.post('/tempos', tempoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar tempo na API:", error);
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

export const getTempoById = async (id) => {
    try {
        const response = await api.get(`/tempos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar tempo com ID ${id} da API:`, error);
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

export const updateTempo = async (id, tempoData) => {
    try {
        const response = await api.put(`/tempos/${id}`, tempoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar tempo com ID ${id} na API:`, error);
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

export const deleteTempo = async (id) => {
    try {
        const response = await api.delete(`/tempos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar tempo com ID ${id} da API:`, error);
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