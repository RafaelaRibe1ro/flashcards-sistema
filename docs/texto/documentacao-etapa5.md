# Prompt para gerar a documentação da Etapa 5

Copie tudo abaixo da linha e cole em outra IA.

---

Você vai escrever a documentação da Etapa 5 de um projeto de faculdade (curso de Engenharias Escaláveis, INFNET). O projeto é um sistema de flashcards para estudo, feito por uma estudante. A documentação será lida pelo professor.

## Regras de escrita

- Escreva em português do Brasil.
- Seja concisa e clara. Frases curtas, sem enrolação, sem repetir a mesma ideia com outras palavras.
- Não use vícios de linguagem típicos de IA: nada de "vale ressaltar", "é importante destacar", "robusto", "de forma eficiente", "em resumo", "além disso" em todo parágrafo, "garantindo", "potencializar", "jornada", "mergulhar", listas de três adjetivos, emojis, frases de efeito ou conclusões motivacionais.
- Não invente nada que não esteja descrito abaixo. Se algo não foi feito, não diga que foi.
- Tom de estudante explicando o próprio trabalho, não de documentação corporativa.
- Formato Markdown, com títulos, blocos de código para comandos e arquivos, e tabelas só quando ajudarem (ex.: portas e URLs).
- Pode incluir um diagrama simples em texto/ASCII da arquitetura.

## Seções que a documentação deve ter

1. Visão geral da Etapa 5 (objetivo: preparar o sistema para operação com containers, orquestração, monitoramento, CI/CD e testes).
2. Arquitetura atual (componentes e portas).
3. Conteinerização com Docker (Dockerfiles e docker-compose).
4. Implantação no Kubernetes (manifestos, como aplicar, escalar e remover).
5. Monitoramento (logs agregados e rastreamento de transações).
6. Gestão de configuração e versionamento (Git/GitHub, configuração por variáveis de ambiente).
7. CI/CD com GitHub Actions.
8. Testes (unitários e de integração, como rodar).
9. Limitações conhecidas.
10. Como executar (resumo de comandos).

## Contexto do projeto (etapas anteriores)

- Etapa 1: monólito Spring Boot 4.0.6 (Java 21) + frontend React (Vite), CRUD de flashcards, banco H2.
- Etapa 2: camada JPA, histórico de alterações dos flashcards, H2 em arquivo.
- Etapa 3: microsserviço separado `categoria-service` (porta 8081, banco H2 próprio). O monólito (`sistema-flashcards`, porta 8080) guarda só o `categoriaId` do flashcard.
- Etapa 4: comunicação assíncrona com RabbitMQ. O `categoria-service` publica eventos (`categoria.criada/atualizada/deletada`) no exchange `categoria.events`; o monólito consome na fila `monolito.categoria.sync.queue` e mantém uma cópia local das categorias (tabela `categoria_cache`). O histórico dos flashcards também é gravado de forma assíncrona pela fila `flashcard.historico.queue` (com retry e dead-letter queue).
- São dois projetos Maven independentes (raiz = monólito, pasta `categoria-service/`) e o frontend em `frontend/`.

## O que foi feito na Etapa 5

### 1. Docker

Arquivos criados:

- `Dockerfile` (raiz, monólito) e `categoria-service/Dockerfile`: build em duas etapas. A primeira usa a imagem `maven:3.9-eclipse-temurin-21` e roda `mvn package -DskipTests`; a segunda usa `eclipse-temurin:21-jre` e só copia o `.jar`. Assim a imagem final não carrega Maven nem código-fonte.
- `frontend/Dockerfile`: primeira etapa com `node:20-alpine` roda `npm ci` e `npm run build`; a segunda usa `nginx:alpine` para servir os arquivos estáticos gerados.
- `.dockerignore` na raiz, em `categoria-service/` e em `frontend/`, para não mandar `target/`, `data/`, `node_modules/` etc. para o build.

`docker-compose.yml` (na raiz) passou a subir o sistema inteiro:

| Serviço | Imagem | Porta no host |
|---|---|---|
| rabbitmq | rabbitmq:3-management | 5672 e 15672 (painel) |
| sistema-flashcards (monólito) | flashcards-monolito:1.0 (build local) | 8080 |
| categoria-service | flashcards-categoria:1.0 (build local) | 8081 |
| frontend | flashcards-frontend:1.0 (build local) | 5173 (nginx na porta 80 do container) |
| zipkin | openzipkin/zipkin | 9411 |
| loki | grafana/loki:3.4.2 | 3100 |
| grafana | grafana/grafana | 3000 |

