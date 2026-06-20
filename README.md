# Sistema de Gerenciamento de Biblioteca - Arquitetura em Camadas

## 📚 Objetivo Educacional

**Como usar este projeto:**
- As **assinaturas dos métodos** e a **estrutura das classes** já estão prontas
- Os **métodos estão vazios** e precisam ser **implementados pelos alunos**
- Siga a documentação abaixo como guia de implementação
- Use o exemplo em `Main.java` como referência de uso

Através deste projeto, você aprenderá:
- Como separar responsabilidades em diferentes camadas (Controller → Service → Repository)
- O padrão de design Repository para acesso a dados
- A implementação de lógica de negócio em Service Layer
- Boas práticas de organização e estrutura de código
- Como implementar um CRUD completo com validações
- Relacionamento entre entidades (Usuário ↔ Livro ↔ Empréstimo)
- Controle de inventário e regras de negócio complexas

## 🏗️ Estrutura da Arquitetura em Camadas

A aplicação é dividida em três camadas principais:

```
┌─────────────────────────────────────┐
│     CAMADA DE APRESENTAÇÃO          │
│     (LivroController)             │
│     - Recebe requisições            │
│     - Controla o fluxo da aplicação │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     CAMADA DE NEGÓCIOS              │
│     (LivroService)                │
│     - Lógica de negócio             │
│     - Regras de validação           │
│     - Processamento de dados        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     CAMADA DE PERSISTÊNCIA          │
│     (LivroRepository)             │
│     - Acesso aos dados              │
│     - Operações no banco de dados   │
└─────────────────────────────────────┘
```

## 📁 Estrutura de Arquivos

```
arquitetura_camadas/
├── Livro.java                 # Entidade (Model)
├── LivroController.java       # Camada de Apresentação
├── LivroService.java          # Interface - Camada de Negócios
├── LivroRepository.java       # Interface - Camada de Persistência
├── Main.java                    # Ponto de entrada da aplicação
└── README.md                    # Este arquivo
```

## 🔍 Descrição das Camadas

### ENTIDADES (Models)

#### 📖 Livro.java
- `id` - Identificador único
- `nome` - Nome do livro
- `autor` - Autor
- `edicao` - Edição
- `quantidade` - Quantidade em estoque

#### 👤 Usuario.java
- `id` - Identificador único
- `nome` - Nome completo
- `email` - Email (único)
- `cpf` - CPF (único)
- `telefone` - Número de telefone

#### 📋 Emprestimo.java
- `id` - Identificador único do empréstimo
- `usuarioId` - Referência ao usuário
- `livroId` - Referência ao livro
- `dataEmprestimo` - Quando o livro foi pego
- `dataDevolucaoPrevista` - Data prevista de devolução
- `dataDevoluçaoReal` - Data real de devolução
- `ativo` - Se o empréstimo está em andamento

---

### 1️⃣ Camada de Apresentação (Controllers)

#### LivroController.java
- **Responsabilidade**: Intermediar requisições de livros
- **Métodos**:
  - `cadastrar(Livro)` - Cria novo livro
  - `buscarPorId(int)` - Busca por ID
  - `buscarPorNome(String)` - Busca por nome
  - `listarTodos()` - Lista todos os livros
  - `alterar(Livro)` - Atualiza livro
  - `remover(int)` - Remove livro

#### UsuarioController.java
- **Responsabilidade**: Intermediar requisições de usuários
- **Métodos**:
  - `cadastrar(Usuario)` - Cria novo usuário
  - `buscarPorId(int)` - Busca por ID
  - `buscarPorCpf(String)` - Busca por CPF
  - `buscarPorEmail(String)` - Busca por email
  - `buscarPorNome(String)` - Busca por nome
  - `listarTodos()` - Lista todos os usuários
  - `alterar(Usuario)` - Atualiza usuário
  - `remover(int)` - Remove usuário

---

### 2️⃣ Camada de Negócios (Services)

#### LivroService.java
- **Responsabilidade**: Lógica de negócio para livros
- **Validações**:
  - ID deve ser > 0
  - Nome não pode ser vazio
- **Métodos**: CRUD similar ao controller

#### UsuarioService.java
- **Responsabilidade**: Lógica de negócio para usuários
- **Validações**:
  - ID deve ser > 0
  - CPF deve ser único
  - Email deve ser único
  - Nome não pode ser vazio
- **Métodos especiais**:
  - Busca por CPF
  - Busca por Email

#### EmprestimoService.java ⭐ (Lógica Complexa)
- **Responsabilidade**: Gerenciar empréstimos com regras de negócio
- **Operações principais**:
  - `realizarEmprestimo(usuarioId, livroId, dias)`
    - Valida usuário e livro
    - Verifica disponibilidade
    - **Decrementa quantidade de livros**
    - Registra data de devolução
    - Previne mesmo livro emprestado 2x
  - `devolverLivro(emprestimoId)`
    - Marca empréstimo como finalizado
    - **Incrementa quantidade de livros**
    - Registra data real de devolução
  - `verificarAtraso(emprestimoId)` - Verifica se passou da data
  - Listagens e buscas por usuário

