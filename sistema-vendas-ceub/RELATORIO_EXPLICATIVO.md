# Relatório Explicativo — Sistema de Vendas (CEUB)

## 1. Visão geral

Este relatório explica, de forma didática, como o sistema funciona por
dentro: o que cada camada faz, onde cada requisito do projeto foi
implementado e como rastrear, no código, a execução de cada ação (por
exemplo: "o que acontece, passo a passo, quando uma venda é registrada?").

O projeto original recebido (`Main.java`, `LivroController`,
`UsuarioController`, etc.) era um cadastro de empréstimo de livros, com
várias classes incompletas (métodos retornando `null`, sem implementação,
sem persistência, sem segurança e sem interface gráfica). Ele foi
reescrito como um **Sistema de Vendas** completo, mantendo a mesma ideia
de arquitetura em camadas, mas implementando de fato todos os requisitos
pedidos.

## 2. Onde cada requisito do projeto foi implementado

| Requisito pedido | Onde está implementado |
|---|---|
| Sistema totalmente funcional | Todo o `src/`; compila e roda sem dependências externas (só o JDK) |
| Integração com Swing | Pacote `view/` — 5 telas (`TelaLogin`, `TelaPrincipal`, `TelaClientes`, `TelaProdutos`, `TelaVendas`, `TelaRelatorios`) |
| Registro de vendas funcionando | `service/VendaService.registrarVenda(...)` + `view/TelaVendas` + `api/handlers/VendaHandler` |
| Controle de estoque | `service/ProdutoService.baixarEstoque(...)` e `.reporEstoque(...)`, chamados pelo `VendaService` |
| Relatórios de vendas | `service/RelatorioService` + `view/TelaRelatorios` + `api/handlers/RelatorioHandler` |
| API protegida | `api/HandlerProtegido` (valida o token JWT antes de qualquer rota) |
| CRUD de clientes e produtos | `service/ClienteService` + `service/ProdutoService`, expostos pelas telas e pela API |
| Autenticação com JWT | `security/JwtUtil` (gera e valida o token) |
| API REST funcional | `api/ApiServer` (usa `com.sun.net.httpserver.HttpServer`, nativo do Java) |
| Autenticação baseada em Token | Header HTTP `Authorization: Bearer <token>`, conferido em `HandlerProtegido` |
| Arquitetura em camadas | `model` → `repository` → `service` → `controller` → (`view` ou `api`) |
| Organização e legibilidade | Pacotes separados por responsabilidade, nomes em português consistentes, comentários Javadoc em cada classe |
| Interfaces com JFrame (telas independentes) | Cada tela do pacote `view/` estende `JFrame` e pode ser aberta isoladamente |

## 3. Arquitetura em camadas (visão macro)

```
 [ Tela Swing ]        [ Cliente HTTP / Postman ]
        |                          |
        v                          v
  Controller   <----------->   API Handler (HandlerProtegido)
        |                          |
        +------------+-------------+
                     |
                     v
                 Service          (regras de negócio)
                     |
                     v
                Repository        (acesso a dados)
                     |
                     v
           Arquivo em disco (pasta dados/)
```

Cada camada só conhece a camada imediatamente abaixo dela:

- **View** (`view/`) e **API Handlers** (`api/handlers/`) só conversam com
  **Controllers**.
- **Controllers** (`controller/`) só repassam chamadas para **Services**;
  não têm nenhuma regra de negócio própria.
- **Services** (`service/`) contêm as regras de negócio (validações,
  cálculos, baixa de estoque) e conversam com **Repositories**.
- **Repositories** (`repository/`) são os únicos que sabem como os dados
  são guardados (hoje: em memória + arquivo binário na pasta `dados/`).

Essa separação permite, por exemplo, trocar a forma de persistência
(arquivo → banco de dados real) alterando **somente** a camada de
repository, sem tocar em service, controller, view ou API.

## 4. Pacote por pacote

### 4.1 `br.ceub.model` — As entidades do sistema

