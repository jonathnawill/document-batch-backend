# document-batch-backend

API REST para gerenciamento de lotes de documentos. Feita com Spring Boot 4, H2 in-memory por padrão e suporte a PostgreSQL via variáveis de ambiente.

## Requisitos

- Java 21
- Maven (ou use o wrapper `./mvnw`)

## Como rodar

```bash
cd document-batch-backend
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080/document-batch`.

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/lotes` | Cria um novo lote com documentos |
| `GET` | `/api/lotes` | Lista lotes com paginação e filtros |
| `PATCH` | `/api/lotes/{id}/status` | Atualiza o status de um lote |

### Filtros do GET /api/lotes

| Parâmetro | Tipo | Default | Descrição |
|-----------|------|---------|-----------|
| `status` | string | — | `PENDENTE`, `EXPORTADO` ou `REJEITADO` |
| `operador` | string | — | Busca parcial, sem distinção de maiúsculas |
| `page` | int | 0 | Número da página |
| `size` | int | 10 | Registros por página |

### Exemplo de criação de lote

```json
POST /api/lotes
{
  "operador": "joao.silva",
  "processo": "ABERTURA_CONTA",
  "documentos": [
    { "tipo": "RG",  "nome": "rg_frente.jpg" },
    { "tipo": "CPF", "nome": "cpf.pdf" }
  ]
}
```

## Regra de negócio

Lotes com status `EXPORTADO` são imutáveis. Qualquer tentativa de alterar o status retorna `422 Unprocessable Entity`.

## Banco de dados

Por padrão o projeto usa H2 in-memory — não precisa instalar nada. O console está disponível em:

```
http://localhost:8080/document-batch/h2-console
JDBC URL: jdbc:h2:mem:testdb
Usuário:  sa
Senha:    (em branco)
```

### Usando PostgreSQL

Ajuste o `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/documentbatch
spring.datasource.username=postgres
spring.datasource.password=sua_senha
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

Rode o script DDL antes de subir a aplicação. Há duas formas:

**Opção 1 — DBeaver (recomendado)**

1. Conecta no banco `documentbatch`
2. Botão direito no banco → **Ferramentas** → **Executar script**
3. Seleciona o arquivo `sql/schema.sql`

**Opção 2 — SQL Shell (psql)**

Abre o SQL Shell pelo menu iniciar, conecta no banco `documentbatch` e executa:

```sql
\i 'caminho/para/sql/schema.sql'
```

Ou via terminal, se o `psql` estiver no PATH:

```bash
psql -U postgres -d documentbatch -f ../sql/schema.sql
```

## Documentação da API

Swagger UI: `http://localhost:8080/document-batch/swagger-ui.html`

## Testes

```bash
./mvnw test
```

Cobre o `LoteService` (11 testes unitários com Mockito) e o `LoteController` (8 testes de camada HTTP com MockMvc), além do teste de contexto do Spring.
