# Sistema de Vendas — CEUB

Sistema de gestão de vendas com interface gráfica (Swing) e API REST
protegida por autenticação JWT, organizado em arquitetura em camadas.

## Importar no Eclipse

O projeto já vem pronto para importar, de **duas formas** (use a que
preferir — não precisa fazer as duas):

### Opção A — Projeto Eclipse nativo (`.project` / `.classpath`)
1. **File → Import... → General → Existing Projects into Workspace**
2. Em "Select root directory", aponte para a pasta `sistema-vendas-ceub`
3. Marque o projeto e clique em **Finish**

> ⚠️ `.project` e `.classpath` são arquivos que começam com ponto, então
> seu sistema operacional pode escondê-los por padrão no Explorador de
> Arquivos (Windows) ou Finder (Mac). Isso é só uma questão de
> **exibição** — eles existem no zip e o Eclipse os enxerga normalmente
> ao importar, mesmo que você não os veja navegando pelas pastas.
> Para revelá-los: no Windows, marque "Itens ocultos" na aba **Exibir**
> do Explorador; no Mac, abra a pasta no Finder e pressione
> `Cmd + Shift + .` (ponto).

### Opção B — Projeto Maven (`pom.xml`)
1. **File → Import... → Maven → Existing Maven Projects**
2. Aponte para a pasta `sistema-vendas-ceub` (onde está o `pom.xml`)
3. Clique em **Finish** (o Eclipse com m2e baixa os plugins do Maven
   Central automaticamente, requer internet na primeira vez)

Com Maven, também é possível rodar pelo terminal:
```bash
mvn compile exec:java
```

## Como compilar (sem Eclipse, sem Maven)

Pré-requisito: JDK 17 ou superior instalado (testado com JDK 21).

A partir da pasta que contém a pasta `src/`:

```bash
mkdir -p out
javac -d out -encoding UTF-8 $(find src -name "*.java")
```

## Como executar

```bash
java -cp out br.ceub.Main
```

Isso vai:
1. Criar a pasta `dados/` (se não existir) e popular alguns clientes e
   produtos de exemplo na primeira execução.
2. Subir a API REST em `http://localhost:8080`.
3. Abrir a tela de login do sistema (Swing).

**Login padrão:** usuário `admin`, senha `admin123`.

## Testando a API REST (exemplo com curl)

```bash
# 1. Login (gera o token JWT)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","senha":"admin123"}'

# 2. Usar o token retornado para acessar uma rota protegida
curl http://localhost:8080/api/clientes \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## Estrutura de pastas

```
src/br/ceub/
  model/        -> entidades (Cliente, Produto, Venda, ItemVenda, Usuario)
  repository/    -> acesso a dados (persistência em arquivo)
  service/       -> regras de negócio
  controller/    -> ponte entre as telas/API e os services
  security/      -> hash de senha e geração/validação de JWT
  util/          -> utilitários (JSON e persistência em arquivo)
  api/           -> servidor HTTP e rotas REST protegidas
  view/          -> telas Swing (JFrame)
  Main.java      -> ponto de entrada do sistema
```

Veja o arquivo `RELATORIO_EXPLICATIVO.md` para uma explicação completa
de como cada parte do sistema funciona e onde cada ação está implementada.
