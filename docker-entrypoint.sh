#!/bin/sh

if [ "${APP}" = "data-collector" ]; then
  # Extract Docker secret files to app environment
  export POSTGRES_PASSWORD=${POSTGRES_PASSWORD:=$(cat ${POSTGRES_PASSWORD_FILE})}
  export NEWS_API_KEY=${NEWS_API_KEY:=$(cat ${NEWS_API_KEY_FILE})}
fi

# Run jar file for specified app
java -jar /app/${APP}.jar