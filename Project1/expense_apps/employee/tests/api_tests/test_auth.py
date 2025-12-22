from types import SimpleNamespace
from unittest.mock import MagicMock

import pytest
from flask import Flask, request, jsonify
from src.api.auth import require_employee_auth
from src.repository import User, Expense
import src.api.expense_controller as expense_controller

@pytest.fixture
def app(monkeypatch):
    app = Flask(__name__)
    app.testing = True
    app.register_blueprint(expense_controller.expense_bp)

    return app

@pytest.fixture
def client(app):
    return app.test_client()

def test_require_employee_auth_no_token(app):
  protected = require_employee_auth(lambda: ("OK", 200))

  with app.test_request_context("/"):
    response, status = protected()

  assert status == 401
  assert response.json["error"] == "Authentication required"


def test_require_employee_auth_wrong_role(app, monkeypatch):
  manager_user = SimpleNamespace(role="Manager")

  auth_service = MagicMock()
  auth_service.get_user_from_token.return_value = manager_user

  monkeypatch.setattr(
    "src.api.auth.get_auth_service",
    lambda: auth_service
  )

  protected = require_employee_auth(lambda: ("OK", 200))

  with app.test_request_context("/", headers={"Cookie": "jwt_token=fake"}):
    response, status = protected()

  assert status == 403
  assert response.json["error"] == "Access denied"