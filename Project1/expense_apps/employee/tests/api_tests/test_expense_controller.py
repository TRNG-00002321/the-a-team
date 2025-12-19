import importlib
import pytest
from flask import Flask
from unittest.mock import MagicMock
from src.repository import User, Expense
from src.api import auth
import src.api.expense_controller as expense_controller

@pytest.fixture
def app(monkeypatch):
  def identity_decorator(fn):
    return fn

  # mock require_employee_auth from auth
  monkeypatch.setattr(auth, "require_employee_auth", identity_decorator)

  # reload imports
  expense_controller_module = importlib.reload(expense_controller)

  app = Flask(__name__)
  app.testing = True
  app.register_blueprint(expense_controller_module.expense_bp)

  return app

@pytest.fixture
def client(app):
    return app.test_client()

@pytest.mark.parametrize(
  "json, expected_amount, expected_description, expected_date",
  [
    (
      {"amount": 100.1, "description": "Lunch", "date": "2025-12-19"},
      100.1,
      "Lunch",
      "2025-12-19",
    ),
    (
      {"amount": 50, "description": "Taxi", "date": "2025-01-01"},
      50.0,
      "Taxi",
      "2025-01-01",
    ),
    (
      {"amount": "75.25", "description": "Hotel"},
      75.25,
      "Hotel",
      None,
    ),
    (
      {"amount": 0.01, "description": "Coffee"},
      0.01,
      "Coffee",
      None,
    ),
  ],
)
def test_submit_expense_positive_inputs_201_expense(client, app, monkeypatch, json, expected_amount, expected_description, expected_date):
  # sample data
  fake_user = User(1, "test_user", "test_pass", "Employee")
  fake_expense = Expense(
    101,
    1,
    expected_amount,
    expected_description,
    expected_date
  )

  # Mock authenticated user
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: fake_user
  )

  # Mock ExpenseService
  mock_service = MagicMock()
  mock_service.submit_expense.return_value = fake_expense
  app.expense_service = mock_service

  response = client.post("/api/expenses", json=json)

  assert response.status_code == 201
  mock_service.submit_expense.assert_called_once_with(
    user_id=1,
    amount=expected_amount,
    description=expected_description,
    date=expected_date
  )
  assert response.json["message"] == "Expense submitted successfully"
  assert response.json["expense"]["amount"] == fake_expense.amount
  assert response.json["expense"]["description"] == fake_expense.description
  assert response.json["expense"]["date"] == fake_expense.date

@pytest.mark.parametrize(
  "json, expected_amount, expected_description, expected_date",
  [

  ],
)
def test_submit_expense_negative_inputs_errors(client, app, monkeypatch, json, expected_amount, expected_description, expected_date):
  pass