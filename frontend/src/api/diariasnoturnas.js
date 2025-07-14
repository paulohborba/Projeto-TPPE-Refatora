import api from './api';

export const getAllDiariasNoturnas = async () => {
    try {
        const response = await api.get('/diariasnoturnas');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar todas as diárias noturnas:", error);
        throw error;
    }
};

export const getDiariaNoturnaById = async (id) => {
    try {
        const response = await api.get(`/diariasnoturnas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar diária noturna com ID ${id}:`, error);
        throw error;
    }
};

export const createDiariaNoturna = async (diariaNoturnaData) => {
    try {
        const response = await api.post('/diariasnoturnas', diariaNoturnaData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar diária noturna:", error);
        throw error;
    }
};

export const updateDiariaNoturna = async (id, diariaNoturnaData) => {
    try {
        const response = await api.put(`/diariasnoturnas/${id}`, diariaNoturnaData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar diária noturna com ID ${id}:`, error);
        throw error;
    }
};

export const deleteDiariaNoturna = async (id) => {
    try {
        const response = await api.delete(`/diariasnoturnas/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar diária noturna com ID ${id}:`, error);
        throw error;
    }
};