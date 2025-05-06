# Product Backlog - Sistema de Estacionamento

## Histórias de Usuário

### Épico 1: Gestão de Estacionamento

**História 1.1**
> Eu, como **administrador do sistema**,
> Desejo **cadastrar um novo estacionamento**,
> Para que eu possa **começar a gerenciar seus acessos, eventos e contratantes**.

**Critérios de Aceitação:**
- Deve ser possível informar nome, endereço e capacidade do estacionamento;
- Deve ser possível horário de funcionamento;
- Deve ser possível associar áreas específicas ao estacionamento.

**História 1.2**
> Eu, como **administrador do sistema**,
> Desejo **visualizar um dashboard com informações gerais do estacionamento**,
> Para que eu possa **ter uma visão geral da ocupação, faturamento e eventos**.

**Critérios de Aceitação:**
- Deve ser possível mostrar o número atual de veículos no estacionamento;
- Deve ser possível mostrar faturamento diário, semanal e mensal;
- Deve ser possível mostrar eventos programados para o dia;
- Deve ser possível mostrar contratantes mensalistas ativos.

### 2. Gestão de Contratantes

**História 2.1**
> Eu, como **atendente**,
> Desejo **cadastrar novos contratantes no sistema**,
> Para que eu possa **poder associá-los a acessos e eventos**.

**Critérios de Aceitação:**
- Deve ser possível registrar nome, CPF, telefone e email;
- Deve ser possível validar o CPF quanto à sua formatação;
- Deve ser possível impedir cadastro duplicado de CPF;
- Deve ser possível associar contratantes a eventos.

**História 2.2**
> Eu, como **atendente**,
> Desejo **consultar e editar informações de contratantes**,
> Para que eu possa **manter seus dados atualizados**.

**Critérios de Aceitação:**
- Deve ser possível buscar por nome, CPF ou telefone;
- Deve ser possível editar qualquer informação cadastral;
- Deve ser possível manter histórico de alterações;
- Deve ser possível exibir histórico de acessos e eventos do contratante.

**História 2.3**
> Eu, como **contratante**,  
> Desejo **visualizar meu histórico de acessos e eventos**,  
> Para que eu possa **acompanhar meus usos do estacionamento**.

**Critérios de Aceitação:**
- Deve ser possível mostrar todos os acessos realizados com data e hora;
- Deve ser possível mostrar valores pagos em cada acesso;
- Deve ser possível filtrar por período;
- Deve ser possível mostrar eventos que participou.

### 3. Gestão de Acessos

**História 3.1**
> Eu, como **atendente**,  
> Desejo **registrar a entrada de veículos no estacionamento**,  
> Para que eu possa **iniciar o controle de permanência e posterior cobrança**.

**Critérios de Aceitação:**
- Deve ser possível registrar placa, marca e modelo do veículo;
- Deve ser possível capturar automaticamente data e hora de entrada;
- Deve ser possível associar o veículo a um contratante existente ou criar um novo;
- Deve ser possível informar tipo de acesso (tempo, diária, mensalista).

**História 3.2**
> Eu, como **atendente**,  
> Desejo **registrar a saída de veículos do estacionamento**,  
> Para que eu possa **finalizar o controle de permanência e realizar cobrança**.

**Critérios de Aceitação:**
- Deve ser possível capturar automaticamente data e hora de saída;
- Deve ser possível calcular automaticamente o valor com base no tipo de acesso;
- Deve ser possível emitir comprovante de pagamento;
- Deve ser possível oferecer diferentes opções de pagamento.

**História 3.3**
> Eu, como **gerente**,  
> Desejo **pesquisar e visualizar histórico de acessos**,  
> Para que eu possa **analisar o fluxo de veículos e faturamento**.

**Critérios de Aceitação:**
- Deve ser possível filtrar por data, tipo de acesso, veículo ou contratante;
- Deve ser possível mostrar tempo médio de permanência;
- Deve ser possível exibir valores totais por período;
- Deve ser possível exportar relatórios.

**História 3.4**
> Eu, como **gerente**,  
> Desejo **atualizar informações de um acesso**,  
> Para que eu possa **corrigir possíveis erros de registro**.

**Critérios de Aceitação:**
- Deve ser possível registrar quem fez a alteração;
- Deve ser possível salvar data e hora da alteração;
- Deve ser possível justificar motivo da alteração;
- Deve ser possível manter histórico da alteração.

### 4. Gestão de Veículos

**História 4.1**
> Eu, como **atendente**,  
> Desejo **cadastrar novos veículos no sistema**,  
> Para que eu possa **associá-los a contratantes e acessos**.

**Critérios de Aceitação:**
- Deve ser possível registrar placa, marca e modelo do veículo;
- Deve ser possível validar formato da placa;
- Deve ser possível associar o veículo a um contratante;
- Deve ser possível impedir cadastro duplicado de placas.

**História 4.2**
> Eu, como **atendente**,  
> Desejo **consultar veículos por placa**,  
> Para que eu possa **agilizar o processo de entrada e saída**.

**Critérios de Aceitação:**
- Deve ser possível buscar veículo instantaneamente ao digitar a placa;
- Deve ser possível mostrar histórico de acessos do veículo;
- Deve ser possível identificar se está associado a um mensalista;
- Deve ser possível exibir informações do contratante associado.

### 5. Gestão de Eventos

