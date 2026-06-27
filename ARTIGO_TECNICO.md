# Arquitetura de Microserviços Resilientes com Padrão SAGA Orchestration: Uma Implementação com Spring Boot 4.1.0 e Java 25

**Resumo**
Este artigo apresenta o projeto SAGA, uma implementação de referência de um sistema de e-commerce baseado em microserviços que utiliza o padrão SAGA Orchestration para garantir a consistência eventual em transações distribuídas. Utilizando tecnologias de ponta como Java 25 e Spring Boot 4.1.0, o sistema integra Apache Kafka para mensageria, PostgreSQL para persistência, HashiCorp Vault para segurança de segredos e Bucket4j para controle de vazão (Rate Limiting). O trabalho descreve a arquitetura, o fluxo de compensação, as decisões de design que visam alta disponibilidade e resiliência, e os desafios encontrados na migração para o ecossistema Spring Boot 4.x.

**Palavras-chave:** Microserviços, SAGA Orchestration, Spring Boot 4.1, Java 25, Kafka, Resiliência, Transações Distribuídas, Event-Driven Architecture.

---

## 1. Introdução

Com a evolução das arquiteturas de software de monolitos para microserviços, o gerenciamento de transações que abrangem múltiplos serviços tornou-se um desafio crítico. A falta de transações atômicas (ACID) em ambientes distribuídos exige padrões que garantam a consistência dos dados sem comprometer a escalabilidade.

O Teorema CAP (Brewer, 2000) estabelece que sistemas distribuídos não podem simultaneamente garantir Consistência, Disponibilidade e Tolerância a Partições. Neste contexto, o padrão SAGA surge como uma solução pragmática para gerenciar fluxos de trabalho complexos através de uma sequência de transações locais e eventos de compensação, priorizando disponibilidade e consistência eventual (eventual consistency).

### 1.1. Motivação

Em um sistema de e-commerce, a criação de um pedido envolve:
- Validação do cliente
- Verificação da existência dos vendedores
- Confirmação dos produtos no catálogo
- Registro do pedido e seus itens
- Processamento do pagamento
- Notificação dos vendedores

Em um monolito, essas operações seriam uma única transação ACID. Em microserviços, cada operação pode pertencer a um serviço diferente, impossibilitando transações distribuídas tradicionais (2PC — Two-Phase Commit) devido à latência e ao acoplamento que introduzem.

### 1.2. Objetivos

- Implementar o padrão SAGA Orchestration para transações distribuídas
- Demonstrar boas práticas de arquitetura com Spring Boot 4.1.0 e Java 25
- Aplicar segurança em camadas (JWT, RBAC, Vault)
- Documentar as particularidades da migração para o ecossistema Spring 7.x / Jackson 3.x

---

## 2. Fundamentação Teórica

### 2.1. Padrão SAGA

O termo SAGA foi introduzido por Garcia-Molina e Salem (1987) no contexto de Long Lived Transactions (LLTs). Uma SAGA é uma sequência de transações locais T1, T2, ..., Tn, onde cada Ti possui uma transação compensatória Ci correspondente. Se Ti falha, as compensações C(i-1), C(i-2), ..., C1 são executadas na ordem reversa.

Existem dois modelos de implementação:

| Modelo | Descrição | Vantagem | Desvantagem |
|---|---|---|---|
| **Coreografia** | Cada serviço publica eventos e os demais reagem | Desacoplamento total | Difícil rastrear o fluxo |
| **Orquestração** | Um coordenador central controla o fluxo | Visibilidade completa | Ponto único de falha potencial |

Este projeto adota a **Orquestração** via `SagaOrchestrator`, que oferece visibilidade centralizada do estado da transação e facilita a implementação de compensações.

### 2.2. Event-Driven Architecture (EDA)

A arquitetura orientada a eventos permite que componentes se comuniquem de forma assíncrona via mensagens. O Apache Kafka é utilizado como event broker, garantindo:
- **Durabilidade:** Eventos persistidos em log
- **Ordem:** Garantia de ordenação por partição
- **Replay:** Possibilidade de reprocessar eventos históricos
- **Escalabilidade:** Particionamento horizontal

### 2.3. Consistência Eventual

Diferente da consistência forte (ACID), a consistência eventual garante que, dado tempo suficiente sem novas atualizações, todas as réplicas convergirão para o mesmo estado. O padrão SAGA materializa este conceito: o sistema pode estar temporariamente inconsistente durante a execução das transações locais, mas eventualmente alcança um estado consistente (via sucesso ou compensação).

