import api from './api';

export const getAllMensalistas = async () => {
    try {
        const response = await api.get('/mensalistas');
        const data = response.data;

        if (Array.isArray(data)) {
            return data;
        }
        if (data && Array.isArray(data.data)) {
            return data.data;
        }
        if (data && Array.isArray(data.mensalistas)) {
            return data.mensalistas;
        }
        console.warn('API response structure unexpected for mensalistas:', data);
        return [];
    } catch (error) {
        console.error("Erro ao buscar todos os mensalistas da API:", error);
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

export const getMensalistaById = async (id) => {
    try {
        const response = await api.get(`/mensalistas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar mensalista com ID ${id} da API:`, error);
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

export const createMensalista = async (mensalistaData) => {
    try {
        const response = await api.post('/mensalistas', mensalistaData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar mensalista na API:", error);
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

export const updateMensalista = async (id, mensalistaData) => {
    try {
        const response = await api.put(`/mensalistas/${id}`, mensalistaData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar mensalista com ID ${id} na API:`, error);
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

export const deleteMensalista = async (id) => {
    try {
        const response = await api.delete(`/mensalistas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar mensalista com ID ${id} da API:`, error);
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