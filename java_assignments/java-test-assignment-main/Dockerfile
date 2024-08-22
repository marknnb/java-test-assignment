# Use a Debian-based image with JDK 21 installed
FROM openjdk:21-slim

# Install the required packages, add the PostgreSQL PGP key, and repository
RUN apt-get update && \
    apt-get install -y curl gnupg lsb-release && \
    curl -sSL https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - && \
    echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list

# Install PostgreSQL client version 13
RUN apt-get update && \
    apt-get install -y postgresql-client-13 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set the classpath to include PostgreSQL binary directory
ENV PATH="/usr/lib/postgresql/13/bin:$PATH"

EXPOSE 9090
ADD target/java-test-assignment-1.0.0.jar app.jar
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]