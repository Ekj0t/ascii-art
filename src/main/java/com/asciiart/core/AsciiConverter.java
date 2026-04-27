package com.asciiart.core;

import com.asciiart.util.FontUtils;

import java.awt.image.BufferedImage;

public class AsciiConverter {

    public String convertToAscii(
            BufferedImage image,
            int width,
            double contrast,
            double brightness,
            boolean invert
    ) {

        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        double aspectRatio = (double) originalHeight / originalWidth;

        double charAspect = FontUtils.getCharAspectRatio();

        int height = (int) (width * aspectRatio * (1.0 / charAspect));

        BufferedImage resized = ImageProcessor.resize(image, width, height);

        StringBuilder ascii = new StringBuilder();

        for (int y = 0; y < resized.getHeight(); y++) {
            for (int x = 0; x < resized.getWidth(); x++) {

                int rgb = resized.getRGB(x, y);
                int gray = ImageProcessor.getGrayscale(rgb);

                char c = mapToChar(gray, contrast, brightness, invert);
                ascii.append(c);
            }
            ascii.append("\n");
        }

        return ascii.toString();
    }

    private char mapToChar(
            int gray,
            double contrast,
            double brightness,
            boolean invert
    ) {

        double normalized = gray / 255.0;

        // Contrast control
        normalized = Math.pow(normalized, contrast);

        // Brightness adjustment
        normalized += brightness;

        // Invert toggle
        if (invert) {
            normalized = 1.0 - normalized;
        }

        // Clamp values (VERY important)
        normalized = Math.max(0, Math.min(1, normalized));

        int index = (int) (normalized * (CharacterMap.DEFAULT.length() - 1));
        return CharacterMap.DEFAULT.charAt(index);
    }
}