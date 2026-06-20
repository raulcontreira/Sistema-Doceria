# Sistema de Vendas - Doceria — CEUB

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
