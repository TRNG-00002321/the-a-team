package com.revature.repository_tests;

import com.revature.repository.DatabaseConnection;
import com.revature.repository.Expense;
import com.revature.repository.ExpenseRepository;
import com.revature.repository.ExpenseWithUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    //MU-094
    @Test
    @DisplayName("Test findById Throws Exception")
    public void testFindById_databaseException() throws Exception {
        int expenseId = 1;

        // Arrange
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> expenseRepo.findById(expenseId)
        );

        assertTrue(exception.getMessage().contains("Error finding expense by ID: " + expenseId));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-095
    @Test
    @DisplayName("Test findById returns empty result")
    public void testFindById_emptyResult() throws SQLException {
        //Arrange
        int expenseId = 999;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows found

        // Act
        Optional<Expense> result = expenseRepo.findById(expenseId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //MU-096
    @Test
    @DisplayName("Test findById Positive")
    public void testFindById_Positive() throws SQLException {
        //Arrange
        int expenseId = 1;

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        // Act
        Optional<Expense> result = expenseRepo.findById(expenseId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    /****************************************************************************************************
     * FIND PENDING EXPENSES WITH USERS TESTS                                                           *
     ****************************************************************************************************/

    //MU-097
    @Test
    @DisplayName("Test findPendingExpensesWithUsers Throws RuntimeException")
    public void testFindPendingExpensesWithUsers_throwsException() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> expenseRepo.findPendingExpensesWithUsers()
        );

        assertTrue(exception.getMessage().contains("Error finding pending expenses"));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-098
    @Test
    @DisplayName("Test findPendingExpensesWithUsers returns Empty List")
    public void testFindPendingExpensesWithUsers_emptyList() throws SQLException {
        //Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows found

        // Act
        List<ExpenseWithUser> result = expenseRepo.findPendingExpensesWithUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //MU-099
    @Test
    @DisplayName("Test findPendingExpensesWithUsers Positive")
    public void testFindPendingExpensesWithUsers_Positive() throws SQLException {
        //Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        // Act
        List<ExpenseWithUser> result = expenseRepo.findPendingExpensesWithUsers();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /****************************************************************************************************
     * FIND EXPENSES BY USER TESTS                                                                      *
     ****************************************************************************************************/
    //MU-100
    @Test
    @DisplayName("Test findExpensesByUser Throws Exception")
    public void testFindExpensesByUser_Exception() throws SQLException {
        // Arrange
        int userId = 1;
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> expenseRepo.findExpensesByUser(userId)
        );

        assertTrue(exception.getMessage().contains("Error finding expenses for user: " + userId));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }
    //MU-101
    @Test
    @DisplayName("Test findExpensesByUser returns Empty List")
    public void testFindExpensesByUser_emptyList() throws SQLException {
        //Arrange
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows found

        // Act
        List<ExpenseWithUser> result = expenseRepo.findExpensesByUser(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //MU-102
    @Test
    @DisplayName("Test findExpensesByUser Positive")
    public void testFindExpensesByUser_Positive() throws SQLException {
        //Arrange
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        // Act
        List<ExpenseWithUser> result = expenseRepo.findExpensesByUser(userId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /****************************************************************************************************
     * FIND EXPENSES BY DATE RANGE TESTS                                                                *
     ****************************************************************************************************/
    //MU-103
    @Test
    @DisplayName("Test findExpensesByDateRange Throws Exception")
    public void testFindExpensesByDateRange_Exception() throws SQLException {
        // Arrange
        String startDate = "01/01/2025";
        String endDate = "01/01/2025";
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> expenseRepo.findExpensesByDateRange(startDate, endDate)
        );

        assertTrue(exception.getMessage().contains("Error finding expenses by date range: " + startDate + " to " + endDate));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-104
    @Test
    @DisplayName("Test findExpensesByDateRange returns EmptyList")
    public void testFindExpensesByDateRange_emptyList() throws SQLException {
        // Arrange
        String startDate = "01/01/2025";
        String endDate = "01/01/2025";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows found

        // Act
        List<ExpenseWithUser> result = expenseRepo.findExpensesByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //MU-105
    @Test
    @DisplayName("Test findExpensesByDateRange Positive")
    public void testFindExpensesByDateRange_positive() throws SQLException {
        //Arrange
        String startDate = "01/01/2025";
        String endDate = "01/09/2025";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        // Act
        List<ExpenseWithUser> result = expenseRepo.findExpensesByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /****************************************************************************************************
     * FIND EXPENSES BY CATEGORY TESTS                                                                  *
     ****************************************************************************************************/
    //MU-106
    @Test
    @DisplayName("Test findExpensesByCategory Throws Exception")
    public void testFindExpensesByCategory_Exception() throws SQLException {
        // Arrange
        String category = "test";
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> expenseRepo.findExpensesByCategory(category)
        );

        assertTrue(exception.getMessage().contains("Error finding expenses by category: " + category));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-107
    @Test
    @DisplayName("Test findExpensesByCategory returns EmptyList")
    public void testFindExpensesByCategory_emptyList() throws SQLException {
        // Arrange
        String category = "test";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows found

        // Act
        List<ExpenseWithUser> result = expenseRepo.findExpensesByCategory(category);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //MU-108
    @Test
    @DisplayName("Test findExpensesByCategory Positive")
    public void testFindExpensesByCategory_Positive() throws SQLException {
        // Arrange
        String category = "test";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false); // No rows found

        // Act
        List<ExpenseWithUser> result = expenseRepo.findExpensesByCategory(category);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /****************************************************************************************************
     * FIND ALL EXPENSES WITH USERS TESTS                                                               *
     ****************************************************************************************************/
    //MU-109
    @Test
    @DisplayName("Test findAllExpensesWithUsers Throws Exception")
    public void testFindAllExpensesWithUsers_Exception() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> expenseRepo.findAllExpensesWithUsers()
        );

        assertTrue(exception.getMessage().contains("Error finding all expenses"));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-110
    @Test
    @DisplayName("Test findAllExpensesWithUsers returns EmptyList")
    public void testFindAllExpensesWithUsers_emptyList() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows found

        // Act
        List<ExpenseWithUser> result = expenseRepo.findAllExpensesWithUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //MU-111
    @Test
    @DisplayName("Test findAllExpensesWithUsers Positive")
    public void testFindAllExpensesWithUsers_Positive() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false); // No rows found

        // Act
        List<ExpenseWithUser> result = expenseRepo.findAllExpensesWithUsers();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
