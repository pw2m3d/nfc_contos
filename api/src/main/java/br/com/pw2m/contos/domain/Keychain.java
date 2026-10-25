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
@Table(name = "keychains")
public class Keychain {

    @Id
    private UUID id;

    @Column(name = "public_code", nullable = false, length = 64, unique = true)
    private String publicCode;

    @Column(name = "pin_hash", length = 100)
    private String pinHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserAccount owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KeychainStatus status;

    @Column(name = "pin_attempts", nullable = false)
    private int pinAttempts;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Keychain() {
    }

    public static Keychain fresh(UUID id, String publicCode, String pinHash, Story story, Instant createdAt) {
        Keychain keychain = new Keychain();
        keychain.id = id;
        keychain.publicCode = publicCode;
        keychain.pinHash = pinHash;
        keychain.story = story;
        keychain.status = KeychainStatus.NEW;
        keychain.pinAttempts = 0;
        keychain.createdAt = createdAt;
        return keychain;
    }

    public void activate(UserAccount owner, Instant activatedAt) {
        this.owner = owner;
        this.status = KeychainStatus.ACTIVATED;
        this.pinHash = null;
        this.activatedAt = activatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public String getPinHash() {
        return pinHash;
    }

    public Story getStory() {
        return story;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public KeychainStatus getStatus() {
        return status;
    }

    public int getPinAttempts() {
        return pinAttempts;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
