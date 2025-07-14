import api from './api';

export const getAllVeiculos = async () => {
    try {
        const response = await api.get('/veiculos');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar todos os veículos:", error);
        throw error;
    }
};

export const getVeiculoById = async (id) => {
    try {
        const response = await api.get(`/veiculos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar veículo com ID ${id}:`, error);
        throw error;
    }
};

export const createVeiculo = async (veiculoData) => {
    try {
        const response = await api.post('/veiculos', veiculoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar veículo:", error);
        throw error;
    }
};

export const updateVeiculo = async (id, veiculoData) => {
    try {
        const response = await api.put(`/veiculos/${id}`, veiculoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar veículo com ID ${id}:`, error);
        throw error;
    }
};

export const deleteVeiculo = async (id) => {
    try {
        const response = await api.delete(`/veiculos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar veículo com ID ${id}:`, error);
        throw error;
    }
};