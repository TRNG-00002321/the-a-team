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
                    rm -rf allure-results htmlcov coverage.xml
                    mkdir -p allure-results htmlcov
                '''
            }
        }
        stage('Unit Tests') {
            steps {
                sh '''
                    mkdir -p coverage htmlcov allure-results

                    docker run --rm \
                    -v ${WORKSPACE}/allure-results:/tmp/allure-results \
                    -v ${WORKSPACE}/coverage:/tmp/coverage \
                    -v ${WORKSPACE}/htmlcov:/tmp/htmlcov \
                    employee-test \
                    pytest tests/unit_tests \
                    --alluredir=/tmp/allure-results \
                    --cov=src \
                    --cov-report=xml:/tmp/coverage/coverage.xml \
                    --cov-report=html:/tmp/htmlcov
                '''
            }
        }
        stage('Integration Tests') {
            steps {
                sh '''
                    docker run --rm \
                    -v ${WORKSPACE}/allure-results:/tmp/allure-results \
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
                    -v ${WORKSPACE}/allure-results:/tmp/allure-results \
                    -e TEST_MODE=true \
                    -e BROWSER=chrome \
                    -e HEADLESS=true \
                    -e TEST_DATABASE_PATH=/app/tests/test_db/test_expense_manager.db \
                    employee-test \
                    sh -c '
                    python main.py &
                    APP_PID=\$!;
                    sleep 5;
                    behave \
                        --tags=-skip \
                        -f allure_behave.formatter:AllureFormatter \
                        -o /tmp/allure-results \
                        tests/end_to_end_test/features;
                    EXIT_CODE=\$?;
                    kill \$APP_PID;
                    exit \$EXIT_CODE
                    '
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

            // 🔹 Allure (Jenkins Allure plugin)
            allure(
                includeProperties: false,
                jdk: '',
                results: [[path: 'allure-results']]
            )

            // 🔹 Coverage (Jenkins Coverage plugin)
            recordCoverage(
                tools: [[parser: 'COBERTURA', pattern: 'coverage.xml']],
                sourceCodeRetention: 'EVERY_BUILD'
            )

            // 🔹 Keep HTML coverage
            archiveArtifacts artifacts: 'htmlcov/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'coverage.xml, htmlcov/**', allowEmptyArchive: true
        }
    }
}