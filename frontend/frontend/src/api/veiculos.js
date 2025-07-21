import api from './api';

export const getAllVeiculos = async () => {
    try {
        const response = await api.get('/veiculos');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.veiculos)) {
            return data.veiculos;
        }
        console.warn('API response structure unexpected for veiculos:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todos os veículos da API:", error);
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

export const getVeiculoById = async (id) => {
    try {
        const response = await api.get(`/veiculos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar veículo com ID ${id} da API:`, error);
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

export const createVeiculo = async (veiculoData) => {
    try {
        const response = await api.post('/veiculos', veiculoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar veículo na API:", error);
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

export const updateVeiculo = async (id, veiculoData) => {
    try {
        const response = await api.put(`/veiculos/${id}`, veiculoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar veículo com ID ${id} na API:`, error);
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

export const deleteVeiculo = async (id) => {
    try {
        const response = await api.delete(`/veiculos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar veículo com ID ${id} da API:`, error);
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