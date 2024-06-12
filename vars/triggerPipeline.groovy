def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent any

        tools {
            jdk 'jdk17'
            maven 'maven3'
        }

//        options {
//            // This is required if you want to clean before build
//            skipDefaultCheckout(true)
//        }

        stages {
            stage('Do we need git checkout?') {
                steps {
                    echo 'Git checkout'
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

            stage('Build Docker Image') {
                steps {
                    sh 'docker '
                }
            }
            stage('Push Docker Image to Dockerhub?') {
                steps {
                    script {
                        def response = input message: 'Push Docker Image to Dockerhub?',
                        parameters: [choice(choices: 'Yes\nNo',
                                            description: 'Proceed or Abort?',
                                            name: 'What do do?')]

                        if (response == 'Yes') {
                            sh 'docker login'
                            sh 'docker push'
                        }
                    }
                }
            }
        }

        post {
            failure {
                mail to: pipelineParams.email, subject: 'Pipeline failed', body: "${env.BUILD_URL}"
            }
        }
    }
}