pipeline {
    agent any
    environment {
        SONARQUBE_JDBC_USERNAME = credentials('SONARQUBE_DB_CREDENTIALS')
        SONARQUBE_JDBC_PASSWORD = credentials('SONARQUBE_DB_CREDENTIALS')
        SONAR_HOST_URL = 'http://192.168.33.10:9080' // Update this with your SonarQube server URL
        SONAR_SCANNER_HOME = tool 'sonar-scanner'
    }
    stages {
        stage('Run Containers') {
            steps {
                // Run the Docker containers
                script {
                    sh 'docker-compose up -d'
                }
            }
        }

        stage('Run JUnit tests') {
            steps {
                // Run JUnit tests using Maven
                script {
                    sh 'mvn test'
                }
            }
        }
        stage('Build and analyze') {
            steps {
                script {
                    // Execute SonarQube scanner
                    withCredentials([usernamePassword(credentialsId: 'SONARQUBE_DB_CREDENTIALS', usernameVariable: 'SONARQUBE_JDBC_USERNAME', passwordVariable: 'SONARQUBE_JDBC_PASSWORD')]) {
                        sh """
                            docker run --network sonarnet \
                                -e SONARQUBE_JDBC_URL=jdbc:mysql://mysql:3306/sonar?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useConfigs=maxPerformance&useSSL=false \
                                -e SONARQUBE_JDBC_USERNAME=${SONARQUBE_JDBC_USERNAME} \
                                -e SONARQUBE_JDBC_PASSWORD=${SONARQUBE_JDBC_PASSWORD} \
                                -e SONAR_HOST_URL=${SONAR_HOST_URL} \
                                -v $(pwd):/usr/src/app \
                                -w /usr/src/app \
                                sonarqube:latest \
                                ${SONAR_SCANNER_HOME}/bin/sonar-scanner
                        """
                    }
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
    }

    post {
        always {
            // Shutdown the Docker containers
            script {
                sh 'docker-compose down'
            }
        }
    }
}
