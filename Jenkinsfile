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
         stage('Wait for SonarQube to Start') {
                    steps {
                        script {
                            // Wait for SonarQube to start
                            timeout(time: 5, unit: 'MINUTES') {
                                waitUntil {
                                    def response = sh(script: "curl -s -o /dev/null -w \"%{http_code}\" ${env.SONAR_HOST_URL}", returnStatus: true)
                                    return response == 200
                                }
                            }
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
                    withSonarQubeEnv('SonarQube') {
                         sh "${env.SONAR_SCANNER_HOME}/bin/sonar-scanner -Dsonar.host.url=${env.SONAR_HOST_URL}"
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
