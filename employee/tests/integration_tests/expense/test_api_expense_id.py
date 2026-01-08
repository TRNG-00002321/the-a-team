import os

import pytest

from src.main import create_app
from src.repository import DatabaseConnection

TEST_DB_PATH = os.path.abspath(os.path.join(
    os.path.dirname(__file__),
    "../../test_db/test_expense_manager.db"
))
SEED_SQL_PATH = os.path.abspath(os.path.join(
    os.path.dirname(__file__),
    "../../sql/seed.sql"
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

@pytest.fixture
def setup_database(test_client):
    """
    Reset database state before each test and reseed.
    Depends on test_client to guarantee schema exists.
    """
    db = DatabaseConnection()

    with db.get_connection() as conn:
        conn.execute("DELETE FROM approvals")
        conn.execute("DELETE FROM expenses")
        conn.execute("DELETE FROM users")

        with open(SEED_SQL_PATH, "r") as f:
            conn.executescript(f.read())

        conn.commit()

    yield

class TestSpecificExpenseAPI:

    @pytest.fixture
    def credentials(self):
        return {"username": "employee1", "password": "password123", "role": "employee"}

    @pytest.fixture
    def sample_expense(self):
        return {
            "amount": 50.0,
            "date": "2025-01-05",
            "description" : "Client lunch",
            "status": "pending"
        }


    def test_get_specific_expense_by_id_positive(self, credentials, sample_expense, test_client, setup_database):
        # Login
        login_url = "/api/auth/login"
        login_response = test_client.post(login_url, json = credentials)
        assert login_response.status_code == 200

        expense_id = 1

        # Get expense by id
        expense_response = test_client.get(f"/api/expenses/{expense_id}")
        assert expense_response.status_code == 200
        expense_data = expense_response.get_json()["expense"]

        assert expense_data["amount"] == sample_expense["amount"]
        assert expense_data["id"] == expense_id

    def test_get_specific_expense_by_id_negative(self, credentials, test_client, setup_database):
        # Login
        login_url = "/api/auth/login"
        login_response = test_client.post(login_url, json=credentials)
        assert login_response.status_code == 200

        expense_id = 999

        # Get expense by id - not found
        expense_response = test_client.get(f"/api/expenses/{expense_id}")
        assert expense_response.status_code == 404

    def test_get_expense_user_not_logged_in(self, test_client):
        # get expense without login
        expense_id = 1
        response = test_client.get(f"/api/expenses/{expense_id}")
        assert response.status_code == 401











