package br.com.pw2m.contos.service;

import br.com.pw2m.contos.domain.Story;
import br.com.pw2m.contos.dto.LibraryItem;
import br.com.pw2m.contos.dto.StoryCard;
import br.com.pw2m.contos.repository.EntitlementRepository;
import br.com.pw2m.contos.repository.StoryRepository;
import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final StoryRepository stories;
    private final EntitlementRepository entitlements;

    public StoryService(StoryRepository stories, EntitlementRepository entitlements) {
        this.stories = stories;
        this.entitlements = entitlements;
    }

    @Transactional(readOnly = true)
    public List<StoryCard> catalog(UUID userId) {
        Set<UUID> owned = ownedStoryIds(userId);
        return stories.findByActiveTrueOrderByCodeAsc().stream()
                .map(story -> StoryViews.card(story, owned.contains(story.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LibraryItem> library(UUID userId) {
        return entitlements.findByUser_IdOrderByCreatedAtAsc(userId).stream()
                .map(entitlement -> new LibraryItem(
                        entitlement.getStory().getSlug(),
                        entitlement.getStory().getCode(),
                        entitlement.getStory().getTitle(),
                        entitlement.getStory().getAuthor(),
                        entitlement.getStory().getSynopsis(),
                        entitlement.getSource().name(),
                        entitlement.getStory().isPublished()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Story requireActive(String slug) {
        Story story = stories.findBySlug(slug)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "História não encontrada."));
        if (!story.isActive()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "História não encontrada.");
        }
        return story;
    }

    private Set<UUID> ownedStoryIds(UUID userId) {
        if (userId == null) {
            return Set.of();
        }
        return entitlements.findByUser_IdOrderByCreatedAtAsc(userId).stream()
                .map(entitlement -> entitlement.getStory().getId())
                .collect(Collectors.toSet());
    }
}
