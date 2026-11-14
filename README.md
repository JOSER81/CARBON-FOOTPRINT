Calculadora de Pegada de Carbono — Java OO + PostgreSQL

Aplicação Java com arquitetura em camadas, CRUD completo e banco **PostgreSQL**.
Projeto pronto para abrir no **IntelliJ IDEA** (Maven).

## O que mudou em relação à versão SQLite

| Item | SQLite (antes) | PostgreSQL (agora) |
|---|---|---|
| Driver (pom.xml) | `org.xerial:sqlite-jdbc` | `org.postgresql:postgresql:42.7.3` |
| Conexão | arquivo `carbon_footprint.db` | servidor `localhost:5432` (usuário/senha) |
| Chave primária | `INTEGER PRIMARY KEY AUTOINCREMENT` | `SERIAL` |
| Datas | `TEXT` (string ISO) | `DATE` e `TIMESTAMP` reais |
| Números reais | `REAL` | `DOUBLE PRECISION` |
| Configuração | fixa no código | `db.properties` + variáveis de ambiente |

Arquivos alterados: `pom.xml`, `DatabaseConnection`, `DatabaseInitializer`, `UsuarioDAO`, `CalculoDAO`.
Novos: `db.properties`, `sql/schema.sql`, `docker-compose.yml`.
Service, Model e UI **não mudaram** (a arquitetura em camadas isolou a troca do banco).

## Como rodar

### 1. Subir o PostgreSQL (escolha uma opção)

**Opção A — Docker (mais fácil):**
```bash
docker compose up -d
```
Já cria o banco `carbon_footprint` com usuário `postgres` / senha `postgres`.

**Opção B — PostgreSQL instalado na máquina:**
```sql
-- no psql ou pgAdmin, como superusuário:
CREATE DATABASE carbon_footprint;
```

### 2. Conferir as credenciais
Edite `src/main/resources/db.properties` se o seu usuário/senha forem diferentes:
```properties
db.url=jdbc:postgresql://localhost:5432/carbon_footprint
db.user=postgres
db.password=postgres
```
Ou use variáveis de ambiente (têm prioridade): `DB_URL`, `DB_USER`, `DB_PASSWORD`.
No IntelliJ: *Run → Edit Configurations → Main → Environment variables*.

### 3. Abrir e executar no IntelliJ
1. `File → Open` → selecione esta pasta
2. Clique em **Load Maven Project** (baixa o driver do PostgreSQL)
3. Selecione **Main** no topo e clique em  Run

As tabelas são criadas automaticamente na primeira execução (`DatabaseInitializer`).
Se preferir criar na mão, use `sql/schema.sql`.

### 4. Ver os dados no IntelliJ
`View → Tool Windows → Database` → **+** → **Data Source → PostgreSQL** →
host `localhost`, porta `5432`, banco `carbon_footprint`, usuário/senha → *Test Connection*.

## Problemas comuns

| Erro | Causa provável |
|---|---|
| `Connection refused` | PostgreSQL não está rodando (suba o Docker ou o serviço) |
| `database "carbon_footprint" does not exist` | Falta executar `CREATE DATABASE carbon_footprint;` |
| `password authentication failed` | Usuário/senha diferentes do `db.properties` |
| `Driver PostgreSQL não encontrado` | Recarregue o Maven (ícone  na aba Maven) |

## Estrutura

```
carbon-footprint-postgres/
├── pom.xml
├── docker-compose.yml
├── sql/schema.sql
├── .idea/                      (configuração do IntelliJ + run config "Main")
└── src/main/
    ├── resources/db.properties
    └── java/com/carbonfootprint/
        ├── Main.java
        ├── model/      (EmissaoFator + 5 subclasses, Usuario, CalculoResult)
        ├── database/   (DatabaseConnection, DatabaseInitializer)
        ├── dao/        (GenericDAO, UsuarioDAO, CalculoDAO)
        ├── service/    (UsuarioService, CalculoService)
        └── ui/         (ConsoleUI)
```
