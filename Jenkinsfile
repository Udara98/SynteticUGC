pipeline {
    agent any
    
    stages {
        stage('Build and Test') {
            steps {
                node {
                    docker.image('maven:3.8.6-openjdk-11').inside('-v $HOME/.m2:/root/.m2') {
                        sh 'mvn clean install -DskipTests'
                        sh 'mvn test'
                    }
                }
            }
            post {
                always {
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