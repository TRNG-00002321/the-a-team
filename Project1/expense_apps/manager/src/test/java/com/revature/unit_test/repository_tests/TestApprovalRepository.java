package com.revature.repository_tests;

import com.revature.repository.Approval;
import com.revature.repository.ApprovalRepository;
import com.revature.repository.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestApprovalRepository {
    @Mock
    private DatabaseConnection db;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ApprovalRepository approvalRepo;

    @BeforeEach
    public void setUp() throws Exception {
        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    /****************************************************************************************************
     * FIND BY EXPENSE ID TESTS                                                                                 *
     ****************************************************************************************************/
    //MU-132
    @Test
    @DisplayName("Test findByExpenseId Throws Exception")
    public void testFindByExpenseId_databaseException() throws Exception {
        // Arrange
        int expenseId = 1;
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> approvalRepo.findByExpenseId(expenseId)
        );

        assertTrue(exception.getMessage().contains("Error finding approval for expense: " + expenseId));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-133
    @Test
    @DisplayName("Test findByExpenseId Returns Empty Optional")
    public void testFindByExpenseId_Empty() throws Exception {
        // Arrange
        int expenseId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No rows found

        //Act
        Optional<Approval> result = approvalRepo.findByExpenseId(expenseId);

        //Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //MU-134
    @Test
    @DisplayName("Test findByExpenseId Positive")
    public void testFindByExpenseId_Positive() throws Exception {
        // Arrange
        int expenseId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false); // No rows found

        //Act
        Optional<Approval> result = approvalRepo.findByExpenseId(expenseId);

        //Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /****************************************************************************************************
     * UPDATE APPROVAL STATUS TESTS                                                                     *
     ****************************************************************************************************/
    //MU-135
    @Test
    @DisplayName("Test updateApprovalStatus Throws Exception")
    public void testUpdateApprovalStatus_Exception() throws SQLException {
        // Arrange
        int expenseId = 1;
        String status = "denied";
        int reviewerId = 1;
        String comment = "test comment";
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("DB failure"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> approvalRepo.updateApprovalStatus(expenseId, status, reviewerId, comment)
        );

        assertTrue(exception.getMessage().contains("Error updating approval for expense: " + expenseId));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-136
    @Test
    @DisplayName("Test updateApprovalStatus Returns False")
    public void testUpdateApprovalStatus_False() throws SQLException {
        // Arrange
        int expenseId = 1;
        String status = "denied";
        int reviewerId = 1;
        String comment = "test comment";
        when(preparedStatement.executeUpdate()).thenReturn(0);

        //Act
        boolean result = approvalRepo.updateApprovalStatus(expenseId, status, reviewerId, comment);

        //Assert
        assertFalse(result);
    }

    //MU-137
    @Test
    @DisplayName("Test updateApprovalStatus Returns True")
    public void testUpdateApprovalStatus_True() throws SQLException {
        // Arrange
        int expenseId = 1;
        String status = "denied";
        int reviewerId = 1;
        String comment = "test comment";
        when(preparedStatement.executeUpdate()).thenReturn(1);

        //Act
        boolean result = approvalRepo.updateApprovalStatus(expenseId, status, reviewerId, comment);

        //Assert
        assertTrue(result);
    }

    //MU-138
    @Test
    @DisplayName("Test updateApprovalStatus Correct Fields")
    public void testUpdateApprovalStatus_CorrectFields() throws SQLException {

        // Arrange
        int expenseId = 1;
        String status = "approved";
        int reviewerId = 1;
        String comment = "test comment";
        when(preparedStatement.executeUpdate()).thenReturn(1); // 1 row updated

        // Act
        boolean result = approvalRepo.updateApprovalStatus(
                expenseId, status, reviewerId, comment
        );

        // Assert
        assertTrue(result);

        // Verify correct parameter binding
        verify(preparedStatement).setString(1, status);
        verify(preparedStatement).setInt(2, reviewerId);
        verify(preparedStatement).setString(3, comment);

        // review_date is time-based verify it's set, not exact value
        verify(preparedStatement).setString(eq(4), anyString());

        verify(preparedStatement).setInt(5, expenseId);
        verify(preparedStatement).executeUpdate();
    }

    /****************************************************************************************************
     * CREATE APPROVAL TESTS                                                                            *
     ****************************************************************************************************/
    //MU-139
    @Test
    @DisplayName("Test createApproval Throws Exception No Rows Updated")
    public void testCreateApproval_ExceptionNoRowsUpdated() throws SQLException {
        // Arrange
        int expenseId = 1;
        String status = "pending";

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> approvalRepo.createApproval(expenseId, status)
        );

        assertEquals(
                "Creating approval failed, no rows affected.",
                exception.getMessage()
        );

        verify(preparedStatement).setInt(1, expenseId);
        verify(preparedStatement).setString(2, status);
    }

    //MU-140
    @Test
    @DisplayName("Test createApproval Throws Exception No ID")
    public void testCreateApproval_ExceptionNoID() throws SQLException {
        // Arrange
        int expenseId = 1;
        String status = "pending";

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> approvalRepo.createApproval(expenseId, status)
        );

        assertEquals(
                "Creating approval failed, no ID obtained.",
                exception.getMessage()
        );

        verify(preparedStatement).setInt(1, expenseId);
        verify(preparedStatement).setString(2, status);
    }

    //MU-141
    @Test
    @DisplayName("Test createApproval Positive")
    public void testCreateApproval_Positive() throws SQLException {
        // Arrange
        int expenseId = 1;
        String status = "pending";
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        // Act
        Approval result = approvalRepo.createApproval(expenseId, status);

        // Assert
        assertNotNull(result);

        // Verify correct parameter binding
        verify(preparedStatement).setInt(1, expenseId);
        verify(preparedStatement).setString(2, status);
    }
}
