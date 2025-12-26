from unittest.mock import MagicMock, patch

import pytest

from src.repository import ExpenseRepository, Expense, ApprovalRepository, Approval
from src.service import ExpenseService

#Expense Repository mock
@pytest.fixture(scope="module")
def mock_expense_repo():
    return MagicMock(spec=ExpenseRepository)

#User Repository mock
@pytest.fixture(scope="module")
def mock_approval_repo():
    return MagicMock(spec=ApprovalRepository)

#Expense Service object
@pytest.fixture(scope="module")
def expense_service_test(mock_expense_repo, mock_approval_repo):
    return ExpenseService(mock_expense_repo, mock_approval_repo)

#========================================================================================================
# SUBMIT EXPENSE TESTS
#========================================================================================================
@pytest.mark.parametrize("amount", [
    -1.0,
    0.0,
    -1000.0,
    -2.0
])
#EU-022
def test_submit_expense_negative_amount_returns_exception(expense_service_test, amount):
    #Arrange
    with pytest.raises(ValueError, match="Amount must be greater than 0"):

        #Act
        result = expense_service_test.submit_expense(1, amount, "test", "date")

        #Assert
        assert result == ValueError

#EU-023
def test_submit_expense_empty_description_returns_exception(expense_service_test):
    # Arrange
    with pytest.raises(ValueError, match="Description is required"):
        # Act
        result = expense_service_test.submit_expense(1, 1.0, "", "date")

        # Assert
        assert result == ValueError

#EU-024
def test_submit_expense_returns_expense(expense_service_test, mock_expense_repo):
    #Arrange
    expense = Expense(1, 1, 1.0, "test", "date")
    mock_expense_repo.create.return_value = expense

    #Act
    result = expense_service_test.submit_expense(1, 1.0, "test", "date")

    #Assert
    assert result == expense
    mock_expense_repo.create.assert_called()

#========================================================================================================
# GET USER EXPENSES WITH STATUS TESTS
#========================================================================================================
#EU-025
def test_get_user_expenses_with_status_returns_list(expense_service_test, mock_approval_repo):

    #Arrange
    user_id = 1

    #Act
    result = expense_service_test.get_user_expenses_with_status(user_id)

    #Assert
    assert result is not None
    mock_approval_repo.find_expenses_with_status_for_user.assert_called_with(user_id)

#EU-026
def test_get_user_expenses_with_status_returns_emptyList(expense_service_test, mock_approval_repo):

    #Arrange
    user_id = 1
    mock_approval_repo.find_expenses_with_status_for_user.return_value = []

    #Act
    result = expense_service_test.get_user_expenses_with_status(user_id)

    #Assert
    assert len(result) is 0
    mock_approval_repo.find_expenses_with_status_for_user.assert_called_with(user_id)


#========================================================================================================
# GET EXPENSE BY ID TESTS
#========================================================================================================
#EU-027
def test_get_expense_by_id_returns_expense(expense_service_test, mock_expense_repo):
    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    mock_expense_repo.find_by_id.return_value = expense

    #Act
    result = expense_service_test.get_expense_by_id(1,1)

    #Assert
    assert result is expense
    mock_expense_repo.find_by_id.assert_called_once_with(1)

#EU-028
def test_get_expense_by_id_returns_None(expense_service_test, mock_expense_repo):
    #Arrange
    mock_expense_repo.find_by_id.return_value = None

    #Act
    result = expense_service_test.get_expense_by_id(1,1)

    #Assert
    assert result is None

#========================================================================================================
# GET EXPENSE WITH STATUS TESTS
#========================================================================================================
#EU-029
def test_get_expense_with_status_returns_tuple(expense_service_test, mock_approval_repo, mock_expense_repo):
    #Arrange
    expense_id = 1
    user_id = 1
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, 'pending', None, None, None)

    mock_approval_repo.find_by_expense_id.return_value = approval
    mock_expense_repo.find_by_id.return_value = expense

    #Act
    result = expense_service_test.get_expense_with_status(expense_id, user_id)

    #Assert
    assert result is not None

#EU-030
def test_get_expense_with_status_returns_None(expense_service_test, mock_approval_repo, mock_expense_repo):

    #Arrange
    mock_approval_repo.find_by_expense_id.return_value = None
    mock_expense_repo.find_by_id.return_value = None

    #Act
    result = expense_service_test.get_expense_with_status(1, 1)

    #Assert
    assert result is None

