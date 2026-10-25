package br.com.pw2m.contos.repository;

import br.com.pw2m.contos.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoryRepository extends JpaRepository<Story, UUID> {

    Optional<Story> findBySlug(String slug);

    Optional<Story> findByCode(String code);

    List<Story> findByActiveTrueOrderByCodeAsc();

    List<Story> findAllByOrderByCodeAsc();
}
