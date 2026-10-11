# Lightweight Java 17 JRE (Alpine, non-root ready)
FROM eclipse-temurin:17-jre-alpine

# Install required libraries for fonts
RUN apk add --no-cache \
    freetype \
    fontconfig \
    ttf-dejavu \
    curl

# Create non-root user & application directory
RUN addgroup -S app && adduser -S -G app -u 65532 app \
  && mkdir -p /app \
  && chown -R app:app /app

USER 65532:65532
WORKDIR /app

# JVM defaults
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/urandom"

# Jar path & runtime args
ENV APP_JAR=/app/app.jar \
    APP_ARGS=""

# Copy application JAR
COPY --chown=app:app app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
