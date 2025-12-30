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

    # Teardown
    session.post(f"{base_url}/api/auth/logout")

#EI-217
def test_api_logout_success(base_url, authenticated_session):
    """
    Authenticated user can log out successfully
    """
    response = authenticated_session.post(
        f"{base_url}/api/auth/logout"
    )

    assert response.status_code == 200

    # After logout, cookie should be removed or empty
    jwt_cookie = authenticated_session.cookies.get("jwt_token")
    assert jwt_cookie is None

#EI-218
def test_api_access_protected_endpoint_after_logout_fails(base_url, authenticated_session):
    # Logout
    logout_response = authenticated_session.post(
        f"{base_url}/api/auth/logout"
    )
    assert logout_response.status_code == 200

    # Try accessing protected endpoint
    response = authenticated_session.get(
        f"{base_url}/api/expenses"
    )

    assert response.status_code in (401, 403)