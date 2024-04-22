# Use AdoptOpenJDK as the base image
FROM adoptopenjdk/openjdk11:alpine-jre

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from Jenkins workspace to the working directory in the container
COPY *.jar app.jar

# Specify the command to run on container start
CMD ["java", "-jar", "app.jar"]