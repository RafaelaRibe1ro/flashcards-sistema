# Roteiro de apresentação — Etapa 5

Demonstração no Kubernetes do Docker Desktop (ambiente simulado de produção). O Docker Compose aparece só rapidamente no início.

## Antes de começar (não precisa mostrar)

1. Abrir o Docker Desktop e conferir que o Kubernetes está ligado (Settings → Kubernetes → Enable Kubernetes).
2. Gerar as imagens e subir tudo:

   ```bash
   docker compose down            # se o compose estiver rodando (usa as mesmas portas)
   docker compose build
   kubectl apply -f k8s/
   kubectl -n flashcards rollout status deployment/categoria-service
   ```

   O último comando só termina quando o categoria-service está pronto (ele espera o monólito subir primeiro). Leva uns 2 minutos.
3. Deixar abertas no navegador: http://localhost:5173, http://localhost:15672 (guest/guest), http://localhost:9411 e http://localhost:3000.
4. Deixar um terminal aberto na raiz do projeto.

---

## 1. Código e containers (Docker)

1. Mostrar o `Dockerfile` da raiz: duas etapas, uma compila com Maven e a outra só leva o `.jar` com o Java.
2. Mostrar o `frontend/Dockerfile`: compila o React com Node e serve com Nginx.
3. Mostrar o `docker-compose.yml`: os 7 serviços (RabbitMQ, dois backends, frontend, Zipkin, Loki, Grafana), as variáveis de ambiente e o `depends_on` com healthcheck (o categoria-service só sobe depois do monólito, para a fila de sincronização já existir).
4. Mostrar as imagens geradas:

   ```bash
   docker images | findstr flashcards
   ```

## 2. Implantação no Kubernetes

1. Abrir a pasta `k8s/` e mostrar rapidamente:
   - `03-monolito.yaml`: Deployment, variáveis de ambiente, `readinessProbe` e `livenessProbe` no Actuator, Service LoadBalancer.
   - `04-categoria.yaml`: o `initContainer` que espera o monólito ficar pronto.
2. Mostrar tudo rodando:

   ```bash
   kubectl -n flashcards get pods,svc
   ```

   Comentar: 8 pods (o frontend tem 2 réplicas), Services com `EXTERNAL-IP localhost`.
3. Mostrar o log do initContainer esperando o monólito:

   ```bash
   kubectl -n flashcards logs deploy/categoria-service -c espera-monolito
   ```

4. Mostrar a saúde de um serviço: abrir http://localhost:8080/actuator/health.

## 3. Sistema funcionando

