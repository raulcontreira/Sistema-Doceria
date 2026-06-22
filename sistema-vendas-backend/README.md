# Sistema de Vendas — Backend (Spring Boot)

API REST protegida por autenticação JWT, usando Spring Boot + Spring
Security + Spring Data JPA + MySQL.

## Antes de rodar: configure o MySQL

Edite `src/main/resources/application.properties` e ajuste estas duas
linhas com o usuário/senha do **seu** servidor MySQL local:

```properties
spring.datasource.username=root
spring.datasource.password=
```

Não é necessário criar o banco `sistema_vendas` manualmente — o parâmetro
`createDatabaseIfNotExist=true` na URL de conexão faz isso automaticamente
na primeira conexão (desde que o usuário tenha permissão para criar bancos,
o que o `root` normalmente tem). As tabelas também são criadas/atualizadas
automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## Importar no Eclipse

**File → Import... → Maven → Existing Maven Projects**, aponte para esta
pasta (`backend-spring-boot`). O Eclipse (via m2e) baixa as dependências
do Maven Central automaticamente — é necessário ter internet na primeira
vez.

## Rodar

Pelo Eclipse: botão direito em `Main.java` → **Run As → Spring Boot App**
(ou **Java Application**, funciona igual).

Pelo terminal:
```bash
mvn spring-boot:run
```

Se tudo estiver certo, o terminal mostra os logs do Spring Boot subindo
o Tomcat embutido na porta **8080**, e a linha:
```
>> Usuario admin criado (login: admin / senha: admin123)
```

## Testando a API (exemplo com curl)

```bash
# 1. Login (gera o token JWT)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","senha":"admin123"}'

# 2. Usar o token retornado para acessar uma rota protegida
curl http://localhost:8080/api/clientes \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## Erros comuns na primeira execução

| Erro | Causa provável | Solução |
|---|---|---|
| `Communications link failure` / `Connection refused` | O servidor MySQL não está rodando | Inicie o serviço do MySQL (ex.: `mysql.server start`, ou pelo XAMPP/Workbench) |
| `Access denied for user 'root'@'localhost'` | Usuário/senha errados no `application.properties` | Ajuste `spring.datasource.username`/`password` |
| `Unknown database 'sistema_vendas'` | Usuário sem permissão para criar bancos | Crie o banco manualmente: `CREATE DATABASE sistema_vendas;` |
| Erro ao baixar dependências no Eclipse | Sem internet, ou proxy bloqueando o Maven Central | Verifique a conexão; em rede corporativa, pode ser necessário configurar um proxy no `settings.xml` do Maven |

Qualquer erro diferente desses, me mande a mensagem completa que te ajudo
a resolver.
