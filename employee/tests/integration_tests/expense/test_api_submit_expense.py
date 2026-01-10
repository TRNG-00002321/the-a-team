import os

import pytest

from main import create_app
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

@pytest.fixture()
def authenticated_session(test_client, setup_database):
    """
    Logs in as employee1 and returns an authenticated session
    """
    login_payload = {
        "username": "employee1",
        "password": "password123"
    }

    response = test_client.post(
        f"/api/auth/login",
        json=login_payload
    )

    assert response.status_code == 200

    yield test_client

@pytest.fixture
def created_expense(authenticated_session):
    auth_test_client = authenticated_session

    #Request body
    expense_payload = {
        "amount": 125.75,
        "description": "Taxi to client meeting"
    }

    #Send post request
    response = auth_test_client.post(
        f"/api/expenses",
        json=expense_payload
    )

    expense_id = response.get_json()["expense"]["id"]

    # Provide the expense ID to the test
    yield response


#EI-215
def test_api_submit_new_expense_success(authenticated_session, created_expense):

    response = created_expense

    #Status code validation
    assert response.status_code in (200, 201)

    body = response.get_json()

    # Basic response validation
    assert body is not None
    assert "expense" in body

    expense = body["expense"]

    assert expense["amount"] == 125.75
    assert expense["description"] == "Taxi to client meeting"
    assert expense["status"].lower() == "pending"
    assert "id" in expense

#EI-216
def test_api_submit_expense_without_auth_should_fail(test_client):
    expense_payload = {
        "amount": 50.00,
        "category": "Food",
        "description": "Lunch"
    }

    response = test_client.post(
        f"/api/expenses",
        json=expense_payload
    )

    assert response.status_code in (401, 403)