Detalhes do compose:

- O RabbitMQ tem healthcheck (`rabbitmq-diagnostics -q ping`) e os serviços Java só sobem depois dele estar saudável.
- O monólito tem healthcheck em `/actuator/health`. O `categoria-service` só sobe depois do monólito estar saudável. Motivo: na Etapa 4 vimos que, se o `categoria-service` publicar os eventos das categorias de exemplo antes de a fila do monólito existir, esses eventos se perdem.
- O frontend fica na porta 5173 do host para não precisar mudar o CORS dos backends (que liberam `http://localhost:5173`). O navegador continua chamando `localhost:8080` e `localhost:8081`.
- O Grafana sobe com login anônimo como Admin (só para a demonstração) e já com as fontes de dados Loki e Zipkin cadastradas pelo arquivo `monitoramento/grafana-datasources.yml`.
- Comando: `docker compose up -d --build`.

### 2. Kubernetes

Pasta `k8s/` com os manifestos, numerados na ordem em que fazem sentido:

- `00-namespace.yaml`: namespace `flashcards`.
- `01-monitoramento.yaml`: Deployments e Services de Zipkin, Loki e Grafana, e um ConfigMap com as fontes de dados do Grafana (mesmo conteúdo do arquivo usado no compose).
- `02-rabbitmq.yaml`: Deployment e Service do RabbitMQ.
- `03-monolito.yaml`: Deployment e Service do monólito.
- `04-categoria.yaml`: Deployment e Service do `categoria-service`.
- `05-frontend.yaml`: Deployment (2 réplicas) e Service do frontend.

Detalhes:

- Ambiente usado: Kubernetes do Docker Desktop (cluster local de um nó). Ele usa as mesmas imagens do Docker local, por isso os manifestos usam as imagens `flashcards-*:1.0` com `imagePullPolicy: IfNotPresent`. As imagens são geradas antes com `docker compose build`.
- Os Services são do tipo `LoadBalancer`; no Docker Desktop isso publica as portas em `localhost` (8080, 8081, 5173, 9411, 3000, 15672). O Loki é `ClusterIP` porque só é acessado de dentro do cluster (pelos serviços e pelo Grafana).
- Os serviços Java têm `readinessProbe` em `/actuator/health/readiness` (o pod só recebe tráfego depois de pronto) e `livenessProbe` em `/actuator/health/liveness` (se travar, o Kubernetes reinicia o container).
- O `categoria-service` tem um `initContainer` (busybox) que fica esperando o monólito responder em `/actuator/health/readiness` antes de iniciar. É o mesmo motivo da ordem de subida no compose.
- As configurações que mudam entre ambientes vão por variáveis de ambiente no Deployment (ver seção de configuração).
- Comandos: `kubectl apply -f k8s/`, `kubectl -n flashcards get pods,svc`, `kubectl -n flashcards scale deployment frontend --replicas=3`, `kubectl delete namespace flashcards` para remover tudo.
- Testado: ao apagar o pod do monólito (`kubectl delete pod`), o Deployment cria outro sozinho; ao escalar o frontend, novos pods aparecem e o Service distribui entre eles.

### 3. Monitoramento

Dependências adicionadas nos dois `pom.xml`:

- `spring-boot-starter-actuator`: endpoints de saúde (`/actuator/health`, com os grupos `liveness` e `readiness` usados pelo Kubernetes). Só `health` e `info` estão expostos.
- `spring-boot-starter-zipkin`: Micrometer Tracing com Brave e envio dos spans para o Zipkin.
- `com.github.loki4j:loki-logback-appender:2.0.0`: envia os logs direto para o Loki.

Configuração nos `application.properties` dos dois serviços:

```properties
management.endpoints.web.exposure.include=health,info
management.tracing.sampling.probability=1.0
management.tracing.export.zipkin.endpoint=http://localhost:9411/api/v2/spans
spring.rabbitmq.template.observation-enabled=true
spring.rabbitmq.listener.simple.observation-enabled=true
loki.url=http://localhost:3100/loki/api/v1/push
```

