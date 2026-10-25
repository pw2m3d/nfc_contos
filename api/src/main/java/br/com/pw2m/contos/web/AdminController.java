package br.com.pw2m.contos.web;

import br.com.pw2m.contos.config.AdminGuard;
import br.com.pw2m.contos.dto.CreateKeychainRequest;
import br.com.pw2m.contos.dto.CreateStoryRequest;
import br.com.pw2m.contos.dto.CreatedKeychainResponse;
import br.com.pw2m.contos.dto.KeychainAdminItem;
import br.com.pw2m.contos.dto.StoryCard;
import br.com.pw2m.contos.dto.UpdateStoryRequest;
import br.com.pw2m.contos.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminGuard guard;
    private final AdminService admin;

    public AdminController(AdminGuard guard, AdminService admin) {
        this.guard = guard;
        this.admin = admin;
    }

    @GetMapping("/stories")
    public List<StoryCard> stories(@RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        guard.check(adminKey);
        return admin.stories();
    }

    @PostMapping("/stories")
    public StoryCard createStory(@RequestHeader(value = "X-Admin-Key", required = false) String adminKey, @Valid @RequestBody CreateStoryRequest request) {
        guard.check(adminKey);
        return admin.createStory(request);
    }

    @PatchMapping("/stories/{slug}")
    public StoryCard updateStory(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey,
            @PathVariable String slug,
            @Valid @RequestBody UpdateStoryRequest request
    ) {
        guard.check(adminKey);
        return admin.updateStory(slug, request);
    }

    @GetMapping("/keychains")
    public List<KeychainAdminItem> keychains(@RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        guard.check(adminKey);
        return admin.keychains();
    }

    @PostMapping("/keychains")
    public CreatedKeychainResponse createKeychain(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey,
            @Valid @RequestBody CreateKeychainRequest request
    ) {
        guard.check(adminKey);
        return admin.createKeychain(request.storySlug());
    }
}
