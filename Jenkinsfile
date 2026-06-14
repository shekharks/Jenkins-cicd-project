pipeline {

    agent any

    environment {
        JAVA_HOME   = '/usr/lib/jvm/java-21-openjdk-amd64'
        MAVEN_OPTS  = '-Xms128m -Xmx256m'
        APP_NAME    = 'jenkins-cicd-project'
        APP_VERSION = '1.0-SNAPSHOT'
        DEPLOY_DIR  = '/opt/myapp'
        APP_PORT    = '9090'
    }

    parameters {
        choice(
            name: 'BUILD_TYPE',
            choices: ['test', 'package', 'verify'],
            description: 'Select what Maven goal to run'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Tick this to skip running tests'
        )
        booleanParam(
            name: 'DEPLOY',
            defaultValue: true,
            description: 'Tick this to deploy after build'
        )
    }

    stages {

        stage('Initialize') {
            steps {
                echo "====================================="
                echo "Job Name     : ${env.JOB_NAME}"
                echo "Build Number : ${env.BUILD_NUMBER}"
                echo "Build URL    : ${env.BUILD_URL}"
                echo "Git Branch   : ${env.GIT_BRANCH}"
                echo "Git Commit   : ${env.GIT_COMMIT}"
                echo "Workspace    : ${env.WORKSPACE}"
                echo "Deploy       : ${params.DEPLOY}"
                echo "====================================="
            }
        }

        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
                sh 'ls -la'
            }
        }

        stage('Build') {
            steps {
                echo "Compiling source code..."
                sh 'mvn clean compile -q'
                echo "Compilation successful"
            }
        }

        stage('Test') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            steps {
                echo "Running unit tests..."
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    echo "Test results published"
                }
            }
        }

        stage('Package') {
            steps {
                echo "Packaging application..."
                sh 'mvn package -DskipTests'
                sh 'ls -lh target/*.jar'
            }
        }

        stage('Archive') {
            steps {
                echo "Archiving artifacts..."
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true,
                                 allowEmptyArchive: false
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
                echo "Starting deployment..."
                echo "Workspace path: ${env.WORKSPACE}"
                sh "/opt/myapp/deploy.sh ${env.WORKSPACE}"
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
                echo "Running health check..."
                retry(3) {
                    sleep(time: 10, unit: 'SECONDS')
                    sh "curl -f -s http://localhost:${APP_PORT}/health"
                }
                echo "Health check passed - application is UP"
            }
        }

    }

    post {
        success {
            echo "====================================="
            echo "PIPELINE SUCCESSFUL"
            echo "App Name  : ${env.APP_NAME}"
            echo "App URL   : http://13.233.145.158:9090"
            echo "====================================="
        }
        failure {
            echo "====================================="
            echo "PIPELINE FAILED"
            echo "Job     : ${env.JOB_NAME}"
            echo "Build # : ${env.BUILD_NUMBER}"
            echo "====================================="
        }
        always {
            echo "Cleaning up workspace..."
            cleanWs()
        }
    }

}
