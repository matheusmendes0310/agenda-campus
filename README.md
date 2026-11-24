# Agenda Campus

Trabalho do 2º bimestre de **Desenvolvimento Back-end** e **Fundamentos de Programação para Web** — 4º período de ADS da UNIPAR.

**Tema escolhido: Eventos** (eventos, participantes e inscrições). O sistema organiza as inscrições de participantes em eventos acadêmicos.

## Integrantes

| Nome | RA |
| --- | --- |
| Matheus Mendes | 60006105 |
| João Crepalli | 60007641 |
| Gabriel Foscarini | 60008405 |

## Tecnologias

| Pasta | Tecnologia |
| --- | --- |
| [`backend/`](backend) | Java 17, Spring Boot 4, Spring Data JPA, Spring Security + JWT, PostgreSQL, Swagger |
| [`frontend/`](frontend) | Angular 22 |
| [`postman/`](postman) | Coleção para testar a API |

---

## Como rodar

### Pré-requisitos

- **Java 17 ou mais recente** (JDK)
- **PostgreSQL** rodando na porta 5432
- **Node.js 24.15+** (com npm)

### 1. Banco de dados

No PostgreSQL (pgAdmin ou psql), crie o banco:

```sql
CREATE DATABASE agenda_campus;
```

As tabelas são criadas automaticamente pelo Hibernate quando a API sobe. Se você já executou a versão anterior do trabalho, guarde o banco antigo e use um banco novo para evitar incompatibilidades. Por exemplo, crie `agenda_campus_entrega` e defina `$env:DB_URL = 'jdbc:postgresql://localhost:5432/agenda_campus_entrega'` antes de iniciar a API.

### 2. Back-end (API)

Por padrão a API usa o usuário `postgres` com senha `postgres`. Se a sua senha for outra, defina a variável antes de rodar:

```powershell
cd backend
$env:DB_PASSWORD = "sua_senha"     # só se for diferente de "postgres"
.\mvnw.cmd spring-boot:run
```

No Linux/macOS: `export DB_PASSWORD=sua_senha` e `./mvnw spring-boot:run`.

A API sobe em **http://localhost:8080** e a documentação Swagger fica em **http://localhost:8080/swagger-ui.html**.

> **Sem PostgreSQL?** Dá para rodar com o banco H2 em arquivo (os dados ficam em `backend/data/`):
> `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"`

### 3. Front-end (Angular)

Em outro terminal:

```powershell
cd frontend
npm ci
npm start
```

Acesse **http://localhost:4200**.

### Usuários de acesso

Criados automaticamente na primeira execução da API:

| Usuário | Senha | Perfil | O que pode fazer |
| --- | --- | --- | --- |
| `admin` | `admin123` | ADMIN | Consultar, cadastrar, editar e excluir |
| `comum` | `comum123` | COMUM | Somente consultar e usar os filtros |

Na primeira execução a API também cadastra eventos, participantes e inscrições de exemplo em um banco vazio (pode desligar com `DADOS_EXEMPLO=false`). As datas de exemplo são calculadas no primeiro início. Confira a data da **Oficina de Angular** na tela Eventos antes de gravar.

As contas acima são para demonstração local. Defina `SENHA_ADMIN` e `SENHA_COMUM` antes do primeiro início em um banco novo para usar outras senhas. Sem `JWT_SECRET`, a aplicação gera uma chave ao iniciar e os tokens deixam de valer após o reinício. Em uma instalação contínua, configure uma chave própria por variável de ambiente.

---

## Requisitos atendidos

### Back-end

| Requisito | Onde está |
| --- | --- |
| Persistência com Spring Data JPA | `repository/` — interfaces que estendem `JpaRepository` |
| Camadas Repository, Service e Controller | `repository/`, `service/` (regras de negócio) e `controller/` |
| DTOs com `record` | `dto/` — `EventoRequest`, `EventoResponse`, `InscricaoRequest`, `InscricaoResponse`... As entidades (`model/`) nunca saem nem entram pela API |
| CRUD completo (Inscrição) | `POST`, `GET`, `GET /{id}`, `PUT /{id}` e `DELETE /{id}` em `/api/inscricoes` |
| Relacionamento 1:N | `Evento` 1:N `Inscricao` e `Participante` 1:N `Inscricao` (`@OneToMany` / `@ManyToOne`) |
| Consulta com filtro no path | `GET /api/eventos/{id}/participantes` |
| Consulta com múltiplos parâmetros | `GET /api/eventos?nome=oficina&data=AAAA-MM-DD` |
| Login | `POST /api/auth/login` devolve um token JWT (Spring Security + senha com BCrypt) |
| Autorização por perfil (extra) | `SecurityConfig`: ADMIN faz tudo; COMUM só consulta (GET/HEAD) e recebe 403 em operações de escrita |
| CORS | `SecurityConfig.corsConfigurationSource()` libera `http://localhost:4200` |
| Tratamento de erros | `exception/TratadorDeErros` devolve JSON com `status` e `mensagem` (400, 401, 403, 404) |

