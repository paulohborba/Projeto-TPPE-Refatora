import api from './api';

export const getAllEstacionamentos = async () => {
    try {
        const response = await api.get('/estacionamentos');

        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
 
        if (data && Array.isArray(data.data)) {
            return data.data;
        }

        if (data && Array.isArray(data.estacionamentos)) {
            return data.estacionamentos;
        }

        console.warn('API response structure unexpected for estacionamentos:', data);
        return [];
        
    } catch (error) {
        console.error("Erro ao buscar todos os estacionamentos da API:", error);
        
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

export const getEstacionamentoById = async (id) => {
    try {
        const response = await api.get(`/estacionamentos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar estacionamento com ID ${id} da API:`, error);
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

export const createEstacionamento = async (estacionamentoData) => {
    try {
        const response = await api.post('/estacionamentos', estacionamentoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar estacionamento na API:", error);
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

export const updateEstacionamento = async (id, estacionamentoData) => {
    try {
        const response = await api.put(`/estacionamentos/${id}`, estacionamentoData); 
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar estacionamento com ID ${id} na API:`, error);
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

export const deleteEstacionamento = async (id) => {
    try {
        const response = await api.delete(`/estacionamentos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar estacionamento com ID ${id} da API:`, error);
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