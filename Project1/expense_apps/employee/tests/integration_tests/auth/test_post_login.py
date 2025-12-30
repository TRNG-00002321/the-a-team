import requests
import pytest
from requests import Session
from requests.auth import HTTPBasicAuth
from requests.exceptions import RequestException

BASE_URL = "http://127.0.0.1:5000/"

def test_login_positive():
    data={
        "username":"employee1",
        "password":"password123"
    }
    response = requests.post(BASE_URL + "/api/auth/login", json=data)
    assert response.status_code == 200
    user = response.json()["user"]
    assert user["username"] == "employee1"
    assert user["role"] == "Employee"
    assert user["id"] == 1

@pytest.mark.parametrize("username, password", [
    ("employee1","wrong_password"),
    ("wrong_username", "password123"),
    (12345, 12345)
])
def test_login_negative(username, password):
    data = {
        "username": username,
        "password": password
    }
    try:
        response = requests.post(BASE_URL + "/api/auth/login", json=data)
        response.raise_for_status()
        assert False
    except requests.exceptions.HTTPError:
        assert response.status_code == 401
        assert "Invalid credentials" in response.json()["error"]


@pytest.mark.parametrize("username, password", [
    ("",""),
    (None, None),
])
def test_login_error(username, password):
    data = {
        "username": username,
        "password": password
    }
    try:
        response = requests.post(BASE_URL + "/api/auth/login", json=data)
        response.raise_for_status()
        assert False
    except requests.exceptions.HTTPError:
        assert response.status_code == 400
        assert "Username and password required" in response.json()["error"]

