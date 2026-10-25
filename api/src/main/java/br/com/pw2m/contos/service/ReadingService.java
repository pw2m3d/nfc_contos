package br.com.pw2m.contos.service;

import br.com.pw2m.contos.config.AppProperties;
import br.com.pw2m.contos.domain.ReadTicket;
import br.com.pw2m.contos.domain.Story;
import br.com.pw2m.contos.repository.EntitlementRepository;
import br.com.pw2m.contos.repository.ReadTicketRepository;
import br.com.pw2m.contos.repository.StoryRepository;
import br.com.pw2m.contos.repository.UserRepository;
import br.com.pw2m.contos.util.Codes;
import br.com.pw2m.contos.util.StoryFiles;
import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class ReadingService {

    private final ReadTicketRepository tickets;
    private final EntitlementRepository entitlements;
    private final UserRepository users;
    private final StoryRepository stories;
    private final AppProperties properties;

    public ReadingService(
            ReadTicketRepository tickets,
            EntitlementRepository entitlements,
            UserRepository users,
            StoryRepository stories,
            AppProperties properties
    ) {
        this.tickets = tickets;
        this.entitlements = entitlements;
        this.users = users;
        this.stories = stories;
        this.properties = properties;
    }

    @Transactional
    public String issue(UUID userId, UUID storyId) {
        if (!entitlements.existsByUser_IdAndStory_Id(userId, storyId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Esta história não está na sua biblioteca.");
        }
        tickets.deleteByExpiresAtBefore(Instant.now());
        String token = Codes.ticket();
        Instant now = Instant.now();
        tickets.save(new ReadTicket(
                token,
                users.getReferenceById(userId),
                stories.getReferenceById(storyId),
                now.plus(properties.getTicketHours(), ChronoUnit.HOURS),
                now
        ));
        return token;
    }

    @Transactional(readOnly = true)
    public Path resolve(String token) {
        ReadTicket ticket = tickets.findByToken(token)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Este link de leitura expirou ou não existe. Encoste o chaveiro novamente."));
        if (ticket.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Este link de leitura expirou. Encoste o chaveiro novamente.");
        }
        if (!entitlements.existsByUser_IdAndStory_Id(ticket.getUser().getId(), ticket.getStory().getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Esta história não está liberada para esta conta.");
        }
        return publishedFile(ticket.getStory());
    }

    public Path publishedFile(Story story) {
        if (!story.isPublished()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "O arquivo desta história ainda não foi publicado.");
        }
        Path root = Path.of(properties.getStoriesDir());
        if (!Files.isDirectory(root)) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "A pasta de histórias não foi encontrada no servidor.");
        }
        Path file = StoryFiles.resolve(root, story.getHtmlFile());
        if (!Files.isRegularFile(file)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "O arquivo desta história ainda não foi publicado.");
        }
        return file;
    }
}
