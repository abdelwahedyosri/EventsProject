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
        EMAIL_RECIPIENTS = 'yosri.abdelwahed@esprit.tn' // Update with your email address
        SONAR_SCANNER_HOME = tool 'sonar-scanner
    }
    triggers {
            git branch: 'main', pushOnly: true
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
         stage('Compile Application') {
                    steps {
                        // Compile the application using Maven
                        script {
                            dir("${PROJECT_NAME}") {
                                sh 'mvn compile'
                            }
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


       stage('Show Prometheus Metrics') {
           steps {
               script {
                   sh "docker exec prometheus wget -q -O - http://192.168.33.10:9090/metrics"
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

       stage('Email Notification') {
           steps {
               script {
                   emailext attachLog: true, body: 'The pipeline has been executed successfully.', subject: 'Pipeline Execution Status', to: "${EMAIL_RECIPIENTS}"
               }
           }
       }
        stage('SonarQube Metrics') {
                   steps {
                       script {
                           dir("${PROJECT_NAME}") {
                               sh "${SONAR_SCANNER_HOME}/bin/sonar-scanner -Dsonar.projectKey=${PROJECT_NAME} -Dsonar.sources=./ -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONARQUBE_JDBC_USERNAME} -Dsonar.password=${SONARQUBE_JDBC_PASSWORD}"
                           }
                       }
                   }
        }

       stage('Push to Docker Hub') {
           steps {
               script {
                   def user = sh(script: 'echo $DOCKER_HUB_USERNAME', returnStdout: true).trim()
                   def pw = sh(script: 'echo $DOCKER_HUB_PASSWORD', returnStdout: true).trim()
                   sh "echo ${pw} | docker login -u ${user} --password-stdin"
               }
           }
       }
    }
}
