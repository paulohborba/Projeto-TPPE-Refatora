import api from './api';

export const getAllMensalistas = async () => {
    try {
        const response = await api.get('/mensalistas');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar todos os mensalistas:", error);
        throw error;
    }
};

export const getMensalistaById = async (id) => {
    try {
        const response = await api.get(`/mensalistas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar mensalista com ID ${id}:`, error);
        throw error;
    }
};

export const createMensalista = async (mensalistaData) => {
    try {
        const response = await api.post('/mensalistas', mensalistaData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar mensalista:", error);
        throw error;
    }
};

export const updateMensalista = async (id, mensalistaData) => {
    try {
        const response = await api.put(`/mensalistas/${id}`, mensalistaData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar mensalista com ID ${id}:`, error);
        throw error;
    }
};

export const deleteMensalista = async (id) => {
    try {
        const response = await api.delete(`/mensalistas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar mensalista com ID ${id}:`, error);
        throw error;
    }
};