# Explicação rápida da arquitetura

O projeto foi separado em três partes para diminuir o acoplamento do sistema original.

## Frontend

Fica na pasta `/frontend` e é uma SPA simples. Ele não chama mais uma rota REST como `GET /carteira/{hash}`. Agora ele envia uma requisição `POST` para a API Gateway usando o formato JSON-RPC 2.0.

## API Gateway

Fica na pasta `/api-gateway`. É a porta de entrada do sistema na porta `8080`. Essa camada recebe a requisição JSON-RPC, confere o método solicitado e chama o DATA-CORE usando Java RMI.

Ela não acessa o MySQL diretamente.

## DATA-CORE

Fica na pasta `/data-core`. Ele é um servidor RMI registrado na porta `1099`. Esse módulo concentra a parte de persistência e consulta do banco. Quando recebe o hash, consulta o MySQL e retorna o saldo total em BTC com o histórico das últimas transações.

## Tolerância a falhas

Se o DATA-CORE estiver fora do ar, a API Gateway captura a falha de RMI e responde com erro JSON-RPC válido:

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

Assim o frontend continua funcionando e consegue mostrar a mensagem de erro sem quebrar a tela.
