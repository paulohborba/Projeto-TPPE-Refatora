import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/common/Card';
import InputGroup from '../components/common/InputGroup';
import Button from '../components/common/Button';
import { createVeiculo, getVeiculoById, updateVeiculo } from '../api/veiculos';
import { getEstacionamentoById } from '../api/estacionamentos';

function VeiculoForm({ isEditing = false }) {
    const navigate = useNavigate();
    const { id, estacionamentoId } = useParams();

    const [veiculo, setVeiculo] = useState({
        id: '',
        placa: '',
        marca: '',
        modelo: '',
        cor: '',
        tipoAcesso: '',
        horaEntrada: '',
        dataEntrada: '',
        horaSaida: '',
        dataSaida: '',
        valorCobrado: '',
        estacionamento: { id: estacionamentoId || '', nome: '' }
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (isEditing && id) {
                    const data = await getVeiculoById(id);
                    setVeiculo({
                        ...data,
                        id: String(data.id),
                        valorCobrado: data.valorCobrado ? String(data.valorCobrado) : '',
                        dataEntrada: data.dataEntrada ? data.dataEntrada.split('T')[0] : '',
                        horaEntrada: data.dataEntrada ? data.dataEntrada.split('T')[1]?.substring(0, 5) : '',
                        dataSaida: data.dataSaida ? data.dataSaida.split('T')[0] : '',
                        horaSaida: data.dataSaida ? data.dataSaida.split('T')[1]?.substring(0, 5) : '',
                    });
                } else if (estacionamentoId) {
                    const estacionamentoData = await getEstacionamentoById(estacionamentoId);
                    setVeiculo(prev => ({
                        ...prev,
                        estacionamento: { id: estacionamentoId, nome: estacionamentoData.nome }
                    }));
                }
            } catch (err) {
                setError('Erro ao carregar dados do veículo ou estacionamento: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [isEditing, id, estacionamentoId]);

    const handleChange = (e) => {
        const { id, value } = e.target;
        setVeiculo(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        const dataToSend = {
            ...veiculo,
            id: veiculo.id ? Number(veiculo.id) : null,
            valorCobrado: veiculo.valorCobrado ? parseFloat(veiculo.valorCobrado) : null,
            dataEntrada: veiculo.dataEntrada && veiculo.horaEntrada ? `${veiculo.dataEntrada}T${veiculo.horaEntrada}:00` : null,
            dataSaida: veiculo.dataSaida && veiculo.horaSaida ? `${veiculo.dataSaida}T${veiculo.horaSaida}:00` : null,
            estacionamento: veiculo.estacionamento.id ? { id: Number(veiculo.estacionamento.id) } : null
        };

        delete dataToSend.horaEntrada;
        delete dataToSend.dataSaida;
        delete dataToSend.horaSaida;


        try {
            if (isEditing) {
                await updateVeiculo(dataToSend.id, dataToSend);
                alert('Veículo atualizado com sucesso!');
            } else {
                await createVeiculo(dataToSend);
                alert('Veículo registrado com sucesso!');
            }
            if (estacionamentoId) {
                navigate(`/estacionamentos/${estacionamentoId}`);
            } else {
                navigate('/');
            }
        } catch (err) {
            console.error("Erro na operação:", err.response ? err.response.data : err.message);
            setError('Erro ao salvar veículo: ' + (err.response?.data?.message || err.message || 'Erro desconhecido'));
        }
    };

    if (loading) {
        return <Card>Carregando formulário...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    return (
        <Card title={isEditing ? `Veículo ${veiculo.placa || ''} - Editar` : "Registrar Veículo"}>
            <form onSubmit={handleSubmit}>
                <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={veiculo.id}
                    onChange={handleChange}
                    placeholder="Insira o ID do veículo/acesso"
                    readOnly={isEditing}
                    required
                />
                {veiculo.estacionamento.nome && (
                    <InputGroup
                        label="Nome do Estacionamento:"
                        id="estacionamentoNome"
                        type="text"
                        value={veiculo.estacionamento.nome}
                        readOnly
                    />
                )}
                <InputGroup
                    label="Placa:"
                    id="placa"
                    type="text"
                    value={veiculo.placa}
                    onChange={handleChange}
                    placeholder="Insira a placa do veículo"
                    maxLength="7"
                    required
                />
                <InputGroup
                    label="Marca:"
                    id="marca"
                    type="text"
                    value={veiculo.marca}
                    onChange={handleChange}
                    placeholder="Insira a marca"
                    required
                />
                <InputGroup
                    label="Modelo:"
                    id="modelo"
                    type="text"
                    value={veiculo.modelo}
                    onChange={handleChange}
                    placeholder="Insira o modelo"
                    required
                />
                <InputGroup
                    label="Cor:"
                    id="cor"
                    type="text"
                    value={veiculo.cor}
                    onChange={handleChange}
                    placeholder="Insira a cor"
                    required
                />

                <InputGroup
                    label="Tipo de Acesso:"
                    id="tipoAcesso"
                    type="text"
                    value={veiculo.tipoAcesso}
                    onChange={handleChange}
                    placeholder="Ex: HORISTA, MENSALISTA, EVENTO"
                    required
                />

                <div className="form-row-date-time">
                    <InputGroup
                        label="Data de Entrada:"
                        id="dataEntrada"
                        type="date"
                        value={veiculo.dataEntrada}
                        onChange={handleChange}
                        required
                    />
                    <InputGroup
                        label="Hora de Entrada:"
                        id="horaEntrada"
                        type="time"
                        value={veiculo.horaEntrada}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-row-date-time">
                    <InputGroup
                        label="Data de Saída:"
                        id="dataSaida"
                        type="date"
                        value={veiculo.dataSaida}
                        onChange={handleChange}
                        required
                    />
                    <InputGroup
                        label="Hora de Saída:"
                        id="horaSaida"
                        type="time"
                        value={veiculo.horaSaida}
                        onChange={handleChange}
                        required
                    />
                </div>
                
                <InputGroup
                    label="Valor Cobrado:"
                    id="valorCobrado"
                    type="number"
                    value={veiculo.valorCobrado}
                    onChange={handleChange}
                    placeholder="0.00"
                    step="0.01"
                    required
                />

                <div className="button-group">
                    <Button type="submit" variant="primary">
                        {isEditing ? 'Confirmar Edição' : 'Registrar Veículo'}
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                        Cancelar
                    </Button>
                </div>
            </form>
        </Card>
    );
}

export default VeiculoForm;