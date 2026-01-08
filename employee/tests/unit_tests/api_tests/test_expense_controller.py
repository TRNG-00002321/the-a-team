import importlib
import pytest
from flask import Flask
from unittest.mock import MagicMock
from src.repository import User, Expense, Approval
from src.api import auth
import src.api.expense_controller as expense_controller

BASE_ROUTE = "/api/expenses"
FAKE_USER = User(1, "test_user", "test_pass", "Employee")

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
    lambda: FAKE_USER
  )

  # Mock ExpenseService
  mock_service = MagicMock()
  mock_service.submit_expense.return_value = fake_expense
  app.expense_service = mock_service

  response = client.post(BASE_ROUTE, json=json)

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
  "json, status_code, error_description",
  [
    ({"description": "lunch"}, 400, "Amount and description are required"),
    ({"amount" : None, "description": "lunch"}, 400, "Amount and description are required"),
    ({"amount" : 10}, 400, "Amount and description are required"),
    ({"amount" : 10, "description" : None}, 400, "Amount and description are required"),
    ({"amount" : "abc", "description": "lunch"}, 400, "Amount must be a valid number"),
  ],
)
def test_submit_expense_negative_inputs_errors(client, app, monkeypatch, json, status_code, error_description):
  # Mock ExpenseService
  app.expense_service = MagicMock()

  response = client.post(BASE_ROUTE, json=json)

  assert response.status_code == status_code
  assert response.get_json()["error"] == error_description
  app.expense_service.submit_expense.assert_not_called()

@pytest.mark.parametrize(
  "exception, status_code",
  [
    (ValueError(), 400),
    (Exception(), 500),
  ]
)
def test_submit_expense_exception_error(client, app, monkeypatch, exception, status_code):
  # Mock authenticated user
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.submit_expense.side_effect = exception
  app.expense_service = mock_service

  response = client.post(BASE_ROUTE, json={
    "amount": 1, "description": "sample", "date": "2025-12-19"
  })
  assert response.status_code == status_code
  mock_service.submit_expense.assert_called_once_with(
    user_id=FAKE_USER.id,
    amount=1,
    description="sample",
    date="2025-12-19"
  )


@pytest.mark.parametrize(
  "status, expense_approval, expected_count",
  [
    (
      "pending",
      [
        (Expense(101, 1, 100.1, "Lunch", "2025-12-19"), Approval(1, 101, "pending", None, None, None))
      ],
      1
    ),
    (
      "approved",
      [
        (Expense(101, 1, 100.1, "Lunch", "2025-12-19"), Approval(1, 101, "approved", 2, "approval comment", "2025-12-20"))
      ],
      1
    ),
    (
      "denied",
      [
        (Expense(101, 1, 100.1, "Lunch", "2025-12-19"), Approval(1, 101, "denied", 2, "denial comment", "2025-12-20"))
      ],
      1
    ),
  ],
)
def test_get_expense_list_different_statuses_filtered(client, app,  monkeypatch, status, expense_approval, expected_count):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.get_expense_history.return_value = expense_approval

  app.expense_service = mock_service

  query = f"?status={status}" if status else ""
  response = client.get(f"{BASE_ROUTE}{query}")

  assert response.status_code == 200

  data = response.get_json()
  expense, approval = expense_approval[0]
  assert data["count"] == expected_count
  assert data["expenses"][0]["id"] == 101
  assert data["expenses"][0]["status"] == approval.status

  mock_service.get_expense_history.assert_called_once_with(
    user_id=1,
    status_filter=status
  )

@pytest.mark.parametrize(
  "expense_approval, expected_count",
  [
    (
      [],
      0
    ),
    (
      [
        (Expense(101, 1, 100.1, "Lunch 1", "2025-12-19"), Approval(1, 101, "approved", 2, "approval comment", "2025-12-20")),
        (Expense(102, 1, 100.1, "Lunch 2", "2025-12-19"), Approval(1, 102, "denied", 2, "denial comment", "2025-12-20"))
      ],
      2
    ),
  ],
)
def test_get_expense_list_different_sizes(client, app, monkeypatch, expense_approval, expected_count):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.get_expense_history.return_value = expense_approval

  app.expense_service = mock_service

  response = client.get(f"{BASE_ROUTE}")

  assert response.status_code == 200

  data = response.get_json()
  assert data["count"] == expected_count

  for i, (expense, approval) in enumerate(expense_approval):
    individual_data = data["expenses"][i]
    assert individual_data["id"] == expense.id
    assert individual_data["status"] == approval.status

  mock_service.get_expense_history.assert_called_once_with(
    user_id=1,
    status_filter=None
  )

def test_get_expense_list_exception_500(client, app, monkeypatch):
  # Mock authenticated user
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.get_expense_history.side_effect = Exception
  app.expense_service = mock_service

  response = client.get(f"{BASE_ROUTE}")
  assert response.status_code == 500
  mock_service.get_expense_history.assert_called_once_with(
    user_id=FAKE_USER.id,
    status_filter=None
  )


@pytest.mark.parametrize(
  "expense_approval",
  [
    (Expense(101, 1, 100.1, "Lunch", "2025-12-19"), Approval(101, 101, "pending", None, None, None)),
    (Expense(102, 1, 100.1, "Lunch", "2025-12-19"), Approval(101, 102, "approved", 2, "approval comment", "2025-12-20")),
    (Expense(103, 1, 100.1, "Lunch", "2025-12-19"), Approval(101, 103, "denied", 2, "denial comment", "2025-12-20"))
  ]
)
def test_get_expense(client, app, monkeypatch, expense_approval):
  fake_expense, fake_approval = expense_approval

  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.get_expense_with_status.return_value = (fake_expense, fake_approval)
  app.expense_service = mock_service

  response = client.get(f"{BASE_ROUTE}/{fake_expense.id}")

  assert response.status_code == 200

  data = response.get_json()
  assert data["expense"]["id"] == fake_expense.id
  assert data["expense"]["status"] == fake_approval.status
  assert data["expense"]["amount"] == fake_expense.amount
  assert data["expense"]["description"] == fake_expense.description
  assert data["expense"]["date"] == fake_expense.date
  mock_service.get_expense_with_status.assert_called_once_with(
    fake_expense.id,
    FAKE_USER.id
  )