- Nos dois `WebConfig` foi adicionado um bean `ObservationPredicate` que não rastreia requisições para `/actuator`. Sem ele, as probes do Kubernetes (a cada 5 segundos) enchiam o Zipkin de traces `http get /actuator/health/**` e escondiam os traces úteis.
- `sampling.probability=1.0` manda 100% das requisições para o Zipkin (adequado para demonstração, não para produção com muito tráfego).
- As duas propriedades `observation-enabled` do RabbitMQ fazem o traceId viajar dentro da mensagem. Resultado: uma requisição `POST /api/categorias` no `categoria-service` aparece no Zipkin como um único trace com os spans `http post /api/categorias` (categoria-service), `categoria.events/categoria.criada send` e `monolito.categoria.sync.queue receive` (sistema-flashcards). Ou seja, dá para seguir a transação de um serviço para o outro passando pelo broker.

Logs (`logback-spring.xml` novo em cada serviço):

- Continua o log normal no console.
- Quando o perfil Spring `docker` está ativo (definido por `SPRING_PROFILES_ACTIVE=docker` no compose e no Kubernetes), um appender extra manda os logs para o Loki com os labels `app` (nome do serviço) e `level`. A mensagem inclui o `traceId`.
- O appender do Loki fica só no perfil `docker` porque, sem o Loki rodando (execução local ou testes), os erros de conexão do appender faziam o Spring falhar ao recarregar o contexto nos testes.
- Foram adicionados dois logs `info` para a demonstração: no `CategoriaEventPublisher` (categoria-service) quando o evento é publicado, e no `CategoriaEventListener` (monólito) quando o evento é recebido. Os dois aparecem no Grafana com o mesmo `traceId`.
- No Grafana (Explore, fonte Loki) as consultas usadas são, por exemplo: `{app="categoria-service"}`, `{app="sistema-flashcards"}`, `{app=~".+"} |= "<traceId>"`.

Ferramentas e por que foram escolhidas:

- Zipkin: rastreamento distribuído, já integrado ao Spring Boot via Micrometer, interface simples.
- Loki + Grafana: agregação de logs de todos os serviços num lugar só, com busca por serviço, nível e texto (traceId).
- Painel do RabbitMQ (já existia): acompanhar filas, mensagens e a DLQ.

### 4. Gestão de configuração e versionamento

- O mesmo `.jar`/imagem roda local, no Docker e no Kubernetes. O que muda são variáveis de ambiente, que o Spring Boot já lê no lugar das propriedades (relaxed binding):
  - `SPRING_PROFILES_ACTIVE=docker`
  - `SPRING_RABBITMQ_HOST=rabbitmq`
  - `MANAGEMENT_TRACING_EXPORT_ZIPKIN_ENDPOINT=http://zipkin:9411/api/v2/spans`
  - `LOKI_URL=http://loki:3100/loki/api/v1/push`
- Sem essas variáveis (rodando com `mvnw spring-boot:run`), os valores padrão apontam para `localhost`.
- Código versionado com Git no GitHub (repositório `RafaelaRibe1ro/flashcards-sistema`, branch `master`), um commit por etapa/alteração. Os arquivos de infraestrutura (Dockerfiles, compose, manifestos do Kubernetes, workflow) ficam versionados junto com o código.

### 5. CI/CD com GitHub Actions

Arquivo `.github/workflows/ci-cd.yml`. Roda em todo push e pull request para `master`.

Jobs:

1. `testes-backend` (CI): usa uma matrix com os dois serviços Java (`.` e `categoria-service`). Instala Java 21 (temurin, com cache do Maven) e roda `mvn -B test` em cada um.
2. `build-frontend` (CI): instala Node 20 e roda `npm ci` e `npm run build` no `frontend/`, para garantir que o React compila.
3. `publicar-imagens` (CD): só roda em push (não em pull request) e só se os dois jobs anteriores passarem. Faz login no GitHub Container Registry com o `GITHUB_TOKEN`, gera as três imagens Docker e publica como `ghcr.io/<dono-em-minúsculas>/flashcards-monolito:latest`, `flashcards-categoria:latest` e `flashcards-frontend:latest`.

A entrega contínua vai até publicar as imagens no registry. O deploy no Kubernetes é feito manualmente com `kubectl apply`, porque o cluster é local (Docker Desktop) e o GitHub não tem acesso a ele.

### 6. Testes

