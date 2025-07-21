import api from './api';

export const getAllAcessos = async () => {
    try {
        const response = await api.get('/acessos');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.acessos)) {
            return data.acessos;
        }
        console.warn('API response structure unexpected for acessos:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todos os acessos da API:", error);
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

export const createAcesso = async (acessoData) => {
    try {
        const response = await api.post('/acessos', acessoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar acesso na API:", error);
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

export const updateAcesso = async (id, acessoData) => {
    try {
        const response = await api.put(`/acessos/${id}`, acessoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar acesso com ID ${id} na API:`, error);
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

export const deleteAcesso = async (id) => {
    try {
        const response = await api.delete(`/acessos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar acesso com ID ${id} da API:`, error);
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