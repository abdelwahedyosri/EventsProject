pipeline {
    agent any
    environment {
        SONARQUBE_JDBC_USERNAME = credentials('SONARQUBE_DB_CREDENTIALS')
        SONARQUBE_JDBC_PASSWORD = credentials('SONARQUBE_DB_CREDENTIALS')
        SONAR_HOST_URL = 'http://192.168.33.10:9080' // Update this with your SonarQube server URL
        SONAR_SCANNER_HOME = tool 'sonar-scanner'
        PROJECT_NAME = 'eventsproject' // Update with your project name
        GIT_REPO_URL = 'https://github.com/abdelwahedyosri/EventsProject.git' // Update with your Git repo URL
        DOCKER_HUB_CREDENTIALS = 'DOCKER_HUB_CREDENTIALS' // Update with the ID of your Docker Hub credentials
        DOCKERFILE_NAME = 'Dockerfile'
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
                     sh "rm -rf ${PROJECT_NAME}"
                    // Clone the Git repository
                    sh "git clone ${GIT_REPO_URL} ${PROJECT_NAME}"
                }
            }
        }
        stage('Build Application') {
            steps {
                // Build the application using Maven
                script {
                    dir("${PROJECT_NAME}") {
                        sh 'mvn clean package'
                    }
                }
            }
        }

        stage('Deploy to Nexus Repository') {
            steps {
                dir("${PROJECT_NAME}") {
                    catchError(buildResult: 'UNSTABLE') {
                        sh 'mvn deploy -DskipTests=true'
                    }
                }
            }
        }

        stage('Build Project Image') {
            steps {
                script {
                    // Build Docker image for the project
                    sh "docker build -t ${PROJECT_NAME}_image -f ./${PROJECT_NAME}/${DOCKERFILE_NAME} ./${PROJECT_NAME}"
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

        stage('Deploy Prometheus') {
            steps {
                // Deploy Prometheus
                script {
                    sh 'docker-compose up -d prometheus'
                }
            }
        }
        stage('Show Prometheus Metrics') {
            steps {
                // Query metrics from Prometheus
                script {
                    sh 'curl -s http://prometheus:9090/metrics'
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

   stage('Push to Docker Hub') {
       steps {
           script {
               withCredentials([usernamePassword(credentialsId: DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                   sh "curl -v -u ${DOCKER_HUB_USERNAME}:${DOCKER_HUB_PASSWORD} --upload-file foo.txt https://nexushost/repository/somerepo/foo.txt"
               }
           }
       }
   }
}