**História 5.1**
> Eu, como **gerente**,  
> Desejo **cadastrar eventos no estacionamento**,  
> Para que eu possa **gerenciar o fluxo e cobrança específica em dias de eventos**.

**Critérios de Aceitação:**
- Deve ser possível registrar nome, data, hora de início e término do evento;
- Deve ser possível definir valor específico para o evento;
- Deve ser possível associar contratantes ao evento;
- Deve ser possível reservar vagas para o evento.

**História 5.2**
> Eu, como **gerente**,  
> Desejo **visualizar e gerenciar eventos programados**,  
> Para que eu possa **organizar a ocupação do estacionamento**.

**Critérios de Aceitação:**
- Deve ser possível mostrar calendário com todos os eventos;
- Deve ser possível filtrar por período, nome ou contratante;
- Deve ser possível mostrar ocupação prevista para cada evento;
- Deve ser possível calcular faturamento previsto para eventos.

### 6. Cobrança e Pagamentos

**História 6.1**
> Eu, como **administrador**,  
> Desejo **configurar os valores de cobrança para diferentes tipos de acesso**,  
> Para que eu possa **estabelecer a política de preços do estacionamento**.

**Critérios de Aceitação:**
- Deve ser possível definir valor da fração de tempo;
- Deve ser possível definir valor da diária;
- Deve ser possível definir valor da diária noturna;
- Deve ser possível definir valor para mensalistas.

**História 6.2**
> Eu, como **atendente**,  
> Desejo **calcular o valor a ser cobrado na saída do veículo**,  
> Para que eu possa **cobrar o valor correto do cliente**.

**Critérios de Aceitação:**
- Deve ser possível calcular com base no tipo de acesso (tempo, diária, mensalista);
- Deve ser possível aplicar descontos quando configurados;
- Deve ser possível considerar eventos especiais;
- Deve ser possível mostrar detalhamento do cálculo.

**História 6.3**
> Eu, como **mensalista**,  
> Desejo **gerenciar minha assinatura mensal**,  
> Para que eu possa **manter meu acesso ao estacionamento**.

**Critérios de Aceitação:**
- Deve ser possível mostrar data de início e fim da assinatura;
- Deve ser possível renovação antecipada;
- Deve ser possível enviar lembretes de vencimento;
- Deve ser possível pagamento online.

**História 6.4**
> Eu, como **gerente**,  
> Desejo **gerar relatórios financeiros por período**,  
> Para que eu possa **analisar o desempenho do estacionamento**.

**Critérios de Aceitação:**
- Deve ser possível mostrar faturamento por tipo de acesso;
- Deve ser possível mostrar faturamento por eventos;
- Deve ser possível filtrar por período;
- Deve ser possível exportar em diferentes formatos (PDF, Excel).

## Tabela das Histórias de Usuário

| Épico | ID |  Eu, como | Desejo | Para que eu possa |  
| --- | --- | --- | --- | --- |
| Gestão de Estacionamento | 1.1 | administrador do sistema | cadastrar um novo estacionamento  |  começar a gerenciar seus acessos, eventos e contratantes | 
| Gestão de Estacionamento | 1.2 | administrador do sistema | visualizar um dashboard com informações gerais do estacionamento  |  ter uma visão geral da ocupação, faturamento e eventos | 
| Gestão de Contratantes | 2.1 | atendente | cadastrar novos contratantes no sistema  | poder associá-los a acessos e eventos  | 
|Gestão de Contratantes | 2.2 | atendente | consultar e editar informações de contratantes  | manter seus dados atualizados  | 
| Gestão de Contratantes | 2.3 | contratante | visualizar meu histórico de acessos e eventos  | acompanhar meus usos do estacionamento  | 
|Gestão de Acessos | 3.1 | atendente | registrar a entrada de veículos no estacionamento  |  iniciar o controle de permanência e posterior cobrança | 
| Gestão de Acessos | 3.2 | atendente |  registrar a saída de veículos do estacionamento | finalizar o controle de permanência e realizar cobrança  | 
| Gestão de Acessos | 3.3 | gerente | esquisar e visualizar histórico de acessos  | analisar o fluxo de veículos e faturamento  | 
| Gestão de Acessos | 3.4 | gerente | atualizar informações de um acesso  | corrigir possíveis erros de registro  | 
| Gestão de Veículos | 4.1 | atendente | cadastrar novos veículos no sistema  |  associá-los a contratantes e acessos | 
| Gestão de Veículos | 4.2 | atendente | consultar veículos por placa  |  agilizar o processo de entrada e saída | 
| Gestão de Eventos | 5.1 | gerente | cadastrar eventos no estacionamento  |  gerenciar o fluxo e cobrança específica em dias de eventos | 
| Gestão de Eventos | 5.2 | gerente |  visualizar e gerenciar eventos programados |  organizar a ocupação do estacionamento | 
| Cobrança e Pagamentos | 6.1 | administrador | configurar os valores de cobrança para diferentes tipos de acesso  |  estabelecer a política de preços do estacionament | 
| Cobrança e Pagamentos | 6.2 | atendente | calcular o valor a ser cobrado na saída do veículo  | cobrar o valor correto do cliente  | 
| Cobrança e Pagamentos | 6.3 | mensalista |  gerenciar minha assinatura mensal | manter meu acesso ao estacionamento  | 
| Cobrança e Pagamentos | 6.4 | gerente | gerar relatórios financeiros por período  | analisar o desempenho do estacionamento  | 