- **`Cliente.java`** — id, nome, CPF, e-mail, telefone.
- **`Produto.java`** — id, nome, descrição, categoria, preço,
  `quantidadeEstoque`, `estoqueMinimo`. O método `isEstoqueBaixo()`
  retorna `true` quando o estoque está no nível mínimo ou abaixo dele —
  é o que alimenta o alerta visual em vermelho na tela de produtos.
- **`Venda.java`** — id, cliente, data/hora, lista de `ItemVenda`,
  `valorTotal` e `status` (`CONCLUIDA` ou `CANCELADA`). O método
  `recalcularTotal()` soma o subtotal de cada item sempre que a lista de
  itens muda.
- **`ItemVenda.java`** — representa uma linha da venda (produto +
  quantidade + preço unitário "congelado" no momento da venda, para que
  alterações futuras no preço do produto não afetem vendas já registradas).
- **`Usuario.java`** — usuário do sistema (quem faz login), com
  `senhaHash` e `salt` (nunca a senha em texto puro) e um `Perfil`
  (`ADMIN` ou `VENDEDOR`).

### 4.2 `br.ceub.repository` — Acesso a dados

Cada repository (`ClienteRepository`, `ProdutoRepository`,
`VendaRepository`, `UsuarioRepository`) guarda seus dados em um
`Map<Integer, T>` **estático** (compartilhado por toda a aplicação dentro
da mesma execução) e usa `util/ArquivoPersistenciaUtil` para salvar/carregar
esse mapa em um arquivo binário na pasta `dados/` (por exemplo,
`dados/produtos.dat`). Isso faz o sistema lembrar dos cadastros mesmo
depois de fechado e reaberto — é o que torna o sistema "totalmente
funcional" e não apenas uma simulação em memória que se perde ao fechar.

> **Por que `static`?** Tanto a tela Swing quanto a API REST criam suas
> próprias instâncias de Controller/Service/Repository. Usar campos
> `static` no repository garante que todas essas instâncias, dentro da
> mesma execução do programa, enxerguem exatamente o mesmo estoque/
> cadastro em memória — sem isso, uma venda feita pela tela poderia não
> refletir no estoque consultado pela API, por exemplo.

### 4.3 `br.ceub.service` — Regras de negócio

- **`ClienteService`** — valida nome obrigatório e impede CPF/e-mail
  duplicados antes de cadastrar ou atualizar um cliente.
- **`ProdutoService`** — valida os campos do produto e contém os dois
  métodos centrais do **controle de estoque**:
  - `baixarEstoque(produtoId, quantidade)` — reduz o estoque; lança
    `RegraNegocioException` se não houver quantidade suficiente.
  - `reporEstoque(produtoId, quantidade)` — devolve unidades ao estoque
    (usado no cancelamento de uma venda).
- **`VendaService`** — o coração do **registro de vendas**. O método
  `registrarVenda(clienteId, pedidos)` faz, nesta ordem:
  1. Busca o cliente (erro se não existir);
  2. Confere, **para todos os itens primeiro**, se há estoque suficiente
     (evita "baixar estoque pela metade" se um item no meio da lista
     falhar);
  3. Só então baixa o estoque de cada produto, chamando `ProdutoService`;
  4. Monta a `Venda` com os `ItemVenda` (nome e preço "congelados") e
     salva no `VendaRepository`.
  - `cancelarVenda(vendaId)` devolve as quantidades ao estoque e marca a
    venda como `CANCELADA`.
- **`RelatorioService`** — gera o **relatório de vendas**: quantidade de
  vendas concluídas, faturamento total, ticket médio e o ranking de
  produtos mais vendidos (ordenado por quantidade vendida).
- **`AutenticacaoService`** — faz login (confere usuário/senha com
  `security/PasswordUtil` e gera o token com `security/JwtUtil`) e
  cadastra novos usuários do sistema. Cria automaticamente o usuário
  `admin`/`admin123` na primeira execução, para o sistema já nascer
  acessível.
- **`RegraNegocioException`** — exceção única usada em toda a camada de
  serviço; tratada de forma diferente pela tela Swing (mostra um
  `JOptionPane`) e pela API (responde HTTP 400 em JSON).

### 4.4 `br.ceub.security` — Autenticação e token JWT

