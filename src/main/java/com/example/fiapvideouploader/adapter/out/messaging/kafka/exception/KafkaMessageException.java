package com.example.fiapvideouploader.adapter.out.messaging.kafka.exception;

public class KafkaMessageException extends RuntimeException {
    public KafkaMessageException(String message) {
        super(message);
    }

    public KafkaMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}