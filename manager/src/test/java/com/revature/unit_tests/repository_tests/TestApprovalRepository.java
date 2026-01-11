package com.revature.unit_tests.repository_tests;

import com.revature.repository.Approval;
import com.revature.repository.ApprovalRepository;
import com.revature.repository.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
     * FIND BY EXPENSE ID TESTS                                                                         *
     ****************************************************************************************************/
    //MU-132
    @Test
    @DisplayName("Test findByExpenseId Throws Exception")
    public void testFindByExpenseId_databaseException() throws Exception {
        int expenseId = 1;
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB failure"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> approvalRepo.findByExpenseId(expenseId)
        );

        assertTrue(exception.getMessage().contains("Error finding approval for expense: " + expenseId));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-133, MU-134
    @ParameterizedTest
    @CsvSource({
            "false, true",  // Empty result
            "true, false"   // Has result
    })
    @DisplayName("Test findByExpenseId Returns Optional")
    public void testFindByExpenseId_ReturnsOptional(boolean hasResult, boolean expectEmpty) throws Exception {
        int expenseId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(hasResult, false);

        Optional<Approval> result = approvalRepo.findByExpenseId(expenseId);

        assertNotNull(result);
        assertEquals(expectEmpty, result.isEmpty());
    }

    /****************************************************************************************************
     * UPDATE APPROVAL STATUS TESTS                                                                     *
     ****************************************************************************************************/
    //MU-135
    @Test
    @DisplayName("Test updateApprovalStatus Throws Exception")
    public void testUpdateApprovalStatus_Exception() throws SQLException {
        int expenseId = 1;
        String status = "denied";
        int reviewerId = 1;
        String comment = "test comment";
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("DB failure"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> approvalRepo.updateApprovalStatus(expenseId, status, reviewerId, comment)
        );

        assertTrue(exception.getMessage().contains("Error updating approval for expense: " + expenseId));
        assertNotNull(exception.getCause());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    //MU-136, MU-137
    @ParameterizedTest
    @CsvSource({
            "0, false",  // No rows updated
            "1, true"    // Row updated
    })
    @DisplayName("Test updateApprovalStatus Returns Boolean")
    public void testUpdateApprovalStatus_ReturnsBoolean(int rowsUpdated, boolean expectedResult) throws SQLException {
        int expenseId = 1;
        String status = "denied";
        int reviewerId = 1;
        String comment = "test comment";
        when(preparedStatement.executeUpdate()).thenReturn(rowsUpdated);

        boolean result = approvalRepo.updateApprovalStatus(expenseId, status, reviewerId, comment);

        assertEquals(expectedResult, result);
    }

    //MU-138
    @Test
    @DisplayName("Test updateApprovalStatus Correct Fields")
    public void testUpdateApprovalStatus_CorrectFields() throws SQLException {
        int expenseId = 1;
        String status = "approved";
        int reviewerId = 1;
        String comment = "test comment";
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = approvalRepo.updateApprovalStatus(expenseId, status, reviewerId, comment);

        assertTrue(result);

        verify(preparedStatement).setString(1, status);
        verify(preparedStatement).setInt(2, reviewerId);
        verify(preparedStatement).setString(3, comment);
        verify(preparedStatement).setString(eq(4), anyString());
        verify(preparedStatement).setInt(5, expenseId);
        verify(preparedStatement).executeUpdate();
    }

    /****************************************************************************************************
     * CREATE APPROVAL TESTS                                                                            *
     ****************************************************************************************************/
    //MU-139, MU-140
    @ParameterizedTest
    @CsvSource({
            "0, false, 'Creating approval failed, no rows affected.'",
            "1, false, 'Creating approval failed, no ID obtained.'"
    })
    @DisplayName("Test createApproval Throws Exception")
    public void testCreateApproval_Exception(int rowsUpdated, boolean hasGeneratedKey, String expectedMessage) throws SQLException {
        int expenseId = 1;
        String status = "pending";

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(rowsUpdated);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(hasGeneratedKey);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> approvalRepo.createApproval(expenseId, status)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(preparedStatement).setInt(1, expenseId);
        verify(preparedStatement).setString(2, status);
    }

    //MU-141
    @Test
    @DisplayName("Test createApproval Positive")
    public void testCreateApproval_Positive() throws SQLException {
        int expenseId = 1;
        String status = "pending";
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        Approval result = approvalRepo.createApproval(expenseId, status);

        assertNotNull(result);
        verify(preparedStatement).setInt(1, expenseId);
        verify(preparedStatement).setString(2, status);
    }
}