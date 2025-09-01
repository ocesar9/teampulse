# TeamPulse 👥

[![NPM](https://img.shields.io/npm/l/react)](https://github.com/ocesar9/teampulse/blob/main/LICENSE)


## 📋 Sobre o Projeto

**TeamPulse** é uma plataforma completa de **gestão de equipes e feedback** desenvolvida para otimizar a comunicação e produtividade em ambientes corporativos. O sistema oferece funcionalidades avançadas de gerenciamento de usuários, criação de squads, sistema de feedback estruturado com rascunhos e controle de permissões hierárquicas.

## ✨ Funcionalidades Principais

### 👤 Gerenciamento de Usuários
- **Sistema de Autenticação** - Login seguro com JWT
- **Hierarquia de Permissões** - Admin, Gerente e Colaborador
- **CRUD Completo** - Criação, edição e exclusão de usuários
- **Filtros por Tipo** - Listagem baseada em permissões
- **Contadores Dinâmicos** - Estatísticas de usuários por tipo

### 🏢 Sistema de Squads
- **Criação de Equipes** - Montagem de squads com validação
- **Composição Balanceada** - Validação de gerentes e colaboradores
- **Gerenciamento Flexível** - Adição e remoção de membros
- **Consultas Especializadas** - Por colaborador ou gerente
- **Controle de Acesso** - Baseado em hierarquia de usuários

### 💬 Sistema de Feedback Avançado
- **Rascunhos de Feedback** - Criação e edição antes do envio
- **Sistema de Avaliação** - Rating de 1 a 5 estrelas
- **Status de Controle** - DRAFT, SENT para organização
- **Histórico Completo** - Feedbacks enviados e recebidos
- **Permissões Específicas** - Gerentes enviam, colaboradores recebem

### 🔐 Segurança e Controle
- **JWT Authentication** - Tokens seguros para autenticação
- **Validação de Permissões** - Controle granular de acesso
- **Criptografia de Senhas** - BCrypt para proteção de dados
- **Validação de Dados** - Sanitização e verificação de entrada

## 📱 Layout da Aplicação

### Tela do Administrador (Home)

![Tela do Administrador (Home)](https://github.com/ocesar9/teampulse/blob/main/images/tela_administrador_home.png)

### Tela do Gerente (Home e Feedbacks)

![Tela do Gerente (Home - Usuários)](https://github.com/ocesar9/teampulse/blob/main/images/tela_gerente_home_1.png)

![Tela do Gerente (Home - Times)](https://github.com/ocesar9/teampulse/blob/main/images/tela_gerente_home_2.png)

![Tela do Gerente (Feedbacks - Rascunho)](https://github.com/ocesar9/teampulse/blob/main/images/tela_gerente_feedbacks_1.png)

![Tela do Gerente (Feedbacks - Enviados)](https://github.com/ocesar9/teampulse/blob/main/images/tela_gerente_feedbacks_2.png)

### Tela do Colaborador (Home e Feedbacks)

![Tela do Colaborador (Home)](https://github.com/ocesar9/teampulse/blob/main/images/tela_colaborador_home.png)

![Tela do Colaborador (Feedbacks)](https://github.com/ocesar9/teampulse/blob/main/images/tela_colaborador_feedbacks.png)

## 🚀 Tecnologias Utilizadas

### Backend
- **[Java 17](https://openjdk.java.net/projects/jdk/17/)** - Linguagem de programação principal
- **[Spring Boot 3.0](https://spring.io/projects/spring-boot)** - Framework para aplicações Java
- **[Spring Security](https://spring.io/projects/spring-security)** - Segurança e autenticação
- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)** - Persistência de dados
- **[JWT (JSON Web Tokens)](https://jwt.io/)** - Sistema de autenticação stateless
- **[Lombok](https://projectlombok.org/)** - Redução de boilerplate code
- **[Bean Validation](https://beanvalidation.org/)** - Validação de dados

### Frontend
- **[HTML5](https://developer.mozilla.org/pt-BR/docs/Web/HTML)** - Estruturação semântica das páginas
- **[CSS3](https://developer.mozilla.org/pt-BR/docs/Web/CSS)** - Estilização e layout
- **[JavaScript](https://developer.mozilla.org/pt-BR/docs/Web/JavaScript)** - Interatividade e lógica client-side
- **[Bootstrap 5](https://getbootstrap.com/)** - Framework CSS responsivo
- **[Font Awesome](https://fontawesome.com/)** - Ícones e elementos visuais

### Banco de Dados
- **[PostgreSQL](https://www.postgresql.org/)** - Banco de dados relacional principal
- **[H2 Database](https://www.h2database.com/)** - Banco em memória para testes
- **[Flyway](https://flywaydb.org/)** - Controle de versão do banco de dados

## 🏗️ Arquitetura do Sistema

### Estrutura de Controllers

#### AuthController
- **POST /auth/login** - Autenticação de usuários
- **POST /auth/register** - Registro de gerentes e colaboradores
- **POST /auth/register/admin** - Registro exclusivo de administradores

#### UserController  
- **GET /user/list** - Listagem de usuários com filtros
- **GET /user/count** - Contadores por tipo de usuário
- **PUT /user/edit/{userId}** - Edição de dados do usuário
- **DELETE /user/delete/{userId}** - Exclusão de usuários

#### SquadController
- **POST /squads** - Criação de novas squads
- **PUT /squads/edit/{squadId}** - Atualização de squads
- **DELETE /squads/{squadId}** - Exclusão de squads
- **GET /squads** - Listagem completa (gerentes)
- **GET /squads/colaborador/{userId}** - Squads de colaborador
- **GET /squads/gerente/{userId}** - Squads de gerente

#### FeedbackController
- **POST /feedback/draft** - Criação de rascunhos
- **PUT /feedback/draft/{feedbackId}** - Edição de rascunhos
- **DELETE /feedback/draft/{feedbackId}** - Exclusão de rascunhos
- **POST /feedback/send** - Envio de feedback
- **GET /feedback/drafts** - Rascunhos do usuário
- **GET /feedback/received** - Feedbacks recebidos
- **GET /feedback/sent** - Feedbacks enviados

## 📱 Interface do Usuário

### Funcionalidades Frontend
- **Dashboard interativo** com estatísticas
- **Formulários validados** com feedback visual
- **Tabelas dinâmicas** com paginação
- **Modais responsivos** para ações rápidas
- **Notificações toast** para feedback do usuário

### Modelo de Dados

#### Entidades Principais
```sql
-- Usuários
User {
  id: String (UUID)
  username: String (3-50 chars)
  email: String (unique, 100 chars)
  password: String (encrypted)
  userType: ADMIN | GERENTE | COLABORADOR
}

-- Squads
Squad {
  id: String (UUID)
  name: String (unique)
  createdAt: LocalDateTime
  members: List<User>
}

-- Feedbacks
Feedback {
  id: String (UUID)
  comment: String
  rating: Integer (1-5)
  status: DRAFT | SENT
  createdAt: LocalDateTime
  sentAt: LocalDateTime
  user: User (receptor)
  author: User (autor)
}
```

## 📦 Como Executar o Projeto

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+ (ou H2 para desenvolvimento)
- Git

### Configuração do Ambiente

#### 1. Clone o Repositório
```bash
git clone https://github.com/usuario/teampulse
cd teampulse
```

#### 2. Configuração do Banco de Dados
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/teampulse
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# JWT Configuration
jwt.secret=sua_chave_secreta_jwt
jwt.expiration=86400000
```

#### 3. Instalação e Execução
```bash
# Instalar dependências
mvn clean install

# Executar aplicação
mvn spring-boot:run

# Ou gerar JAR e executar
mvn package
java -jar target/teampulse-1.0.0.jar
```

#### 4. Acesso à Aplicação
- **Backend API:** http://localhost:8080
- **Frontend:** http://localhost:8080 (servido pelo Spring Boot)

## 📄 Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 👨‍💻 Autores

**Júlio Guimarães e Sérgio Adriani**
- GitHub: [@ocesar9](https://github.com/ocesar9),[@Userkarf](https://github.com/Userkarf)
- LinkedIn: [Júlio Guimarães](https://www.linkedin.com/in/j%C3%BAlio-guimar%C3%A3es-183110162/), [Sérgio Adriani](https://www.linkedin.com/in/s%C3%A9rgio-adriani-6090a8164),  

---

⭐ Se este projeto te ajudou, deixe uma estrela no repositório!