---

## 3. Arquitetura do Sistema

### 3.1. Visão Geral

```
┌─────────────────────────────────────────────────────────────────┐
│                        CONTROLLER LAYER                          │
│   AuthController · CustomerController · OrderController          │
│   ProductController · SellerController · UserController           │
├─────────────────────────────────────────────────────────────────┤
│                         SERVICE LAYER                            │
│   AuthService · CustomerService · OrderService                   │
│   ProductService · SellerService · UserService                    │
├─────────────────────────────────────────────────────────────────┤
│                      SAGA ORCHESTRATION                          │
│   SagaOrchestrator · SagaEventListener                          │
│          ↕ Kafka Topics                                          │
│   order-created · seller-notified · payment-processed            │
│   order-compensate                                               │
├─────────────────────────────────────────────────────────────────┤
│                       REPOSITORY LAYER                           │
│   Spring Data JPA Repositories                                   │
├─────────────────────────────────────────────────────────────────┤
│                        INFRASTRUCTURE                            │
│   PostgreSQL · Kafka (KRaft) · Vault · Liquibase                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2. Tecnologias e Justificativas

| Tecnologia | Justificativa |
|---|---|
| **Java 25** | Últimas otimizações de GC (ZGC Generational), Virtual Threads, Pattern Matching |
| **Spring Boot 4.1.0** | Framework maduro com ecossistema completo; migração para Spring Framework 7.x |
| **Spring Security 7** | Autenticação stateless moderna com API redesenhada |
| **Jackson 3.x** | Novo namespace (`tools.jackson.databind`), melhor performance de serialização |
| **Apache Kafka (KRaft)** | Mensageria sem dependência do ZooKeeper, menor overhead operacional |
| **PostgreSQL** | Banco relacional robusto com suporte a JSON, CTEs e materialização |
| **Liquibase 5.x** | Versionamento de schema declarativo e auditável |
| **MapStruct** | Mapeamento DTO↔Entity compilado, sem reflection em runtime |
| **Bucket4j** | Rate limiting em memória com algoritmo Token Bucket |
| **HashiCorp Vault** | Secrets management centralizado com rotação automática |

### 3.3. Modelo de Dados

O sistema utiliza o dataset Brazilian E-Commerce (Olist) com ~500k registros distribuídos em:
- **customers** (~99k) — Clientes com localização
- **orders** (~99k) — Pedidos com timestamps de ciclo de vida
- **order_items** (~112k) — Itens vinculando pedido ↔ produto ↔ vendedor
- **order_payments** (~103k) — Pagamentos com tipo e parcelamento
- **products** (~32k) — Catálogo com dimensões e categoria
- **sellers** (~3k) — Vendedores com localização
- **geolocation** (~1M) — Coordenadas por CEP

---

## 4. Implementação do Padrão SAGA

### 4.1. Fluxo de Criação de Pedido (Happy Path)

```
Cliente → POST /api/v1/orders
              │
              ▼
    ┌─────────────────────┐
    │  Validações Pré-SAGA │
    │  • Customer existe?  │
    │  • Seller(s) existe? │
    │  • Product(s) existe?│
    └──────────┬──────────┘
               │ OK
               ▼
    ┌─────────────────────┐
    │  SagaEvent: STARTED  │
    └──────────┬──────────┘
               │
               ▼
    ┌─────────────────────┐
    │  Persistir:          │
    │  • Order             │
    │  • OrderItems        │
    │  • OrderPayment      │
    └──────────┬──────────┘
               │
               ▼
    ┌─────────────────────┐
    │  SagaEvent: COMPLETED│
    └──────────┬──────────┘
               │
          ┌────┼────────┐
          ▼              ▼
  ┌──────────────┐  ┌──────────────┐
  │ Kafka:       │  │ Kafka:       │
  │ order-created│  │seller-notified│
  └──────────────┘  └──────────────┘
```

### 4.2. Validações Pré-SAGA

Antes de iniciar a transação, o `SagaOrchestrator` executa validações síncronas:

```java
customerRepository.findById(request.getCustomerId())
    .orElseThrow(() -> new ResourceNotFoundException("Customer não encontrado"));

