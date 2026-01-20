package com.revature.unit_tests.repository_tests;

import com.revature.repository.DatabaseConnection;
import com.revature.repository.User;
import com.revature.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    // MU-184, MU-185, MU-186, MU-187, MU-188, MU-189
    @ParameterizedTest
    @CsvSource({
            "id, 1, '', found, manager1, Manager, ''",                           // MU-184: findById - User Found
            "id, 999, '', notFound, '', '', ''",                                 // MU-185: findById - Not Found
            "id, 999, '', exception, '', '', 'Error finding user by ID'",       // MU-186: findById - Exception
            "username, 0, manager1, found, manager1, Manager, ''",               // MU-187: findByUsername - User Found
            "username, 0, 'unknown user', notFound, '', '', ''",                 // MU-189: findByUsername - Not Found
            "username, 0, manager1, exception, '', '', 'Error finding user by username'" // MU-188: findByUsername - Exception
    })
    @DisplayName("Test Find Methods - All Scenarios")
    void testFindMethods_AllScenarios(
            String findBy,
            int id,
            String username,
            String scenario,
            String expectedUsername,
            String expectedRole,
            String expectedMessage
    ) throws SQLException {
        when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);

        if (scenario.equals("exception")) {
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                if (findBy.equals("id")) {
                    userRepository.findById(id);
                } else {
                    userRepository.findByUsername(username);
                }
            });

            assertTrue(exception.getMessage().contains(expectedMessage));
        } else {
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            if (scenario.equals("found")) {
                when(mockResultSet.next()).thenReturn(true);
                when(mockResultSet.getInt("id")).thenReturn(1);
                when(mockResultSet.getString("username")).thenReturn("manager1");
                when(mockResultSet.getString("password")).thenReturn("password123");
                when(mockResultSet.getString("role")).thenReturn("Manager");

                Optional<User> result = findBy.equals("id")
                        ? userRepository.findById(id)
                        : userRepository.findByUsername(username);

                assertTrue(result.isPresent());
                assertEquals(expectedUsername, result.get().getUsername());
                assertEquals(expectedRole, result.get().getRole());

                if (findBy.equals("id")) {
                    verify(mockStatement).setInt(1, id);
                } else {
                    verify(mockStatement).setString(1, username);
                }
            } else { // notFound
                when(mockResultSet.next()).thenReturn(false);

                Optional<User> result = findBy.equals("id")
                        ? userRepository.findById(id)
                        : userRepository.findByUsername(username);

                assertTrue(result.isEmpty());
            }
        }
    }
}