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

def test_update_expense(setup_database, test_client):
  auth_response = test_client.post(
    "/api/auth/login",
    json={
      "username": "employee1",
      "password": "password123"
    }
  )

  assert auth_response.status_code == 200

  expense_id = 1

  response = test_client.put(
    f"/api/expenses/{expense_id}",
    json={
      "amount": 99.99,
      "description": "Updated lunch",
      "date": "2025-01-06"
    }
  )

  assert response.status_code == 200

  data = response.get_json()
  assert data["expense"]["amount"] == 99.99
  assert data["expense"]["description"] == "Updated lunch"
  assert data["message"] == "Expense updated successfully"

@pytest.mark.parametrize("amount, description, date", [
  (None,"Updated lunch","2025-01-06"),
  ("99.99",None,"2025-01-06"),
  ("99.99","Updated lunch",None)
])
def test_update_expense_missing_parameter(setup_database, amount, description, date, test_client):
  auth_response = test_client.post(
    "/api/auth/login",
    json={
      "username": "employee1",
      "password": "password123"
    }
  )

  assert auth_response.status_code == 200

  expense_id = 1

  response = test_client.put(
    f"/api/expenses/{expense_id}",
    json={
      "amount": amount,
      "description": description,
      "date": date
    }
  )

  assert response.status_code == 400

  data = response.get_json()
  assert data["error"] == "Amount, description, and date are required"

@pytest.mark.parametrize("amount, description, date", [
  ("abc","Updated lunch","2025-01-06"),
  ("3/4","Updated lunch","2025-01-06"),
])
def test_update_expense_invalid_amount(setup_database, amount, description, date, test_client):
  auth_response = test_client.post(
    "/api/auth/login",
    json={
      "username": "employee1",
      "password": "password123"
    }
  )

  assert auth_response.status_code == 200

  expense_id = 1

  response = test_client.put(
    f"/api/expenses/{expense_id}",
    json={
      "amount": amount,
      "description": description,
      "date": date
    }
  )

  assert response.status_code == 400

  data = response.get_json()
  assert data["error"] == "Amount must be a valid number"

def test_update_expense_missing_expense_id(setup_database, test_client):
  auth_response = test_client.post(
  f"/api/auth/login",
    json={
      "username": "employee1",
      "password": "password123"
    }
  )

  assert auth_response.status_code == 200

  expense_id = 9999

  response = test_client.put(
    f"/api/expenses/{expense_id}",
    json={
      "amount": 99.99,
      "description": "Updated lunch",
      "date": "2025-01-06"
    }
  )

  assert response.status_code == 404

  data = response.get_json()
  assert data["error"] == "Expense not found"

def test_update_expense_missing_json(setup_database, test_client):
  auth_response = test_client.post(
    "/api/auth/login",
    json={
      "username": "employee1",
      "password": "password123"
    }
  )

  assert auth_response.status_code == 200

  expense_id = 1

  response = test_client.put(
    f"/api/expenses/{expense_id}",
    json={}
  )

  assert response.status_code == 400

  data = response.get_json()
  assert data["error"] == "JSON data required"

def test_update_expense_not_owner(setup_database, test_client):
  auth_response = test_client.post(
    "/api/auth/login",
    json={"username": "employee1", "password": "password123"}
  )
  assert auth_response.status_code == 200

  # Expense 4 belongs to employee2
  expense_id = 4
  response = test_client.put(
    f"/api/expenses/{expense_id}",
    json={
      "amount": 10.0,
      "description": "Hack attempt",
      "date": "2025-01-06"
    }
  )

  assert response.status_code == 404

def test_update_expense_not_authorized(setup_database, test_client):
  # Expense 4 belongs to employee2
  expense_id = 1
  response = test_client.put(
    f"/api/expenses/{expense_id}",
    json={
      "amount": 10.0,
      "description": "Hack attempt",
      "date": "2025-01-06"
    }
  )

  assert response.status_code == 401