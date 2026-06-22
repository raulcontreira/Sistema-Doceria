Sistema Doceria

Sobre o Projeto

O Sistema Doceria é uma aplicação abrangente desenvolvida para gerenciar as operações de uma doceria, com foco principal em um backend robusto construído com Java e Spring Boot. O projeto é modular, separando as responsabilidades do backend (API REST) de um possível frontend ou módulo relacionado (sistema-vendas-ceub).

O módulo sistema-vendas-backend implementa uma API RESTful completa para operações CRUD (Create, Read, Update, Delete) em entidades como clientes, doces, usuários, vendas e itens de venda. Ele incorpora autenticação JWT (JSON Web Token) para segurança e utiliza Spring Security para controle de acesso. A arquitetura em camadas garante manutenibilidade, escalabilidade e organização do código, seguindo as melhores práticas de desenvolvimento de software.

Tecnologias Utilizadas

Backend

•
Java 17

•
Spring Boot 4.0.6: Framework para construção de aplicações Java robustas e escaláveis.

•
Spring Web: Para construção de APIs RESTful.

•
Spring Data JPA: Para persistência de dados e interação com o banco de dados.

•
Spring Security: Para autenticação e autorização via JWT.

•
Maven: Ferramenta de automação de build e gerenciamento de dependências.

Banco de Dados

•
MySQL: Sistema de gerenciamento de banco de dados relacional.

•
MySQL Connector/J: Driver JDBC para conexão com MySQL.

•
Hibernate / JPA: Implementação da especificação JPA para mapeamento objeto-relacional.

Documentação

•
SpringDoc OpenAPI / Swagger UI: Para geração automática e interativa da documentação da API.

Estrutura do Projeto

O repositório é organizado em módulos, sendo os principais:

Plain Text


Sistema-Doceria/
├── sistema-vendas-backend/  # Módulo principal do backend (API REST)
│   ├── src/main/java/com/sistema/doceria/
│   │   ├── controller/      # Controladores REST
│   │   ├── dito/            # Data Transfer Objects (DTOs)
│   │   ├── model/           # Modelos de domínio (entidades JPA)
│   │   ├── repository/      # Repositórios para acesso a dados
│   │   ├── service/         # Camada de serviço (lógica de negócio)
│   │   └── util/            # Classes utilitárias (ex: MapperUtil)
│   ├── src/main/resources/  # Arquivos de configuração (ex: application.properties)
│   └── pom.xml              # Configuração Maven do backend
├── sistema-vendas-ceub/     # Módulo relacionado (detalhes a serem explorados)
└── README.md                # Este arquivo



Configuração e Execução

Para configurar e executar o projeto localmente, siga os passos abaixo:

1. Clonar o Repositório

Bash


git clone https://github.com/raulcontreira/Sistema-Doceria.git
cd Sistema-Doceria/sistema-vendas-backend



2. Configurar o Banco de Dados MySQL

Certifique-se de ter o MySQL instalado e em execução. Crie um banco de dados chamado doceria:

SQL


CREATE DATABASE doceria;



3. Configurar application.properties

Edite o arquivo src/main/resources/application.properties dentro do módulo sistema-vendas-backend e ajuste as credenciais do seu banco de dados:

Plain Text


spring.datasource.url=jdbc:mysql://localhost:3306/doceria?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=SEU_USUARIO_MYSQL
spring.datasource.password=SUA_SENHA_MYSQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true



Observação: O parâmetro createDatabaseIfNotExist=true na URL de conexão e spring.jpa.hibernate.ddl-auto=update farão com que o banco de dados e as tabelas sejam criados/atualizados automaticamente na primeira execução, desde que o usuário MySQL tenha as permissões necessárias.

4. Executar o Projeto

Você pode executar o projeto via Maven ou IDE:

Via Maven

No diretório sistema-vendas-backend:

Bash


mvn spring-boot:run



Via IDE (IntelliJ IDEA, Eclipse, etc. )

Importe o projeto Maven sistema-vendas-backend em sua IDE. Em seguida, execute a classe principal DoceriaApplication.java.

Após a inicialização bem-sucedida, você deverá ver nos logs a mensagem de que o servidor Spring Boot está rodando na porta 8080 e que um usuário admin padrão foi criado (login: admin / senha: admin123).

Endpoints da API

A API oferece os seguintes endpoints principais:

Recurso
Método
Endpoint
Descrição
Clientes
GET
/clientes
Lista todos os clientes


GET
/clientes/{id}
Busca cliente por ID


POST
/clientes
Cadastra um novo cliente


PUT
/clientes/{id}
Atualiza um cliente existente


DELETE
/clientes/{id}
Remove um cliente
Doces
GET
/doces
Lista todos os doces


POST
/doces
Cadastra um novo doce


PUT
/doces/{id}
Atualiza um doce existente


DELETE
/doces/{id}
Remove um doce
Vendas
GET
/vendas
Lista todas as vendas


POST
/vendas
Realiza uma nova venda


DELETE
/vendas/{id}
Remove uma venda




Documentação da API (Swagger UI)

Após iniciar o projeto, a documentação interativa da API pode ser acessada em:

•
http://localhost:8080/swagger-ui.html

•
http://localhost:8080/swagger-ui/index.html

Funcionalidades

O sistema oferece as seguintes funcionalidades principais:

•
Cadastro e Gerenciamento de Clientes: CRUD completo para informações de clientes.

•
Cadastro e Gerenciamento de Doces: CRUD completo para o catálogo de produtos (doces ).

•
Cadastro e Gerenciamento de Usuários: CRUD para usuários do sistema.

•
Registro e Gerenciamento de Vendas: Criação e acompanhamento de vendas.

•
Gerenciamento de Itens de Venda: Detalhes dos produtos incluídos em cada venda.

•
Autenticação JWT: Segurança da API com tokens JWT.

•
Integração com Banco de Dados MySQL: Persistência de dados confiável.

•
Documentação Automática com Swagger: Facilita o uso e teste da API.

Conceitos Aplicados

Este projeto demonstra a aplicação de diversos conceitos e padrões de desenvolvimento de software:

•
API RESTful: Design de API seguindo os princípios REST.

•
Arquitetura em Camadas: Separação clara de responsabilidades (Controller, Service, Repository, Model).

•
Programação Orientada a Objetos (POO): Utilização de classes, objetos, herança, polimorfismo, etc.

•
JPA/Hibernate: Mapeamento Objeto-Relacional para persistência de dados.

•
Persistência de Dados: Gerenciamento do ciclo de vida dos dados no banco.

•
Relacionamento entre Entidades: Definição de relacionamentos (One-to-Many, Many-to-One, etc.) entre os modelos.

•
DTOs (Data Transfer Objects): Utilização de objetos para transferência de dados entre camadas, otimizando a comunicação e a segurança.

•
CRUD Completo: Implementação de todas as operações básicas de manipulação de dados.

Erros Comuns na Primeira Execução

| Erro | Causa Provável | Solução

Plain Text



## Referências

[1] [GitHub - raulcontreira/Sistema-Doceria](https://github.com/raulcontreira/Sistema-Doceria )
[2] [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/ )
[3] [Spring Security Documentation](https://docs.spring.io/spring-security/site/docs/current/reference/html5/ )
[4] [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/ )
[5] [MySQL Documentation](https://dev.mysql.com/doc/ )
[6] [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/ )



