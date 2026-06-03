# CryptoWallet Distribuído - Recuperação N2

Projeto prático de refatoração arquitetural do CryptoWallet, saindo de um modelo monolítico para uma comunicação distribuída em três partes:

```text
/frontend  ->  /api-gateway  ->  /data-core  ->  MySQL
 HTML/JS       JSON-RPC HTTP      Java RMI       JDBC
```

## Estrutura do projeto

```text
cryptowallet-distribuido/
├── frontend/       # SPA simples em HTML, CSS e JavaScript
├── api-gateway/    # Projeto Java Maven que recebe JSON-RPC e chama RMI
├── data-core/      # Projeto Java Maven que expõe servidor RMI e consulta MySQL
├── sql/            # Script de criação e população do banco
└── docs/           # Apoio para commits e entrega
```

## Tecnologias usadas

- Java 17
- Maven
- Java RMI
- JSON-RPC 2.0
- MySQL
- JDBC
- HTML, CSS e JavaScript puro

## 1. Configurando o banco MySQL

Crie e popule o banco executando o script abaixo no MySQL Workbench, phpMyAdmin ou terminal:

```sql
source sql/01-criar-banco.sql;
```

Ou copie e execute o conteúdo do arquivo:

```text
sql/01-criar-banco.sql
```

O script cria o banco `cryptowallet_db`, as tabelas `carteiras` e `transacoes`, e insere alguns dados de teste.

Hash principal para teste:

```text
1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa
```

## 2. Configurando usuário e senha do banco

Por padrão o `data-core` tenta conectar assim:

```text
DB_URL=jdbc:mysql://localhost:3306/cryptowallet_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=
```

Se o seu MySQL tiver senha, rode o `data-core` passando as variáveis de ambiente.

No PowerShell:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="sua_senha"
$env:DB_URL="jdbc:mysql://localhost:3306/cryptowallet_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
```

## 3. Ordem correta para iniciar os serviços

### Primeiro: DATA-CORE

O `data-core` é o servidor RMI. Ele deve subir primeiro, pois a API depende dele.

```bash
cd data-core
mvn clean package
java -jar target/data-core-1.0-SNAPSHOT.jar
```

Porta usada:

```text
RMI: 1099
```

### Segundo: API Gateway

A API Gateway recebe as chamadas JSON-RPC do frontend e chama o `data-core` via RMI.

```bash
cd api-gateway
mvn clean package
java -jar target/api-gateway-1.0-SNAPSHOT.jar
```

Porta usada:

```text
HTTP: 8080
Endpoint: http://localhost:8080/rpc
```

### Terceiro: Frontend

Abra o arquivo abaixo no navegador:

```text
frontend/index.html
```

Também pode usar a extensão Live Server do VS Code.

## 4. Exemplo de requisição JSON-RPC

Endpoint:

```text
POST http://localhost:8080/rpc
```

Payload:

```json
{
  "jsonrpc": "2.0",
  "method": "buscarCarteiraByHash",
  "params": ["1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"],
  "id": 1
}
```

## 5. Exemplo de resposta JSON-RPC com sucesso

```json
{
  "jsonrpc": "2.0",
  "result": {
    "hash": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "saldoTotalBtc": 40.75000000,
    "transacoes": [
      {
        "id": 5,
        "tipo": "ENTRADA",
        "valorBtc": 0.75000000,
        "descricao": "Ajuste de saldo recebido",
        "dataMovimentacao": "2026-06-01 13:40:00"
      }
    ]
  },
  "id": 1
}
```

## 6. Exemplo de erro quando o DATA-CORE cair

Se o `data-core` estiver desligado, a API Gateway não derruba o frontend. Ela responde com erro JSON-RPC válido:

```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32000,
    "message": "Servidor de Dados Indisponível"
  },
  "id": 1
}
```

## 7. Como testar rápido

1. Execute o SQL no MySQL.
2. Suba o `data-core`.
3. Suba a `api-gateway`.
4. Abra `frontend/index.html`.
5. Pesquise pelo hash:

```text
1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa
```

## 8. Observação sobre arquitetura

A API Gateway não consulta o banco diretamente. Ela recebe JSON-RPC, pega o parâmetro `hash` e chama o método remoto do DATA-CORE por Java RMI. A persistência fica isolada no DATA-CORE, que consulta o MySQL por JDBC.
