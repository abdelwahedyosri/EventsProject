pipeline {
    agent any
    environment {
        SONARQUBE_JDBC_USERNAME = credentials('SONARQUBE_DB_CREDENTIALS')
        SONARQUBE_JDBC_PASSWORD = credentials('SONARQUBE_DB_CREDENTIALS')
        SONARQUBE_SCANNER_HOME = '/var/lib/jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar-scanner'
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
                    withSonarQubeEnv('SonarQube') {
                         def scannerHome = tool 'sonar-scanner'
                          sh "${scannerHome}/bin/sonar-scanner"
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
