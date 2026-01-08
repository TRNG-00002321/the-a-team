import pytest
from src.repository import Expense, ExpenseRepository
from unittest.mock import call

@pytest.fixture
def setUp(mocker):
    mock_db = mocker.MagicMock()
    mock_conn = mocker.MagicMock()
    mock_cursor = mocker.MagicMock()
    expenseRepo = ExpenseRepository(mock_db)
    yield mock_db, mock_conn, mock_cursor, expenseRepo

class TestExpenseRepository:

    def test_create_expense_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].lastrowid = 5

        newExpense = Expense(None, 1, 95.49, "printer supplies", "2025-12-17")

        # Act
        actualExpense = setUp[3].create(newExpense)

        # Assert
        expectedCalls = [
            call("INSERT INTO expenses (user_id, amount, description, date) VALUES (?, ?, ?, ?)",
            (newExpense.user_id, newExpense.amount, newExpense.description, newExpense.date)),
            call("INSERT INTO approvals (expense_id, status) VALUES (?, 'pending')",
            (5,))
        ]
        setUp[1].execute.assert_has_calls(expectedCalls, any_order=False)
        setUp[1].commit.assert_called_once()

        assert actualExpense.id == 5
        assert actualExpense.user_id == newExpense.user_id
        assert actualExpense.amount == newExpense.amount
        assert actualExpense.description == newExpense.description
        assert actualExpense.date == newExpense.date

    def test_create_expense_null(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        newExpense = None

        # Act/Assert
        with pytest.raises(AttributeError) as ex:
            setUp[3].create(newExpense)
            assert str(ex) == "'NoneType' object has no attribute 'user_id'"

    @pytest.mark.parametrize("idInput, userIdInput, amountInput, descInput, dateInput", [
        (None, 1, 95.49, "printer supplies", "2025-12-17"),
        (5, None, 95.49, "printer supplies", "2025-12-17"),
        (5, 1, None, "printer supplies", "2025-12-17"),
        (5, 1, 95.49, None, "2025-12-17"),
        (5, 1, 95.49, "printer supplies", None)
    ])
    def test_create_expense_null_attributes(self, setUp, idInput, userIdInput, amountInput, descInput, dateInput):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].lastrowid = idInput
        newExpense = Expense(None, userIdInput, amountInput, descInput, dateInput)
        # Act
        actualExpense = setUp[3].create(newExpense)
        # Assert
        setUp[1].commit.assert_called_once()
        assert actualExpense.id == idInput
        assert actualExpense.user_id == newExpense.user_id
        assert actualExpense.amount == newExpense.amount
        assert actualExpense.description == newExpense.description
        assert actualExpense.date == newExpense.date

    def test_find_by_id_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        expectedData = {"id": 5, "user_id": 1, "amount": 79.32, "description": "printer supplies", "date": "2025-12-17"}
        setUp[2].fetchone.return_value = expectedData
        # Act
        actualExpense = setUp[3].find_by_id(expectedData["id"])
        # Assert
        setUp[1].execute.assert_called_once_with(
            "SELECT id, user_id, amount, description, date FROM expenses WHERE id = ?",
            (expectedData['id'],))
        assert actualExpense.id == expectedData['id']
        assert actualExpense.user_id == expectedData['user_id']
        assert actualExpense.amount == expectedData['amount']
        assert actualExpense.description == expectedData['description']
        assert actualExpense.date == expectedData['date']

    @pytest.mark.parametrize("idInput", [
        (-5),
        (9999999),
        (None)
    ])
    def test_find_by_id_negative(self, setUp, idInput):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].fetchone.return_value = None
        # Act
        actualExpense = setUp[3].find_by_id(idInput)
        # Assert
        setUp[1].execute.assert_called_once_with(
            "SELECT id, user_id, amount, description, date FROM expenses WHERE id = ?",
            (idInput,))
        assert actualExpense is None

    def test_find_by_user_id_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        expectedData = {"id": 5, "user_id": 1, "amount": 79.32, "description": "printer supplies", "date": "2025-12-17"}
        expectedData2 = {"id": 6, "user_id": 1, "amount": 100.50, "description": "office supplies", "date": "2025-12-17"}
        expectedData3 = {"id": 7, "user_id": 1, "amount": 350.99, "description": "door repair", "date": "2025-12-17"}
        setUp[2].fetchall.return_value = [
            expectedData, expectedData2, expectedData3
        ]
        newExpense1 = Expense(5, 1, 79.32, "printer supplies", "2025-12-17")
        newExpense2 = Expense(6, 1, 100.50, "office supplies", "2025-12-17")
        newExpense3 = Expense(7, 1, 350.99, "door repair", "2025-12-17")
        # Act
        actualExpenseList = setUp[3].find_by_user_id(1)
        # Assert
        setUp[1].execute.assert_called_once_with(
            "SELECT id, user_id, amount, description, date FROM expenses WHERE user_id = ? ORDER BY date DESC",
            (1,))
        assert len(actualExpenseList) == 3
        assert newExpense1 in actualExpenseList
        assert newExpense2 in actualExpenseList
        assert newExpense3 in actualExpenseList

    @pytest.mark.parametrize("userIdInput", [
        (-34),
        (567898888),
        (None)
    ])
    def test_find_by_user_id_negative(self, setUp, userIdInput):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].fetchall.return_value = []
        # Act
        actualExpenseList = setUp[3].find_by_user_id(userIdInput)
        # Assert
        setUp[1].execute.assert_called_once_with(
            "SELECT id, user_id, amount, description, date FROM expenses WHERE user_id = ? ORDER BY date DESC",
            (userIdInput,))
        assert len(actualExpenseList) == 0

    def test_update_expense_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        newExpense = Expense(5, 1, 79.32, "printer supplies", "2025-12-17")
        # Act
        actualExpense = setUp[3].update(newExpense)
        # Assert
        setUp[1].execute.assert_called_once_with(
            "UPDATE expenses SET amount = ?, description = ?, date = ? WHERE id = ?",
            (newExpense.amount, newExpense.description, newExpense.date, newExpense.id)
        )
        setUp[1].commit.assert_called_once()
        assert newExpense == actualExpense

    def test_update_expense_null(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        newExpense = None
        # Act
        with pytest.raises(AttributeError) as ex:
            actualExpense = setUp[3].update(newExpense)
            assert str(ex) == "'NoneType' object has no attribute 'amount'"

    @pytest.mark.parametrize("idInput, userIdInput, amountInput, descInput, dateInput", [
        (None, 1, 79.32, "printer supplies", "2025-12-17"),
        (5, None, 79.32, "printer supplies", "2025-12-17"),
        (5, 1, None, "printer supplies", "2025-12-17"),
        (5, 1, 79.32, None, "2025-12-17"),
        (5, 1, 79.32, "printer supplies", None),
    ])
    def test_update_expense_null_attributes(self, setUp, idInput, userIdInput, amountInput, descInput, dateInput):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        newExpense = Expense(idInput, userIdInput, amountInput, descInput, dateInput)
        # Act
        actualExpense = setUp[3].update(newExpense)
        # Assert
        setUp[1].commit.assert_called_once_with()
        assert actualExpense == newExpense

    def test_delete_positive(self, setUp):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].rowcount = 1
        expectedCalls = [
            call("DELETE FROM approvals WHERE expense_id = ?", (5,)),
            call("DELETE FROM expenses WHERE id = ?", (5,))
        ]
        # Act
        result = setUp[3].delete(5)
        # Assert
        setUp[1].execute.assert_has_calls(expectedCalls, any_order=False)
        setUp[1].commit.assert_called_once()
        assert result == True

    @pytest.mark.parametrize("idInput",[
        (-49),
        (83918479),
        (None)
    ])
    def test_delete_negative(self, setUp, idInput):
        # Arrange
        # setUp[0] = mock_db, setUp[1] = mock_conn,
        # setUp[2] = mock_cursor, setUp[3] = expenseRepo
        setUp[0].get_connection.return_value.__enter__.return_value = setUp[1]
        setUp[1].execute.return_value = setUp[2]
        setUp[2].rowcount = 0
        expectedCalls = [
            call("DELETE FROM approvals WHERE expense_id = ?", (idInput,)),
            call("DELETE FROM expenses WHERE id = ?", (idInput,))
        ]
        # Act
        result = setUp[3].delete(idInput)
        # Assert
        setUp[1].execute.assert_has_calls(expectedCalls, any_order=False)
        setUp[1].commit.assert_called_once()
        assert result == False