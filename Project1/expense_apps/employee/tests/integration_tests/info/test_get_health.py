import requests

BASE_URL = "http://127.0.0.1:5000/"

def test_get_api_info():
    response = requests.get(BASE_URL+"/health")
    assert response.status_code == 200
    assert response.json()["message"] == "Employee Expense Management API is running"
    assert response.json()["status"] == "healthy"