- **`PasswordUtil.java`** — gera um `salt` aleatório por usuário e calcula
  o hash da senha com SHA-256 (`MessageDigest`, da própria biblioteca
  padrão do Java). A senha em texto puro nunca é armazenada.
- **`JwtUtil.java`** — implementação manual do padrão **JWT** (formato
  `header.payload.assinatura`, todos em Base64URL), usando HMAC-SHA256
  (`javax.crypto.Mac`, também nativo do Java):
  - `gerarToken(login, nome, perfil)` — monta o token com data de emissão
    (`iat`) e expiração (`exp`, 8 horas de validade).
  - `validarToken(token)` — confere formato, assinatura e expiração;
    devolve um `TokenInfo` dizendo se é válido e, se for, quem é o
    usuário autenticado.

> **Nota didática:** normalmente um projeto Java usaria uma biblioteca
> pronta (ex.: `io.jsonwebtoken`/JJWT) para gerar JWT. Como este ambiente
> de desenvolvimento não tem acesso a um gerenciador de dependências
> (Maven/Gradle) nem à internet para baixar `.jar`s externos, o JWT foi
> implementado manualmente, seguindo exatamente a especificação (RFC
> 7519), usando apenas classes que já vêm dentro do JDK. O comportamento
> e a segurança (assinatura HMAC, expiração) são equivalentes aos de uma
> biblioteca pronta.

### 4.5 `br.ceub.api` — A API REST protegida

- **`ApiServer.java`** — sobe um `HttpServer` (classe nativa do JDK, em
  `com.sun.net.httpserver`) na porta **8080** e registra as rotas.
- **`HandlerProtegido.java`** — classe abstrata que **todo** handler
  protegido estende. Antes de executar a rota, ela lê o cabeçalho
  `Authorization: Bearer <token>`, chama `JwtUtil.validarToken(...)` e só
  deixa a requisição prosseguir se o token for válido — senão, responde
  HTTP `401 Unauthorized`. É exatamente esse mecanismo que cumpre o
  requisito **"API protegida"** e **"Autenticação baseada em Token"**.
- **`api/handlers/`** — um handler por recurso:
  - `AuthHandler` — única rota **pública**: `POST /api/auth/login`.
  - `ClienteHandler` — CRUD de clientes via `GET/POST/PUT/DELETE /api/clientes`.
  - `ProdutoHandler` — CRUD de produtos e `GET /api/produtos/estoque-baixo`.
  - `VendaHandler` — `POST /api/vendas` (registrar) e
    `POST /api/vendas/{id}/cancelar`.
  - `RelatorioHandler` — `GET /api/relatorios/vendas`.
- **`util/JsonUtil.java`** — conversor de JSON escrito apenas com a
  biblioteca padrão do Java (sem Gson/Jackson, pelo mesmo motivo do JWT).
  Quando a API devolve um objeto de modelo (`Cliente`, `Produto`,
  `Venda`...), o `JsonUtil` usa *reflection* para ler todos os métodos
  `getXxx()`/`isXxx()` do objeto e montar um JSON real com os campos —
  por isso a resposta da API é, por exemplo,
  `{"id":1,"nome":"Ana Pereira",...}` e não um texto solto.

### 4.6 `br.ceub.controller` — A ponte entre telas/API e os services

Os controllers (`AuthController`, `ClienteController`,
`ProdutoController`, `VendaController`, `RelatorioController`) são
intencionalmente "burros": cada método só repassa a chamada para o
service correspondente. Eles existem para que **a mesma lógica** sirva
tanto à tela Swing quanto à API REST, sem duplicar regra de negócio em
dois lugares.

### 4.7 `br.ceub.view` — As telas Swing (JFrame)

Cada tela é um `JFrame` independente (pode ser aberta, movida e fechada
separadamente das outras):

- **`TelaLogin`** — primeira tela exibida. Chama `AuthController.login(...)`
  (mesma lógica usada pela API) e, se a autenticação for bem-sucedida,
  abre a `TelaPrincipal` com o token da sessão.
- **`TelaPrincipal`** — menu com botões para abrir cada uma das telas de
  funcionalidade.
