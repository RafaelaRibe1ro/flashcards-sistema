 # Sistema de Flashcards

  Aplicação web para criação e estudo de flashcards, desenvolvida como projeto de bloco do curso de Engenharias de Software - INFNET.

  ## Tecnologias

  **Back-end:** Java 21, Spring Boot, Spring Data JPA, Spring AMQP (RabbitMQ).
  **Front-end:** React

  ## Arquitetura

  O projeto é composto por dois serviços independentes, cada um com seu próprio
  banco H2, mais um broker RabbitMQ para comunicação assíncrona entre eles:

  - **sistema-flashcards** (raiz, porta 8080) — monólito com o CRUD de
    flashcards e histórico de alterações.
  - **categoria-service** (porta 8081) — microsserviço responsável pelas
    categorias/matérias dos flashcards.
  - **RabbitMQ** (via `docker compose up -d`, porta 5672, painel em
    `:15672`) — broker de eventos usado para sincronizar categorias e
    para a gravação assíncrona do histórico de flashcards.

  O categoria-service publica eventos de categoria (criação/atualização/
  exclusão) no RabbitMQ; o monólito consome esses eventos e mantém uma
  réplica local (cache) para exibir o nome da categoria de cada flashcard,
  sem chamada síncrona entre os serviços. Detalhes completos em
  `docs/texto/arquitetura-eventos.txt` (arquitetura orientada a eventos,
  Etapa 4) e `docs/texto/microservico-categorias.txt` (microsserviço,
  Etapa 3).

  ## Funcionalidades

  - Criar flashcards com pergunta e resposta, associando uma categoria opcional
  - Criar, listar e excluir categorias
  - Listar e excluir flashcards
  - Modo estudo: navegar pelos cards e revelar respostas

  ## Como executar

  Ver `docs/texto/comoExecutar.txt` para o passo a passo completo (é preciso
  subir o `categoria-service` antes do monólito). Resumo:

  ```bash
  cd categoria-service
  mvn spring-boot:run
  Disponível em http://localhost:8081

  mvn spring-boot:run
  Disponível em http://localhost:8080

  cd frontend
  npm install
  npm run dev
  Disponível em http://localhost:5173
