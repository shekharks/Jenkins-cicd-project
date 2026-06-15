// Import the shared library registered in Jenkins
@Library('my-shared-library') _

pipeline {

    agent any

    environment {
        JAVA_HOME  = '/usr/lib/jvm/java-21-openjdk-amd64'
        MAVEN_OPTS = '-Xms128m -Xmx256m'
        APP_PORT   = '9090'
    }

    parameters {
        booleanParam(
            name: 'DEPLOY',
            defaultValue: true,
            description: 'Deploy after build?'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip tests?'
        )
    }

    stages {

        stage('Initialize') {
            steps {
                echo "====================================="
                echo "Job    : ${env.JOB_NAME}"
                echo "Build  : ${env.BUILD_NUMBER}"
                echo "Branch : ${env.GIT_BRANCH}"
                echo "Commit : ${env.GIT_COMMIT}"
                echo "====================================="
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
                sh 'ls -la'
            }
        }

        stage('Build') {
            steps {
                // Calling shared library function - one line!
                buildApp('compile')
            }
        }

        stage('Test') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            steps {
                // Calling shared library function - one line!
                runTests()
            }
        }

        stage('Package') {
            steps {
                buildApp('package -DskipTests')
                sh 'ls -lh target/*.jar'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }

        stage('Deploy') {
            when {
                allOf {
                    expression { params.DEPLOY == true }
                    anyOf {
                        branch 'main'
                        expression { env.GIT_BRANCH == 'origin/main' }
                    }
                }
            }
            steps {
                // Calling shared library function - one line!
                deployApp("${env.DEPLOY_DIR}", "${env.WORKSPACE}")
            }
        }

        stage('Health Check') {
            when {
                allOf {
                    expression { params.DEPLOY == true }
                    anyOf {
                        branch 'main'
                        expression { env.GIT_BRANCH == 'origin/main' }
                    }
                }
            }
            steps {
                // Calling shared library function - one line!
                healthCheck("http://localhost:${env.APP_PORT}/health")
            }
        }

    }

    post {
        success {
            // Calling shared library function - one line!
            notifyBuild('SUCCESSFUL')
        }
        failure {
            notifyBuild('FAILED')
        }
        always {
            cleanWs()
        }
    }

}
