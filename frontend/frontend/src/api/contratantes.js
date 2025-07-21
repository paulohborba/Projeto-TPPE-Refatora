import api from './api';

export const getAllContratantes = async () => {
    try {
        const response = await api.get('/contratantes');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.contratantes)) {
            return data.contratantes;
        }
        console.warn('API response structure unexpected for contratantes:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todos os contratantes da API:", error);
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

export const getContratanteById = async (id) => {
    try {
        const response = await api.get(`/contratantes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar contratante com ID ${id} da API:`, error);
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

export const createContratante = async (contratanteData) => {
    try {
        const response = await api.post('/contratantes', contratanteData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar contratante na API:", error);
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

export const updateContratante = async (id, contratanteData) => {
    try {
        const response = await api.put(`/contratantes/${id}`, contratanteData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar contratante com ID ${id} na API:`, error);
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

export const deleteContratante = async (id) => {
    try {
        const response = await api.delete(`/contratantes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar contratante com ID ${id} da API:`, error);
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