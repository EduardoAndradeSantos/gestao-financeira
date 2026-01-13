# 💰 Gestão Financeira – Backend API

API REST desenvolvida em **Java 21 + Spring Boot** para gestão financeira básica, com controle de usuários, transações, transferências, análises financeiras e geração de relatórios.

O projeto foi desenvolvido como **POC (Prova de Conceito)**, priorizando **clareza das regras de negócio**, **organização em camadas** e **facilidade de evolução**.

---

## 🛠️ Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Bean Validation (Jakarta Validation)
- Lombok
- OpenAPI / Swagger
- Apache POI (Excel)
- OpenPDF (PDF)

---

## 🗂️ Arquitetura do Projeto

com.ntt.gestao.financeira
├── controller # Endpoints REST
├── dto
│ ├── request # DTOs de entrada
│ └── response # DTOs de saída
├── entity # Entidades JPA
├── exception # Exceções e handler global
├── repository # Repositórios JPA
├── service # Regras de negócio
└── Application # Classe principal


A aplicação segue uma **arquitetura em camadas**, separando responsabilidades entre **API**, **regras de negócio** e **persistência**.

---

## 📌 Entidades Principais

### 👤 Usuario
Representa o titular da conta bancária.

**Campos principais:**
- id
- nome
- cpf (único)
- email (único)
- endereco
- senha
- numeroConta (único)

---

### 💰 Transacao
Representa qualquer movimentação financeira.

**Campos principais:**
- id
- descricao
- valor
- dataHora
- tipo (`DEPOSITO`, `RETIRADA`, `TRANSFERENCIA`)
- categoria (`ALIMENTACAO`, `LAZER`, `TRANSPORTE`, `MORADIA`, `SAUDE`, `OUTROS`)
- usuario
- contaRelacionada (utilizada em transferências)

---

## 🔁 Regras de Negócio Importantes

### ✔️ Depósito
- Não possui categoria
- Sempre soma ao saldo

### ✔️ Retirada
- Categoria é obrigatória
- Subtrai do saldo

### ✔️ Transferência
- Gera duas transações:
    - Débito na conta de origem
    - Crédito na conta de destino
- Categoria automaticamente definida como `OUTROS`
- Saldo da conta de origem é validado antes da operação

---

## 🔗 Endpoints da API

### 👤 Usuários

| Método | Endpoint | Descrição |
|------|---------|----------|
| GET | `/usuarios` | Lista todos os usuários |
| POST | `/usuarios` | Cria um novo usuário |
| GET | `/usuarios/{id}` | Busca usuário por ID |
| PUT | `/usuarios/{id}` | Atualiza usuário |
| DELETE | `/usuarios/{id}` | Remove usuário |

---

### 💰 Transações

| Método | Endpoint |
|------|---------|
| POST | `/transacoes` |
| POST | `/transacoes/por-conta` |
| POST | `/transacoes/transferir` |
| GET | `/transacoes` |
| GET | `/transacoes/{id}` |
| GET | `/transacoes/por-conta/{numeroConta}` |
| PUT | `/transacoes/{id}` |
| DELETE | `/transacoes/{id}` |

---

### 📊 Análise Financeira

| Método | Endpoint |
|------|---------|
| GET | `/analise/resumo/{numeroConta}` |
| GET | `/analise/despesas-por-categoria/{numeroConta}` |

---

### 📄 Relatórios

| Método | Endpoint |
|------|---------|
| GET | `/relatorios/excel/{numeroConta}` |
| GET | `/relatorios/pdf/{numeroConta}` |

---

## ⚠️ Tratamento de Erros

Todas as exceções são centralizadas em `GlobalExceptionHandler`.

**Formato padrão de resposta:**

{
  "timestamp": "2026-01-12T19:40:23",
  "status": 404,
  "error": "Mensagem de erro"
}

---

## ▶️ Como Executar o Projeto
Pré-requisitos
Java 21
Docker e Docker Compose

Executando com Docker:
docker-compose up -d

A API ficará disponível em:
http://localhost:8080

Executando localmente (sem Docker)
./mvnw spring-boot:run

---

## 📘 Documentação da API

A documentação está disponível via Swagger UI, cobrindo os principais endpoints e contratos.

http://localhost:8080/swagger-ui.html

## 🧠 Decisões Técnicas

BigDecimal foi utilizado para valores monetários, evitando erros de precisão.

Transferências geram duas transações para manter histórico financeiro consistente.

O saldo é calculado via consulta agregada no banco, evitando inconsistência de estado.

Categoria é opcional para permitir modelagem adequada entre tipos de transação.

## 🔐 Segurança

A autenticação e autorização não foram implementadas nesta fase, pois o foco da POC foi modelagem de domínio e regras de negócio.

A estrutura já está preparada para futura inclusão de:
Spring Security
JWT
Criptografia de senha

## 🧪 Status do Projeto

✔️ Backend funcional
✔️ Regras de negócio implementadas
✔️ Relatórios funcionando
✔️ Documentação clara
✔️ Base sólida para evolução

## 🚫 Integração com câmbio foi descartada nesta fase (mantida apenas como conceito)

## 🚀 Próximos Passos

Implementar autenticação (JWT)
Paginação e filtros
Testes automatizados
Frontend (Angular)
Evoluir Docker Compose (API + DB)