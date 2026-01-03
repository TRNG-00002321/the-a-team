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

class Test_Get_Delete_Expenses:
    @pytest.fixture
    def setup(self, test_client, setup_database):
        auth_response = test_client.post(
            "api/auth/login",
            json={
                "username": "employee1",
                "password": "password123"
            }
        )

        assert auth_response.status_code == 200

        yield test_client

    # # EI-209
    # def test_get_expenses(self, setup):
    #     response = setup[1].get(setup[0])
    #
    #     assert response.status_code == 200
    #
    #     data = response.json()
    #     assert data["count"] is not None
    #     assert data["expenses"] is not None
    #     assert isinstance(data["expenses"], list)

    # EI-210
    def test_delete_expense_positive(self, setup):
        auth_test_client = setup

        exp_to_delete_id = 1

        exp_deletion_response = auth_test_client.delete(
            f"/api/expenses/{exp_to_delete_id}"
        )

        assert exp_deletion_response.status_code == 200

        data = exp_deletion_response.get_json()
        assert data["message"] == "Expense deleted successfully"

    # EI-211
    def test_delete_expense_negative(self, setup):
        auth_test_client = setup

        exp_to_delete_id = 9999

        exp_deletion_response = auth_test_client.delete(
            f"/api/expenses/{exp_to_delete_id}"
        )

        assert exp_deletion_response.status_code == 404

        data = exp_deletion_response.get_json()
        assert data["error"] == "Expense not found"