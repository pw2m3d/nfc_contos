package br.com.pw2m.contos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "stories")
public class Story {

    @Id
    private UUID id;

    @Column(nullable = false, length = 20, unique = true)
    private String code;

    @Column(nullable = false, length = 80, unique = true)
    private String slug;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 160)
    private String author;

    @Column(nullable = false)
    private String synopsis;

    @Column(name = "price_cents", nullable = false)
    private int priceCents;

    @Column(name = "html_file", length = 255)
    private String htmlFile;

    @Column(nullable = false)
    private boolean active = true;

    protected Story() {
    }

    public Story(UUID id, String code, String slug, String title, String author, String synopsis, int priceCents, String htmlFile) {
        this.id = id;
        this.code = code;
        this.slug = slug;
        this.title = title;
        this.author = author;
        this.synopsis = synopsis;
        this.priceCents = priceCents;
        this.htmlFile = htmlFile;
        this.active = true;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(int priceCents) {
        this.priceCents = priceCents;
    }

    public String getHtmlFile() {
        return htmlFile;
    }

    public void setHtmlFile(String htmlFile) {
        this.htmlFile = htmlFile;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPublished() {
        return htmlFile != null && !htmlFile.isBlank();
    }
}
