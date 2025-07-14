import api from './api';

export const getAllEstacionamentos = async () => {
    try {
        const response = await api.get('/estacionamentos');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar todos os estacionamentos:", error);
        throw error;
    }
};

export const getEstacionamentoById = async (id) => {
    try {
        const response = await api.get(`/estacionamentos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar estacionamento com ID ${id}:`, error);
        throw error;
    }
};

export const createEstacionamento = async (estacionamentoData) => {
    try {
        const response = await api.post('/estacionamentos', estacionamentoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar estacionamento:", error);
        throw error;
    }
};

export const updateEstacionamento = async (id, estacionamentoData) => {
    try {
        const response = await api.put(`/estacionamentos/${id}`, estacionamentoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar estacionamento com ID ${id}:`, error);
        throw error;
    }
};

export const deleteEstacionamento = async (id) => {
    try {
        const response = await api.delete(`/estacionamentos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar estacionamento com ID ${id}:`, error);
        throw error;
    }
};