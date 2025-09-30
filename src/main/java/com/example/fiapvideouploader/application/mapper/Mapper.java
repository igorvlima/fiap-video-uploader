package com.example.fiapvideouploader.application.mapper;

import com.example.fiapvideouploader.adapter.in.web.VideoUploadRequest;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;
import com.example.fiapvideouploader.domain.model.VideoMetadata;
import com.example.fiapvideouploader.domain.port.in.UploadVideoCommand;

import java.util.UUID;

public class Mapper {

    public static VideoProcessorMessage buildVideoProcessorMessage(UUID videoId, String customerEmail, String videoName, String s3Key){
        return VideoProcessorMessage.builder()
                .videoId(String.valueOf(videoId))
                .s3Key(s3Key)
                .customerEmail(customerEmail)
                .videoName(videoName)
                .build();
    }

    public static VideoStatusMessage buildVideoStatusMessage(VideoMetadata videoMetadata, String status){
        return VideoStatusMessage.builder()
                .videoId(videoMetadata.getVideoId().getValue())
                .videoName(videoMetadata.getOriginalName())
                .customerEmail(videoMetadata.getCustomerEmail())
                .videoStatus(status)
                .build();
    }

    public static VideoStatusMessage buildVideoStatusMessage(UploadVideoCommand uploadVideoCommand, UUID videoId, String status){
        return VideoStatusMessage.builder()
                .videoId(videoId)
                .videoName(uploadVideoCommand.getFileName())
                .customerEmail(uploadVideoCommand.getCustomerEmail())
                .videoStatus(status)
                .build();
    }

    public static VideoDataMessage buildVideoDataMessage(VideoMetadata videoMetadata){
        return VideoDataMessage.builder()
                .videoId(videoMetadata.getVideoId().getValue())
                .originalName(videoMetadata.getOriginalName())
                .generatedName(videoMetadata.getGeneratedName())
                .customerId(videoMetadata.getCustomerId())
                .customerEmail(videoMetadata.getCustomerEmail())
                .build();
    }
}
