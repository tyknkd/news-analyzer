#!/bin/sh

# Extract Docker secret file to app environment
export POSTGRES_PASSWORD=${POSTGRES_PASSWORD:=$(cat ${POSTGRES_PASSWORD_FILE})}

if [ "${APP}" = "data-collector" ]; then
  export NEWS_API_KEY=${NEWS_API_KEY:=$(cat ${NEWS_API_KEY_FILE})}
fi

# Run jar file for specified app
java -jar /app/${APP}.jar