---

### 3️⃣ Camada de Persistência (Repositories)

#### LivroRepository.java
- **Responsabilidade**: Operações CRUD para livros
- **Estrutura**: HashMap em memória
- **Métodos**:
  - `salvar(Livro)`, `buscarPorId(int)`, `buscarPorNome(String)`
  - `listarTodos()`, `atualizar(Livro)`, `deletar(int)`

#### UsuarioRepository.java
- **Responsabilidade**: Operações CRUD para usuários
- **Métodos especiais**:
  - `buscarPorCpf(String)` - Busca CPF para validação
  - `buscarPorEmail(String)` - Busca email para validação
  - `buscarPorNome(String)` - Busca parcial por nome

#### EmprestimoRepository.java
- **Responsabilidade**: Operações CRUD para empréstimos
- **Métodos especiais**:
  - `buscarPorUsuario(int)` - Todos os empréstimos de um usuário
  - `buscarEmprestimosAtivos(int)` - Apenas empréstimos em andamento
  - `listarEmprestimosAtivos()` - Todos os empréstimos ativos do sistema

## ✅ Funcionalidades a Implementar

### Gestão de Livros
- ⬜ Cadastro, busca por ID/nome, listagem, alteração e remoção
- ⬜ Controle automático de quantidade disponível
- ⬜ Validação de dados obrigatórios

### Gestão de Usuários
- ⬜ Cadastro com validação de CPF/Email únicos
- ⬜ Busca por ID, CPF, Email e nome
- ⬜ Listagem, alteração e remoção de usuários
- ⬜ Validações de integridade referencial

### Gestão de Empréstimos
- ⬜ Empréstimo de livro com cálculo automático de devolução
- ⬜ Devolução com atualização de inventário
- ⬜ Verificação de atrasos
- ⬜ Listagem de empréstimos por usuário e status
- ⬜ Prevenção de duplicatas (mesmo livro 2x para mesmo usuário)
- ⬜ Validação de disponibilidade

### Estrutura de Dados
Use a estrutura de dados apropriada (HashMap, ArrayList, etc.) para implementar o armazenamento em memória.

## 📖 Exemplo de Uso
Observe o arquivo `Main.java` que contém um exemplo de como as classes devem ser utilizadas após a implementação. Execute-o após implementar todos os métodos para validar seu trabalho.

## 💡 Fluxo de Empréstimo e Devolução

### Novo Empréstimo
1. Usuário acessa `EmprestimoService.realizarEmprestimo(usuarioId, livroId, dias)`
2. Service deve validar:
   - ✓ Usuário existe (use UsuarioRepository)
   - ✓ Livro existe (use LivroRepository)
   - ✓ Livro tem quantidade > 0
   - ✓ Usuário não tem este livro emprestado já
3. Se válido:
   - Decrementa quantidade do livro
   - Cria registro de Empréstimo com ID único
   - Data de devolução = hoje + dias (use `LocalDate.now().plusDays()`)
4. Retorna empréstimo

### Devolução
1. Usuário acessa `EmprestimoService.devolverLivro(emprestimoId)`
2. Service deve:
   - Validar se empréstimo existe
   - Validar se ainda está ativo
   - Marcar empréstimo como finalizado (setAtivo(false))
   - Incrementa quantidade do livro
   - Registra data real de devolução

### Verificação de Atrasos
- `EmprestimoService.verificarAtraso(emprestimoId)`
- Retorna `true` se data atual > data prevista E empréstimo ainda está ativo
- Retorna `false` se já foi devolvido

---

## 🚀 Guia de Implementação - Passo a Passo

### 1️⃣ Implementar Repositories (Camada de Persistência)
Comece pelos repositories, usando `HashMap<Integer, Objeto>` para armazenar dados:

**LivroRepository.java**
- Crie um `static Map<Integer, Livro> banco = new HashMap<>()`
- `salvar()`: adicionar ao lista
- `buscarPorId()`: recuperar do lista
- `buscarPorNome()`: iterar e comparar (case-insensitive)
- `listarTodos()`: retornar lista com os objetos
- `atualizar()`: atualizar no lista
- `deletar()`: remover do lista

**UsuarioRepository.java**
- Similar ao LivroRepository
- `buscarPorCpf()`: iterar e comparar CPF
- `buscarPorEmail()`: iterar e comparar email (case-insensitive)
- `buscarPorNome()`: iterar e comparar nome (case-insensitive, busca parcial)

**EmprestimoRepository.java**
- Similar aos anteriores
- `buscarPorUsuario()`: filtrar por usuarioId
- `buscarEmprestimosAtivos()`: filtrar por usuarioId && ativo == true
- `listarEmprestimosAtivos()`: filtrar por ativo == true

### 2️⃣ Implementar Services (Camada de Negócio)
Services contêm a lógica de validação e chamam os repositories:

