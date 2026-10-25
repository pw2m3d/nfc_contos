package br.com.pw2m.contos.repository;

import br.com.pw2m.contos.domain.Entitlement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EntitlementRepository extends JpaRepository<Entitlement, UUID> {

    boolean existsByUser_IdAndStory_Id(UUID userId, UUID storyId);

    @EntityGraph(attributePaths = "story")
    List<Entitlement> findByUser_IdOrderByCreatedAtAsc(UUID userId);
}
