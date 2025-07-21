import api from './api';

export const getAllDiarias = async () => {
    try {
        const response = await api.get('/diarias');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.diarias)) { 
            return data.diarias;
        }
        console.warn('API response structure unexpected for diarias:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todas as diárias da API:", error);
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

export const createDiaria = async (diariaData) => {
    try {
        const response = await api.post('/diarias', diariaData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar diária na API:", error);
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

export const getDiariaById = async (id) => {
    try {
        const response = await api.get(`/diarias/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar diária com ID ${id} da API:`, error);
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

export const updateDiaria = async (id, diariaData) => {
    try {
        const response = await api.put(`/diarias/${id}`, diariaData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar diária com ID ${id} na API:`, error);
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

export const deleteDiaria = async (id) => {
    try {
        const response = await api.delete(`/diarias/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar diária com ID ${id} da API:`, error);
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