for (var item : request.getItems()) {
    sellerRepository.findById(item.getSellerId())
        .orElseThrow(() -> new ResourceNotFoundException("Seller não encontrado"));
    productRepository.findById(item.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Product não encontrado"));
}
```

Se qualquer entidade não existir, retorna HTTP 404 sem iniciar a SAGA. Isso evita compensações desnecessárias para erros de validação previsíveis.

### 4.3. Mecanismo de Compensação

```
Falha durante persistência
         │
         ▼
┌─────────────────────┐
│  SagaEvent: FAILED   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────┐
│  Kafka: order-compensate │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│  SagaEventListener:      │
│  • Reverte operações     │
│  • Registra compensação  │
└─────────────────────────┘
```

O princípio é: cada ação deve ter uma contra-ação idempotente. A idempotência é fundamental para que reprocessamentos (em caso de falha do próprio listener) não causem efeitos colaterais.

### 4.4. Notificação de Vendedores

Após a criação bem-sucedida do pedido, o sistema notifica cada vendedor envolvido:

```java
request.getItems().stream()
    .map(OrderRequest.OrderItemRequest::getSellerId)
    .distinct()
    .forEach(sellerId -> kafkaTemplate.send(SELLER_NOTIFIED_TOPIC, sellerId, orderId));
```

O uso de `sellerId` como key do Kafka garante que eventos do mesmo vendedor vão para a mesma partição, mantendo a ordenação.

### 4.5. Rastreabilidade

Todos os eventos são persistidos na tabela `saga_events`:

| Campo | Descrição |
|---|---|
| `saga_id` | UUID da transação distribuída |
| `event_type` | ORDER_CREATION, COMPENSATION |
| `status` | STARTED, PROCESSING, COMPLETED, COMPENSATING, FAILED |
| `payload` | Dados serializados (JSON) |
| `created_at` | Timestamp do evento |

Isso permite auditoria completa e debugging de transações complexas.

---

## 5. Segurança e Resiliência

### 5.1. Autenticação JWT

O sistema implementa autenticação stateless com dois tokens:

| Token | TTL | Armazenamento | Uso |
|---|---|---|---|
| **Access Token** | 15min | Cliente (header) | Autenticação de requisições |
| **Refresh Token** | 24h | Banco de dados | Renovação do access token |

**Token Rotation:** Ao usar o refresh token, o anterior é revogado e um novo par é emitido. Isso limita a janela de ataque em caso de vazamento.

### 5.2. RBAC (Role-Based Access Control)

```
┌─────────┐     ┌────────────┐     ┌───────┐
│  users  │◄───▶│ user_roles │◄───▶│ roles │
└─────────┘     └────────────┘     └───────┘
```

- **ROLE_USER:** Acesso a endpoints de leitura e criação de pedidos
- **ROLE_ADMIN:** Gerenciamento completo de usuários e roles

Implementado via `@PreAuthorize("hasRole('ADMIN')")` com `@EnableMethodSecurity`.

### 5.3. Secrets Management (Vault)

Em produção, credenciais são recuperadas do HashiCorp Vault:
- Credenciais de banco de dados
- Chave de assinatura JWT
- Tokens de integração

Em desenvolvimento, o sistema usa defaults do `application.yml` como fallback.

### 5.4. Rate Limiting

O algoritmo Token Bucket protege contra abuso:

```
Bucket: [capacity=100, refill=100 tokens/60s]

Requisição → Bucket tem tokens? → SIM → Processa (header: X-Rate-Limit-Remaining)
                                 → NÃO → HTTP 429 (header: X-Rate-Limit-Retry-After-Seconds)
```

Aplicado por IP do cliente em todos os endpoints `/api/**`.

---

## 6. Desafios da Migração para Spring Boot 4.1.0

A adoção do Spring Boot 4.1.0 (baseado no Spring Framework 7.x) trouxe breaking changes significativas:

### 6.1. Jackson 3.x

O `ObjectMapper` migrou de `com.fasterxml.jackson.databind` para `tools.jackson.databind`. A auto-configuração do Spring Boot não registra mais o bean automaticamente em todos os cenários, exigindo um `@Bean` explícito via `JacksonConfig`.

### 6.2. Spring Security 7

O `DaoAuthenticationProvider` não aceita mais configuração via setters — o `UserDetailsService` deve ser passado no construtor:

```java
// Spring Security 6.x (antes)
provider.setUserDetailsService(userDetailsService);

// Spring Security 7.x (agora)
new DaoAuthenticationProvider(userDetailsService);
```

### 6.3. Kafka Auto-Configuration

O `KafkaTemplate` não é mais auto-configurado se o broker não responder durante a inicialização. A solução: configuração explícita do `ProducerFactory` + `KafkaTemplate` via `KafkaConfig`, garantindo que o bean existe independente do estado do broker.

### 6.4. Liquibase Starter

No Spring Boot 4.1, usar apenas `liquibase-core` não ativa a auto-configuração. É necessário o `spring-boot-starter-liquibase` que inclui `spring-boot-liquibase` (módulo de auto-configuração).

### 6.5. JPA e Liquibase — Ordem de Inicialização

Com `ddl-auto: validate`, o Hibernate tenta validar o schema antes do Liquibase executar, causando `SchemaManagementException`. A solução é usar `ddl-auto: none` e delegar 100% do schema ao Liquibase.

---

## 7. Padrões e Boas Práticas Aplicados

| Padrão | Aplicação no Projeto |
|---|---|
| **SAGA Orchestration** | Coordenação centralizada de transações distribuídas |
| **Event-Driven Architecture** | Comunicação assíncrona via Kafka topics |
| **HATEOAS (REST Level 3)** | Hypermedia links nos responses para navegabilidade |
| **DTO Pattern** | Separação entre representação interna e externa |
| **MapStruct** | Mapeamento typesafe compilado |
| **Bean Validation** | Validação declarativa de inputs |
| **Global Exception Handler** | Tratamento centralizado de erros |
| **Repository Pattern** | Abstração de acesso a dados |
| **Token Rotation** | Refresh token single-use para segurança |
| **12-Factor App** | Configuração via variáveis de ambiente |
| **Database Versioning** | Schema gerenciado por Liquibase |
| **Idempotência** | Compensações seguras para reprocessamento |

---

## 8. Conclusão

A implementação do projeto SAGA demonstra que é possível construir sistemas distribuídos altamente complexos mantendo a clareza arquitetural e a resiliência. O uso de orquestração via Kafka provou ser eficaz para gerenciar o ciclo de vida de pedidos em um e-commerce, permitindo escalabilidade horizontal e tolerância a falhas.

A validação pré-SAGA (customer, seller, product) antes de iniciar a transação reduz significativamente compensações desnecessárias, melhorando a eficiência do sistema. A notificação dos vendedores via eventos Kafka possibilita que o sistema evolua para cenários mais complexos (confirmação de estoque, preparação de envio) sem alterar o fluxo principal.

A adoção do Spring Boot 4.1.0 e Java 25 posiciona o projeto na vanguarda do ecossistema Java, embora exija atenção às breaking changes documentadas na seção 6. As lições aprendidas nesta migração servem como referência para equipes que planejam adotar estas versões.

---

## 9. Trabalhos Futuros

- **Circuit Breaker:** Implementação de Resilience4j para falhas em cascata
- **Distributed Tracing:** Integração com OpenTelemetry para rastreamento cross-service
- **Event Sourcing:** Evolução do `saga_events` para event store completo
- **CQRS:** Separação de modelos de leitura e escrita para escalabilidade
- **Seller Confirmation Flow:** SAGA step adicional onde o seller confirma disponibilidade antes do pagamento

---

## 10. Referências Bibliográficas

1. **GARCIA-MOLINA, Hector; SALEM, Kenneth.** Sagas. ACM SIGMOD Record, v. 16, n. 3, p. 249-259, 1987.
2. **RICHARDSON, Chris.** Microservices Patterns: With examples in Java. Manning Publications, 2018.
3. **NEWMAN, Sam.** Building Microservices: Designing Fine-Grained Systems. 2nd ed. O'Reilly Media, 2021.
4. **KLEPPMANN, Martin.** Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems. O'Reilly Media, 2017.
5. **BREWER, Eric A.** Towards Robust Distributed Systems. ACM Symposium on Principles of Distributed Computing, 2000.
6. **SPRING PROJECTS.** Spring Boot Reference Documentation v4.1.0. Disponível em: <https://docs.spring.io/spring-boot/documentation.html>. Acesso em: 27 jun. 2026.
7. **APACHE KAFKA.** Apache Kafka Documentation. Disponível em: <https://kafka.apache.org/documentation/>. Acesso em: 27 jun. 2026.
8. **HASHICORP.** Vault Documentation. Disponível em: <https://www.vaultproject.io/docs>. Acesso em: 27 jun. 2026.
9. **FOWLER, Martin.** Event-Driven Architecture. Disponível em: <https://martinfowler.com/articles/201701-event-driven.html>. Acesso em: 27 jun. 2026.
10. **ORACLE.** JDK 25 Release Notes. Disponível em: <https://docs.oracle.com/en/java/javase/25/>. Acesso em: 27 jun. 2026.
