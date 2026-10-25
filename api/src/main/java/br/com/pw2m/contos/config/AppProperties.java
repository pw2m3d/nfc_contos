package br.com.pw2m.contos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String jwtSecret;
    private int jwtHours = 72;
    private String adminKey;
    private String publicBaseUrl = "http://localhost:4200";
    private String storiesDir = "..";
    private int ticketHours = 12;
    private String frameAncestors = "http://localhost:4200 http://127.0.0.1:4200";

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public int getJwtHours() {
        return jwtHours;
    }

    public void setJwtHours(int jwtHours) {
        this.jwtHours = jwtHours;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getStoriesDir() {
        return storiesDir;
    }

    public void setStoriesDir(String storiesDir) {
        this.storiesDir = storiesDir;
    }

    public int getTicketHours() {
        return ticketHours;
    }

    public void setTicketHours(int ticketHours) {
        this.ticketHours = ticketHours;
    }

    public String getFrameAncestors() {
        return frameAncestors;
    }

    public void setFrameAncestors(String frameAncestors) {
        this.frameAncestors = frameAncestors;
    }
}
