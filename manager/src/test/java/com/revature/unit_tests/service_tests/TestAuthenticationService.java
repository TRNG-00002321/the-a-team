package com.revature.unit_tests.service_tests;

import com.revature.repository.User;
import com.revature.repository.UserRepository;
import com.revature.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestAuthenticationService {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testValidateAuthenticationValidHeaderReturnsUser() {
        User user = new User(1, "username", "password", "Employee");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.validateAuthentication("Bearer 1");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(userRepository).findById(1);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "Bearer abc", "Invalid 1", "Bearer"})
    public void testValidateAuthenticationBadHeaderReturnsEmptyOptional(String header) {
        Optional<User> result = authenticationService.validateAuthentication(header);

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Manager", "Employee"})
    public void testIsManagerChecksRole(String role) {
        User user = new User(1, "user", "password", role);

        boolean result = authenticationService.isManager(user);

        assertEquals(role.equals("Manager"), result);
    }

    @Test
    public void testValidateManagerAuthenticationLegacyValidHeaderReturnsUser() {
        User user = new User(1, "username", "password", "Manager");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.validateManagerAuthenticationLegacy("Bearer 1");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(userRepository).findById(1);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "Bearer abc", "Invalid 1", "Bearer"})
    public void testValidateManagerAuthenticationLegacyInvalidHeaderReturnsEmpty(String header) {
        Optional<User> result = authenticationService.validateManagerAuthenticationLegacy(header);

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    public void testAuthenticateUserValidCredentialsReturnsUser() {
        User user = new User(1, "manager", "password", "Manager");
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.authenticateUser("manager", "password");

        assertTrue(result.isPresent());
        assertSame(user, result.get());
        verify(userRepository).findByUsername("manager");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "PASSWORD", " password "})
    public void testAuthenticateUserInvalidPasswordReturnsEmpty(String password) {
        User user = new User(1, "manager", "password", "Manager");
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.authenticateUser("manager", password);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "manager"})
    public void testAuthenticateUserUsernameNotFoundReturnsEmpty(String username) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = authenticationService.authenticateUser(username, "password");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testAuthenticateManagerValidCredentialsReturnsUser() {
        User manager = new User(1, "manager", "password", "Manager");
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(manager));

        Optional<User> result = authenticationService.authenticateManager("manager", "password");

        assertTrue(result.isPresent());
        assertSame(manager, result.get());
        verify(userRepository).findByUsername("manager");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "PASSWORD", " password "})
    public void testAuthenticateManagerInvalidPasswordReturnsEmpty(String password) {
        User user = new User(1, "manager", "password", "Manager");
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.authenticateManager("manager", password);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "manager"})
    public void testAuthenticateManagerUsernameNotFoundReturnsEmpty(String username) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = authenticationService.authenticateManager(username, "password");

        assertTrue(result.isEmpty());
    }
}