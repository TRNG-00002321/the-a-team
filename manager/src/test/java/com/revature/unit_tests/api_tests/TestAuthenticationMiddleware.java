package com.revature.unit_tests.api_tests;

import com.revature.api.AuthenticationMiddleware;
import com.revature.repository.User;
import com.revature.service.AuthenticationService;
import io.javalin.http.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TestAuthenticationMiddleware {
    @Mock
    private AuthenticationService authService;
    @Mock
    private Context ctx;

    @InjectMocks
    private AuthenticationMiddleware authMiddleware;

    @DisplayName("Testing validate manager, positive case")
    @Test
    public void testValidateManager_Positive() throws Exception {
        //Arrange
        String jwtToken = "validJwtToken";
        User validManager = new User(1, "bob123", "password123", "Manager");
        Optional<User> managerOpt = Optional.of(validManager);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(ctx.cookie("jwt")).thenReturn(jwtToken);
        when(authService.validateManagerAuthentication(jwtToken)).thenReturn(managerOpt);

        //Act
        Handler actualHandler = authMiddleware.validateManager();
        actualHandler.handle(ctx);

        //Assert
        verify(ctx, times(1)).cookie("jwt");
        verify(ctx, times(1)).attribute("manager", validManager);
        verify(ctx).attribute(eq("manager"), userCaptor.capture());
        User actualUser = userCaptor.getValue();
        assertEquals(validManager, actualUser);
    }

    @DisplayName("Testing validate manager, no manager found")
    @Test
    public void testValidateManager_ManagerNotFound() throws Exception {
        //Arrange
        String jwtToken = "invalidToken";

        when(ctx.cookie("jwt")).thenReturn(jwtToken);
        when(authService.validateManagerAuthentication(jwtToken)).thenReturn(Optional.empty());
        when(authService.validateJwtToken(jwtToken)).thenReturn(Optional.empty());

        //Act/Assert
        Handler actualHandler = authMiddleware.validateManager();
        UnauthorizedResponse ex = assertThrows(UnauthorizedResponse.class, ()->actualHandler.handle(ctx));
        assertEquals("Authentication required", ex.getMessage());
        verify(authService, times(1)).validateManagerAuthentication(jwtToken);
        verify(authService, times(1)).validateJwtToken(jwtToken);
    }

    @DisplayName("Testing validate manager, employee found")
    @Test
    public void testValidateManager_EmployeeFound() throws Exception{
        //Arrange
        String jwtToken = "employeeToken";
        User employee = new User(1,"bob123","password123","Employee");

        when(ctx.cookie("jwt")).thenReturn(jwtToken);
        when(authService.validateManagerAuthentication(jwtToken)).thenReturn(Optional.empty());
        when(authService.validateJwtToken(jwtToken)).thenReturn(Optional.of(employee));

        //Act/Assert
        Handler actualHandler = authMiddleware.validateManager();
        ForbiddenResponse ex = assertThrows(ForbiddenResponse.class, ()->actualHandler.handle(ctx));
        assertEquals("Access denied - managers only", ex.getMessage());
        verify(authService, times(1)).validateManagerAuthentication(jwtToken);
        verify(authService, times(1)).validateJwtToken(jwtToken);
    }

    @DisplayName("Test get authenticated manager, positive test")
    @Test
    public void testGetAuthenticatedManager_Positive(){
        User validManager = new User(1, "bob123", "password123", "Manager");
        when(ctx.attribute("manager")).thenReturn(validManager);
        User actualUser = authMiddleware.getAuthenticatedManager(ctx);
        assertEquals(validManager, actualUser);
        verify(ctx, times(1)).attribute("manager");
    }

    @DisplayName("Test get authenticated manager, negative test")
    @Test
    public void testGetAuthenticatedManager_Negative(){
        when(ctx.attribute("manager")).thenReturn(null);
        User actualUser = authMiddleware.getAuthenticatedManager(ctx);
        assertNull(actualUser);
        verify(ctx, times(1)).attribute("manager");
    }
}
