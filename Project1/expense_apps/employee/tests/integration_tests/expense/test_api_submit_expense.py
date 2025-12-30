import pytest
import requests

BASE_URL = "http://localhost:5000"


@pytest.fixture(scope="module")
def base_url():
    return BASE_URL


@pytest.fixture(scope="module")
def authenticated_session(base_url):
    """
    Logs in as employee1 and returns an authenticated session
    """
    session = requests.Session()

    login_payload = {
        "username": "employee1",
        "password": "password123"
    }

    response = session.post(
        f"{base_url}/api/auth/login",
        json=login_payload
    )

    assert response.status_code == 200
    assert session.cookies.get("jwt_token") is not None

    yield session

    #Teardown
    session.post(f"{base_url}/api/auth/logout")

@pytest.fixture
def created_expense(base_url, authenticated_session):
    #Request body
    expense_payload = {
        "amount": 125.75,
        "description": "Taxi to client meeting"
    }

    #Send post request
    response = authenticated_session.post(
        f"{base_url}/api/expenses",
        json=expense_payload
    )

    expense_id = response.json()["expense"]["id"]

    # Provide the expense ID to the test
    yield response

    #Teardown: delete the expense
    delete_response = authenticated_session.delete(
        f"{base_url}/api/expenses/{expense_id}"
    )

    assert delete_response.status_code in (200, 204)

#EI-215
def test_api_submit_new_expense_success(base_url, authenticated_session, created_expense):

    response = created_expense

    #Status code validation
    assert response.status_code in (200, 201)

    body = response.json()

    # Basic response validation
    assert body is not None
    assert "expense" in body

    expense = body["expense"]

    assert expense["amount"] == 125.75
    assert expense["description"] == "Taxi to client meeting"
    assert expense["status"].lower() == "pending"
    assert "id" in expense

#EI-216
def test_api_submit_expense_without_auth_should_fail(base_url):
    expense_payload = {
        "amount": 50.00,
        "category": "Food",
        "description": "Lunch"
    }

    response = requests.post(
        f"{base_url}/api/expenses",
        json=expense_payload
    )

    assert response.status_code in (401, 403)