def test_get_expense_negative_404(client, app, monkeypatch):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.get_expense_with_status.return_value = None
  app.expense_service = mock_service

  response = client.get(f"{BASE_ROUTE}/404")

  assert response.status_code == 404
  mock_service.get_expense_with_status.assert_called_once_with(
    404,
    FAKE_USER.id
  )

def test_get_expense_exception_500(client, app, monkeypatch):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.get_expense_with_status.side_effect = Exception
  app.expense_service = mock_service

  response = client.get(f"{BASE_ROUTE}/101")

  assert response.status_code == 500
  mock_service.get_expense_with_status.assert_called_once_with(
    101,
    FAKE_USER.id
  )

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
      {"amount": 50, "description": "Taxi", "date": "2025-12-19"},
      50,
      "Taxi",
      "2025-12-19",
    ),
    (
      {"amount": "75.25", "description": "Hotel", "date": "2025-12-19"},
      75.25,
      "Hotel",
      "2025-12-19",
    ),
    (
      {"amount": 0.01, "description": "Coffee", "date": "2025-12-19"},
      0.01,
      "Coffee",
      "2025-12-19",
    ),
  ],
)
def test_update_expense_positive_inputs_200(client, app, monkeypatch, json, expected_amount, expected_description, expected_date):
  fake_expense = Expense(
    101,
    1,
    expected_amount,
    expected_description,
    expected_date
  )

  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.update_expense.return_value = fake_expense
  app.expense_service = mock_service

  response = client.put(f"{BASE_ROUTE}/{fake_expense.id}", json=json)
  assert response.status_code == 200
  mock_service.update_expense.assert_called_once_with(
    expense_id=fake_expense.id,
    user_id=1,
    amount=expected_amount,
    description=expected_description,
    date=expected_date
  )
  assert response.json["message"] == "Expense updated successfully"
  assert response.json["expense"]["amount"] == fake_expense.amount
  assert response.json["expense"]["description"] == fake_expense.description
  assert response.json["expense"]["date"] == fake_expense.date

@pytest.mark.parametrize(
  "json, status_code, error_description",
  [
    ({}, 400, "JSON data required"),
    ({"amount" : 10, "description": "lunch"}, 400, "Amount, description, and date are required"),
    ({"amount" : 10, "date": "2025-12-20"}, 400, "Amount, description, and date are required"),
    ({"description" : "lunch", "date" : "2025-12-20"}, 400, "Amount, description, and date are required"),
    ({"amount" : 10}, 400, "Amount, description, and date are required"),
    ({"description" : "lunch", "date" : None}, 400, "Amount, description, and date are required"),
    ({"amount" : "abc", "description": "lunch", "date" : "2025-12-20"}, 400, "Amount must be a valid number"),
  ],
)
def test_update_expense_negative_inputs_error(client, app, monkeypatch, json, status_code, error_description):
  app.expense_service = MagicMock()

  response = client.put(f"{BASE_ROUTE}/101", json=json)

  assert response.status_code == status_code
  assert response.get_json()["error"] == error_description
  app.expense_service.update_expense.assert_not_called()

@pytest.mark.parametrize(
  "exception, status_code",
  [
    (ValueError(), 400),
    (Exception(), 500),
  ]
)
def test_update_expense_exception_error(client, app, monkeypatch, exception, status_code):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.update_expense.side_effect = exception
  app.expense_service = mock_service

  response = client.put(f"{BASE_ROUTE}/101", json={
    "amount": 1, "description": "sample", "date": "2025-12-19"
  })
  assert response.status_code == status_code
  mock_service.update_expense.assert_called_once_with(
    expense_id=101,
    user_id=FAKE_USER.id,
    amount=1,
    description="sample",
    date="2025-12-19"
  )

def test_delete_expense_positive_200(client, app, monkeypatch):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.delete_expense.return_value = True
  app.expense_service = mock_service

  response = client.delete(f"{BASE_ROUTE}/101")
  assert response.status_code == 200

  data = response.get_json()
  assert data["message"] == "Expense deleted successfully"

  mock_service.delete_expense.assert_called_once_with(
    101,
    FAKE_USER.id
  )

def test_delete_expense_negative_404(client, app, monkeypatch):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.delete_expense.return_value = False
  app.expense_service = mock_service

  response = client.delete(f"{BASE_ROUTE}/101")
  assert response.status_code == 404

  data = response.get_json()
  assert data["error"] == "Expense not found"

  mock_service.delete_expense.assert_called_once_with(
    101,
    FAKE_USER.id
  )

@pytest.mark.parametrize(
  "exception, status_code",
  [
    (ValueError(), 400),
    (Exception(), 500),
  ]
)
def test_delete_expense_exceptions_error(client, app, monkeypatch, exception, status_code):
  monkeypatch.setattr(
    expense_controller,
    "get_current_user",
    lambda: FAKE_USER
  )

  mock_service = MagicMock()
  mock_service.delete_expense.side_effect = exception
  app.expense_service = mock_service

  response = client.delete(f"{BASE_ROUTE}/101")
  assert response.status_code == status_code

  mock_service.delete_expense.assert_called_once_with(
    101,
    FAKE_USER.id
  )