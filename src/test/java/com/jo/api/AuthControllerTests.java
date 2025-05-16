package com.jo.api;

import com.jo.api.security.jwt.JwtUtils;
import com.jo.api.security.models.ERole;
import com.jo.api.security.models.Role;
import com.jo.api.security.models.User;
import com.jo.api.security.repository.RoleRepository;
import com.jo.api.security.repository.UserRepository;
import com.jo.api.security.request.LoginRequest;
import com.jo.api.security.request.SignupRequest;
import com.jo.api.security.response.JwtResponse;
import com.jo.api.security.response.MessageResponse;
import com.jo.api.security.service.AuthService;
import com.jo.api.security.service.RoleService;
import com.jo.api.security.service.UserDetailsImpl;
import com.jo.api.ws.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthService authService;

    @Mock
    private RoleService roleService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private SecurityContextLogoutHandler logoutHandler;

    @InjectMocks
    private AuthController authController;

    @Test
    void testAuthenticateUser_Success() {
        // Préparation des données de test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test@example.com");
        loginRequest.setPassword("password");

        // Création du UserDetails mock
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "test@example.com",
                "TestUser",
                "userKey123",
                "password",
                authorities
        );

        // Configuration des mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt-token");

        // Exécution du test
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JwtResponse.class);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getToken()).isEqualTo("fake-jwt-token");
        assertThat(jwtResponse.getId()).isEqualTo(1L);
        assertThat(jwtResponse.getUsername()).isEqualTo("test@example.com");
        assertThat(jwtResponse.getAlias()).isEqualTo("TestUser");
        assertThat(jwtResponse.getUserKey()).isEqualTo("userKey123");
        assertThat(jwtResponse.getRoles()).contains("ROLE_CUSTOMER");
    }

    @Test
    void testLogOut_Success() {
        // Configuration des mocks
        ReflectionTestUtils.setField(authController, "logoutHandler", logoutHandler);

        // Exécution du test
        ResponseEntity<?> responseEntity = authController.logOut(authentication, request, httpResponse);

        // Vérification des résultats
        verify(logoutHandler, times(1)).logout(request, httpResponse, authentication);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) responseEntity.getBody()).getMessage()).isEqualTo("User disconnected!");
    }

    @Test
    void testLoadRoleCustomer() {
        // Exécution du test
        authController.loadRoleCustomer();

        // Vérification que la méthode du service a été appelée
        verify(roleService, times(1)).createRole();
    }

    @Test
    void testLoadRoleAdmin() {
        // Exécution du test
        authController.loadRoleAdmin();

        // Vérification que la méthode du service a été appelée
        verify(roleService, times(1)).createRoleAdmin();
    }

    @Test
    void testRegisterUser_Success() {
        // Préparation des données de test
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("user@example.com");
        signupRequest.setPassword("password");
        signupRequest.setAlias("User");

        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName(ERole.ROLE_CUSTOMER);

        when(userRepository.existsByUsername("user@example.com")).thenReturn(false);
        when(userRepository.existsByAlias("User")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.of(userRole));
        when(encoder.encode("password")).thenReturn("encodedPassword");

        // Exécution du test
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Vérification des résultats
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) response.getBody()).getMessage()).isEqualTo("User registered successfully!");
    }

    @Test
    void testRegisterUser_UsernameAlreadyTaken() {
        // Préparation des données de test
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existing@example.com");
        signupRequest.setPassword("password");
        signupRequest.setAlias("User");

        when(userRepository.existsByUsername("existing@example.com")).thenReturn(true);

        // Exécution du test
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Vérification des résultats
        verify(userRepository, never()).save(any(User.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) response.getBody()).getMessage()).isEqualTo("Error: Username is already taken!");
    }

    @Test
    void testRegisterUser_AliasAlreadyTaken() {
        // Préparation des données de test
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("user@example.com");
        signupRequest.setPassword("password");
        signupRequest.setAlias("ExistingAlias");

        when(userRepository.existsByUsername("user@example.com")).thenReturn(false);
        when(userRepository.existsByAlias("ExistingAlias")).thenReturn(true);

        // Exécution du test
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Vérification des résultats
        verify(userRepository, never()).save(any(User.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) response.getBody()).getMessage()).isEqualTo("Error: email is already taken!");
    }

    @Test
    void testRegisterUser_WithAdminRole() {
        // Préparation des données de test
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("admin_user@example.com");
        signupRequest.setPassword("password");
        signupRequest.setAlias("AdminUser");
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        signupRequest.setRole(roles);

        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName(ERole.ROLE_ADMIN);

        when(userRepository.existsByUsername("admin_user@example.com")).thenReturn(false);
        when(userRepository.existsByAlias("AdminUser")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(encoder.encode("password")).thenReturn("encodedPassword");

        // Exécution du test
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Vérification des résultats
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) response.getBody()).getMessage()).isEqualTo("User registered successfully!");
    }
}

