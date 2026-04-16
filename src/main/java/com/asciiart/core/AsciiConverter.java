package com.asciiart.core;

import java.awt.image.BufferedImage;

public class AsciiConverter {

    // Dark → Light characters
    private static final String DEFAULT_CHARS = "@#S%?*+;:,. ";

    public String convertToAscii(BufferedImage image, int width) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Maintain aspect ratio (important)
        double aspectRatio = (double) originalHeight / originalWidth;
        int height = (int) (width * aspectRatio * 0.5); // 0.5 fixes character ratio

        BufferedImage resized = resizeImage(image, width, height);

        StringBuilder ascii = new StringBuilder();

        for (int y = 0; y < resized.getHeight(); y++) {
            for (int x = 0; x < resized.getWidth(); x++) {

                int rgb = resized.getRGB(x, y);
                int gray = getGrayscale(rgb);

                char c = mapToChar(gray);
                ascii.append(c);
            }
            ascii.append("\n");
        }

        return ascii.toString();
    }

    private int getGrayscale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        return (r + g + b) / 3;
    }

    private char mapToChar(int gray) {
        int index = (gray * (DEFAULT_CHARS.length() - 1)) / 255;
        return DEFAULT_CHARS.charAt(index);
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        resized.getGraphics().drawImage(original, 0, 0, width, height, null);
        return resized;
    }
}