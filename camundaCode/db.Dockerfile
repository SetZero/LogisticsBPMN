FROM  postgres:13.1-alpine

COPY database/sql/10_init_tables_package.sql  /docker-entrypoint-initdb.d/