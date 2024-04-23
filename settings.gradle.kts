rootProject.name = "news-analyzer"
include(
    "applications:web-server",
    "applications:data-collector",
    "applications:data-analyzer",
    "components:data-support",
    "components:http-support",
    "components:test-support",
    "components:mq-support",
    "components:scheduler"
)