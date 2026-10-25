package br.com.pw2m.contos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "read_tickets")
public class ReadTicket {

    @Id
    @Column(length = 80)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ReadTicket() {
    }

    public ReadTicket(String token, UserAccount user, Story story, Instant expiresAt, Instant createdAt) {
        this.token = token;
        this.user = user;
        this.story = story;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public UserAccount getUser() {
        return user;
    }

    public Story getStory() {
        return story;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
