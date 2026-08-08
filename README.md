 # Sistema de Flashcards

  Aplicação web para criação e estudo de flashcards, desenvolvida como projeto de bloco do curso de Engenharias de Software - INFNET.

  ## Tecnologias

  **Back-end:** Java 21, Spring Boot, Spring Data JPA, Spring Cloud OpenFeign.
  **Front-end:** React

  ## Arquitetura

  O projeto é composto por dois serviços independentes, cada um com seu próprio
  banco H2:

  - **sistema-flashcards** (raiz, porta 8080) — monólito com o CRUD de
    flashcards e histórico de alterações.
  - **categoria-service** (porta 8081) — microsserviço responsável pelas
    categorias/matérias dos flashcards.

  O monólito consulta o categoria-service via Spring Cloud OpenFeign para
  exibir o nome da categoria de cada flashcard. Detalhes em
  `docs/texto/microservico-categorias.txt`.

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
