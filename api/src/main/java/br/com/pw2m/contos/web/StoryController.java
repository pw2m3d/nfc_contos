package br.com.pw2m.contos.web;

import br.com.pw2m.contos.dto.LibraryItem;
import br.com.pw2m.contos.dto.StoryCard;
import br.com.pw2m.contos.dto.TicketResponse;
import br.com.pw2m.contos.security.CurrentUser;
import br.com.pw2m.contos.service.ReadingService;
import br.com.pw2m.contos.service.StoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StoryController {

    private final StoryService stories;
    private final ReadingService reading;

    public StoryController(StoryService stories, ReadingService reading) {
        this.stories = stories;
        this.reading = reading;
    }

    @GetMapping("/stories")
    public List<StoryCard> catalog() {
        return stories.catalog(CurrentUser.optionalId());
    }

    @GetMapping("/me/library")
    public List<LibraryItem> library() {
        return stories.library(CurrentUser.id());
    }

    @PostMapping("/stories/{slug}/ticket")
    public TicketResponse ticket(@PathVariable String slug) {
        var story = stories.requireActive(slug);
        return new TicketResponse(reading.issue(CurrentUser.id(), story.getId()));
    }
}
