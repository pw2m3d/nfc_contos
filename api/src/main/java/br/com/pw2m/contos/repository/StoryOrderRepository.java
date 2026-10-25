package br.com.pw2m.contos.repository;

import br.com.pw2m.contos.domain.OrderStatus;
import br.com.pw2m.contos.domain.StoryOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoryOrderRepository extends JpaRepository<StoryOrder, UUID> {

    @EntityGraph(attributePaths = "story")
    Optional<StoryOrder> findByIdAndUser_Id(UUID id, UUID userId);

    @EntityGraph(attributePaths = "story")
    Optional<StoryOrder> findByUser_IdAndStory_IdAndStatus(UUID userId, UUID storyId, OrderStatus status);
}
