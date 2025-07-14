import api from './api';

export const createDiaria = async (diariaData) => {
    try {
        const response = await api.post('/diarias', diariaData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar diária:", error);
        throw error;
    }
};

export const getDiariaById = async (id) => {
    try {
        const response = await api.get(`/diarias/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao buscar diária com ID ${id}:`, error);
        throw error;
    }
};

export const updateDiaria = async (id, diariaData) => {
    try {
        const response = await api.put(`/diarias/${id}`, diariaData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar diária com ID ${id}:`, error);
        throw error;
    }
};

export const deleteDiaria = async (id) => {
    try {
        const response = await api.delete(`/diarias/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar diária com ID ${id}:`, error);
        throw error;
    }
};