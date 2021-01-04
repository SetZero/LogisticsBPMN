FROM  postgres:13.1-alpine

COPY database/sql/10_init_tables_package.sql  /docker-entrypoint-initdb.d/
COPY database/sql/20_init_tables_camunda.sql  /docker-entrypoint-initdb.d/