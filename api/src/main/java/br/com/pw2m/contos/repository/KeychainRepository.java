package br.com.pw2m.contos.repository;

import br.com.pw2m.contos.domain.Keychain;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KeychainRepository extends JpaRepository<Keychain, UUID> {

    @EntityGraph(attributePaths = {"story", "owner"})
    Optional<Keychain> findByPublicCode(String publicCode);

    @EntityGraph(attributePaths = "story")
    @Query("select k from Keychain k where k.id = :id")
    Optional<Keychain> findWithStoryById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"story", "owner"})
    List<Keychain> findAllByOrderByCreatedAtDesc();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Keychain k set k.pinAttempts = k.pinAttempts + 1 where k.id = :id and k.status = br.com.pw2m.contos.domain.KeychainStatus.NEW")
    int incrementPinAttempts(@Param("id") UUID id);
}
