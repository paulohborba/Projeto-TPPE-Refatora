import api from './api';

export const getAllDiariasNoturnas = async () => {
    try {
        const response = await api.get('/diariasnoturnas');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.diariasnoturnas)) {
            return data.diariasnoturnas;
        }
        console.warn('API response structure unexpected for diariasnoturnas:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todas as diárias noturnas da API:", error);
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

export const getDiariaNoturnaById = async (id) => {
    try {
        const response = await api.get(`/diariasnoturnas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar diária noturna com ID ${id} da API:`, error);
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

export const createDiariaNoturna = async (diariaNoturnaData) => {
    try {
        const response = await api.post('/diariasnoturnas', diariaNoturnaData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar diária noturna na API:", error);
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

export const updateDiariaNoturna = async (id, diariaNoturnaData) => {
    try {
        const response = await api.put(`/diariasnoturnas/${id}`, diariaNoturnaData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar diária noturna com ID ${id} na API:`, error);
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

export const deleteDiariaNoturna = async (id) => {
    try {
        const response = await api.delete(`/diariasnoturnas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar diária noturna com ID ${id} da API:`, error);
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