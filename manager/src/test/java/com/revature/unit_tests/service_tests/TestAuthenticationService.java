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
  @ValueSource(
    strings = {"", "Bearer abc", "Invalid 1", "Bearer"}
  )
  public void testValidateAuthenticationBadHeaderReturnsEmptyOptional(String header) {
    Optional<User> result = authenticationService.validateAuthentication(header);
    assertTrue(result.isEmpty());
    verifyNoInteractions(userRepository);
  }

  @Test
  public void isManagerManagerTrue() {
    User manager = new User(1, "manager", "password", "Manager");
    assertTrue(authenticationService.isManager(manager));
  }

  @Test
  public void isManagerEmployeeFalse() {
    User employee = new User(1, "employee", "password", "Employee");
    assertFalse(authenticationService.isManager(employee));
  }

  @Test
  public void testValidateMangerAuthenticationLegacyValidHeaderReturnsUser() {
    User user = new User(1, "username", "password", "Manager");

    when(userRepository.findById(1)).thenReturn(Optional.of(user));

    Optional<User> result = authenticationService.validateManagerAuthenticationLegacy("Bearer 1");

    assertTrue(result.isPresent());
    assertEquals(1, result.get().getId());

    verify(userRepository).findById(1);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(
    strings = {"", "Bearer abc", "Invalid 1", "Bearer"}
  )
  public void testValidateMangerAuthenticationLegacyInvalidHeaderReturnsEmpty(String header) {
    Optional<User> result = authenticationService.validateManagerAuthenticationLegacy(header);
    assertTrue(result.isEmpty());
    verifyNoInteractions(userRepository);
  }

  @Test
  public void testAuthenticateUserValidUserReturnsUser() {
    User manager = new User(1, "manager", "password", "Manager");

    when(userRepository.findByUsername("manager")).thenReturn(Optional.of(manager));

    Optional<User> result = authenticationService.authenticateUser("manager", "password");

    assertTrue(result.isPresent());
    assertSame(manager, result.get());
    verify(userRepository).findByUsername("manager");
  }

  @ParameterizedTest
  @ValueSource(
    strings = {"", "PASSWORD", " password "}
  )
  public void testAuthenticateUserInvalidPasswordReturnsEmpty(String password) {
    User user = new User(1, "manager", "password", "Manager");

    when(userRepository.findByUsername("manager")).thenReturn(Optional.of(user));

    Optional<User> result = authenticationService.authenticateUser("manager", password);

    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(
    strings = {"", "manager"}
  )
  public void testAuthenticateUserUsernameNotFoundReturnsEmpty(String username) {
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    Optional<User> result = authenticationService.authenticateUser(username, "password");

    assertTrue(result.isEmpty());
  }

  @Test
  public void testAuthenticateManagerValidUserReturnsUser() {
    User manager = new User(1, "manager", "password", "Manager");

    when(userRepository.findByUsername("manager")).thenReturn(Optional.of(manager));

    Optional<User> result = authenticationService.authenticateManager("manager", "password");

    assertTrue(result.isPresent());
    assertSame(manager, result.get());
    verify(userRepository).findByUsername("manager");
  }

  @ParameterizedTest
  @ValueSource(
    strings = {"", "PASSWORD", " password "}
  )
  public void testAuthenticateManagerInvalidPasswordReturnsEmpty(String password) {
    User user = new User(1, "manager", "password", "Manager");

    when(userRepository.findByUsername("manager")).thenReturn(Optional.of(user));

    Optional<User> result = authenticationService.authenticateManager("manager", password);

    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(
    strings = {"", "manager"}
  )
  public void testAuthenticateManagerUsernameNotFoundReturnsEmpty(String username) {
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    Optional<User> result = authenticationService.authenticateManager(username, "password");

    assertTrue(result.isEmpty());
  }
}