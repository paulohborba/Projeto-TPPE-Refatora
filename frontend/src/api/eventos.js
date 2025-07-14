import api from './api';

export const getAllEventos = async () => {
    try {
        const response = await api.get('/eventos');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar todos os eventos:", error);
        throw error;
    }
};

export const getEventoById = async (id) => {
    try {
        const response = await api.get(`/eventos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar evento com ID ${id}:`, error);
        throw error;
    }
};

export const createEvento = async (eventoData) => {
    try {
        const response = await api.post('/eventos', eventoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar evento:", error);
        throw error;
    }
};

export const updateEvento = async (id, eventoData) => {
    try {
        const response = await api.put(`/eventos/${id}`, eventoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar evento com ID ${id}:`, error);
        throw error;
    }
};

export const deleteEvento = async (id) => {
    try {
        const response = await api.delete(`/eventos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar evento com ID ${id}:`, error);
        throw error;
    }
};