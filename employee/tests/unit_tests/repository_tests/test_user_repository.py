import pytest
from src.repository import User, UserRepository


@pytest.fixture
def setUp(mocker):
    mock_db = mocker.MagicMock()
    mock_conn = mocker.MagicMock()
    mock_cursor = mocker.MagicMock()
    userRepo = UserRepository(mock_db)
    yield mock_db, mock_conn, mock_cursor, userRepo

class TestUserRepository:

    def test_find_by_username_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]

        expectedData = {"id":5, "username":"john123", "password":"password123", "role":"Employee"}
        setUp[2].fetchone.return_value = expectedData

        #Act
        actualUser = setUp[3].find_by_username("john123")

        #Assert
        assert actualUser.id == expectedData['id']
        assert actualUser.username == expectedData['username']
        assert actualUser.password == expectedData['password']
        assert actualUser.role == expectedData['role']

        setUp[1].execute.assert_called_once_with(
            "SELECT id, username, password, role FROM users WHERE username = ?",
            ("john123",)
        )

    def test_find_by_username_not_found(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].fetchone.return_value = None

        # Act
        actualUser = setUp[3].find_by_username("DoesntExist")

        # Assert
        assert actualUser is None
        setUp[1].execute.assert_called_once_with(
            "SELECT id, username, password, role FROM users WHERE username = ?",
            ("DoesntExist",)
        )

    def test_find_by_username_empty(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].fetchone.return_value = None

        # Act
        actualUser = setUp[3].find_by_username("")

        #Assert
        assert actualUser is None
        setUp[1].execute.assert_called_once_with(
            "SELECT id, username, password, role FROM users WHERE username = ?",
            ("",)
        )

    def test_find_by_id_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        expectedData = {"id": 5, "username": "john123", "password": "password123", "role": "Employee"}
        setUp[2].fetchone.return_value = expectedData
        # Act
        actualUser = setUp[3].find_by_id(5)
        # Assert
        assert actualUser.id == expectedData['id']
        assert actualUser.username == expectedData['username']
        assert actualUser.password == expectedData['password']
        assert actualUser.role == expectedData['role']

        setUp[1].execute.assert_called_once_with(
            "SELECT id, username, password, role FROM users WHERE id = ?",
            (5,)
        )

    def test_find_by_id_not_found(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].fetchone.return_value = None
        # Act
        actualUser = setUp[3].find_by_id(9999)

        # Assert
        assert actualUser is None
        setUp[1].execute.assert_called_once_with(
            "SELECT id, username, password, role FROM users WHERE id = ?",
            (9999,)
        )

    def test_find_by_id_negative_num(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].fetchone.return_value = None
        # Act
        actualUser = setUp[3].find_by_id(-1)

        # Assert
        assert actualUser is None
        setUp[1].execute.assert_called_once_with(
            "SELECT id, username, password, role FROM users WHERE id = ?",
            (-1,)
        )

    def test_find_by_id_null(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].fetchone.return_value = None
        # Act
        actualUser = setUp[3].find_by_id(None)

        # Assert
        assert actualUser is None
        setUp[1].execute.assert_called_once_with(
            "SELECT id, username, password, role FROM users WHERE id = ?",
            (None,)
        )

    def test_create_user_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        newUser = User(id=None, username="abc123", password="password123", role="Employee")
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].lastrowid = 100
        # Act
        actualUser = setUp[3].create(newUser)
        # Assert
        setUp[1].execute.assert_called_once_with(
            "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
            (newUser.username, newUser.password, newUser.role)
        )
        setUp[1].commit.assert_called_once()

        assert actualUser.id == 100
        assert actualUser.username == newUser.username
        assert actualUser.password == newUser.password
        assert actualUser.role == newUser.role

    def test_create_user_null(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = userRepo
        newUser = None
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].lastrowid = 100
        # Act/Assert
        with pytest.raises(AttributeError) as ex:
            setUp[3].create(newUser)
            assert str(ex) == "'NoneType' object has no attribute 'username'"

    @pytest.mark.parametrize("userIdInput, usernameInput, passwordInput, roleInput", [
        (None, "user123", "password123", "Employee"),
        (1, None, "password123", "Employee"),
        (1, "user123", None, "Employee")
    ])
    def test_create_user_null_attributes(self, setUp, userIdInput, usernameInput, passwordInput, roleInput):
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].lastrowid = userIdInput
        newUser = User(id=userIdInput, username=usernameInput, password = passwordInput, role=roleInput)

        actualUser = setUp[3].create(newUser)

        setUp[1].execute.assert_called_once_with(
            "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
            (newUser.username, newUser.password, newUser.role)
        )
        setUp[1].commit.assert_called_once()
        assert actualUser.id == newUser.id
        assert actualUser.username == newUser.username
        assert actualUser.password == newUser.password
        assert actualUser.role == newUser.role