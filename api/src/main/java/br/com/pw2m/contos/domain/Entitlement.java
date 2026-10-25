package br.com.pw2m.contos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "entitlements")
public class Entitlement {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntitlementSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keychain_id")
    private Keychain keychain;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Entitlement() {
    }

    public Entitlement(UUID id, UserAccount user, Story story, EntitlementSource source, Keychain keychain, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.story = story;
        this.source = source;
        this.keychain = keychain;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public Story getStory() {
        return story;
    }

    public EntitlementSource getSource() {
        return source;
    }

    public Keychain getKeychain() {
        return keychain;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
