package com.example.fiapvideouploader.adapter.out.messaging.kafka.producer;

import com.example.fiapvideouploader.adapter.out.messaging.kafka.exception.KafkaMessageException;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoDataProducer {

    private static final String TOPIC = "video-data";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(VideoDataMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String key = message.getVideoId().toString();
            kafkaTemplate.send(TOPIC, key, messageJson);
            log.info("Dados do vídeo enviados para tópico {}: videoId={}", TOPIC, message.getVideoId());
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar VideoDataMessage", e);
            throw new KafkaMessageException("Falha ao enviar dados do vídeo", e);
        } catch (Exception e) {
            log.error("Erro ao enviar dados para Kafka", e);
            throw new KafkaMessageException("Falha na comunicação com Kafka", e);
        }
    }
}
