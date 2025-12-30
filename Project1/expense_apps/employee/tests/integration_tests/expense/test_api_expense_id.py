import pytest
import requests

BASE_URL = "http://127.0.0.1:5000"

class TestSpecificExpenseAPI:

    @pytest.fixture
    def api_session(self):
        """Create an authenticated session for API tests.
        This logs in as employee1 and returns a session with the JWT cookie set."""
        session = requests.Session()
        yield session
        session.close()

    @pytest.fixture
    def credentials(self):
        return {"username": "employee1", "password": "password123", "role": "employee"}

    @pytest.fixture
    def sample_expense(self):
        return {
            "amount": 200.0,
            "date": "2025-12-28",
            "description" : "Travel Expenses",
            "status": "pending"
        }


    def test_get_specific_expense_by_id_positive(self, api_session, credentials, sample_expense):
        # Login
        login_url = f"{BASE_URL}/api/auth/login"
        login_response = api_session.post(login_url, json = credentials)
        assert login_response.status_code == 200

        # Create expense to get valid id
        create_url = f"{BASE_URL}/api/expenses"
        create_response = api_session.post(create_url, json = sample_expense)
        assert create_response.status_code == 201
        expense_id = create_response.json()["expense"]["id"]

        # Get expense by id
        expense_response = api_session.get(f"{BASE_URL}/api/expenses/{expense_id}")
        assert expense_response.status_code == 200
        expense_data = expense_response.json()["expense"]

        assert expense_data["amount"] == sample_expense["amount"]
        assert expense_data["id"] == expense_id

    def test_get_specific_expense_by_id_negative(self, api_session, credentials):
        # Login
        login_url = f"{BASE_URL}/api/auth/login"
        login_response = api_session.post(login_url, json=credentials)
        assert login_response.status_code == 200

        # Get expense by id - not found
        expense_response = api_session.get(f"{BASE_URL}/api/expenses/999")
        assert expense_response.status_code == 404

    def test_get_expense_user_not_logged_in(self):
        # get expense without login
        expense_id = 1
        response = requests.get(f"{BASE_URL}/api/expenses/{expense_id}")
        assert response.status_code == 401