- **`TelaClientes`** — CRUD de clientes com tabela (`JTable`) e formulário.
  Clicar em uma linha preenche o formulário para editar; "Excluir" remove
  o cliente selecionado.
- **`TelaProdutos`** — CRUD de produtos e controle de estoque. A coluna
  "Estoque" fica **vermelha** quando o produto está com estoque baixo
  (usa `Produto.isEstoqueBaixo()`). Tem um botão para filtrar e mostrar
  só os produtos com estoque baixo.
- **`TelaVendas`** — tela de registro de vendas: o operador monta um
  "carrinho" (adicionando produto + quantidade), informa o cliente e
  clica em "Finalizar venda", que chama
  `VendaController.registrarVenda(...)`. Uma tabela inferior mostra o
  histórico de vendas; dar duplo clique em uma venda permite cancelá-la
  (devolvendo os itens ao estoque).
- **`TelaRelatorios`** — mostra o resumo de vendas (quantidade,
  faturamento total, ticket médio) e a tabela de produtos mais vendidos,
  gerados pelo `RelatorioController`.

### 4.8 `br.ceub.Main` — Ponto de entrada

Ao executar `java -cp out br.ceub.Main`, o programa:
1. Cadastra alguns clientes/produtos de exemplo, **apenas na primeira
   execução** (quando ainda não existem dados salvos);
2. Sobe a API REST em uma thread separada (porta 8080), para não travar
   a abertura da tela;
3. Abre a `TelaLogin` na *Event Dispatch Thread* do Swing (prática
   recomendada pela própria documentação do Java para interfaces
   gráficas).

## 5. Passo a passo de uma venda (do clique ao banco de dados)

Para entender como as camadas conversam entre si, veja o que acontece
quando alguém registra uma venda pela tela Swing:

1. **`TelaVendas.finalizarVenda()`** lê o id do cliente e a lista de
   itens do "carrinho" (`List<ItemPedido>`).
2. Chama **`VendaController.registrarVenda(clienteId, itens)`**, que
   apenas repassa para...
3. **`VendaService.registrarVenda(...)`**, que:
   - busca o cliente via `ClienteService.buscarPorId(...)`;
   - confere o estoque de cada produto via
     `ProdutoService.buscarPorId(...)`;
   - chama `ProdutoService.baixarEstoque(...)` para cada item (isso
     atualiza o `Produto` e persiste a mudança através do
     `ProdutoRepository`);
   - monta a `Venda` e chama `VendaRepository.salvar(...)`, que grava o
     mapa de vendas atualizado em `dados/vendas.dat`.
4. O resultado (`Venda` registrada) volta para a tela, que mostra uma
   mensagem de sucesso e atualiza a tabela de histórico.

O mesmíssimo caminho (passos 3 e 4) acontece quando a venda é registrada
via API REST: o `VendaHandler` apenas troca o passo 1-2 por "ler o JSON
da requisição HTTP e chamar o `VendaController`" — a partir daí, a lógica
é idêntica.

## 6. Como testar rapidamente

**Pela tela:** rode o sistema, faça login com `admin`/`admin123`, abra
"Produtos e Estoque" e cadastre um produto, abra "Vendas", monte um
carrinho com esse produto e finalize a venda — o estoque do produto
diminui automaticamente. Depois, abra "Relatórios de Vendas" para ver o
faturamento.

**Pela API**, com a aplicação rodando:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","senha":"admin123"}'
# copie o "token" da resposta e use abaixo:

curl http://localhost:8080/api/produtos \
  -H "Authorization: Bearer <token>"
```

## 7. Possíveis evoluções futuras (fora do escopo atual)

- Trocar a persistência em arquivo por um banco de dados real (MySQL/
  PostgreSQL) via JDBC, alterando apenas os Repositories.
- Adicionar Maven/Gradle ao projeto e substituir o JWT/JSON manuais por
  bibliotecas consolidadas (JJWT, Gson), sem alterar o restante das
  camadas.
- Adicionar perfis de acesso (ex.: só `ADMIN` pode excluir produtos),
  usando o campo `perfil` que já vem no `TokenInfo` retornado pelo JWT.
