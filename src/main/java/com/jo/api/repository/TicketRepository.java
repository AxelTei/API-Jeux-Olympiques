package com.jo.api.repository;

import com.jo.api.pojo.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByQrCodeKey(String qrCodeKey);
}
