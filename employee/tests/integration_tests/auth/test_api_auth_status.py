import pytest
import os

from main import create_app
from src.repository import DatabaseConnection

BASE_URL = "http://127.0.0.1:5000/"
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

class TestAuthenticationAPI:
    @pytest.fixture
    def valid_credentials(self):
        """Valid employee credentials for testing."""
        return{
            "username" : "employee1",
            "password" : "password123",
            "role" : "employee"
        }

    @pytest.fixture
    def invalid_credentials(self):
        return{
            "username" : "manager1",
            "password" : "password123",
            "role" : "manager"
        }


    #Status tests

    def test_status_unauthenticated(self, test_client, setup_database):
        # Test auth status when not logged in.
        #Arrange
        url = "/api/auth/status"
        #Act
        response = test_client.get(url)
        #Assert
        assert response.status_code == 200
        json_response = response.get_json()
        assert json_response.get("authenticated") is False

    def test_status_authenticated_valid_cred(self, test_client, setup_database, valid_credentials):
        # Test auth status when logged in.
        #Arrange login and status
        login_url = "/api/auth/login"
        status_url = "/api/auth/status"

        #Act+Assert for login
        login_response = test_client.post(login_url, json=valid_credentials)
        assert login_response.status_code == 200

        #Act+Assert for status
        status_response = test_client.get(status_url)
        assert status_response.status_code == 200
        data = status_response.get_json()
        assert data["authenticated"] is True
        assert valid_credentials["username"] == "employee1"
        assert valid_credentials["password"] == "password123"
        assert valid_credentials["role"] == "employee"


    def test_status_invalid_cred(self, test_client, setup_database, invalid_credentials):
        login_url = "/api/auth/login"
        status_url = "/api/auth/status"

        test_client.post(login_url, json=invalid_credentials)
        response = test_client.get(status_url)
        assert response.status_code == 200
        json_response = response.get_json()
        assert json_response.get("authenticated") is False








