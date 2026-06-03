# Sugestão de commits para entregar no GitHub

O professor pediu histórico de commits na branch `main`. Depois de descompactar o projeto, rode estes comandos dentro da pasta principal.

```bash
git init
git branch -M main
```

Commit 1:

```bash
git add README.md .gitignore sql/ docs/
git commit -m "docs: adiciona readme e script inicial do banco"
```

Commit 2:

```bash
git add data-core/
git commit -m "feat: implementa data-core com servidor rmi e mysql"
```

Commit 3:

```bash
git add api-gateway/
git commit -m "feat: implementa api gateway com json rpc e cliente rmi"
```

Commit 4:

```bash
git add frontend/
git commit -m "feat: adiciona frontend com chamada json rpc"
```

Commit 5:

```bash
git add scripts/
git commit -m "chore: adiciona scripts de inicializacao dos servicos"
```

Depois crie o repositório público no GitHub e rode:

```bash
git remote add origin https://github.com/SEU_USUARIO/cryptowallet-distribuido.git
git push -u origin main
```
