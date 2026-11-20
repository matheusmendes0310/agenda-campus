# Roteiro do vídeo (até 5 minutos)

Antes de gravar: deixe a API, o Angular, o Swagger (ou Postman) e o VS Code/IntelliJ abertos. Comece deslogado no navegador.
Use um banco de demonstração com os dados iniciais. Confira antes da gravação a data da Oficina de Angular, se Ana e Bruno estão inscritos nela e se Diego Rocha ainda não tem inscrição.

| Tempo | O que mostrar | O que falar |
| --- | --- | --- |
| 0:00 – 0:20 | Tela de login | Apresentar o grupo e o tema: "Agenda Campus, sistema de inscrições em eventos acadêmicos". |
| 0:20 – 1:30 | **Código do back-end**: pastas `controller`, `service`, `repository`, `dto`, `model`. Abrir `Inscricao` (o `@ManyToOne`), `InscricaoService` (método `validarInscricao`), um `record` de DTO e o `SecurityConfig`. | "O controller só recebe a requisição, o service tem as regras de negócio — não inscrever duas vezes, não passar do limite de vagas — e o repository usa Spring Data JPA. Os DTOs são records, a entidade não sai da API. Um evento tem muitas inscrições. A segurança usa Spring Security com JWT: ADMIN faz tudo e COMUM só consulta." |
| 1:30 – 2:20 | **Swagger** (`/swagger-ui.html`): login com admin, copiar apenas o valor do token e colar em **Authorize**. Executar `GET /api/eventos/{id}/participantes` e `GET /api/eventos?nome=oficina&data=...`. Mostrar um erro 401 (sem token) ou 400. | "No login a API devolve o token. Com ele eu chamo as rotas protegidas. Aqui a consulta com filtro no path e a busca com dois parâmetros." |
| 2:20 – 2:40 | **Código do front-end**: `app.routes.ts`, `auth.guard.ts`, `auth.interceptor.ts` e `inscricao.service.ts`. | "As rotas são protegidas pelo guard, o interceptor coloca o token em toda requisição e as chamadas HTTP ficam nos serviços, não nos componentes." |
| 2:40 – 3:00 | No navegador, abrir `/eventos` deslogado → vai para o login. Entrar como **admin**. | "Sem login o sistema redireciona para a tela de login." |
| 3:00 – 4:00 | **CRUD**: Inscrições → Nova inscrição → escolher evento e participante nos selects (vêm da API). Escolher **Oficina de Angular** e **Diego** → salvar. Voltar à lista e abrir **Ver detalhes**. Editar trocando para **Oficina de Spring Boot**. Excluir e confirmar. Se sobrar tempo, mostre a mensagem ao tentar cadastrar Ana outra vez. | "Os selects mostram o relacionamento 1:N. A regra de negócio do back-end aparece aqui como mensagem. Depois detalhes, edição e exclusão com confirmação." |
| 4:00 – 4:35 | **Eventos**: buscar por nome **oficina** + data da Oficina de Angular → só ela aparece. Clicar **Ver participantes**. | "Busca com dois campos e a tela da consulta no path, que lista os participantes do evento." |
| 4:35 – 4:55 | Sair e entrar como **comum**: lista sem botões de cadastro/edição/exclusão. | "O perfil comum só consulta; o back-end também bloqueia com 403." |

Dica: deixe os arquivos de código abertos em abas, na ordem do roteiro, para não perder tempo procurando.
