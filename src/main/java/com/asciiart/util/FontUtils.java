package com.asciiart.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FontUtils {

    public static double getCharAspectRatio() {
        BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        var g = temp.createGraphics();

        Font font = new Font("Consolas", Font.PLAIN, 10);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics();

        int charWidth = metrics.charWidth('A');
        int charHeight = metrics.getHeight();

        g.dispose();

        return (double) charHeight / charWidth;
    }
}