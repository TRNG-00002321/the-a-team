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
                sh 'docker-compose down --remove-orphans || true'
                sh 'docker-compose up -d --force-recreate selenium employee'
                sh 'docker-compose exec -T employee behave tests/end_to_end_test/features'
                sh 'docker-compose down --remove-orphans'
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
