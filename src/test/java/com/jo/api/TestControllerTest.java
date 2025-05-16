package com.jo.api;

import com.jo.api.ws.TestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TestControllerTest {

    @InjectMocks
    private TestController testController;

    @Test
    void testAllAccess() {
        // Exécution du test
        String result = testController.allAccess();

        // Vérification des résultats
        assertThat(result).isEqualTo("Contenu public.");

        // Vérification que la méthode n'a pas d'annotation @PreAuthorize
        try {
            Method method = TestController.class.getMethod("allAccess");
            PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
            assertThat(annotation).isNull();
        } catch (NoSuchMethodException e) {
            assertThat(false).isTrue(); // Force l'échec du test si la méthode n'existe pas
        }
    }

    @Test
    void testCustomerAccess() {
        // Exécution du test
        String result = testController.customerAccess();

        // Vérification des résultats
        assertThat(result).isEqualTo("Contenu client.");

        // Vérification que la méthode a l'annotation @PreAuthorize avec les rôles appropriés
        try {
            Method method = TestController.class.getMethod("customerAccess");
            PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
            assertThat(annotation).isNotNull();
            assertThat(annotation.value()).contains("ROLE_CUSTOMER");
            assertThat(annotation.value()).contains("ROLE_ADMIN");
        } catch (NoSuchMethodException e) {
            assertThat(false).isTrue(); // Force l'échec du test si la méthode n'existe pas
        }
    }

    @Test
    void testAdminAccess() {
        // Exécution du test
        String result = testController.adminAccess();

        // Vérification des résultats
        assertThat(result).isEqualTo("Contenu administrateur.");

        // Vérification que la méthode a l'annotation @PreAuthorize avec le rôle ADMIN
        try {
            Method method = TestController.class.getMethod("adminAccess");
            PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
            assertThat(annotation).isNotNull();
            assertThat(annotation.value()).contains("ROLE_ADMIN");
            assertThat(annotation.value()).doesNotContain("ROLE_CUSTOMER");
        } catch (NoSuchMethodException e) {
            assertThat(false).isTrue(); // Force l'échec du test si la méthode n'existe pas
        }
    }
}

