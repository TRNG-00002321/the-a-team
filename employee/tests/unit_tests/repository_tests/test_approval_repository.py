import pytest
from unittest.mock import Mock, MagicMock
from contextlib import contextmanager

#import sys
import os
from src.repository import approval_model, approval_repository, database, DatabaseConnection
#sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))
from src.repository.approval_repository import ApprovalRepository
from src.repository.approval_model import Approval
from src.repository.database import DatabaseConnection

@pytest.fixture
def mock_db_connection():
    """Create a mock database connection."""
    return MagicMock(spec=DatabaseConnection)


@pytest.fixture
def approval_repository(mock_db_connection):
    """Create approval repository with mocked database connection."""
    return ApprovalRepository(mock_db_connection)


@pytest.fixture
def mock_user_row():
    """Create a mock database row with user data."""
    row = MagicMock()
    row.__getitem__ = Mock(side_effect=lambda key: {
        'id': 1,
        'expense_id': 1,
        'status' : 'pending',
        'reviewer': 'johnathan',
        'comment': 'this is a comment',
        'review_date': '02/02/2025'
    }[key])
    return row

@pytest.fixture
def mock_expense_approval_row():
    """Create a mock database row with expense and approval data."""
    row_dict = {
        "id": 1,
        "user_id": 1,
        "amount": 100.0,
        "description": "Meal",
        "date": "02/02/2025",
        "status":"approved",
        "comment":"this is a comment",
        "review_date":"02/03/2025"
    }
    row = MagicMock()
    row.__getitem__ = Mock(side_effect=lambda key: row_dict[key])
    return [row]


def create_mock_connection(cursor_mock):
    """Helper to create a mock connection context manager."""
    @contextmanager
    def mock_get_connection():
        conn = Mock()
        conn.execute.return_value = cursor_mock
        yield conn
    return mock_get_connection


class TestFindByExpenseId:
    """Test cases for find_by_expense_id."""

    def test_find_by_expense_id_positive(self, approval_repository, mock_db_connection, mock_user_row):
        """Test finding a user by expense id successfully."""
        #Arrange
        cursor_mock = Mock()
        cursor_mock.fetchone.return_value = mock_user_row
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None

        #Act
        result = approval_repository.find_by_expense_id(1)

        #Assert
        assert result is not None
        assert isinstance(result, Approval)
        assert result.id == 1
        assert result.expense_id == 1
        assert result.status == 'pending'
        assert result.reviewer == 'johnathan'
        assert result.comment == 'this is a comment'
        assert result.review_date == '02/02/2025'

    @pytest.mark.parametrize(
        "expense_id",
        [
            999,
            0,
            -1,
            9999999
        ]
    )
    def test_find_by_expense_id_negative(self, expense_id, approval_repository, mock_db_connection):
        """ Test finding a user by expense id negative """
        # Arrange
        cursor_mock = Mock()
        cursor_mock.fetchone.return_value = None
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None
        # Act
        result = approval_repository.find_by_expense_id(expense_id)
        # Assert
        assert result is None


    def test_find_by_expense_id_correct_sql_executed(self, approval_repository, mock_db_connection, mock_user_row):
        """Test that the correct SQL query is executed."""
        # Arrange
        cursor_mock = Mock()
        cursor_mock.fetchone.return_value = mock_user_row
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None
        #Act
        approval_repository.find_by_expense_id(1)

        #Verify SQL query and parameters
        conn_mock.execute.assert_called_once()
        call_args = conn_mock.execute.call_args
        sql_query = call_args[0][0]
        params = call_args[0][1]
        #Assert
        assert "SELECT id, expense_id, status, reviewer, comment, review_date FROM approvals" in sql_query
        assert "WHERE expense_id = ?" in sql_query
        assert params == (1, )


# Find expense with status for user review test
class TestFindExpensesWithStatusForUser:
    """Test cases for find_expenses_with_status_for_user."""
    def test_find_expenses_with_status_positive(self, approval_repository, mock_db_connection, mock_expense_approval_row):
        """Test finding expenses with status positive."""
        #Arrange
        cursor_mock = MagicMock()
        cursor_mock.fetchall.return_value = mock_expense_approval_row
        conn_mock = MagicMock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None
        #Act
        results = approval_repository.find_expenses_with_status_for_user(1)
        #Assert
        assert len(results) == 1
        expense, approval = results[0]
        assert expense.user_id == 1
        assert expense.amount == 100.00
        assert approval.status == "approved"
        assert approval.comment == "this is a comment"

    @pytest.mark.parametrize(
        "user_id",
        [
            999,
            0,
            -1,
            9999999
        ]
    )

    def test_find_expenses_with_status_negative(self, user_id, approval_repository, mock_db_connection):
        """Test finding expenses with status returns empty list if no rows found."""
        #Arrange
        cursor_mock = MagicMock()
        cursor_mock.fetchall.return_value = []
        conn_mock = MagicMock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None
        #Act
        result = approval_repository.find_expenses_with_status_for_user(user_id)
        #Assert
        assert result == []
        assert len(result) ==0



# update status tests
class TestUpdateApproval:
    """Test cases for update_approval."""

    def test_update_user_positive(self, approval_repository, mock_db_connection):
        """Testing updating a user's approval status positive."""
        #Arrange
        cursor_mock = Mock()
        cursor_mock.rowcount = 1
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None
        #Act
        result = approval_repository.update_status(1, status='approved')
        #Assert
        assert result is True

    @pytest.mark.parametrize(
        "expense_id",
        [
            999,
            0,
            -1,
            9999999
        ]
    )

    def test_update_user_negative(self, expense_id, approval_repository, mock_db_connection):
        """Testing updating a user's approval status negative."""
        #Arrange
        cursor_mock = Mock()
        cursor_mock.rowcount = 0
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None
        #Act
        result = approval_repository.update_status(expense_id, status='pending')
        #Assert
        assert result is False

    def test_update_approvals_correct_sql_executed(self, approval_repository, mock_db_connection):
        #Arrange
        cursor_mock = Mock()
        cursor_mock.rowcount = 1
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock
        mock_db_connection.get_connection.return_value.__enter__.return_value = conn_mock
        mock_db_connection.get_connection.return_value.__exit__.return_value = None
        #Act
        result = approval_repository.update_status(1, "approved")
        conn_mock.execute.assert_called_once()
        sql, params = conn_mock.execute.call_args[0]
        #Assert
        assert "UPDATE approvals" in sql
        assert "SET status = ?" in sql
        assert "reviewer = ?" in sql
        assert "comment = ?" in sql
        assert "review_date = ?" in sql
        assert "WHERE expense_id = ?" in sql
        assert params == ("approved", None, None, None, 1)
        assert result is True









