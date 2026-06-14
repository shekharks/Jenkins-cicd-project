pipeline {

    agent any

    environment {
        JAVA_HOME    = '/usr/lib/jvm/java-21-openjdk-amd64'
        MAVEN_OPTS   = '-Xms128m -Xmx256m'
        APP_NAME     = 'jenkins-cicd-project'
        APP_VERSION  = '1.0-SNAPSHOT'
        DEPLOY_DIR   = '/opt/myapp'
        APP_PORT     = '9090'
        HEALTH_URL   = "http://localhost:${APP_PORT}/health"
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
                echo "Build Type   : ${params.BUILD_TYPE}"
                echo "Skip Tests   : ${params.SKIP_TESTS}"
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

        // NEW — Deploy stage
        stage('Deploy') {
            // Only deploy when DEPLOY parameter is true
            // AND only on main branch — never auto-deploy dev
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
                echo "Deploying to: ${env.DEPLOY_DIR}"

                // Run the deployment script as jenkins user
                sh """
                    echo "Running deploy script..."
                    echo "Workspace: ${env.WROKSPACE}"
                    /opt/myapp/deploy.sh ${env.WORKSPACE}
                """
            }
        }

        // NEW — Health Check stage
        stage('Health Check') {
            when {
                allOf {
                    expression { params.DEPLOY == true }
                    anyOf {
                        branch 'main'
                        expression { env.GIT_BRANCH == 'origin/main' }
                    }
                }
