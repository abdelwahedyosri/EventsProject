# Use AdoptOpenJDK as the base image
FROM adoptopenjdk/openjdk11:alpine-jre

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download all Maven dependencies (dependencies will be cached if the pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy the project source
COPY src ./src

# Build the application
RUN mvn clean package

# Copy the packaged JAR file from the target directory to the working directory in the container
COPY target/*.jar app.jar

# Specify the command to run on container start
CMD ["java", "-jar", "app.jar"]
