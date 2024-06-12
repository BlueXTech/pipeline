pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    stages {
        stage('Git checkout') {
            steps {

            }
        }
        stage('Static code analysis & Code coverage') {
            // Jacoco
            steps {
                sh 'echo "Code coverage"'
            }
        }
        stage('SonarQube analysis') {
            steps {
                sh 'echo "SonarQube analysis"'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean package'
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }

        stage('Archive artifact') {
            steps {
                sh 'echo "Archive"'
            }
        }
        stage('Deployment') {
            steps {
                sh 'echo "Deployment"'
            }
        }
    }
}
