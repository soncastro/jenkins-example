# Estágio 1: Build da aplicação
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copiar os arquivos necessários para o build
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permissão de execução ao mvnw
RUN chmod +x mvnw

# Baixar as dependências (isso fica em cache se o pom.xml não mudar)
RUN ./mvnw dependency:go-offline -B

# Copiar o código fonte
COPY src src

# Compilar a aplicação
RUN ./mvnw clean package

# Estágio 2: Imagem final para executar a aplicação
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Criar um usuário não-root para executar a aplicação
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar o JAR compilado do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expor a porta padrão do Spring Boot
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]