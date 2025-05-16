package com.jo.api;

import com.jo.api.pojo.SellsByOffer;
import com.jo.api.service.SellsByOfferService;
import com.jo.api.ws.SellsByOfferController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SellsByOfferControllerTest {

    @Mock
    private SellsByOfferService sellsByOfferService;

    @InjectMocks
    private SellsByOfferController sellsByOfferController;

    @Test
    void testGetAllSellsByOffer_Success() {
        // Préparation des données de test
        List<SellsByOffer> mockSellsByOffers = new ArrayList<>();

        SellsByOffer sellsByOffer1 = new SellsByOffer();
        sellsByOffer1.setId(1L);
        sellsByOffer1.setOfferTitle("Duo");
        sellsByOffer1.setOfferPrice(BigDecimal.valueOf(10));

        SellsByOffer sellsByOffer2 = new SellsByOffer();
        sellsByOffer2.setId(2L);
        sellsByOffer2.setOfferTitle("Family");
        sellsByOffer2.setOfferPrice(BigDecimal.valueOf(5));

        mockSellsByOffers.add(sellsByOffer1);
        mockSellsByOffers.add(sellsByOffer2);

        when(sellsByOfferService.getAllSellsByOffer()).thenReturn(mockSellsByOffers);

        // Exécution du test
        List<SellsByOffer> result = sellsByOfferController.getAllSellsByOffer();

        // Vérification des résultats
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOfferTitle()).isEqualTo("Duo");
        assertThat(result.get(0).getOfferPrice()).isEqualTo(BigDecimal.valueOf(10));
        assertThat(result.get(1).getOfferTitle()).isEqualTo("Family");
        assertThat(result.get(1).getOfferPrice()).isEqualTo(BigDecimal.valueOf(5));

        // Vérification que la méthode du service a été appelée
        verify(sellsByOfferService, times(1)).getAllSellsByOffer();
    }

    @Test
    void testAdminRoleRequired() {
        // Vérification que la méthode est annotée avec @PreAuthorize avec le rôle ADMIN
        try {
            PreAuthorize preAuthorizeAnnotation = SellsByOfferController.class
                    .getMethod("getAllSellsByOffer")
                    .getAnnotation(PreAuthorize.class);

            assertThat(preAuthorizeAnnotation).isNotNull();
            assertThat(preAuthorizeAnnotation.value()).contains("ROLE_ADMIN");
        } catch (NoSuchMethodException e) {
            // Si la méthode n'existe pas, le test échoue
            assertThat(false).isTrue(); // Force l'échec du test
        }
    }

    @Test
    void testGetAllSellsByOffer_EmptyList() {
        // Préparation des données de test
        List<SellsByOffer> emptySellsByOffers = new ArrayList<>();
        when(sellsByOfferService.getAllSellsByOffer()).thenReturn(emptySellsByOffers);

        // Exécution du test
        List<SellsByOffer> result = sellsByOfferController.getAllSellsByOffer();

        // Vérification des résultats
        assertThat(result).isEmpty();

        // Vérification que la méthode du service a été appelée
        verify(sellsByOfferService, times(1)).getAllSellsByOffer();
    }
}
