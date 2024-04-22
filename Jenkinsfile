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

        stage('Deploy to Nexus Repository') {
            steps {
                script {
                    sh 'mvn deploy'
                }
            }
        }
    }


}
