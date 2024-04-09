#!/bin/sh

# Extract Docker secret file to app environment
export POSTGRES_PASSWORD=${POSTGRES_PASSWORD:=$(cat "${POSTGRES_PASSWORD_FILE}")}

if [ "${APP}" = "data-collector" ]; then
  export NEWS_API_KEY=${NEWS_API_KEY:=$(cat "${NEWS_API_KEY_FILE}")}
  java -jar /app/"${APP}".jar
elif [ "${APP}" = "data-analyzer" ]; then
  # Java options for Spark-Java17 compatibility
  # https://github.com/apache/spark/blob/v3.3.0/launcher/src/main/java/org/apache/spark/launcher/JavaModuleOptions.java
  java \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    --add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
    --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
    --add-opens=java.base/java.io=ALL-UNNAMED \
    --add-opens=java.base/java.net=ALL-UNNAMED \
    --add-opens=java.base/java.nio=ALL-UNNAMED \
    --add-opens=java.base/java.util=ALL-UNNAMED \
    --add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
    --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED \
    --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
    --add-opens=java.base/sun.nio.cs=ALL-UNNAMED \
    --add-opens=java.base/sun.security.action=ALL-UNNAMED \
    --add-opens=java.base/sun.util.calendar=ALL-UNNAMED \
    --add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED \
    -jar /app/"${APP}".jar
else
  java -jar /app/"${APP}".jar
fi