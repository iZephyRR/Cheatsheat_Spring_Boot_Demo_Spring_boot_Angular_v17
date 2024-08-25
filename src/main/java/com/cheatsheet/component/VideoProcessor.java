package com.cheatsheet.component;

import java.io.File;
import java.io.IOException;

public class VideoProcessor {
    public File reduceVideoQuality(File videoFile, String outputFilePath, int width, int height, int bitrate) throws IOException {
        // Construct the FFmpeg command to reduce video quality
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", videoFile.getAbsolutePath(),
                "-vf", "scale=" + width + ":" + height,
                "-b:v", bitrate + "k",
                "-c:a", "copy", // Keep the original audio quality
                outputFilePath
        );

        processBuilder.inheritIO();
        Process process = processBuilder.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg process was interrupted", e);
        }

        return new File(outputFilePath);
    }
}
