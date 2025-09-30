package com.example.fiapvideouploader.adapter.out.messaging.kafka.producer;

import com.example.fiapvideouploader.adapter.out.messaging.kafka.exception.KafkaMessageException;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoToProcessProducer {

    private static final String TOPIC = "video-processor";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(VideoProcessorMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.getVideoId(), messageJson);
            log.info("Mensagem enviada para tópico {}: videoId={}", TOPIC, message.getVideoId());
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar VideoProcessorMessage", e);
            throw new KafkaMessageException("Falha ao enviar mensagem para processamento", e);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para Kafka", e);
            throw new KafkaMessageException("Falha na comunicação com Kafka", e);
        }
    }
}
