const API_URL = "http://localhost:8080/rpc";

const form = document.getElementById("form-consulta");
const inputHash = document.getElementById("hash");
const mensagem = document.getElementById("mensagem");
const resultado = document.getElementById("resultado");
const saldoTotal = document.getElementById("saldo-total");
const hashConsultado = document.getElementById("hash-consultado");
const listaTransacoes = document.getElementById("lista-transacoes");
const botao = form.querySelector("button");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const hash = inputHash.value.trim();
    if (!hash) {
        exibirErro("Informe o hash da carteira.");
        return;
    }

    const payload = {
        jsonrpc: "2.0",
        method: "buscarCarteiraByHash",
        params: [hash],
        id: Date.now()
    };

    try {
        botao.disabled = true;
        botao.textContent = "Buscando...";
        esconderErro();
        resultado.classList.add("escondido");

        const respostaHttp = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const respostaRpc = await respostaHttp.json();

        if (respostaRpc.error) {
            exibirErro(`${respostaRpc.error.message} (código ${respostaRpc.error.code})`);
            return;
        }

        preencherResultado(respostaRpc.result);
    } catch (erro) {
        exibirErro("Não foi possível conectar na API Gateway. Confira se ela está rodando na porta 8080.");
    } finally {
        botao.disabled = false;
        botao.textContent = "Buscar";
    }
});

function preencherResultado(carteira) {
    saldoTotal.textContent = `${Number(carteira.saldoTotalBtc).toFixed(8)} BTC`;
    hashConsultado.textContent = carteira.hash;
    listaTransacoes.innerHTML = "";

    if (!carteira.transacoes || carteira.transacoes.length === 0) {
        listaTransacoes.innerHTML = `
            <tr>
                <td colspan="4">Nenhuma transação encontrada para esse hash.</td>
            </tr>
        `;
    } else {
        carteira.transacoes.forEach((transacao) => {
            const tr = document.createElement("tr");
            const classeTipo = transacao.tipo === "ENTRADA" ? "tipo-entrada" : "tipo-saida";

            tr.innerHTML = `
                <td>${transacao.dataMovimentacao}</td>
                <td class="${classeTipo}">${transacao.tipo}</td>
                <td>${Number(transacao.valorBtc).toFixed(8)}</td>
                <td>${transacao.descricao}</td>
            `;

            listaTransacoes.appendChild(tr);
        });
    }

    resultado.classList.remove("escondido");
}

function exibirErro(texto) {
    mensagem.textContent = texto;
    mensagem.classList.remove("escondido");
}

function esconderErro() {
    mensagem.textContent = "";
    mensagem.classList.add("escondido");
}
