# Use AdoptOpenJDK as the base image
FROM adoptopenjdk/openjdk11:alpine-jre

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file from the build stage to the working directory in the container
COPY target/*.jar app.jar

# Specify the command to run on container start
CMD ["java", "-jar", "app.jar"]