# Estágio 1: Compilação do projeto com Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copia os arquivos pom.xml e código fonte
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
# Compila o JAR do Spring Boot (ignorando testes para agilizar a build do container)
RUN mvn clean package -DskipTests -B

# Estágio 2: Imagem leve de execução (JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o artefato compilado do primeiro estágio
COPY --from=build /app/target/sangue-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta da API Spring Boot
EXPOSE 8080

# Comando para iniciar o servidor Java
ENTRYPOINT ["java", "-jar", "app.jar"]
