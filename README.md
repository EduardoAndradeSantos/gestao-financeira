# Gestão Financeira – Backend API

API REST desenvolvida em **Java 21 + Spring Boot** para gestão financeira básica, com controle de usuários, transações, transferências, análises financeiras e relatórios.

O projeto foi desenvolvido como **POC (Prova de Conceito)**, priorizando clareza de regras de negócio, organização em camadas e fácil evolução.

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


---

## 📌 Entidades Principais

### 👤 Usuario
Representa o titular da conta.

Campos principais:
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

Campos principais:
- id
- descricao
- valor
- dataHora
- tipo (DEPOSITO, RETIRADA, TRANSFERENCIA)
- categoria (ALIMENTACAO, LAZER, TRANSPORTE, MORADIA, SAUDE, OUTROS)
- usuario
- contaRelacionada (usada em transferências)

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
    - Débito (origem)
    - Crédito (destino)
- Categoria automaticamente definida como `OUTROS`
- Saldo da conta de origem é validado antes da operação

---

## 🔗 Endpoints da API

### 👤 Usuários

| Método | Endpoint        | Descrição                 |
|------|----------------|---------------------------|
| GET  | /usuarios       | Lista todos os usuários   |
| POST | /usuarios       | Cria um novo usuário      |
| GET  | /usuarios/{id}  | Busca usuário por ID      |
| PUT  | /usuarios/{id}  | Atualiza usuário          |
| DELETE | /usuarios/{id} | Remove usuário            |

---

### 💰 Transações

| Método | Endpoint |
|------|---------|
| POST | /transacoes |
| POST | /transacoes/por-conta |
| POST | /transacoes/transferir |
| GET  | /transacoes |
| GET  | /transacoes/{id} |
| GET  | /transacoes/por-conta/{numeroConta} |
| PUT  | /transacoes/{id} |
| DELETE | /transacoes/{id} |

---

### 📊 Análise Financeira

| Método | Endpoint |
|------|---------|
| GET | /analise/resumo/{numeroConta} |
| GET | /analise/despesas-por-categoria/{numeroConta} |

---

### 📄 Relatórios

| Método | Endpoint |
|------|---------|
| GET | /relatorios/excel/{numeroConta} |
| GET | /relatorios/pdf/{numeroConta} |

---

## ⚠️ Tratamento de Erros

Todas as exceções são centralizadas em `GlobalExceptionHandler`.

Padrão de resposta de erro:
```json
{
  "timestamp": "2026-01-12T19:40:23",
  "status": 404,
  "error": "Mensagem de erro"
}

🧪 Status do Projeto

✔️ Backend funcional
✔️ Regras de negócio implementadas
✔️ Relatórios funcionando
✔️ Estrutura pronta para evolução

🚫 Integração com câmbio foi descartada (mantido como backup conceitual)

🚀 Próximos passos sugeridos

Autenticação (JWT)

Paginação e filtros

Testes automatizados

Frontend (Angular)

Docker Compose (API + DB)

-----------------------------------------

✅ Situação atual

✔️ Backend bem documentado
✔️ Swagger completo
✔️ README pronto para avaliação técnica
✔️ Base sólida para continuar depois

Se quiser, no próximo passo posso:

Gerar testes

Criar Docker Compose

Preparar versão final para entrega

Reintroduzir câmbio corretamente (se mudar de ideia)