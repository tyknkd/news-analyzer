#!/bin/sh

# Adapted from:
# https://github.com/docker-library/docs/blob/master/postgres/README.md#initialization-scripts
# https://github.com/mrts/docker-postgresql-multiple-databases

set -e  # Exit script immediately if command exits with non-zero status
IFS=',' # Internal field separator

for database in $ADDITIONAL_POSTGRES_DB; do
  echo "Creating database: '$database'"
  psql -v ON_ERROR_STOP=1 --host "$POSTGRES_HOST" --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "create database $database"
done
