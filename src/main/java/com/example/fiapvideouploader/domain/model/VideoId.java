package com.example.fiapvideouploader.domain.model;

import java.util.UUID;

public class VideoId {
    private final UUID value;

    public VideoId(UUID value) {
        this.value = value;
    }

    public static VideoId generate() {
        return new VideoId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}