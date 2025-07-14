import api from './api';

export const createTempo = async (tempoData) => {
    try {
        const response = await api.post('/tempos', tempoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar tempo:", error);
        throw error;
    }
};

export const getTempoById = async (id) => {
    try {
        const response = await api.get(`/tempos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar tempo com ID ${id}:`, error);
        throw error;
    }
};

export const getAllTempos = async () => {
    try {
        const response = await api.get('/tempos');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar todos os tempos:", error);
        throw error;
    }
};

export const updateTempo = async (id, tempoData) => {
    try {
        const response = await api.put(`/tempos/${id}`, tempoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar tempo com ID ${id}:`, error);
        throw error;
    }
};