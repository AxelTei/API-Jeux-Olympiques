package com.jo.api;

import com.jo.api.pojo.BookingOffer;
import com.jo.api.repository.BookingOfferRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingOfferRepositoryTest {

    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withDatabaseName("bookingOfferTest")
            .withUsername("testUser")
            .withPassword("testPass");

    @Autowired
    private BookingOfferRepository bookingOfferRepository;

    @BeforeAll
    static void setUp() {
        mariaDBContainer.start();
        System.setProperty("spring.datasource.url", mariaDBContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mariaDBContainer.getUsername());
        System.setProperty("spring.datasource.password", mariaDBContainer.getPassword());
    }

    @Test
    void testBookingOfferCreationAndRetrieval() {
        BookingOffer bookingOffer = new BookingOffer();
        bookingOffer.setTitle("Duo");
        bookingOffer.setPrice(BigDecimal.valueOf(10.00));
        bookingOffer.setNumberOfCustomers(2);
        BookingOffer savedBookingOffer = bookingOfferRepository.save(bookingOffer);

        assertThat(savedBookingOffer.getBookingOfferId()).isNotNull();

        BookingOffer retrievedBookingOffer = bookingOfferRepository.findById(savedBookingOffer.getBookingOfferId()).orElse(null);
        assertThat(retrievedBookingOffer).isNotNull();
        assertThat(retrievedBookingOffer.getTitle()).isEqualTo("Duo");
    }

}
