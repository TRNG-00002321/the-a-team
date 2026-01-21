pipeline {
    agent any

    stages {
        stage('Build Images') {
            steps {
                sh 'docker build --no-cache -t employee-app ./employee'
//                sh 'docker build -t manager-app ./manager'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'docker run --rm employee-app pytest tests/unit_tests'
//                sh 'docker run --rm manager-app mvn test'
            }
        }

        stage('Integration Tests') {
            steps {
                sh 'docker run --rm employee-app pytest tests/integration_tests'
            }
        }

        stage('E2E Tests') {
            steps {
                sh '''
                  docker build --target test -t employee-test ./employee
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
