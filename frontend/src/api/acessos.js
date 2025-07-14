import api from './api';

export const createAcesso = async (acessoData) => {
    try {
        const response = await api.post('/acessos', acessoData);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar acesso:", error);
        throw error;
    }
};

export const updateAcesso = async (id, acessoData) => {
    try {
        const response = await api.put(`/acessos/${id}`, acessoData);
        return response.data;
    } catch (error) {
        console.error(`Erro ao atualizar acesso com ID ${id}:`, error);
        throw error;
    }
};

export const deleteAcesso = async (id) => {
    try {
        const response = await api.delete(`/acessos/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Erro ao deletar acesso com ID ${id}:`, error);
        throw error;
    }
};