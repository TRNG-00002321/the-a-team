pipeline {
    agent any

    environment {
        ALLURE_RESULTS = "${WORKSPACE}/allure-results"
        COVERAGE_DIR   = "${WORKSPACE}/htmlcov"
    }

    stages {

        stage('Build Test Image') {
            steps {
                sh 'docker build --no-cache --target test -t employee-test ./employee'
            }
        }

        stage('Prepare Report Directories') {
            steps {
                sh '''
                    rm -rf allure-results htmlcov
                    mkdir -p allure-results htmlcov
                '''
            }
        }

        stage('Unit Tests') {
            steps {
                sh '''
                    docker run --rm \
                    -v ${ALLURE_RESULTS}:/tmp/allure-results \
                    -v ${COVERAGE_DIR}:/app/htmlcov \
                    employee-test \
                    pytest tests/unit_tests \
                    --alluredir=/tmp/allure-results \
                    --cov=src \
                    --cov-report=html:/app/htmlcov
                '''
            }
        }

        stage('Integration Tests') {
            steps {
                sh '''
                    docker run --rm \
                    -v ${ALLURE_RESULTS}:/tmp/allure-results \
                    employee-test \
                    pytest tests/integration_tests \
                    --alluredir=/tmp/allure-results
                '''
            }
        }

        stage('E2E Tests') {
            steps {
                sh '''
                    docker run --rm \
                    -v ${ALLURE_RESULTS}:/tmp/allure-results \
                    -e TEST_MODE=true \
                    -e BROWSER=chrome \
                    -e HEADLESS=true \
                    -e TEST_DATABASE_PATH=/app/tests/test_db/test_expense_manager.db \
                    employee-test \
                    sh -c "
                    python main.py & 
                    APP_PID=\$! ;
                    sleep 5 ;
                    behave \
                        -f allure_behave.formatter:AllureFormatter \
                        -o /tmp/allure-results \
                        tests/end_to_end_test/features ;
                    EXIT_CODE=\$? ;
                    kill \$APP_PID ;
                    exit \$EXIT_CODE
                    "
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
                sh '''
                  docker-compose down --remove-orphans || true
                  docker-compose up -d employee
                '''
            }
        }
    }

    post {
        always {
            script {
            allure([
                includeProperties: false,
                jdk: '',
                resultPolicy: 'LEAVE_AS_IS',
                results: [[path: 'allure-results']]
            ])
        }

            archiveArtifacts artifacts: 'htmlcov/**', allowEmptyArchive: true

            sh 'docker system prune -f'
        }
    }
}