Os testes rodam com `mvnw test` em cada projeto e não precisam de RabbitMQ, Zipkin ou Loki rodando (usam H2 em memória).

Monólito (30 testes):

- `FlashcardServiceTest` (13): regras do serviço com Mockito.
- `FlashcardRepositoryTest` (5) e `FlashcardHistoricoRepositoryTest` (4): consultas JPA com `@DataJpaTest`.
- `CategoriaEventListenerTest` (3) e `FlashcardHistoricoListenerTest` (1): consumidores do RabbitMQ testados isoladamente.
- `SistemaFlashcardsApplicationTests` (1): contexto Spring sobe.
- `FlashcardIntegrationTest` (3, novo na Etapa 5): sobe a aplicação inteira com `@SpringBootTest` + `MockMvc` e testa controller, serviço, repositório e banco juntos: ciclo completo criar → buscar → atualizar → deletar → 404; flashcard criado com `categoriaId` retorna o `categoriaNome` da tabela de cache; 404 para id inexistente.

categoria-service (17 testes):

- `CategoriaServiceTest` (8), `CategoriaRepositoryTest` (3), `CategoriaEventPublisherTest` (2), `CategoriaServiceApplicationTests` (1).
- `CategoriaIntegrationTest` (3, novo na Etapa 5): ciclo completo criar → buscar → atualizar → deletar → 404 pela API; listagem traz as categorias criadas pelo `DataInitializer`; 404 ao deletar id inexistente.

Divisão: testes unitários verificam cada componente isolado (com mocks); testes de integração verificam as camadas funcionando juntas dentro de cada serviço. A integração entre os serviços (categoria-service → RabbitMQ → monólito) foi verificada rodando o sistema completo no Docker Compose e no Kubernetes: criar uma categoria e depois um flashcard com ela faz o monólito devolver o `categoriaNome` correto, e o Zipkin mostra o trace passando pelos dois serviços.

Os testes também rodam automaticamente no GitHub Actions a cada push.

### 7. Limitações conhecidas (escrever com honestidade)

- Os bancos continuam H2 dentro do container, sem volume. Se o container/pod for recriado, os dados voltam aos exemplos iniciais. No monólito, a tabela de cache de categorias também é perdida quando o pod é recriado; ela só volta a ter uma categoria quando essa categoria for criada/atualizada de novo no `categoria-service`.
- Por causa do H2 local, os backends ficam com 1 réplica. Escalar o monólito ou o `categoria-service` para mais réplicas deixaria cada pod com um banco diferente. Para escalar de verdade seria preciso um banco compartilhado (ex.: PostgreSQL). Quem foi escalado na demonstração é o frontend, que não tem estado.
- Loki, Zipkin e Grafana guardam os dados só em memória/disco do container; são configurações de demonstração.
- Grafana com acesso anônimo de Admin e RabbitMQ com guest/guest: aceitável só em ambiente local.
- Se o Loki ainda não estiver pronto quando os serviços sobem, os primeiros logs de inicialização podem não chegar nele (o appender tenta algumas vezes e desiste). Os logs depois disso chegam normalmente.
- O cluster Kubernetes é local (Docker Desktop), um "ambiente simulado de produção".

### 8. Como executar (para a seção final)

Docker Compose:

```bash
docker compose up -d --build
docker compose ps
docker compose down
```

Kubernetes (Docker Desktop com Kubernetes habilitado):

```bash
docker compose build          # gera as imagens flashcards-*:1.0
kubectl apply -f k8s/
kubectl -n flashcards get pods,svc
kubectl delete namespace flashcards
```

Não rodar o compose e o Kubernetes ao mesmo tempo, porque usam as mesmas portas no `localhost`.

Endereços (nos dois modos):

| URL | O quê |
|---|---|
| http://localhost:5173 | Frontend |
| http://localhost:8080/api/flashcards | API do monólito |
| http://localhost:8081/api/categorias | API do categoria-service |
| http://localhost:8080/actuator/health | Saúde do monólito |
| http://localhost:15672 | Painel do RabbitMQ (guest/guest) |
| http://localhost:9411 | Zipkin |
| http://localhost:3000 | Grafana |

Testes:

```bash
./mvnw test                       # monólito, na raiz
cd categoria-service && ./mvnw test
```

Gere a documentação completa em um único arquivo Markdown seguindo as regras de escrita acima.
