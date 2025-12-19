import pytest
from flask import Flask
from src.api import auth_controller


class Test_Auth_Controller:
    @pytest.fixture
    def setup(self, mocker):
        app = Flask(__name__)
        app.register_blueprint(auth_controller.auth_bp)

        app.auth_service = mocker.Mock()
        client = app.test_client()

        return app, client

    # EU-053
    @pytest.mark.parametrize("payload", [
        {},
        {"username": "user"},
        {"password": "pass"},
        {"username": "", "password": "pass"},
        {"username": "user", "password": ""}
    ])
    def test_login_missing_credentials(self, setup, payload):
        response = setup[1].post("/api/auth/login", json=payload)

        assert response.status_code == 400
        assert "error" in response.get_json()

    # EU-054
    def test_login_invalid_json(self, setup):
        response = setup[1].post("/api/auth/login", json=None)

        assert response.status_code == 500
        assert "error" in response.get_json()

    # EU-055
    def test_login_invalid_credentials(self, setup):
        setup[0].auth_service.authenticate_user.return_value = None

        response = setup[1].post("/api/auth/login", json={"username": "user", "password": "wrong"})

        assert response.status_code == 401
        assert response.get_json()["error"] == "Invalid credentials"

    # EU-056
    def test_login_positive(self, setup):
        user = type("User", (), {"id": 1, "username": "testuser", "role": "admin"})()

        setup[0].auth_service.authenticate_user.return_value = user
        setup[0].auth_service.generate_jwt_token.return_value = "fake-jwt-token"

        response = setup[1].post("/api/auth/login", json={"username": "testuser", "password": "password"})

        assert response.status_code == 200

        data = response.get_json()
        assert data["message"] == "Login successful"
        assert data["user"]["username"] == "testuser"

        set_cookie_header = response.headers.get("Set-Cookie")
        assert "jwt_token=fake-jwt-token" in set_cookie_header
        assert "HttpOnly" in set_cookie_header

    # EU-057
    def test_login_exception(self, setup):
        setup[0].auth_service.authenticate_user.side_effect = Exception("Broken")

        response = setup[1].post("/api/auth/login", json={"username": "user", "password": "pass"})

        assert response.status_code == 500
        assert response.get_json()["error"] == "Login failed"

    # EU-058
    def test_logout(self, setup):
        response = setup[1].post("/api/auth/logout")
        set_cookie = response.headers.get("Set-Cookie")

        assert response.status_code == 200
        assert response.get_json() == {"message": "Logout successful"}
        assert "jwt_token" in set_cookie
        assert "HttpOnly" in set_cookie

    # EU-059
    def test_status_negative(self, setup):
        response = setup[1].get("/api/auth/status")

        assert response.status_code == 200
        assert response.get_json() == {"authenticated": False}

    # EU-060
    def test_status_positive(self, setup):
        user = type("User", (), {"id": 1, "username": "testuser", "role": "Employee"})()

        setup[0].auth_service.get_user_from_token.return_value = user
        setup[1].set_cookie("jwt_token", "valid_token")
        response = setup[1].get("/api/auth/status")

        data = response.get_json()

        assert data["authenticated"]
        assert data["user"]["username"] == "testuser"
        assert data["user"]["role"] == "Employee"

    # EU-060
    @pytest.mark.parametrize("side_effect, return_value", [
        (None, None),
        (Exception("bad token"), None)
    ])
    def test_status_exception(self, setup, side_effect, return_value):
        if side_effect:
            setup[0].auth_service.get_user_from_token.side_effect = side_effect
        else:
            setup[0].auth_service.get_user_from_token.return_value = return_value

        setup[1].set_cookie("jwt_token", "invalid_token")
        response = setup[1].get("/api/auth/status")

        assert response.status_code == 200
        assert response.get_json() == {"authenticated": False}