#========================================================================================================
# UPDATE EXPENSE TESTS
#========================================================================================================
#EU-031
def test_update_expense_returns_expense(expense_service_test, mock_expense_repo):

    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, 'pending', None, None, None)
    mock_expense_repo.update.return_value = expense
    result = None

    #Act
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = (expense, approval)):
        result = expense_service_test.update_expense(1, 1, 1.0, 'test', 'date')

    #Assert
    assert result == expense
    mock_expense_repo.update.assert_called()

#EU-032
def test_update_expense_returns_none(expense_service_test, mock_expense_repo):
    #Arrange
    result = None

    #Act
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = None):
        result = expense_service_test.update_expense(1, 1, 1.0, 'test', 'date')

    #Assert
    assert result is None

@pytest.mark.parametrize("status", [
    'denied',
    'approved'
])
#EU-033
def test_update_expense_not_pending_returns_exception(expense_service_test, mock_expense_repo, status):
    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, status, None, None, None)

    #Act
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = (expense, approval)):
        with pytest.raises(ValueError, match="Cannot edit expense that has been reviewed"):
            result = expense_service_test.update_expense(1, 1, 1.0, 'test', 'date')

            # Assert
            assert result == ValueError
            mock_expense_repo.update.assert_not_called()

@pytest.mark.parametrize("amount", [
    -1.0,
    0.0
])
#EU-034
def test_update_expense_negative_amount_returns_exception(expense_service_test, mock_expense_repo, amount):
    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, 'pending', None, None, None)

    #Act
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = (expense, approval)):
        with pytest.raises(ValueError, match="Amount must be greater than 0"):
            result = expense_service_test.update_expense(1, 1, amount, 'test', 'date')

            # Assert
            assert result == ValueError
            mock_expense_repo.update.assert_not_called()

#EU-035
def test_update_expense_empty_description_returns_exception(expense_service_test, mock_expense_repo):
    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, 'pending', None, None, None)

    #Act
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = (expense, approval)):
        with pytest.raises(ValueError, match="Description is required"):
            result = expense_service_test.update_expense(1, 1, 1.0, "", 'date')

            # Assert
            assert result == ValueError
            mock_expense_repo.update.assert_not_called()

#========================================================================================================
# DELETE EXPENSE TESTS
#========================================================================================================
#EU-036
def test_delete_expense_returns_true(expense_service_test, mock_expense_repo):
    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, 'pending', None, None, None)
    mock_expense_repo.delete.return_value = True

    #Act
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = (expense, approval)):
            result = expense_service_test.delete_expense(1,1)

            # Assert
            assert result == True

@pytest.mark.parametrize("status", [
    'denied',
    'approved',
])
#EU-037
def test_delete_expense_returns_exception_if_status_not_pending(expense_service_test, status):
    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, status, None, None, None)

    #Act
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = (expense, approval)):
        with pytest.raises(ValueError):
            result = expense_service_test.delete_expense(1,1)

            # Assert
            assert result == ValueError

#EU-038
def test_delete_expense_returns_false(expense_service_test):

    #Arrange
    with patch("src.service.expense_service.ExpenseService.get_expense_with_status", return_value = None):
        #Act
        result = expense_service_test.delete_expense(1, 1)

        # Assert
        assert result == False

#========================================================================================================
# GET EXPENSE HISTORY TESTS
#========================================================================================================
@pytest.mark.parametrize("status", [
    'denied',
    'approved',
    'pending',
])
#EU-039
def test_get_expense_history_returns_list(expense_service_test, status):
    #Arrange
    expense = Expense(1, 1, 1.0, 'test', 'date')
    approval = Approval(1, 1, status, None, None, None)

    with patch("src.service.expense_service.ExpenseService.get_user_expenses_with_status",
               return_value = [(expense, approval)]):

        #Act
        result = expense_service_test.get_expense_history(1,status)

        #Assert
        assert len(result) > 0

#EU-040
def test_get_expense_history_returns_empty_list(expense_service_test):
    #Arrange
    with patch("src.service.expense_service.ExpenseService.get_user_expenses_with_status",
               return_value = []):

        #Act
        result = expense_service_test.get_expense_history(1, "pending")

        #Assert
        assert len(result) == 0