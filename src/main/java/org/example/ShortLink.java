package org.example;

import org.example.utils.TimeConverter;

public class ShortLink {

    public static final int MAX_LINK_CLICK = 3;

    private final String shortCode;
    private String originalLink;
    private final Long createdAt;
    private int accessCount;
    private String UUID;
    private boolean isBlocked;

    public ShortLink(String shortCode, String originalLink, String UUID) {
        this.shortCode = shortCode;
        this.originalLink = originalLink;
        this.createdAt = System.currentTimeMillis();
        this.accessCount = 0;
        this.UUID = UUID;
    }

    public ShortLink(String shortCode, String originalLink, Long createdAt, int accessCount, String UUID, boolean isBlocked) {
        this.shortCode = shortCode;
        this.originalLink = originalLink;
        this.createdAt = createdAt;
        this.accessCount = accessCount;
        this.UUID = UUID;
        this.isBlocked = isBlocked;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public String getUUID() {
        return UUID;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public void updateOriginalLink(String newOriginalLink) {
        this.originalLink = newOriginalLink;
    }

    public void incrementAccessCount() {
        accessCount++;
    }

    @Override
    public String toString() {
        return shortCode + " -> " + originalLink + " (переходов: " + accessCount + ", создан: " + TimeConverter.millisToDateTime(createdAt) + ", Блокировка ссылки: "+ isBlocked + ")";
    }
}
