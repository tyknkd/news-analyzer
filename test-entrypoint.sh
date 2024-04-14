#!/bin/sh

# Extract Docker secret files to app environment
export POSTGRES_PASSWORD=${POSTGRES_PASSWORD:=$(cat "${POSTGRES_PASSWORD_FILE}")}
export NEWS_API_KEY=${NEWS_API_KEY:=$(cat "${NEWS_API_KEY_FILE}")}

gradle clean test
