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
        "/api/auth/login",
        json=login_payload
    )

    assert response.status_code == 200

    yield test_client

#EI-217
def test_api_logout_success(authenticated_session):
    """
    Authenticated user can log out successfully
    """
    response = authenticated_session.post(
        "/api/auth/logout"
    )

    assert response.status_code == 200

    # After logout, cookie should be removed or empty
    auth_response = authenticated_session.get("/api/auth/status")
    json_response = auth_response.get_json()
    assert json_response.get("authenticated") is False

#EI-218
def test_api_access_protected_endpoint_after_logout_fails(authenticated_session):
    # Logout
    logout_response = authenticated_session.post(
        "/api/auth/logout"
    )
    assert logout_response.status_code == 200

    # Try accessing protected endpoint
    response = authenticated_session.get(
        f"/api/expenses"
    )

    assert response.status_code in (401, 403)