package com.jo.api.ws;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "API pour tester les différents niveaux d'accès")
public class TestController {

    @Operation(summary = "Accès public",
            description = "Endpoint accessible à tous les utilisateurs sans authentification")
    @ApiResponse(responseCode = "200", description = "Accès réussi",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string")))
    @GetMapping("/all")
    public String allAccess() {
        return "Contenu public.";
    }

    @Operation(summary = "Accès client",
            description = "Endpoint accessible uniquement aux utilisateurs authentifiés avec le rôle CLIENT ou ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accès réussi",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle CLIENT ou ADMIN requis",
                    content = @Content)
    })
    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    public String customerAccess() {
        return "Contenu client.";
    }

    @Operation(summary = "Accès administrateur",
            description = "Endpoint accessible uniquement aux utilisateurs authentifiés avec le rôle ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accès réussi",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis",
                    content = @Content)
    })
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminAccess() {
        return "Contenu administrateur.";
    }
}
