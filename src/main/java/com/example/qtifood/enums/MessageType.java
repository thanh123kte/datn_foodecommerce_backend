package com.example.qtifood.enums;

public enum MessageType {
    TEXT("Text message"),
    IMAGE("Image message"),
    FILE("File attachment"),
    SYSTEM("System message"),
    ORDER("Order information"),
    LOCATION("Location message");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSystemMessage() {
        return this == SYSTEM || this == ORDER;
    }

    public boolean isUserMessage() {
        return this == TEXT || this == IMAGE || this == FILE || this == LOCATION;
    }
}