FROM  postgis/postgis:11-3.1-alpine

COPY database/sql/000_init_database.sql  /docker-entrypoint-initdb.d/
COPY database/sql/110_init_tables_package.sql  /docker-entrypoint-initdb.d/
COPY database/sql/020_init_tables_camunda.sql  /docker-entrypoint-initdb.d/