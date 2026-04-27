package com.asciiart.core;

import java.awt.image.BufferedImage;

public class ImageProcessor {

    public static BufferedImage resize(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g2d = resized.createGraphics();

        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();

        return resized;
    }

    public static int getGrayscale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }
}