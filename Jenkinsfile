pipeline {
    agent any
   environment {
          SONARQUBE_JDBC_USERNAME = credentials('SONARQUBE_DB_CREDENTIALS')
          SONARQUBE_JDBC_PASSWORD = credentials('SONARQUBE_DB_CREDENTIALS')
          SONARQUBE_SCANNER_HOME = tool 'SonarQube Scanner'
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
                    withSonarQubeEnv('SonarQube') {
                        // Path to your source code
                        sh "${tool 'sonar-scanner'}/bin/sonar-scanner"
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
