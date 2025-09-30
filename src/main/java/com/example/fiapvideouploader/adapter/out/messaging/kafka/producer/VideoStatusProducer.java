package com.example.fiapvideouploader.adapter.out.messaging.kafka.producer;

import com.example.fiapvideouploader.adapter.out.messaging.kafka.exception.KafkaMessageException;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoStatusProducer {

    private static final String TOPIC = "video-status";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(VideoStatusMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String key = message.getVideoId().toString();
            kafkaTemplate.send(TOPIC, key, messageJson);
            log.info("Status de vídeo enviado para tópico {}: videoId={}, status={}",
                    TOPIC, message.getVideoId(), message.getVideoStatus());
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar VideoStatusMessage", e);
            throw new KafkaMessageException("Falha ao enviar status do vídeo", e);
        } catch (Exception e) {
            log.error("Erro ao enviar status para Kafka", e);
            throw new KafkaMessageException("Falha na comunicação com Kafka", e);
        }
    }
}
