package com.jo.api.ws;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentification", description = "API pour la gestion de l'authentification et des utilisateurs")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthService authService;

    @Autowired
    RoleService roleService;

    @Value("${admin.mail}")
    private String adminMail;

    @Value("${admin.password}")
    private String adminPassword;

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @Operation(summary = "Connexion utilisateur",
            description = "Authentifie un utilisateur et génère un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides",
                    content = @Content)
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "Informations de connexion", required = true)
            @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getAlias(),
                userDetails.getUserKey(),
                roles));
    }

    @Operation(summary = "Déconnexion utilisateur",
            description = "Déconnecte l'utilisateur actuellement authentifié",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Déconnexion réussie",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponse.class)))
    @PostMapping("/signout")
    public ResponseEntity<?> logOut(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        this.logoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok(new MessageResponse("User disconnected!"));
    }

    /**
     * Endpoints à executer une fois et à supprimer en prod
     * Ces endpoints servent pour la génération de comtpe Admin et le chargement des données
     * */
    //@PostMapping("/loadRoleCustomer")
    //public void loadRoleCustomer() {
         //this.roleService.createRole();
    //}

    //@PostMapping("/loadRoleAdmin")
    //public void loadRoleAdmin() {
        //this.roleService.createRoleAdmin();
    //}

    //@PostMapping("/signupAdmin")
    //public ResponseEntity<?> registerAdmin() {

        //Create Object Admin

        //User admin = new User();

        //Create new userKey

        //UUID randomUUID = UUID.randomUUID();
        //String userKey = randomUUID.toString().replaceAll("_", "");

        // Data Admin
        //admin.setUsername(adminMail);
        //admin.setPassword(encoder.encode(adminPassword));
        //admin.setAlias("Admin");
        //admin.setUserKey(userKey);

        //Set<Role> roles = new HashSet<>();
        //Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                //.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        //roles.add(adminRole);

        //admin.setRoles(roles);
        //userRepository.save(admin);

        //réponse
        //return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
    //}

    @Operation(summary = "Inscription utilisateur",
            description = "Permet à un nouvel utilisateur de créer un compte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nom d'utilisateur ou alias déjà pris",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "Informations d'inscription", required = true)
            @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByAlias(signupRequest.getAlias())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: email is already taken!"));
        }

        //Create new userKey

        UUID randomUUID = UUID.randomUUID();
        String userKey = randomUUID.toString().replaceAll("_", "");

        //Create new user's account
        User user = new User(signupRequest.getUsername(),
                encoder.encode(signupRequest.getPassword()), signupRequest.getAlias(), userKey);

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
