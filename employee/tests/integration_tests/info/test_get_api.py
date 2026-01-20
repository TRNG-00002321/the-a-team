import os
import pytest

from main import create_app
from src.repository import DatabaseConnection

TEST_DB_PATH = os.path.abspath(os.path.join(
    os.path.dirname(__file__),
    "../../test_db/test_expense_manager.db"
))

@pytest.fixture()
def test_client():
    # Ensure test DB directory exists
    os.makedirs(os.path.dirname(TEST_DB_PATH), exist_ok=True)

    # Set DB path BEFORE app creation
    os.environ["TEST_MODE"] = "true"
    os.environ["TEST_DATABASE_PATH"] = TEST_DB_PATH

    # Initialize schema once
    db = DatabaseConnection()
    db.initialize_database()

    app = create_app()
    app.config["TESTING"] = True

    with app.test_client() as client:
        yield client

def test_get_api_info(test_client):
    response = test_client.get("/api")
    assert response.status_code == 200
    assert response.get_json()["service"] == "Employee Expense Management API"
    assert response.get_json()["version"] == "1.0.0"
    endpoints = response.get_json()["endpoints"]
    assert endpoints["authentication"] == "/api/auth"
    assert endpoints["expenses"] == "/api/expenses"
    assert endpoints["health"] == "/health"