**LivroService.java**
- `cadastrar()`: validar ID > 0, nome não vazio, depois chamar repository.salvar()
- `buscarPorId()`: delegar ao repository
- `buscarPorNome()`: validar nome não vazio, depois chamar repository
- `listarTodos()`: delegar ao repository
- `alterar()`: validar se livro existe, depois chamar repository.atualizar()
- `remover()`: validar se livro existe, depois chamar repository.deletar()

**UsuarioService.java**
- `cadastrar()`: validações completas:
  - ID > 0
  - Nome não vazio
  - CPF único (verificar com buscarPorCpf)
  - Email único (verificar com buscarPorEmail)
  - Depois chamar repository.salvar()
- `buscarPorCpf()` / `buscarPorEmail()`: validar string não vazia, delegar ao repository
- `buscarPorNome()`: validar nome não vazio, delegar ao repository
- `alterar()` / `remover()`: validar existência antes de atualizar/deletar

**EmprestimoService.java** (mais complexo!)
- `realizarEmprestimo()`:
  - Validar usuário existe
  - Validar livro existe
  - Validar livro.quantidade > 0
  - Validar usuário não tem este livro emprestado (buscarEmprestimosAtivos)
  - Decrementar livro.quantidade
  - Atualizar livro no repository
  - Criar novo Emprestimo e salvar
  - Retornar empréstimo
- `devolverLivro()`:
  - Validar empréstimo existe
  - Validar empréstimo.isAtivo()
  - Marcar como inativo
  - Incrementar quantidade do livro
  - Registrar data real de devolução
- `verificarAtraso()`: comparar LocalDate.now() com dataDevolucaoPrevista
- Outros métodos: delegar ao repository

### 3️⃣ Implementar Controllers (Camada de Apresentação)
Controllers são simples - apenas delegam para o service:

**LivroController.java**
- Todos os métodos chamam o serviço correspondente

**UsuarioController.java**
- Todos os métodos chamam o serviço correspondente

### 4️⃣ Testar com Main.java
- Depois de implementar, execute `java Main`
- O programa deve rodar sem erros e demonstrar todas as funcionalidades

---

## ⚠️ Dicas Importantes

1. **Use List**: A persistência é em memória, use `List<Objeto>`
2. **ID única**: Use um contador estático em EmprestimoService para gerar IDs únicos
3. **Coloque try-catch em Main.java**: As validações vão lançar exceções se dados inválidos
4. **LocalDate.now()**: Use para pegar data/hora atual em empréstimos
5. **Estudar os métodos herdados de String**: `equals()`, `equalsIgnoreCase()`, `contains()`, `trim()`
6. **Retorne NOT NULL**: Controllers que retornam objetos devem delegar ao service
7. **Mantenha a estrutura**: Não modifique assinaturas de métodos, apenas implemente

## ✅ Checklist de Implementação

- [ ] LivroRepository totalmente implementado
- [ ] UsuarioRepository totalmente implementado
- [ ] EmprestimoRepository totalmente implementado
- [ ] LivroService totalmente implementado
- [ ] UsuarioService totalmente implementado
- [ ] EmprestimoService totalmente implementado
- [ ] LivroController totalmente implementado
- [ ] UsuarioController totalmente implementado
- [ ] Executa `java Main` sem erros
- [ ] Todas as funcionalidades funcionam conforme esperado

---  

## 🔧 Padrões de Design Utilizados

- **MVC (Model-View-Controller)**: Separação clara de responsabilidades
- **Repository Pattern**: Abstração da camada de dados
- **Service Layer Pattern**: Consolidação de lógica de negócio
- **Dependency Injection**: Injeção de dependências nos construtores
- **Singleton Pattern**: Variáveis estáticas para persistência em memória

## 📚 Conceitos Educacionais Demonstrados

1. **Separação de Camadas** - Cada camada tem uma responsabilidade clara
2. **Encapsulamento** - Dados privados acessados por getters/setters
3. **Validações** - Regras de negócio implementadas na camada de serviço
4. **Relacionamentos** - Entidades podem referenciar outras entidades
5. **Coleções** - Uso de List, Map, HashMap para armazenamento
6. **Exceções** - Uso de IllegalArgumentException para validações
7. **Datas** - Manipulação com LocalDate
8. **Lógica Complexa** - Integração de múltiplas entidades

## 🚀 Melhorias Futuras Possíveis

- [ ] Persistência em banco de dados (JDBC, JPA/Hibernate)
- [ ] Interface gráfica (Swing, JavaFX)
- [ ] API REST (Spring Boot)
- [ ] Testes unitários (JUnit)
- [ ] Sistema de multas para atrasos
- [ ] Reservas de livros
- [ ] Renovação de empréstimos
- [ ] Relatórios de uso (livros mais emprestados, etc)
- [ ] Sistema de notificações
- [ ] Autenticação de usuários
- [ ] Dashboard de estatísticas

---

**Este é um projeto de aprendizado. Sinta-se livre para experimentar, modificar e evoluir o código!** 🎓

**Data de criação**: Março 2026  
**Disciplina**: Desenvolvimento de Sistemas em Camadas  
**Instituição**: CEUB