1. Abrir http://localhost:5173.
2. Aba **Categorias**: criar a categoria `Kubernetes`.
3. Aba **Gerenciar**: criar um flashcard e selecionar a categoria `Kubernetes`. O card aparece com a etiqueta da categoria.
4. Explicar: o nome da categoria chegou no monólito por evento do RabbitMQ, não por chamada direta entre os serviços.
5. Aba **Estudar**: navegar pelos cards.
6. No painel do RabbitMQ (http://localhost:15672 → Queues): mostrar as filas `monolito.categoria.sync.queue`, `flashcard.historico.queue` e a DLQ.

## 4. Monitoramento

### Rastreamento (Zipkin)

1. Abrir http://localhost:9411 → **Run Query**.
2. Clicar no trace `categoria-service: http post /api/categorias` (o da categoria criada no passo anterior). Se não aparecer, criar outra categoria e rodar a busca de novo (o Zipkin mostra os últimos 15 minutos).
3. Mostrar os spans em sequência:
   - `http post /api/categorias` (categoria-service)
   - `categoria.events/categoria.criada send` (publicação no RabbitMQ)
   - `monolito.categoria.sync.queue receive` (sistema-flashcards recebendo)
4. Explicar: é uma transação só, atravessando os dois serviços e o broker. Se algo der errado ou ficar lento, dá para ver em qual etapa foi.
5. Copiar o **Trace ID** desse trace.

### Logs agregados (Grafana + Loki)

1. Abrir http://localhost:3000 → menu **Explore** → fonte **Loki**.
2. Trocar para o modo **Code** e rodar:

   ```
   {app="categoria-service"}
   ```

   Depois:

   ```
   {app="sistema-flashcards"}
   ```

   Mostrar que os logs dos dois serviços estão num lugar só, sem precisar abrir o terminal de cada pod.
3. Buscar pelo Trace ID copiado do Zipkin:

   ```
   {app=~".+"} |= "COLE_O_TRACE_ID_AQUI"
   ```

   Aparecem duas linhas de serviços diferentes com o mesmo traceId: "Evento 'categoria.criada' publicado..." (categoria-service) e "Evento de categoria recebido..." (sistema-flashcards). É assim que se liga o log ao rastreamento.
4. (Opcional) Filtrar só avisos e erros: `{app=~".+", level=~"WARN|ERROR"}`.

## 5. Escalabilidade e recuperação

1. Escalar o frontend para 4 réplicas:

   ```bash
   kubectl -n flashcards scale deployment frontend --replicas=4
   kubectl -n flashcards get pods -l app=frontend
   ```

   Mostrar os pods novos. Voltar ao navegador e recarregar a página: continua funcionando, o Service distribui entre as réplicas.
2. Recuperação automática: apagar o pod do categoria-service e ver o Kubernetes criar outro.

   ```bash
   kubectl -n flashcards delete pod -l app=categoria-service
   kubectl -n flashcards get pods -w
   ```

   (Ctrl+C para parar de acompanhar.) O pod novo aparece, passa pelo initContainer e fica `1/1 Running` quando a readinessProbe responde.
3. Explicar a limitação: os backends usam H2 dentro do container, então ficaram com 1 réplica. Com mais réplicas, cada pod teria um banco diferente. Para escalar os backends seria preciso um banco compartilhado. Por isso o pod recriado volta com os dados de exemplo.
4. Voltar o frontend para 2 réplicas:

   ```bash
   kubectl -n flashcards scale deployment frontend --replicas=2
   ```

## 6. Testes

1. Rodar os testes do monólito no terminal:

   ```bash
   .\mvnw.cmd test
   ```

   Resultado esperado: `Tests run: 30, Failures: 0, Errors: 0`.
2. Mostrar o `FlashcardIntegrationTest`: sobe a aplicação inteira e testa o ciclo criar → buscar → atualizar → deletar pela API.
3. Comentar os outros tipos que já existiam: unitários do serviço com Mockito, repositórios com `@DataJpaTest`, listeners e publisher do RabbitMQ testados isoladamente.
4. (Se der tempo) `cd categoria-service` e `.\mvnw.cmd test`: 17 testes.

## 7. CI/CD (GitHub Actions)

1. Mostrar o arquivo `.github/workflows/ci-cd.yml`:
   - `testes-backend`: roda `mvn test` nos dois serviços (matrix).
   - `build-frontend`: compila o React.
   - `publicar-imagens`: só depois dos dois passarem e só em push; gera as 3 imagens e publica no GitHub Container Registry.
2. No GitHub, abrir a aba **Actions** do repositório e mostrar a última execução com os jobs verdes.
3. Mostrar as imagens publicadas em **Packages** (no perfil do GitHub).
4. Explicar que o deploy no Kubernetes é manual (`kubectl apply`) porque o cluster é local.

## 8. Versionamento

1. Mostrar o histórico de commits:

   ```bash
   git log --oneline
   ```

2. Comentar que Dockerfiles, compose, manifestos e workflow estão versionados junto com o código, e que a configuração muda por variável de ambiente (a mesma imagem roda local, no compose e no Kubernetes).

## Para desligar tudo no final

```bash
kubectl delete namespace flashcards
```
