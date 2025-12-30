import requests
import pytest
from requests import Session
from requests.auth import HTTPBasicAuth
from requests.exceptions import RequestException

BASE_URL = "http://127.0.0.1:5000/"

def test_get_api_info():
    response = requests.get(BASE_URL+"/api")
    assert response.status_code == 200
    assert response.json()["service"] == "Employee Expense Management API"
    assert response.json()["version"] == "1.0.0"
    endpoints = response.json()["endpoints"]
    assert endpoints["authentication"] == "/api/auth"
    assert endpoints["expenses"] == "/api/expenses"
    assert endpoints["health"] == "/health"