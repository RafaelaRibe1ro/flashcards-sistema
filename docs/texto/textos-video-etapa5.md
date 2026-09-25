# Textos para o vídeo — Etapa 5

Um bloco por cena, na ordem do roteiro (`roteiro-apresentacao-etapa5.md`).

## 1. Docker

- **Dockerfile:** "Build em 2 etapas: Maven compila, a imagem final leva só o .jar"
- **Frontend:** "React compilado e servido pelo Nginx"
- **docker-compose:** "7 serviços: RabbitMQ, 2 backends, frontend, Zipkin, Loki e Grafana"
- **depends_on:** "O categoria-service só sobe depois do monólito, para a fila já existir"

## 2. Kubernetes

- **Manifestos:** "Um Deployment e um Service para cada componente"
- **Probes:** "Readiness: só recebe tráfego quando está pronto. Liveness: reinicia se travar"
- **initContainer:** "O categoria-service espera o monólito ficar pronto"
- **get pods,svc:** "8 pods rodando no namespace flashcards"

## 3. Sistema funcionando

- **Categoria no card:** "O nome da categoria chega ao monólito por evento do RabbitMQ"
- **Painel RabbitMQ:** "Filas de sincronização, de histórico e a DLQ"

## 4. Monitoramento

- **Zipkin:** "Uma requisição rastreada do categoria-service até o monólito, passando pelo RabbitMQ"
- **Spans:** "Dá para ver em qual etapa algo falhou ou demorou"
- **Grafana/Loki:** "Logs de todos os serviços num lugar só"
- **Busca por traceId:** "Mesmo traceId nos logs dos dois serviços: log e rastreamento ligados"

## 5. Escalabilidade e recuperação

- **Scale:** "Frontend escalado para 4 réplicas com um comando"
- **Delete pod:** "Pod apagado: o Kubernetes cria outro sozinho"
- **Limitação:** "Backends com 1 réplica: cada pod teria seu próprio banco H2"

## 6. Testes

- **mvn test:** "30 testes no monólito, 17 no categoria-service"
- **Integração:** "Teste de integração: API, serviço e banco testados juntos"
- **Unitários:** "Testes unitários: serviço, repositórios e mensageria isolados"

## 7. CI/CD

- **Workflow:** "A cada push: testes, build do frontend e publicação das imagens"
- **Actions:** "As imagens só são publicadas se todos os testes passarem"
- **Deploy:** "Deploy no Kubernetes manual: o cluster é local"

## 8. Versionamento

- **git log:** "Código e infraestrutura versionados juntos no GitHub"
- **Variáveis de ambiente:** "A mesma imagem roda local, no Docker e no Kubernetes"
