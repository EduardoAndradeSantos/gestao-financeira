
# 💰 Gestão Financeira – API REST

## 📌 Objetivo do Projeto
Esta aplicação tem como objetivo fornecer uma **API RESTful para gestão financeira pessoal**, permitindo que usuários realizem:
- Controle de receitas e despesas
- Transferências entre contas
- Geração de relatórios financeiros
- Análises consolidadas por categoria

O foco do projeto está em **boas práticas de arquitetura, segurança, domínio e testes**, simulando um cenário real de backend corporativo.

---

## 🧱 Arquitetura
O projeto segue uma arquitetura em camadas bem definida:

- **Controller** → Camada de entrada (API REST)
- **Service** → Regras de negócio e domínio
- **Repository** → Persistência de dados (JPA)
- **DTOs** → Contratos de entrada e saída
- **Security** → Autenticação, autorização e contexto JWT
- **Config** → Configurações globais da aplicação

Padrões utilizados:
- MVC (adaptado para API REST)
- DTO Pattern
- Separation of Concerns
- Stateless Authentication (JWT)

---

## 🔐 Segurança e Autenticação

A autenticação é baseada em **JWT (JSON Web Token)**.

### Fluxo de autenticação:
1. Usuário realiza login via `/auth/login`
2. A API valida e-mail e senha
3. Um token JWT é gerado e retornado
4. O token deve ser enviado no header:
   ```
   Authorization: Bearer <token>
   ```
5. O usuário autenticado é identificado automaticamente pelo backend

### Contexto do usuário
O sistema **não recebe usuário ou conta via request**.
Essas informações são sempre obtidas a partir do token JWT, garantindo segurança e consistência.

### Respostas de erro padronizadas
- **401 Unauthorized**
  ```json
  { "erro": "Usuário precisa estar logado para acessar este recurso" }
  ```

- **403 Forbidden**
  ```json
  { "erro": "Acesso negado" }
  ```

- **400 Bad Request**
  ```json
  { "erro": "Credenciais inválidas" }
  ```

---

## 💸 Modelo de Transações

Tipos de transação:
- **DEPOSITO**
- **RETIRADA**
- **TRANSFERENCIA**

📌 Não existe endpoint explícito de saque.  
No domínio da aplicação, qualquer saída de recursos é representada como **TRANSFERENCIA**, o que simplifica o modelo e evita duplicação de regras de negócio.

### Regras importantes:
- DEPÓSITO → não possui categoria
- RETIRADA → exige categoria
- TRANSFERENCIA → sempre utiliza endpoint específico

---

## 📊 Análises Financeiras

Endpoints disponíveis:
- `/analise/resumo`
- `/analise/despesas-por-categoria`

Permitem:
- Visualizar totais consolidados
- Agrupar despesas por categoria
- Base para geração de gráficos

---

## 📄 Relatórios

A API permite exportação de relatórios para o usuário autenticado:

### 📘 PDF
- Resumo financeiro
- Lista completa de transações

### 📗 Excel
- Aba **Relatório Financeiro** (dados completos)
- Aba **Despesas por Categoria** (base para gráfico de pizza)

📌 O layout da aba principal foi mantido estável para garantir compatibilidade.

---

## 🌎 Integração Externa

Integração com **BrasilAPI** para consulta de moedas e cotações.
Foi utilizada abstração via client dedicado, mantendo desacoplamento.

---

## 🧪 Testes

- Testes unitários focados na camada **Service** e **Controller**
- Validação de regras de negócio críticas

---

## 🐳 Docker

### Build das imagens
```bash
docker-compose build
```

### Subir os containers
```bash
docker-compose up -d
```

Serviços:
- API principal
- PostgreSQL
- Mock de saldo externo

---

## ✅ Status do Projeto
✔ Funcional  
✔ Seguro  
✔ Dockerizado
✔ Testado

---

## 📬 Contato
Projeto desenvolvido por **Edu Andrade**
