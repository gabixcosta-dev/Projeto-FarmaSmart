# Sistema FarmaSmart

## Descrição 

Sistema para gestão de uma farmácia/drogaria, controlando o catálogo de medicamentos, receitas médicas, clientes e vendas no balcão. O sistema deve diferenciar medicamentos comuns, genéricos e controlados, exigindo receita válida para a venda destes últimos, além de impedir a venda de itens vencidos.

Como diferencial, o cadastro de novos medicamentos é validado contra uma base pública de dados abertos de medicamentos (bula/princípio ativo), garantindo que apenas produtos com informação sanitária confirmada em fonte oficial sejam comercializados.


> **Nota:** a API oficial de dados abertos de medicamentos da ANVISA (via `dados.gov.br` ou `consultas.anvisa.gov.br`) hoje exige autenticação/token ou está protegida por proteção anti-robô, o que a torna inviável como integração simples e estável para um projeto de 2º período. Por isso, a sugestão abaixo usa a **openFDA** (base de dados aberta de medicamentos da FDA/EUA), que é pública, gratuita e não exige chave — mantendo o mesmo objetivo pedagógico de validar o cadastro contra uma fonte de dados real. Caso surja uma alternativa nacional estável e sem autenticação, ela pode substituir a openFDA sem alterar a estrutura dos requisitos.


## Requisitos Funcionais

### 1. Cadastro de Medicamentos
- REQ01: Cadastrar medicamentos (nome comercial, princípio ativo, código de registro interno, data de validade, preço)
- REQ02: Implementar herança para tipos de medicamento: Comum, Genérico e Controlado
- REQ03: Consultar a base pública **openFDA** (endpoint `https://api.fda.gov/drug/label.json?search=openfda.generic_name:"<princípio ativo>"`) pelo princípio ativo para validar sua existência em fonte oficial e sugerir indicações/avisos no cadastro

### 2. Clientes e Receitas
- REQ04: Cadastrar clientes (nome, CPF)
- REQ05: Cadastrar receitas médicas (médico prescritor, CRM, data de emissão, medicamentos prescritos)

### 3. Estoque
- REQ06: Controlar quantidade em estoque por medicamento
- REQ07: Consultar medicamentos próximos do vencimento (dentro de 60 dias)

### 4. Vendas
- REQ08: Registrar venda vinculada a um cliente, com itens de venda (composição) referenciando medicamentos e quantidades
- REQ09: Vincular uma receita a uma venda quando houver medicamento Controlado no carrinho
- REQ10: Calcular valor total da venda, aplicando desconto automático para medicamentos Genéricos

### 5. Relatórios
- REQ11: Relatório de vendas por período e por categoria de medicamento, exportável em CSV
- REQ12: Relatório de medicamentos com maior giro de estoque
- REQ13: Listar medicamentos cujo princípio ativo não foi confirmado na última consulta à API

### 6. Regras e Restrições
- REQ14: **Bloquear** a venda de medicamento Controlado sem uma receita válida vinculada
- REQ15: **Validar** que a receita não tenha mais de 30 dias entre a emissão e a data da venda
- REQ16: **Não permitir** a venda de medicamento cuja data de validade já tenha expirado
- REQ17: **Bloquear** o cadastro de medicamento cujo princípio ativo não seja encontrado na consulta à base de dados openFDA
- REQ18: **Impedir** a finalização de venda quando a quantidade solicitada exceder o estoque disponível
- REQ19: **Garantir** que cada item prescrito em uma receita seja vendido no máximo uma vez para a mesma receita
- REQ20: **Bloquear** a exclusão de medicamentos que já possuam vendas registradas


### Integrantes: 
Ana Paula Cunha Bezerra - ana.paulab@ufrpe.br
Gabriela Victoria Costa e Silva - gabriela.victoriacosta@ufrpe.br
Luanna Gomes Lucena - luanna.lucena@ufrpe.br
