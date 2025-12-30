import sqlite3
import requests
import pytest

BASE_URL = "http://127.0.0.1:5000/"

@pytest.fixture
def db_conn():
  conn = sqlite3.connect("expense_manager.db")
  conn.row_factory = sqlite3.Row
  yield conn
  conn.close()

@pytest.fixture
def setup_database(db_conn):
  cursor = db_conn.cursor()

  cursor.execute("""
    INSERT INTO users (id, username, password, role)
    VALUES
      (997, 'employee997', 'password123', 'Employee'),
      (998, 'employee998', 'password123', 'Employee'),
      (999, 'manager999',  'password123', 'Manager')
  """)

  cursor.execute("""
    INSERT INTO expenses (id, user_id, amount, description, date)
    VALUES
      (901, 997, 25.50,  'Lunch with client', '2025-01-05'),
      (902, 997, 120.00, 'Hotel stay',        '2025-01-06'),
      (903, 998, 15.75,  'Taxi to office',    '2025-01-07')
  """)

  cursor.execute("""
    INSERT INTO approvals (id, expense_id, status, reviewer, comment, review_date)
    VALUES
      (801, 901, 'pending', NULL, NULL, NULL),
      (802, 902, 'pending', NULL, NULL, NULL),
      (803, 903, 'pending', NULL, NULL, NULL)
  """)

  db_conn.commit()

  yield

  cursor.execute("DELETE FROM approvals WHERE id IN (801, 802, 803)")
  cursor.execute("DELETE FROM expenses WHERE id IN (901, 902, 903)")
  cursor.execute("DELETE FROM users WHERE id IN (997, 998, 999)")
  db_conn.commit()

def test_update_expense(setup_database):
  auth_response = requests.post(
    f"{BASE_URL}/api/auth/login",
    json={
      "username": "employee997",
      "password": "password123"
    }
  )

  cookie = auth_response.cookies

  expense_id = 901

  response = requests.put(
    f"{BASE_URL}/api/expenses/{expense_id}",
    json={
      "amount": 99.99,
      "description": "Updated lunch",
      "date": "2025-01-06"
    },
    cookies=cookie
  )

  assert response.status_code == 200

  data = response.json()
  assert data["expense"]["amount"] == 99.99
  assert data["expense"]["description"] == "Updated lunch"
  assert data["message"] == "Expense updated successfully"

@pytest.mark.parametrize("amount, description, date", [
  (None,"Updated lunch","2025-01-06"),
  ("99.99",None,"2025-01-06"),
  ("99.99","Updated lunch",None)
])
def test_update_expense_missing_parameter(setup_database, amount, description, date):
  auth_response = requests.post(
    f"{BASE_URL}/api/auth/login",
    json={
      "username": "employee997",
      "password": "password123"
    }
  )

  cookie = auth_response.cookies

  expense_id = 901

  response = requests.put(
    f"{BASE_URL}/api/expenses/{expense_id}",
    json={
      "amount": amount,
      "description": description,
      "date": date
    },
    cookies=cookie
  )

  assert response.status_code == 400

  data = response.json()
  assert data["error"] == "Amount, description, and date are required"

@pytest.mark.parametrize("amount, description, date", [
  ("abc","Updated lunch","2025-01-06"),
  ("3/4","Updated lunch","2025-01-06"),
])
def test_update_expense_invalid_amount(setup_database, amount, description, date):
  auth_response = requests.post(
    f"{BASE_URL}/api/auth/login",
    json={
      "username": "employee997",
      "password": "password123"
    }
  )

  cookie = auth_response.cookies

  expense_id = 901

  response = requests.put(
    f"{BASE_URL}/api/expenses/{expense_id}",
    json={
      "amount": amount,
      "description": description,
      "date": date
    },
    cookies=cookie
  )

  assert response.status_code == 400

  data = response.json()
  assert data["error"] == "Amount must be a valid number"

def test_update_expense_missing_expense_id(setup_database):
  auth_response = requests.post(
    f"{BASE_URL}/api/auth/login",
    json={
      "username": "employee997",
      "password": "password123"
    }
  )

  cookie = auth_response.cookies

  expense_id = 9999

  response = requests.put(
    f"{BASE_URL}/api/expenses/{expense_id}",
    json={
      "amount": 99.99,
      "description": "Updated lunch",
      "date": "2025-01-06"
    },
    cookies=cookie
  )

  assert response.status_code == 404

  data = response.json()
  assert data["error"] == "Expense not found"

def test_update_expense_missing_json(setup_database):
  auth_response = requests.post(
    f"{BASE_URL}/api/auth/login",
    json={
      "username": "employee997",
      "password": "password123"
    }
  )

  cookie = auth_response.cookies

  expense_id = 901

  response = requests.put(
    f"{BASE_URL}/api/expenses/{expense_id}",
    json={},
    cookies=cookie
  )

  assert response.status_code == 400

  data = response.json()
  assert data["error"] == "JSON data required"

def test_update_expense_not_owner(setup_database):
  auth_response = requests.post(
    f"{BASE_URL}/api/auth/login",
    json={"username": "employee997", "password": "password123"}
  )
  cookie = auth_response.cookies

  # Expense 903 belongs to employee998
  response = requests.put(
    f"{BASE_URL}/api/expenses/903",
    json={
      "amount": 10.0,
      "description": "Hack attempt",
      "date": "2025-01-06"
    },
    cookies=cookie
  )

  assert response.status_code == 404
