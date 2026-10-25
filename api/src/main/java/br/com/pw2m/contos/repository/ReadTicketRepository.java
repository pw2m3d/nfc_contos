package br.com.pw2m.contos.repository;

import br.com.pw2m.contos.domain.ReadTicket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface ReadTicketRepository extends JpaRepository<ReadTicket, String> {

    @EntityGraph(attributePaths = {"story", "user"})
    Optional<ReadTicket> findByToken(String token);

    void deleteByExpiresAtBefore(Instant instant);
}
