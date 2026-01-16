# 💰 Gestão Financeira – Backend API

API REST desenvolvida em **Java 21 + Spring Boot** para simular um sistema de gestão financeira bancária, com foco em **arquitetura limpa**, **boas práticas** e **integração com serviços externos**.

O projeto foi construído como **prova técnica / POC**, priorizando clareza de domínio, desacoplamento e facilidade de evolução.

---

## 📌 Objetivo

Permitir que usuários:

* Gerenciem seu perfil
* Realizem transações financeiras (depósitos e transferências)
* Consultem análises financeiras
* Gerem relatórios em PDF e Excel

Além disso, o projeto demonstra:

* Autenticação e autorização com JWT
* Integração com API externa (mock de saldo)
* Uso de Docker para padronização de ambiente
* Testes unitários focados em regras de negócio

---

## 🛠️ Stack e Tecnologias

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security (JWT stateless)
* PostgreSQL
* Docker / Docker Compose
* OpenAPI / Swagger
* Apache POI (Excel)
* OpenPDF (PDF)
* JUnit 5 + Mockito

---

## 🏗️ Arquitetura do Projeto

O projeto segue uma **arquitetura em camadas**, separando claramente responsabilidades:

```
controller  → Camada de API (REST)
service     → Regras de negócio
repository  → Persistência (JPA)
dto         → Contratos de entrada e saída
security    → Autenticação e contexto do usuário
config      → Configurações gerais
```

Essa separação facilita testes, manutenção e evolução do sistema.

---

## 🔐 Segurança

### Autenticação

* JWT (stateless)
* Login via email e senha
* Token contém:

    * `usuarioId`
    * `numeroConta`
    * `role`

### Autorização

* Roles:

    * `ROLE_USER`
    * `ROLE_ADMIN`
* Controle via `@PreAuthorize` e validações no service

📌 Todas as operações sensíveis utilizam o **usuário logado obtido a partir do token**, não por parâmetros de requisição.

---

## 💸 Modelo de Transações

O domínio financeiro foi modelado com base em **movimentações**, não em saldo persistido.

### Tipos suportados

* **DEPÓSITO** → entrada de recursos
* **TRANSFERÊNCIA** → saída e entrada entre contas

📌 Não existe endpoint explícito de saque.
Transferência representa qualquer débito de saldo, mantendo o modelo simples e coerente.

---

## 🌐 Integrações Externas

### 🔹 API Mock de Saldo

O saldo do usuário **não é persistido no banco**.
Ele é obtido a partir de uma **API externa mockada**, simulando um core bancário.

#### Contrato

```
GET /saldo/{numeroConta}

{
  "numeroConta": "70806207",
  "saldo": 2500.75
}
```

#### Decisão técnica

* Evita duplicidade de estado
* Simula arquitetura bancária real
* Prepara o backend para integrações futuras

📌 Nesta fase, o serviço de saldo é **mockado e dockerizado**.

---

### 🔹 BrasilAPI – Câmbio

Foi integrada a **BrasilAPI** para permitir **consulta de moedas disponíveis** e **cotações do Real em relação a moedas estrangeiras**, exclusivamente para fins de consulta.

#### Endpoints expostos pelo backend

```
GET /cambio/moedas
GET /cambio/{moeda}/{data}
```

#### Observações importantes

* Integração **somente leitura**
* Nenhuma persistência em banco
* Nenhuma dependência do domínio financeiro
* Implementada como client isolado

📌 A integração foi projetada para **não impactar regras de negócio existentes**, mantendo o core da aplicação estável.

---

## 🧪 Testes

O projeto possui **testes unitários focados em regras de negócio**, cobrindo:

* `TransacaoService`
* `UsuarioService`
* `AnaliseFinanceiraService`
* `SaldoClient`

Características:

* JUnit 5 + Mockito
* Sem subir Spring Context
* Sem banco real
* Mock do contexto de segurança (`SecurityUtils`)

📌 Testes de PDF/Excel e controllers foram propositalmente deixados fora do escopo inicial.

---

## 🐳 Docker

O projeto utiliza Docker para padronizar o ambiente.

### Serviços dockerizados

* PostgreSQL
* API mock de saldo
* Backend Spring Boot

### Subir tudo via Docker

```bash
docker-compose up --build -d
```

### Desenvolvimento local (recomendado)

* Backend rodando pela IDE
* Docker apenas para Postgres e mock

---

## ▶️ Como Executar

### Pré-requisitos

* Java 21
* Docker e Docker Compose

### Rodar localmente

```bash
./mvnw spring-boot:run
```

### Rodar com Docker

```bash
docker-compose up --build -d
```

A aplicação ficará disponível em:

```
http://localhost:8080
```

---

## 📖 Documentação da API

Swagger UI disponível em:

```
http://localhost:8080/swagger-ui.html
```

---

## 🧠 Decisões Técnicas Importantes

* **Saldo externo**: evita inconsistência e simula core bancário
* **BrasilAPI isolada**: integração externa somente leitura, sem acoplamento ao domínio
* **Records em DTOs**: imutabilidade e clareza de contrato
* **Sem saque explícito**: domínio baseado em movimentações
* **Importação Excel sem @Transactional**: permite importação parcial
* **Backend stateless**: escalável e alinhado a microsserviços

---

## 🚧 Fora do Escopo (Consciente)

* Sincronização de saldo com transações
* Cache / Redis
* Circuit breaker
* Observabilidade avançada
* Frontend

Esses pontos foram deixados fora propositalmente para manter foco no escopo principal.

---

## ✅ Status Final

✔️ Backend funcional
✔️ Arquitetura clara
✔️ Integração externa demonstrada
✔️ Dockerizado
✔️ Testado
✔️ Pronto para avaliação técnica

---

## 👤 Autor

Projeto desenvolvido como prova técnica para fins de demonstração de arquitetura e boas práticas em backend Java.
