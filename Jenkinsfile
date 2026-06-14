pipeline {

    agent any

    // Environment variables available to every stage
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        MAVEN_OPTS = '-Xms128m -Xmx256m'
        APP_NAME = 'jenkins-cicd-project'
        APP_VERSION = '1.0-SNAPSHOT'
    }

    // Parameters — user can choose which Maven goal to run at trigger time
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
    }

    stages {

        // Stage 1 — Print build info so logs are always identifiable
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
                echo "====================================="
            }
        }

        // Stage 2 — Checkout code from GitHub
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
                echo "Checkout complete. Files in workspace:"
                sh 'ls -la'
            }
        }

        // Stage 3 — Compile the source code only
        stage('Build') {
            steps {
                echo "Compiling source code..."
                sh 'mvn clean compile -q'
                echo "Compilation successful"
            }
        }

        // Stage 4 — Run all JUnit tests
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
                    // Publish test results regardless of pass or fail
                    junit 'target/surefire-reports/*.xml'
                    echo "Test results published"
                }
            }
        }

        // Stage 5 — Package into jar (only runs if tests passed)
        stage('Package') {
            steps {
                echo "Packaging application into jar..."
                sh 'mvn package -DskipTests'
                echo "Package complete. Artifact:"
                sh 'ls -lh target/*.jar'
            }
        }

        // Stage 6 — Archive the jar as a Jenkins artifact
        stage('Archive') {
            steps {
                echo "Archiving build artifacts..."
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true,
                                 allowEmptyArchive: false
                echo "Artifact archived successfully"
            }
        }

    }

    // Post pipeline actions
    post {

        success {
            echo "====================================="
            echo "BUILD SUCCESSFUL"
            echo "Application : ${env.APP_NAME}"
            echo "Version     : ${env.APP_VERSION}"
            echo "Build #     : ${env.BUILD_NUMBER}"
            echo "Artifact    : target/jenkins-cicd-project-1.0-SNAPSHOT.jar"
            echo "====================================="
        }

        failure {
            echo "====================================="
            echo "BUILD FAILED"
            echo "Job     : ${env.JOB_NAME}"
            echo "Build # : ${env.BUILD_NUMBER}"
            echo "Check console output for details"
            echo "====================================="
        }

        always {
            echo "Pipeline finished. Cleaning up workspace..."
            cleanWs()
        }

    }

}
