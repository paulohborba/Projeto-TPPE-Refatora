import api from './api';

export const getAllContratantes = async () => {
    try {
        const response = await api.get('/contratantes');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar todos os contratantes:", error);
        throw error;
    }
};

export const getContratanteById = async (id) => {
    try {
        const response = await api.get(`/contratantes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar contratante com ID ${id}:`, error);
        throw error;
    }
};

export const createContratante = async (contratanteData) => {
    try {
        const response = await api.post('/contratantes', contratanteData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar contratante:", error);
        throw error;
    }
};

export const updateContratante = async (id, contratanteData) => {
    try {
        const response = await api.put(`/contratantes/${id}`, contratanteData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar contratante com ID ${id}:`, error);
        throw error;
    }
};

export const deleteContratante = async (id) => {
    try {
        const response = await api.delete(`/contratantes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar contratante com ID ${id}:`, error);
        throw error;
    }
};