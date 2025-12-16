from unittest.mock import MagicMock

import pytest

from repository import UserRepository, ExpenseRepository, Expense
from service import ExpenseService



@pytest.fixture(scope='module')
def startUp():
    mock_expense_repository = MagicMock(spec=ExpenseRepository)
    mock_user_repository = MagicMock(spec=UserRepository)

    service = ExpenseService(mock_expense_repository, mock_user_repository)

    yield service, mock_expense_repository, mock_user_repository

def test_get_expense_by_id_returns_None(startUp):

    startUp[1].find_by_id.return_value = None

    result = startUp[0].get_expense_by_id(1,1)

    assert result is None

