import pytest
import requests
import allure
import sys
import os


BASE_URL = "http://127.0.0.1:5000"

class TestAuthenticationAPI:

    @pytest.fixture
    def api_session(self):
        """Creates a requests session for API testing.

        A session maintains cookies across requests,
        which is needed for JWT authentication."""
        session = requests.Session()
        yield session
        session.close()

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

    def test_status_unauthenticated(self, api_session):
        # Test auth status when not logged in.
        #Arrange
        url = f"{BASE_URL}/api/auth/status"
        #Act
        response = api_session.get(url)
        #Assert
        assert response.status_code == 200
        json_response = response.json()
        assert json_response.get("authenticated") is False

    def test_status_authenticated_valid_cred(self, api_session, valid_credentials):
        # Test auth status when logged in.
        #Arrange login and status
        login_url = f"{BASE_URL}/api/auth/login"
        status_url = f"{BASE_URL}/api/auth/status"

        #Act+Assert for login
        login_response = api_session.post(login_url, json=valid_credentials)
        assert login_response.status_code == 200

        #Act+Assert for status
        status_response = api_session.get(status_url)
        assert status_response.status_code == 200
        data = status_response.json()
        assert data["authenticated"] is True
        assert valid_credentials["username"] == "employee1"
        assert valid_credentials["password"] == "password123"
        assert valid_credentials["role"] == "employee"


    def test_status_invalid_cred(self, invalid_credentials, api_session):
        login_url = f"{BASE_URL}/api/auth/login"
        status_url = f"{BASE_URL}/api/auth/status"

        api_session.post(login_url, json=invalid_credentials)
        response = api_session.get(status_url)
        assert response.status_code == 200
        json_response = response.json()
        assert json_response.get("authenticated") is False








