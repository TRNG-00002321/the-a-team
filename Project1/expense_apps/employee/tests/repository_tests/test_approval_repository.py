import pytest
from unittest.mock import Mock, MagicMock
from contextlib import contextmanager

import sys
import os
from repository import approval_model, approval_repository, database, DatabaseConnection
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))
from repository.approval_repository import ApprovalRepository
from repository.approval_model import Approval
from repository.database import DatabaseConnection

@pytest.fixture
def mock_db_connection():
    """Create a mock database connection."""
    return Mock(spec=DatabaseConnection)


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


def create_mock_connection(cursor_mock):
    """Helper to create a mock connection context manager."""
    @contextmanager
    def mock_get_connection():
        conn = Mock()
        conn.execute.return_value = cursor_mock
        yield conn
    return mock_get_connection

# Find by expense_id test:
class TestFindByExpenseId:
    """Test cases for find_by_expense_id."""

    def test_find_by_expense_id_positive(self, approval_repository, mock_db_connection, mock_user_row):
        """Test finding a user by expense id successfully."""
        cursor_mock = Mock()
        cursor_mock.fetchone.return_value = mock_user_row
        mock_db_connection.get_connection = create_mock_connection(cursor_mock)
        result = approval_repository.find_by_expense_id(1)

        assert result is not None
        assert isinstance(result, Approval)
        assert result.id == 1
        assert result.expense_id == 1
        assert result.status == 'pending'
        assert result.reviewer == 'johnathan'
        assert result.comment == 'this is a comment'
        assert result.review_date == '02/02/2025'

    def test_find_expense_id_negative(self, approval_repository, mock_db_connection):
        """Test that None is returned when expense_id doesn't exist"""
        cursor_mock = Mock()
        cursor_mock.fetchone.return_value = None
        mock_db_connection.get_connection = create_mock_connection(cursor_mock)
        result = approval_repository.find_by_expense_id(999)
        assert result is None


    def test_find_by_expense_id_correct_sql_executed(self, approval_repository, mock_db_connection, mock_user_row):
        """Test that the correct SQL query is executed."""
        cursor_mock = Mock()
        cursor_mock.fetchone.return_value = mock_user_row
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock

        @contextmanager
        def mock_get_connection():
            yield conn_mock
        mock_db_connection.get_connection = mock_get_connection
        approval_repository.find_by_expense_id(1)

        #Verify SQL query and parameters
        conn_mock.execute.assert_called_once()
        call_args = conn_mock.execute.call_args
        sql_query = call_args[0][0]
        params = call_args[0][1]

        assert "SELECT id, expense_id, status, reviewer, comment, review_date FROM approvals" in sql_query
        assert "WHERE expense_id = ?" in sql_query
        assert params == (1, )


# Find expense with status for user review test

# update status tests
class TestUpdateApproval:
    """Test cases for update_approval."""

    def test_update_user_positive(self, approval_repository, mock_db_connection):
        """Testing updating a user's approval status positive."""
        cursor_mock = Mock()
        cursor_mock.rowcount = 1
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock
        @contextmanager
        def mock_get_connection():
            yield conn_mock

        mock_db_connection.get_connection = mock_get_connection

        result = approval_repository.update_status(1, status='approved')
        assert result is True

    def test_update_user_negative(self, approval_repository, mock_db_connection):
        """Testing updating a user's approval status negative."""
        cursor_mock = Mock()
        cursor_mock.rowcount = 0
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock

        @contextmanager
        def mock_get_connection():
            yield conn_mock

        mock_db_connection.get_connection = mock_get_connection
        result = approval_repository.update_status(999, status='pending')
        assert result is False


class TestFindExpensesWithStatusForUser:
    """Test cases for find_expenses_with_status_for_user."""

    def test_find_expenses_with_status_positive(self, approval_repository, mock_db_connection, mock_user_row):
        """Test finding expenses with status positive."""
        cursor_mock = Mock()
        cursor_mock.fetchall.return_value = mock_user_row
        conn_mock = Mock()
        conn_mock.execute.return_value = cursor_mock

        @contextmanager
        def mock_get_connection():
            yield conn_mock

        mock_db_connection.get_connection = mock_get_connection
        result = approval_repository.find_expenses_with_status_for_user(1)

        assert result is not None
        assert isinstance(result, results)
        assert result.id == 1
        assert result.expense_id == 1
        assert result.status == 'pending'
        assert result.reviewer == 'johnathan'
        assert result.comment == 'this is a comment'
        assert result.review_date == '02/02/2025'





