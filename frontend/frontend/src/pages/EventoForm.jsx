import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/common/Card';
import InputGroup from '../components/common/InputGroup';
import Button from '../components/common/Button';
import { createEvento, getEventoById, updateEvento } from '../api/eventos';
import { getEstacionamentoById } from '../api/estacionamentos';
import { getContratanteById } from '../api/contratantes';

function EventoForm({ isEditing = false }) {
    const navigate = useNavigate();
    const { id, estacionamentoId, contratanteId } = useParams();

    const [evento, setEvento] = useState({
        id: '',
        nome: '',
        dataInicio: '',
        horaInicio: '',
        dataFim: '',
        horaFim: '',
        valorDiaria: '',
        qtdVagasContratadas: '',
        contratante: { id: contratanteId || '', nome: '' },
        estacionamento: { id: estacionamentoId || '', nome: '' }
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (isEditing && id) {
                    const data = await getEventoById(id);
                    setEvento({
                        ...data,
                        id: String(data.id),
                        valorDiaria: data.valorDiaria ? String(data.valorDiaria) : '',
                        qtdVagasContratadas: data.qtdVagasContratadas ? String(data.qtdVagasContratadas) : '',
                        dataInicio: data.dataInicio ? data.dataInicio.split('T')[0] : '',
                        horaInicio: data.dataInicio ? data.dataInicio.split('T')[1]?.substring(0, 5) : '',
                        dataFim: data.dataFim ? data.dataFim.split('T')[0] : '',
                        horaFim: data.dataFim ? data.dataFim.split('T')[1]?.substring(0, 5) : '',
                    });
                } else {
                    if (estacionamentoId) {
                        const estData = await getEstacionamentoById(estacionamentoId);
                        setEvento(prev => ({ ...prev, estacionamento: { id: estacionamentoId, nome: estData.nome } }));
                    }
                    if (contratanteId) {
                        const contData = await getContratanteById(contratanteId);
                        setEvento(prev => ({ ...prev, contratante: { id: contratanteId, nome: contData.nome } }));
                    }
                }
            } catch (err) {
                setError('Erro ao carregar dados do evento, estacionamento ou contratante: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [isEditing, id, estacionamentoId, contratanteId]);

    const handleChange = (e) => {
        const { id, value } = e.target;
        setEvento(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        const dataToSend = {
            ...evento,
            id: evento.id ? Number(evento.id) : null,
            valorDiaria: evento.valorDiaria ? parseFloat(evento.valorDiaria) : null,
            qtdVagasContratadas: evento.qtdVagasContratadas ? parseInt(evento.qtdVagasContratadas, 10) : null,
            dataInicio: evento.dataInicio && evento.horaInicio ? `${evento.dataInicio}T${evento.horaInicio}:00` : null,
            dataFim: evento.dataFim && evento.horaFim ? `${evento.dataFim}T${evento.horaFim}:00` : null,
            contratante: evento.contratante.id ? { id: Number(evento.contratante.id) } : null,
            estacionamento: evento.estacionamento.id ? { id: Number(evento.estacionamento.id) } : null
        };

        delete dataToSend.horaInicio;
        delete dataToSend.horaFim;

        try {
            if (isEditing) {
                await updateEvento(dataToSend.id, dataToSend);
                alert('Evento atualizado com sucesso!');
            } else {
                await createEvento(dataToSend);
                alert('Evento cadastrado com sucesso!');
            }
            if (contratanteId) {
                navigate(`/contratantes/${contratanteId}`);
            } else if (estacionamentoId) {
                navigate(`/estacionamentos/${estacionamentoId}`);
            } else {
                navigate('/');
            }
        } catch (err) {
            console.error("Erro na operação:", err.response ? err.response.data : err.message);
            setError('Erro ao salvar evento: ' + (err.response?.data?.message || err.message || 'Erro desconhecido'));
        }
    };

    if (loading) {
        return <Card>Carregando formulário...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    return (
        <Card title={isEditing ? `Evento ${evento.nome || ''} - Editar` : "Registrar Evento"}>
            <form onSubmit={handleSubmit}>
                <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={evento.id}
                    onChange={handleChange}
                    placeholder="Insira o ID do evento"
                    readOnly={isEditing}
                    required
                />
                {evento.contratante.nome && (
                    <InputGroup
                        label="Contratante:"
                        id="contratanteNome"
                        type="text"
                        value={evento.contratante.nome}
                        readOnly
                    />
                )}
                {evento.estacionamento.nome && (
                    <InputGroup
                        label="Estacionamento:"
                        id="estacionamentoNome"
                        type="text"
                        value={evento.estacionamento.nome}
                        readOnly
                    />
                )}
                <InputGroup
                    label="Nome do Evento:"
                    id="nome"
                    type="text"
                    value={evento.nome}
                    onChange={handleChange}
                    placeholder="Nome do evento"
                    required
                />

                <div className="form-row-date-time">
                    <InputGroup
                        label="Data de Início:"
                        id="dataInicio"
                        type="date"
                        value={evento.dataInicio}
                        onChange={handleChange}
                        required
                    />
                    <InputGroup
                        label="Hora de Início:"
                        id="horaInicio"
                        type="time"
                        value={evento.horaInicio}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-row-date-time">
                    <InputGroup
                        label="Data de Fim:"
                        id="dataFim"
                        type="date"
                        value={evento.dataFim}
                        onChange={handleChange}
                        required
                    />
                    <InputGroup
                        label="Hora de Fim:"
                        id="horaFim"
                        type="time"
                        value={evento.horaFim}
                        onChange={handleChange}
                        required
                    />
                </div>

                <InputGroup
                    label="Valor Diária Evento:"
                    id="valorDiaria"
                    type="number"
                    value={evento.valorDiaria}
                    onChange={handleChange}
                    placeholder="0.00"
                    step="0.01"
                    required
                />
                <InputGroup
                    label="Quantidade de Vagas Contratadas:"
                    id="qtdVagasContratadas"
                    type="number"
                    value={evento.qtdVagasContratadas}
                    onChange={handleChange}
                    placeholder="Número de vagas"
                    required
                />

                <div className="button-group">
                    <Button type="submit" variant="primary">
                        {isEditing ? 'Confirmar Edição' : 'Registrar Evento'}
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                        Cancelar
                    </Button>
                </div>
            </form>
        </Card>
    );
}

export default EventoForm;