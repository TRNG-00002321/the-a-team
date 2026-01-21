pipeline {
    agent any

    stages {

        stage('Build Test Image') {
            steps {
                sh 'docker build --no-cache --target test -t employee-test ./employee'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'docker run --rm employee-test pytest tests/unit_tests'
            }
        }

        stage('Integration Tests') {
            steps {
                sh 'docker run --rm employee-test pytest tests/integration_tests'
            }
        }

        stage('E2E Tests') {
            steps {
                sh '''
                  docker run --rm \
                  -e TEST_MODE=true \
                  -e BROWSER=chrome \
                  -e HEADLESS=true \
                  -e TEST_DATABASE_PATH=/app/tests/test_db/test_expense_manager.db \
                  employee-test \
                  behave tests/end_to_end_test/features
                '''
            }
        }

        stage('Build Production Image') {
            steps {
                sh 'docker build --no-cache --target production -t employee-app ./employee'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose up -d employee'
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'
        }
    }
}
