package com.revature.repository_tests;

import com.revature.repository.DatabaseConnection;
import com.revature.repository.User;
import com.revature.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.*;

// Testing repository layer with mocked database connection
// Mocking JDBC PreparedStatement and ResultSet
// Testing SQL Query execution in isolation

@ExtendWith(MockitoExtension.class)
public class TestUserRepository {

    @Mock
    private DatabaseConnection mockDatabaseConnection;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException {
        userRepository = new UserRepository(mockDatabaseConnection);
    }

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {
        // MU 184
        @Test
        @DisplayName("Id Found")
        void testFindById_Found() throws SQLException {
            // Arrange
            when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("username")).thenReturn("manager1");
            when(mockResultSet.getString("password")).thenReturn("password1");
            when(mockResultSet.getString("role")).thenReturn("Manager");

            // Act
            Optional<User> result = userRepository.findById(1);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getId());
            assertEquals("manager1", result.get().getUsername());
            assertEquals("Manager", result.get().getRole());
            verify(mockStatement).setInt(1, 1);
        }

        // MU 185
        @Test
        @DisplayName("Id Not Found")
        void testFindById_NotFound() throws SQLException{
            when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            Optional<User> result = userRepository.findById(999);

            assertTrue(result.isEmpty());

        }

        // MU 186
        @Test
        @DisplayName("Test findById Throws SQL Exception")
        void testFindById_ThrowsException() throws SQLException {
            //Arrange
            when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            //Act
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userRepository.findById(999);
            });
            //Assert
            assertTrue(exception.getMessage().contains("Error finding user by ID"));
        }

    }
    @Nested
    @DisplayName("Find by Username Tests")
    class FindByUserNameTests{
        @Test
        // MU 187
        void TestFindByUsername_Positive() throws SQLException {
            //Arrange
            when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("username")).thenReturn("manager1");
            when(mockResultSet.getString("password")).thenReturn("password123");
            when(mockResultSet.getString("role")).thenReturn("Manager");
            //Act
            Optional<User> result = userRepository.findByUsername("manager1");
            //Assert
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getId());
            assertEquals("manager1", result.get().getUsername());
            assertEquals("Manager", result.get().getRole());
            verify(mockStatement).setString(1, "manager1");
        }

        @Test
        // MU 188
        void TestFindByUsername_ThrowsException() throws SQLException {
            //Arrange
            when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            //Act
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userRepository.findByUsername("manager1");
            });
            //Assert
            assertTrue(exception.getMessage().contains("Error finding user by username"));

        }

        @Test
        // MU 189
        @DisplayName("Username Not Found")
        void testFindByUsername_NotFound() throws SQLException{
            when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            Optional<User> result = userRepository.findByUsername("unknown user");

            assertTrue(result.isEmpty());

        }
    }

}




