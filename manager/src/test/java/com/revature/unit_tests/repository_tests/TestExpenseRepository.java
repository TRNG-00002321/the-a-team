package com.revature.unit_tests.repository_tests;

import com.revature.repository.DatabaseConnection;
import com.revature.repository.Expense;
import com.revature.repository.ExpenseRepository;
import com.revature.repository.ExpenseWithUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestExpenseRepository {
    @Mock
    private DatabaseConnection db;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ExpenseRepository expenseRepo;

    @BeforeEach
    public void setUp() throws Exception {
        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    /****************************************************************************************************
     * FIND BY ID TESTS                                                                                 *
     ****************************************************************************************************/

    //MU-094, MU-095, MU-096
    @ParameterizedTest
    @CsvSource({
            "true, false, false, ''",                    // MU-096: Has result
            "false, true, false, ''",                    // MU-095: Empty result
            "false, false, true, 'DB failure'"          // MU-094: Exception thrown
    })
    @DisplayName("Test findById All Scenarios")
    public void testFindById_AllScenarios(
            boolean hasResult,
            boolean expectEmpty,
            boolean throwException,
            String errorMessage
    ) throws SQLException {
        int expenseId = 1;

        if (throwException) {
            when(preparedStatement.executeQuery()).thenThrow(new SQLException(errorMessage));
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> expenseRepo.findById(expenseId)
            );
            assertTrue(exception.getMessage().contains("Error finding expense by ID: " + expenseId));
            assertInstanceOf(SQLException.class, exception.getCause());
        } else {
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(hasResult);

            Optional<Expense> result = expenseRepo.findById(expenseId);
            assertNotNull(result);
            assertEquals(expectEmpty, result.isEmpty());
        }
    }

    /****************************************************************************************************
     * LIST-RETURNING METHOD TESTS                                                                      *
     ****************************************************************************************************/

    //MU-097, MU-100, MU-103, MU-106, MU-109
    @ParameterizedTest
    @CsvSource({
            "findPendingExpensesWithUsers, '', '', 'Error finding pending expenses'",
            "findExpensesByUser, '1', '', 'Error finding expenses for user: 1'",
            "findExpensesByDateRange, '01/01/2025', '01/09/2025', 'Error finding expenses by date range: 01/01/2025 to 01/09/2025'",
            "findExpensesByCategory, 'test', '', 'Error finding expenses by category: test'",
            "findAllExpensesWithUsers, '', '', 'Error finding all expenses'"
    })
    @DisplayName("Test Repository Methods Throw Exception")
    public void testRepositoryMethods_Exception(String methodName, String param1, String param2, String expectedMessage) throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            switch (methodName) {
                case "findPendingExpensesWithUsers":
                    expenseRepo.findPendingExpensesWithUsers();
                    break;
                case "findExpensesByUser":
                    expenseRepo.findExpensesByUser(Integer.parseInt(param1));
                    break;
                case "findExpensesByDateRange":
                    expenseRepo.findExpensesByDateRange(param1, param2);
                    break;
                case "findExpensesByCategory":
                    expenseRepo.findExpensesByCategory(param1);
                    break;
                case "findAllExpensesWithUsers":
                    expenseRepo.findAllExpensesWithUsers();
                    break;
            }
        });

        assertTrue(exception.getMessage().contains(expectedMessage));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-098, MU-099, MU-101, MU-102, MU-104, MU-105, MU-107, MU-108, MU-110, MU-111
    @ParameterizedTest
    @CsvSource({
            "findPendingExpensesWithUsers, '', '', false, true",
            "findPendingExpensesWithUsers, '', '', true, false",
            "findExpensesByUser, '1', '', false, true",
            "findExpensesByUser, '1', '', true, false",
            "findExpensesByDateRange, '01/01/2025', '01/09/2025', false, true",
            "findExpensesByDateRange, '01/01/2025', '01/09/2025', true, false",
            "findExpensesByCategory, 'test', '', false, true",
            "findExpensesByCategory, 'test', '', true, false",
            "findAllExpensesWithUsers, '', '', false, true",
            "findAllExpensesWithUsers, '', '', true, false"
    })
    @DisplayName("Test Repository Methods Return List")
    public void testRepositoryMethods_ReturnsList(String methodName, String param1, String param2, boolean hasResult, boolean expectEmpty) throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(hasResult, false);

        List<ExpenseWithUser> result = switch (methodName) {
            case "findPendingExpensesWithUsers" -> expenseRepo.findPendingExpensesWithUsers();
            case "findExpensesByUser" -> expenseRepo.findExpensesByUser(Integer.parseInt(param1));
            case "findExpensesByDateRange" -> expenseRepo.findExpensesByDateRange(param1, param2);
            case "findExpensesByCategory" -> expenseRepo.findExpensesByCategory(param1);
            case "findAllExpensesWithUsers" -> expenseRepo.findAllExpensesWithUsers();
            default -> throw new IllegalArgumentException("Unknown method");
        };

        assertNotNull(result);
        assertEquals(expectEmpty, result.isEmpty());
    }
}