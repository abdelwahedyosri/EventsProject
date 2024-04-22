pipeline {
    agent any
    environment {
        SONARQUBE_JDBC_USERNAME = credentials('SONARQUBE_DB_CREDENTIALS')
        SONARQUBE_JDBC_PASSWORD = credentials('SONARQUBE_DB_CREDENTIALS')
        SONAR_HOST_URL = 'http://192.168.33.10:9080' // Update this with your SonarQube server URL
        SONAR_SCANNER_HOME = tool 'sonar-scanner'
        PROJECT_NAME = 'eventsproject' // Update with your project name
        GIT_REPO_URL = 'https://github.com/abdelwahedyosri/EventsProject.git' // Update with your Git repo URL
        DOCKER_HUB_USERNAME = credentials('DOCKER_HUB_CREDENTIALS') // Update with your Docker Hub username
        DOCKER_HUB_PASSWORD = credentials('DOCKER_HUB_CREDENTIALS') // Add your Docker Hub password as a Jenkins credential
        DOCKER_HUB_REPO = "${DOCKER_HUB_CREDENTIALS}/${PROJECT_NAME}"
    }
    stages {
        stage('Shutdown Containers') {
            steps {
                script {
                   // Stop all running Docker containers
                   sh 'docker stop $(docker ps -aq) || echo "No containers to stop"'
                   // Remove all stopped Docker containers
                   sh 'docker rm $(docker ps -aq) || echo "No containers to remove"'
                }
            }
        }

        stage('Clone Repository') {
            steps {
                script {
                    // Clone the Git repository
                    sh "git clone ${GIT_REPO_URL} ${PROJECT_NAME}"
                }
            }
        }

        stage('Build Project Image') {
            steps {
                script {
                    // Build Docker image for the project
                    sh "docker build -t ${PROJECT_NAME}_image ./${PROJECT_NAME}"
                }
            }
        }

        stage('Run Containers') {
            steps {
                // Run the Docker containers
                script {
                    sh 'docker-compose up -d'
                }
            }
        }

       /* stage('Run JUnit tests') {
            steps {
                // Run JUnit tests using Maven
                script {
                    sh 'mvn test'
                }
            }
        }*/

        stage('Build Application') {
            steps {
                // Build the application using Maven
                script {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Deploy to Nexus Repository') {
            steps {
                script {
                    sh 'mvn deploy'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    // Log in to Docker Hub
                    sh "docker login -u ${DOCKER_HUB_CREDENTIALS} -p ${DOCKER_HUB_CREDENTIALS}"
                    // Tag the Docker image
                    sh "docker tag ${PROJECT_NAME}_image ${DOCKER_HUB_REPO}:latest"
                    // Push the Docker image to Docker Hub
                    sh "docker push ${DOCKER_HUB_REPO}:latest"
                }
            }
        }
    }
}
