pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    // Publish TestNG results
                    publishTestNGResults '**/testng-results.xml'
                    // Publish JUnit results (for compatibility)
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
} 