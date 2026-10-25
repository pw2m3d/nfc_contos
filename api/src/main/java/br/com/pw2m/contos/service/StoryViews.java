package br.com.pw2m.contos.service;

import br.com.pw2m.contos.domain.Story;
import br.com.pw2m.contos.dto.StoryCard;

public final class StoryViews {

    private StoryViews() {
    }

    public static StoryCard card(Story story, boolean owned) {
        return new StoryCard(
                story.getSlug(),
                story.getCode(),
                story.getTitle(),
                story.getAuthor(),
                story.getSynopsis(),
                story.getPriceCents(),
                story.isPublished(),
                owned
        );
    }
}
