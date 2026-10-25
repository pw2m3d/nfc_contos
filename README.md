# Contos Encantados

Cada chaveiro NFC abre uma história. O primeiro toque pede o PIN da embalagem, cria a conta e prende o chaveiro a ela. Depois disso, encostar o chaveiro no celular abre o livro. Outras histórias são compradas pelo valor de cada uma e ficam na mesma conta. A ativação não pode ser repetida nem transferida.

A página ilustrada de exemplo continua na raiz do repositório: `0001 - O_patinho_feio.html`. A API só entrega esse arquivo depois de confirmar o acesso.

## Como rodar

1. Suba o PostgreSQL:

```powershell
docker compose up -d
```

2. Suba a API Java, na pasta `api`:

```powershell
.\gradlew.bat bootRun
```

3. Suba o Angular, na pasta `frontend`:

```powershell
npm start
```

Abra `http://localhost:4200`.

O chaveiro de demonstração é `http://localhost:4200/c/PW2M-0001-DEMO`. O PIN da embalagem, só para este exemplo, é `482913`.

A área de gravação fica em `http://localhost:4200/admin`. A chave local é `troque-esta-chave-admin`.

## O que gravar na tag

Grave apenas o endereço público, por exemplo `https://seu-dominio/c/PW2M-0001-AB12CD`. O PIN fica impresso na embalagem. Quem só copiar o endereço antes da ativação não consegue criar a conta. Depois da ativação, encostar a tag abre a história daquele chaveiro.

## Onde mexer

- `api` — Spring Boot, PostgreSQL e Flyway. Regras de chaveiro, biblioteca, pedido e leitura ficam em `service`.
- `frontend` — telas Angular.
- Arquivos HTML das histórias ficam na raiz, no mesmo formato do exemplo. Cadastre o nome do arquivo no painel, no campo do arquivo HTML.
- O preço de cada história fica em centavos no banco e pode ser alterado no painel.

O botão de pagamento grava o valor do pedido e libera a história. O ponto único para uma operadora futura é `OrderService.pay`: é ali que o pedido passa a pago e a história entra na conta.

A pasta `backend` é um esqueleto NestJS anterior e não faz parte deste sistema.
