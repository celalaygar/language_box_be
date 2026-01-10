# 1. Build aşaması (Multi-stage build)
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy files
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean bootJar -x test

# 2. Runtime aşaması
FROM eclipse-temurin:21-jre
WORKDIR /app

# Zaman dilimi ayarları
# Güncel saat dilimi verisini almak için `tzdata` paketini kurun
RUN apt-get update && apt-get install -y tzdata && rm -rf /var/lib/apt/lists/*

# Ortam değişkenini ayarlayın ve sembolik link oluşturun
ENV TZ=Europe/Istanbul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/find-word-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5105
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
