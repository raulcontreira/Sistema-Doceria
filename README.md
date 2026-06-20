Doceria CRUD API

Sobre o Projeto

O Doceria CRUD API é um sistema backend desenvolvido em Java utilizando Spring Boot com foco em operações CRUD para gerenciamento de uma doceria.

O projeto foi estruturado utilizando arquitetura em camadas, separando responsabilidades entre controllers, services, repositories e models, facilitando manutenção, escalabilidade e organização do código.

A aplicação possui gerenciamento de:

Clientes
Doces
Usuários
Vendas
Itens de venda
Além disso, o sistema utiliza DTOs para transferência de dados e uma classe utilitária (MapperUtil) para conversão entre objetos.

Tecnologias Utilizadas

Backend

Java 17
Spring Boot 4.0.6
Spring Web
Spring Data JPA
Maven
Banco de Dados

MySQL Connector/J
Hibernate / JPA
Documentação

SpringDoc OpenAPI
Swagger UI
Estrutura Real do Projeto

src/main/java/com/sistema/doceria
│
├── controller
│   ├── ClienteController.java
│   ├── DoceController.java
│   ├── ItemVendaController.java
│   ├── UsuarioController.java
│   └── VendaController.java
│
├── dito
│   ├── ClienteDTO.java
│   └── ProdutoDTO.java
│
├── model
│   ├── Cliente.java
│   ├── Doce.java
│   ├── ItemVenda.java
│   ├── Usuario.java
│   └── Venda.java
│
├── repository
│   ├── ClienteRepository.java
│   ├── DoceRepository.java
│   ├── ItemVendaRepository.java
│   ├── UsuarioRepository.java
│   └── VendaRepository.java
│
├── service
│   ├── ClienteService.java
│   ├── DoceService.java
│   ├── ItemVendaService.java
│   ├── UsuarioService.java
│   └── VendaService.java
│
├── util
│   └── MapperUtil.java
│
└── DoceriaApplication.java
```bash
Configuração do Projeto

Clonar o Repositório

git clone <URL_DO_REPOSITORIO>
Configurar o Banco de Dados

Crie um banco de dados MySQL chamado:

CREATE DATABASE doceria;
Configurar o arquivo application.properties

Localização:

src/main/resources/application.properties
Exemplo:

spring.datasource.url=jdbc:mysql://localhost:3306/doceria
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
Executando o Projeto

Via Maven

mvn spring-boot:run
Via IDE

Execute a classe:

DoceriaApplication.java
Endpoints da API

Cliente

Método	Endpoint	Descrição
GET	/clientes	Lista todos os clientes
GET	/clientes/{id}	Busca cliente por ID
POST	/clientes	Cadastra cliente
PUT	/clientes/{id}	Atualiza cliente
DELETE	/clientes/{id}	Remove cliente
Doces

Método	Endpoint	Descrição
GET	/doces	Lista doces
POST	/doces	Cadastra doce
PUT	/doces/{id}	Atualiza doce
DELETE	/doces/{id}	Remove doce
Vendas

Método	Endpoint	Descrição
GET	/vendas	Lista vendas
POST	/vendas	Realiza venda
DELETE	/vendas/{id}	Remove venda
Swagger

Após iniciar o projeto, a documentação pode ser acessada em:

http://localhost:8080/swagger-ui.html
ou

http://localhost:8080/swagger-ui/index.html
Funcionalidades

Cadastro de clientes

Cadastro de doces

Cadastro de usuários

Registro de vendas

Gerenciamento de itens de venda

Operações CRUD completas

Integração com banco MySQL

Documentação automática com Swagger

Conceitos Aplicados

API REST
Arquitetura em Camadas
Programação Orientada a Objetos
JPA/Hibernate
Persistência de Dados
Relacionamento entre entidades
DTOs
CRUD completo
