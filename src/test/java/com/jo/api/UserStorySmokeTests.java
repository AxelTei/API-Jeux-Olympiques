package com.jo.api;

import com.jo.api.pojo.Booking;
import com.jo.api.pojo.BookingOffer;
import com.jo.api.pojo.SellsByOffer;
import com.jo.api.pojo.Ticket;
import com.jo.api.request.TicketRequest;
import com.jo.api.security.models.ERole;
import com.jo.api.security.models.Role;
import com.jo.api.security.models.User;
import com.jo.api.security.repository.RoleRepository;
import com.jo.api.security.repository.UserRepository;
import com.jo.api.security.request.LoginRequest;
import com.jo.api.security.response.JwtResponse;
import com.jo.api.ws.ApiRegistration;
import com.jo.api.ws.AuthController;
import com.jo.api.ws.BookingOfferController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserStorySmokeTests {

    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withDatabaseName("bookingOfferTest")
            .withUsername("testUser")
            .withPassword("testPass");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthController authController;

    @Autowired
    private BookingOfferController bookingOfferController;

    @Value("${admin.mail}")
    private String adminMail;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    void setUp() {
        mariaDBContainer.start();
        System.setProperty("spring.datasource.url", mariaDBContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mariaDBContainer.getUsername());
        System.setProperty("spring.datasource.password", mariaDBContainer.getPassword());
        authController.loadRoleAdmin();
        authController.loadRoleCustomer();
    }

    // Les Tests de notre User Entity

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    // Test US1 et US2 sont écrits dans l'application next.js dans le dossier cypress, fichier e2e.
    // Test US3 : Je suis un visiteur et je veux créer un compte utilisateur puis me connecter à mon compte.
    @Test
    void testCreateUserAndLogin() {

        // Load Customer Role
        //authController.loadRoleCustomer();

        // Create User
        User user = new User();
        user.setUsername("henri.dupont@gmail.com");
        user.setAlias("HenriDupont");
        user.setPassword(encoder.encode("@Toto1994"));
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);

        // Create New userKey
        UUID randomUUID = UUID.randomUUID();
        String userKey = randomUUID.toString().replaceAll("_", "");
        user.setUserKey(userKey);

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();

        //Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("henri.dupont@gmail.com");
        loginRequest.setPassword("@Toto1994");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    // Test US4 : Je suis une administrateur connecté et je veux créer une nouvelle offre,
    // la modifier puis la supprimer.
    @Test
    void testCreateAdminLoginAndCreateBookingOfferUpdateItThenDeleteIt() {

        // Load Customer Admin
        //authController.loadRoleAdmin();

        //Create Object Admin
        User admin = new User();

        //Create new userKey
        UUID randomUUID = UUID.randomUUID();
        String userKey = randomUUID.toString().replaceAll("_", "");

        // Data Admin
        admin.setUsername(adminMail);
        admin.setPassword(encoder.encode(adminPassword));
        admin.setAlias("Admin");
        admin.setUserKey(userKey);

        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(adminRole);

        admin.setRoles(roles);

        // Save Admin
        User savedAdmin = userRepository.save(admin);

        assertThat(savedAdmin.getId()).isNotNull();

        //Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(adminMail);
        loginRequest.setPassword(adminPassword);

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        // Extraire le token JWT de la réponse
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse).isNotNull();
        String token = jwtResponse.getToken();
        assertThat(token).isNotNull().isNotEmpty();

        // Configurer les en-têtes avec le token JWT
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Création d'un objet BookingOffer
        BookingOffer bookingOffer = new BookingOffer();
        bookingOffer.setTitle("Offre Test");
        bookingOffer.setPrice(BigDecimal.valueOf(199.99));
        bookingOffer.setNumberOfCustomers(2);

        // Création d'une entité HTTP pour le corps de la requête
        HttpEntity<BookingOffer> createRequest = new HttpEntity<>(bookingOffer, headers);

        // Faire la requête POST pour créer l'offre
        ResponseEntity<BookingOffer> createResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER,
                HttpMethod.POST,
                createRequest,
                BookingOffer.class
        );

        assertThat(createResponse.getStatusCode().value()).isEqualTo(200);
        BookingOffer createdOffer = createResponse.getBody();
        assertThat(createdOffer).isNotNull();
        assertThat(createdOffer.getBookingOfferId()).isNotNull();

        // Mettre à jour l'offre
        createdOffer.setTitle("Updated Offre Test");
        createdOffer.setPrice(BigDecimal.valueOf(249.99));
        createdOffer.setNumberOfCustomers(3);

        HttpEntity<BookingOffer> updateRequest = new HttpEntity<>(createdOffer, headers);

        // Faire la requête PUT pour mettre à jour l'offre
        ResponseEntity<BookingOffer> updateResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER + "/" + createdOffer.getBookingOfferId(),
                HttpMethod.PUT,
                updateRequest,
                BookingOffer.class
        );

        assertThat(updateResponse.getStatusCode().value()).isEqualTo(200);
        BookingOffer updatedOffer = updateResponse.getBody();
        assertThat(updatedOffer).isNotNull();
        assertThat(updatedOffer.getTitle()).isEqualTo("Updated Offre Test");
        assertThat(updatedOffer.getPrice()).isEqualTo(BigDecimal.valueOf(249.99));
        assertThat(updatedOffer.getNumberOfCustomers()).isEqualTo(3);

        // Supprimer l'offre
        ResponseEntity<?> deleteResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER + "/" + updatedOffer.getBookingOfferId(),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                BookingOffer.class
        );

        assertThat(deleteResponse.getStatusCode().value()).isEqualTo(204);

        // Vérifier que l'offre a été supprimée en essayant de la récupérer
        ResponseEntity<BookingOffer> getResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER + "/" + updatedOffer.getBookingOfferId(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BookingOffer.class
        );

        assertThat(getResponse.getStatusCode().value()).isEqualTo(404);

    }

    // Test US5 : Je suis un utilisateur connecté et je veux ajouter une offre de réservation à mon panier.
    @Test
    void testCreateAdminAndUserLoginAndCreateBookingOfferThenCreateBooking() {

        // Load Roles
        //authController.loadRoleAdmin();
        //authController.loadRoleCustomer();

        // Création Admin
        User admin = new User();
        UUID adminUUID = UUID.randomUUID();
        String adminUserKey = adminUUID.toString().replace("_", "");

        admin.setUsername("admin@test.com");
        admin.setPassword(encoder.encode("P@sswordAdmin2"));
        admin.setAlias("Admin2");
        admin.setUserKey(adminUserKey);

        Set<Role> adminRoles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role Admin is not found."));
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);

        User savedAdmin = userRepository.save(admin);
        assertThat(savedAdmin.getId()).isNotNull();

        // Connexion Admin
        LoginRequest adminloginRequest = new LoginRequest();
        adminloginRequest.setUsername("admin@test.com");
        adminloginRequest.setPassword("P@sswordAdmin2");

        ResponseEntity<?> adminResponse = authController.authenticateUser(adminloginRequest);
        assertThat(adminResponse.getStatusCode().value()).isEqualTo(200);

        JwtResponse adminJwtResponse = (JwtResponse) adminResponse.getBody();
        assertThat(adminJwtResponse).isNotNull();
        String adminToken = adminJwtResponse.getToken();
        assertThat(adminToken).isNotNull().isNotEmpty();

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Création d'une offre
        BookingOffer bookingOffer = new BookingOffer();
        bookingOffer.setTitle("Offre2 Test");
        bookingOffer.setPrice(BigDecimal.valueOf(249.99));
        bookingOffer.setNumberOfCustomers(2);

        HttpEntity<BookingOffer> createOfferRequest = new HttpEntity<>(bookingOffer, adminHeaders);

        ResponseEntity<BookingOffer> createOfferResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER,
                HttpMethod.POST,
                createOfferRequest,
                BookingOffer.class
        );

        assertThat(createOfferResponse.getStatusCode().value()).isEqualTo(200);
        BookingOffer createdOffer = createOfferResponse.getBody();
        assertThat(createdOffer).isNotNull();
        assertThat(createdOffer.getBookingOfferId()).isNotNull();

        // Création Utilisateur Test
        User user = new User();
        UUID userUUID = UUID.randomUUID();
        String userKey = userUUID.toString().replace("_", "");
        String userMail = "user@test.com";
        String userPassword = "@Userpass123";

        user.setUsername(userMail);
        user.setPassword(encoder.encode(userPassword));
        user.setAlias("User Test");
        user.setUserKey(userKey);

        Set<Role> userRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Error: Role User is not found."));
        userRoles.add(userRole);
        user.setRoles(userRoles);

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        // Connexion de l'utilisateur
        LoginRequest userloginRequest = new LoginRequest();
        userloginRequest.setUsername(userMail);
        userloginRequest.setPassword(userPassword);

        ResponseEntity<?> userResponse = authController.authenticateUser(userloginRequest);
        assertThat(userResponse.getStatusCode().value()).isEqualTo(200);

        JwtResponse userJwtResponse = (JwtResponse) userResponse.getBody();
        assertThat(userJwtResponse).isNotNull();
        String userToken = userJwtResponse.getToken();
        assertThat(userToken).isNotNull().isNotEmpty();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        userHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Création d'une réservation (ajout d'une offre au panier) avec utilisateur test
        Booking booking = new Booking();
        booking.setBookingOfferTitle(bookingOffer.getTitle());
        booking.setPrice(bookingOffer.getPrice());
        booking.setNumberOfGuests(bookingOffer.getNumberOfCustomers());
        booking.setUserKey(savedUser.getUserKey());

        HttpEntity<Booking> createBookingRequest = new HttpEntity<>(booking, userHeaders);

        ResponseEntity<Booking> createBookingResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKING,
                HttpMethod.POST,
                createBookingRequest,
                Booking.class
        );

        assertThat(createBookingResponse.getStatusCode().value()).isEqualTo(201);
        Booking createdBooking = createBookingResponse.getBody();
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getBookingId()).isNotNull();

        // Vérification de l'existance de la réservation(offre dans panier)
        ResponseEntity<Booking> getBookingResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKING + "/" + createdBooking.getBookingId(),
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                Booking.class
        );

        assertThat(getBookingResponse.getStatusCode().value()).isEqualTo(200);
        Booking retrievedBooking = getBookingResponse.getBody();
        assertThat(retrievedBooking).isNotNull();
    }

    // Test US6 : Je suis un utilisateur connecté et je veux payer la réservation enregistrer dans mon panier.
    @Test
    void testCreateAdminAndUserLoginCreateBookingOfferCreateBookingThenCreateTicket() {

        // Load Roles
        //authController.loadRoleAdmin();
        //authController.loadRoleCustomer();

        // Création Admin
        User admin = new User();
        UUID adminUUID = UUID.randomUUID();
        String adminUserKey = adminUUID.toString().replace("_", "");

        admin.setUsername("admin@test3.com");
        admin.setPassword(encoder.encode("P@sswordAdmin3"));
        admin.setAlias("Admin3");
        admin.setUserKey(adminUserKey);

        Set<Role> adminRoles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role Admin is not found."));
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);

        User savedAdmin = userRepository.save(admin);
        assertThat(savedAdmin.getId()).isNotNull();

        // Connexion Admin
        LoginRequest adminloginRequest = new LoginRequest();
        adminloginRequest.setUsername("admin@test3.com");
        adminloginRequest.setPassword("P@sswordAdmin3");

        ResponseEntity<?> adminResponse = authController.authenticateUser(adminloginRequest);
        assertThat(adminResponse.getStatusCode().value()).isEqualTo(200);

        JwtResponse adminJwtResponse = (JwtResponse) adminResponse.getBody();
        assertThat(adminJwtResponse).isNotNull();
        String adminToken = adminJwtResponse.getToken();
        assertThat(adminToken).isNotNull().isNotEmpty();

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Création d'une offre
        BookingOffer bookingOffer = new BookingOffer();
        bookingOffer.setTitle("Offre3 Test");
        bookingOffer.setPrice(BigDecimal.valueOf(249.99));
        bookingOffer.setNumberOfCustomers(2);

        HttpEntity<BookingOffer> createOfferRequest = new HttpEntity<>(bookingOffer, adminHeaders);

        ResponseEntity<BookingOffer> createOfferResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER,
                HttpMethod.POST,
                createOfferRequest,
                BookingOffer.class
        );

        assertThat(createOfferResponse.getStatusCode().value()).isEqualTo(200);
        BookingOffer createdOffer = createOfferResponse.getBody();
        assertThat(createdOffer).isNotNull();
        assertThat(createdOffer.getBookingOfferId()).isNotNull();

        // Création Utilisateur Test
        User user = new User();
        UUID userUUID = UUID.randomUUID();
        String userKey = userUUID.toString().replace("_", "");
        String userMail = "user2@test.com";
        String userPassword = "@Userpass1234";

        user.setUsername(userMail);
        user.setPassword(encoder.encode(userPassword));
        user.setAlias("User2 Test");
        user.setUserKey(userKey);

        Set<Role> userRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Error: Role User is not found."));
        userRoles.add(userRole);
        user.setRoles(userRoles);

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        // Connexion de l'utilisateur
        LoginRequest userloginRequest = new LoginRequest();
        userloginRequest.setUsername(userMail);
        userloginRequest.setPassword(userPassword);

        ResponseEntity<?> userResponse = authController.authenticateUser(userloginRequest);
        assertThat(userResponse.getStatusCode().value()).isEqualTo(200);

        JwtResponse userJwtResponse = (JwtResponse) userResponse.getBody();
        assertThat(userJwtResponse).isNotNull();
        String userToken = userJwtResponse.getToken();
        assertThat(userToken).isNotNull().isNotEmpty();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        userHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Création d'une réservation (ajout d'une offre au panier) avec utilisateur test
        Booking booking = new Booking();
        booking.setBookingOfferTitle(bookingOffer.getTitle());
        booking.setPrice(bookingOffer.getPrice());
        booking.setNumberOfGuests(bookingOffer.getNumberOfCustomers());
        booking.setUserKey(savedUser.getUserKey());

        HttpEntity<Booking> createBookingRequest = new HttpEntity<>(booking, userHeaders);

        ResponseEntity<Booking> createBookingResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKING,
                HttpMethod.POST,
                createBookingRequest,
                Booking.class
        );

        assertThat(createBookingResponse.getStatusCode().value()).isEqualTo(201);
        Booking createdBooking = createBookingResponse.getBody();
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getBookingId()).isNotNull();

        // Création de Ticket directement (simulation de paiement réussi,
        // car simulation échec bloque la création de ticket)
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setOfferTitle(createdBooking.getBookingOfferTitle());
        ticketRequest.setTicketPrice(createdBooking.getPrice());
        ticketRequest.setNumberOfGuests(String.valueOf(createdBooking.getNumberOfGuests()));
        ticketRequest.setUsername(savedUser.getAlias());
        ticketRequest.setUserKey(createdBooking.getUserKey());

        HttpEntity<TicketRequest> ticketRequestEntity = new HttpEntity<>(ticketRequest, userHeaders);

        ResponseEntity<Ticket> ticketResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_TICKET,
                HttpMethod.POST,
                ticketRequestEntity,
                Ticket.class
        );

        assertThat(ticketResponse.getStatusCode().value()).isEqualTo(201);
        Ticket createdTicket = ticketResponse.getBody();
        assertThat(createdTicket).isNotNull();
        assertThat(createdTicket.getTicketId()).isNotNull();
    }

    // Test US7 : Je suis un administrateur connecté et je veux consulter dans mon espace admin
    // le nombre de ventes par offre de réservation.
    // ATTENTION: si vous souhaitez lancer ce test uniquement, veuillez suivre les instructions notées
    // à la fin de la méthode.
    @Test
    void testCreateAdminLoginCreateBookingAndGetAllSellsByOffer() {

        // Load Role Admin
        //authController.loadRoleAdmin();

        // Création Admin
        User admin = new User();
        UUID adminUUID = UUID.randomUUID();
        String adminUserKey = adminUUID.toString().replace("_", "");

        admin.setUsername("admin@test4.com");
        admin.setPassword(encoder.encode("@Dmin123"));
        admin.setAlias("Admin4");
        admin.setUserKey(adminUserKey);

        Set<Role> adminRoles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role Admin is not found."));
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);

        User savedAdmin = userRepository.save(admin);
        assertThat(savedAdmin.getId()).isNotNull();

        // Connexion Admin
        LoginRequest adminloginRequest = new LoginRequest();
        adminloginRequest.setUsername("admin@test4.com");
        adminloginRequest.setPassword("@Dmin123");

        ResponseEntity<?> adminResponse = authController.authenticateUser(adminloginRequest);
        assertThat(adminResponse.getStatusCode().value()).isEqualTo(200);

        JwtResponse adminJwtResponse = (JwtResponse) adminResponse.getBody();
        assertThat(adminJwtResponse).isNotNull();
        String adminToken = adminJwtResponse.getToken();
        assertThat(adminToken).isNotNull().isNotEmpty();

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Création de deux offres de réservation
        // Offre 1
        BookingOffer bookingOffer = new BookingOffer();
        bookingOffer.setTitle("Offre4 Test");
        bookingOffer.setPrice(BigDecimal.valueOf(249.99));
        bookingOffer.setNumberOfCustomers(2);

        HttpEntity<BookingOffer> createOfferRequest = new HttpEntity<>(bookingOffer, adminHeaders);

        ResponseEntity<BookingOffer> createOfferResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER,
                HttpMethod.POST,
                createOfferRequest,
                BookingOffer.class
        );

        assertThat(createOfferResponse.getStatusCode().value()).isEqualTo(200);
        BookingOffer createdOffer = createOfferResponse.getBody();
        assertThat(createdOffer).isNotNull();
        assertThat(createdOffer.getBookingOfferId()).isNotNull();

        // Offre 2
        BookingOffer bookingOffer2 = new BookingOffer();
        bookingOffer2.setTitle("Offre4 Test2");
        bookingOffer2.setPrice(BigDecimal.valueOf(299.99));
        bookingOffer2.setNumberOfCustomers(3);

        HttpEntity<BookingOffer> createOffer2Request = new HttpEntity<>(bookingOffer2, adminHeaders);

        ResponseEntity<BookingOffer> createOffer2Response = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER,
                HttpMethod.POST,
                createOffer2Request,
                BookingOffer.class
        );

        assertThat(createOffer2Response.getStatusCode().value()).isEqualTo(200);
        BookingOffer createdOffer2 = createOffer2Response.getBody();
        assertThat(createdOffer2).isNotNull();
        assertThat(createdOffer2.getBookingOfferId()).isNotNull();

        // En tant qu'administrateur, consulter les ventes par Offre
        ResponseEntity<List<SellsByOffer>> sellsByOfferResponse = restTemplate.exchange(
                ApiRegistration.API + ApiRegistration.REST_SELLSBYOFFER,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                new ParameterizedTypeReference<List<SellsByOffer>>() {}
        );

        assertThat(sellsByOfferResponse.getStatusCode().value()).isEqualTo(200);
        List<SellsByOffer> sellsByOffer = sellsByOfferResponse.getBody();
        assertThat(sellsByOffer).isNotNull();
        assertThat(sellsByOffer.size()).isEqualTo(3); //Valeur pour le lancement de la class entière de Test
        // Si vous chercher à lancer uniquement cette méthode remplacer la valeur isEqualTo(3) par 2.
    }
}
