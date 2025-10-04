package com.bis;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ImageUtil {

    public static CompletableFuture<BufferedImage> loadImageAsync(String imageUrl) {
        return CompletableFuture.supplyAsync(() -> {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return null;
            }
            try {
                log.info("Loading image from: {}", imageUrl);
                return ImageIO.read(new URL(imageUrl));
            } catch (IOException e) {
                log.warn("Failed to load image from url: {}", imageUrl, e);
                return null;
            }
        });
    }
}
