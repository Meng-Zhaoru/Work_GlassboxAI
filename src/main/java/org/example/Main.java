package org.example;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.videointelligence.v1.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import java.lang.System;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    /**
     * Detect Text in a video.
     *
     * @param gcsUri the path to the video file to analyze.
     */
    public static VideoAnnotationResults detectTextGcs(String gcsUri) throws Exception {
        try (VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()) {
            // Create the request
            AnnotateVideoRequest request =
                    AnnotateVideoRequest.newBuilder()
                            .setInputUri(gcsUri)
                            .addFeatures(Feature.TEXT_DETECTION)
                            .build();

            // Asynchronously perform object tracking on videos
            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> future =
                    client.annotateVideoAsync(request);

            System.out.println("Waiting for operation to complete...");
            // The first result is retrieved because a single video was processed.
            AnnotateVideoResponse response = future.get(300, TimeUnit.SECONDS);
            VideoAnnotationResults results = response.getAnnotationResults(0);

            // Get only the first annotation for demo purposes.
            TextAnnotation annotation = results.getTextAnnotations(0);
            System.out.println("Text: " + annotation.getText());

            // Get the first text segment.
            TextSegment textSegment = annotation.getSegments(0);
            System.out.println("Confidence: " + textSegment.getConfidence());
            // For the text segment display its time offset
            VideoSegment videoSegment = textSegment.getSegment();
            Duration startTimeOffset = videoSegment.getStartTimeOffset();
            Duration endTimeOffset = videoSegment.getEndTimeOffset();
            // Display the offset times in seconds, 1e9 is part of the formula to convert nanos to seconds
            System.out.println(
                    String.format(
                            "Start time: %.2f", startTimeOffset.getSeconds() + startTimeOffset.getNanos() / 1e9));
            System.out.println(
                    String.format("End time: %.2f", endTimeOffset.getSeconds() + endTimeOffset.getNanos() / 1e9));

            // Show the first result for the first frame in the segment.
            TextFrame textFrame = textSegment.getFrames(0);
            Duration timeOffset = textFrame.getTimeOffset();
            System.out.println(
                    String.format("Time offset for the first frame: %.2f", timeOffset.getSeconds() + timeOffset.getNanos() / 1e9));

            // Display the rotated bounding box for where the text is on the frame.
            System.out.println("Rotated Bounding Box Vertices:");
            List<NormalizedVertex> vertices = textFrame.getRotatedBoundingBox().getVerticesList();
            for (NormalizedVertex normalizedVertex: vertices) {
                System.out.println(
                        String.format(
                                "\tVertex.x: %.2f, Vertex.y: %.2f",
                                normalizedVertex.getX(), normalizedVertex.getY()
                        )
                );
            }
            return results;
        }
    }

    /**
     * Detect text in a video.
     *
     * @param filePath the path to the video file to analyze.
     */
    public static VideoAnnotationResults detectText(String filePath) throws Exception {
        try (VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()) {
            // Read file
            Path path = Paths.get(filePath);
            byte[] data = Files.readAllBytes(path);

            // Calculate timeout duration based on file size (30 secs per megabyte)
            long fileSizeInMB = Files.size(path) / (1024 * 1024);
            long timeoutDuration = Math.max(600, 60 * fileSizeInMB);

            // Create the request
            AnnotateVideoRequest request =
                AnnotateVideoRequest.newBuilder()
                    .setInputContent(ByteString.copyFrom(data))
                    .addFeatures(Feature.TEXT_DETECTION)
                    .build();

            // Asynchronously perform object tracking on videos
            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> future =
                client.annotateVideoAsync(request);

            System.out.println("Waiting for operation to complete...Text Detection");
            // The first result is retrieved because a single video was processed.
            AnnotateVideoResponse response = future.get(timeoutDuration, TimeUnit.SECONDS);
            VideoAnnotationResults results = response.getAnnotationResults(0);

            List<TextAnnotation> textAnnotations = results.getTextAnnotationsList();
            System.out.print("Text: ");
            for (TextAnnotation annotation : textAnnotations) {
                System.out.print(annotation.getText());
            }
            System.out.println();

            // Try using speech transcription instead of text detection
            // Create the SpeechTranscriptionConfig
            SpeechTranscriptionConfig speechTranscriptionConfig = SpeechTranscriptionConfig.newBuilder()
                .setLanguageCode("zh-HK")  // Set language code for Cantonese
                .setMaxAlternatives(1)
                .setEnableAutomaticPunctuation(true)
                .build();

            // Create the VideoContext
            VideoContext videoContext = VideoContext.newBuilder()
                .setSpeechTranscriptionConfig(speechTranscriptionConfig)
                .build();

            // Create the request
            AnnotateVideoRequest request_s = AnnotateVideoRequest.newBuilder()
                .setInputContent(ByteString.copyFrom(data))
                .addFeatures(Feature.SPEECH_TRANSCRIPTION)
                .setVideoContext(videoContext)
                .build();

            // Asynchronously perform speech transcription on videos
            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> future_s =
                client.annotateVideoAsync(request_s);

            System.out.println("Waiting for operation to complete...Speech Transcription");
            // The first result is retrieved because a single video was processed.
            AnnotateVideoResponse response_s = future_s.get(timeoutDuration, TimeUnit.SECONDS);
            VideoAnnotationResults results_s = response_s.getAnnotationResults(0);

            // Process the speech transcription results
            for (SpeechTranscription transcription : results_s.getSpeechTranscriptionsList()) {
                    System.out.println("Transcript: " + transcription.getAlternatives(0).getTranscript());
            }


            // Get only the first annotation for demo purposes.
//            TextAnnotation annotation = results.getTextAnnotations(0);
//            System.out.println("Text: " + annotation.getText());

            // Get the first text segment.
//            TextSegment textSegment = annotation.getSegments(0);
//            System.out.println("Confidence: " + textSegment.getConfidence());

            // For the text segment display its time offset.
//            VideoSegment videoSegment = textSegment.getSegment();
//            Duration startTimeOffset = videoSegment.getStartTimeOffset();
//            Duration endTimeOffset = videoSegment.getEndTimeOffset();
//            // Display the offset time in seconds, 1e9 is part of the formula to convert nanos to seconds.
//            System.out.println(
//                String.format(
//                    "Start time: %.2f", startTimeOffset.getSeconds() + startTimeOffset.getNanos() / 1e9
//                )
//            );
//            System.out.println(
//                String.format(
//                    "End time: %.2f", endTimeOffset.getSeconds() + endTimeOffset.getNanos() / 1e9
//                )
//            );

            // Show the result for the first frame in the segment.
//            TextFrame textFrame = textSegment.getFrames(0);
//            Duration timeOffset = textFrame.getTimeOffset();
//            System.out.println(
//                String.format(
//                    "Time offset for the first frame: %.2f",
//                    timeOffset.getSeconds() + timeOffset.getNanos() / 1e9
//                )
//            );

            // Display the rotated bounding box for where the text if on the frame.
//            System.out.println("Rotated Bounding Box Vertices: ");
//            List<NormalizedVertex> vertices = textFrame.getRotatedBoundingBox().getVerticesList();
//            for (NormalizedVertex normalizedVertex: vertices) {
//                System.out.println(
//                    String.format(
//                        "\tVertex.x: %.2f, Vertex.y: %.2f",
//                        normalizedVertex.getX(), normalizedVertex.getY()
//                    )
//                );
//            }
            return results;
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean isValid = false;
        int userChoice = 0;
        VideoAnnotationResults videoAnnotationResults = null;



        while (!isValid) {
            System.out.print("Do you want to recognize text " +
                "for a video on Cloud Storage or from a local file?\n" +
                "1. Cloud Storage\n" +
                "2. Local File\n");

            if (sc.hasNextInt()) {
                userChoice = sc.nextInt();
                sc.nextLine(); // Consume the newline character left by nextInt()

                if (userChoice == 1 || userChoice == 2) {
                    isValid = true; // Valid input received, exit the loop.
                } else {
                    System.out.println("Invalid input. Please enter 1 or 2 based on your choice.");
                }
            } else {
                System.out.println("Invalid input. Please enter 1 or 2 based on your choice.");
                sc.next(); // Consume the invalid input to avoid the infinite loop.
            }
        }

        if (userChoice == 1) {
            System.out.println("Please enter the path to the video file to analyze: ");
            videoAnnotationResults = detectTextGcs(sc.nextLine());
        } else if (userChoice == 2) {
            System.out.println("Please enter the path to the video file to analyze: ");
            videoAnnotationResults = detectText(sc.nextLine());
        }
    }
}