**Regras de negócio (na camada de serviço):**

- não permite inscrever o mesmo participante duas vezes no mesmo evento;
- não permite inscrição quando o evento está lotado (limite de `vagas`);
- não permite inscrição em evento que já aconteceu;
- não permite cadastrar evento com data no passado;
- não permite dois participantes com o mesmo e-mail.

### Front-end

| Requisito | Onde está |
| --- | --- |
| Angular | `frontend/` |
| Dados vindos da API (sem mock) | Serviços em `core/services/` chamam `http://localhost:8080/api` |
| Tela de login, token guardado e enviado | `pages/login`, `AuthService` (localStorage) e `auth.interceptor.ts` (cabeçalho `Authorization: Bearer`) |
| Bloquear telas sem login | `core/guards/auth.guard.ts` redireciona para `/login` |
| Logout | Botão **Sair** no menu |
| CRUD: listar, detalhes, cadastrar/editar, excluir com confirmação | `pages/inscricoes/` (lista, detalhe e formulário) + janela de confirmação `shared/confirmacao` |
| Relacionamento 1:N na interface | No formulário de inscrição, evento e participante são escolhidos em `select` carregados da API |
| Tela da consulta no path | **Eventos → Ver participantes** (`/eventos/:id/participantes`) |
| Tela da busca com filtros | **Eventos**: formulário com nome e data |
| Validação e mensagens | Campos obrigatórios validados antes de enviar; mensagens de sucesso e de erro (400, 401, 403, 404) |
| Organização | `pages/` (telas), `core/services/` (HTTP), `core/guards/`, `core/interceptors/`, `app.routes.ts` (rotas) |
| Navegação | Rotas e menu no topo (Inscrições, Eventos, Participantes) |

---

## Endpoints da API

| Método | Rota | Perfil |
| --- | --- | --- |
| POST | `/api/auth/login` | público |
| GET | `/api/inscricoes` | ADMIN, COMUM |
| GET | `/api/inscricoes/{id}` | ADMIN, COMUM |
| POST | `/api/inscricoes` | ADMIN |
| PUT | `/api/inscricoes/{id}` | ADMIN |
| DELETE | `/api/inscricoes/{id}` | ADMIN |
| GET | `/api/eventos?nome=&data=` | ADMIN, COMUM |
| GET | `/api/eventos/{id}` | ADMIN, COMUM |
| GET | `/api/eventos/{id}/participantes` | ADMIN, COMUM |
| POST | `/api/eventos` | ADMIN |
| GET | `/api/participantes` | ADMIN, COMUM |
| POST | `/api/participantes` | ADMIN |

Exemplo de login:

```json
POST /api/auth/login
{ "login": "admin", "senha": "admin123" }
```

Resposta: `{ "token": "eyJ...", "login": "admin", "perfil": "ADMIN" }`. Nas outras chamadas, envie `Authorization: Bearer <token>`.

**No Swagger:** execute `POST /api/auth/login`, copie apenas o valor de `token` e cole em **Authorize**, sem o prefixo `Bearer`. Depois execute uma rota protegida como `GET /api/inscricoes` e confira o HTTP 200.

**No Postman:** importe `postman/Agenda-Campus.postman_collection.json`, confira as variáveis `baseUrl`, `senhaAdmin` e `senhaComum` e execute a coleção em ordem. Use o banco de demonstração. A coleção salva os tokens, cria dois eventos e um participante e remove a inscrição criada durante o teste.

---

## Testes

```powershell
cd backend
.\mvnw.cmd test          # testa a API inteira com banco H2 em memória
```

```powershell
cd frontend
npm test -- --watch=false
```

