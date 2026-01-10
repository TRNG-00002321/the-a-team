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

def test_login_positive(test_client, setup_database):
    data={
        "username":"employee1",
        "password":"password123"
    }
    response = test_client.post("/api/auth/login", json=data)
    assert response.status_code == 200
    user = response.get_json()["user"]
    assert user["username"] == "employee1"
    assert user["role"] == "Employee"
    assert user["id"] == 1

@pytest.mark.parametrize("username, password", [
    ("employee1","wrong_password"),
    ("wrong_username", "password123"),
    (12345, 12345)
])
def test_login_negative(username, password, test_client, setup_database):
    data = {
        "username": username,
        "password": password
    }
    response = test_client.post("/api/auth/login", json=data)
    assert response.status_code == 401


@pytest.mark.parametrize("username, password", [
    ("",""),
    (None, None),
])
def test_login_error(username, password, test_client, setup_database):
    data = {
        "username": username,
        "password": password
    }

    response = test_client.post("/api/auth/login", json=data)
    assert response.status_code == 400
    assert "Username and password required" in response.get_json()["error"]

