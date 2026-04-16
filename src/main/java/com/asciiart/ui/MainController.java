package com.asciiart.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import com.asciiart.core.AsciiConverter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainController {

    @FXML
    private TextArea outputArea;

    @FXML
    private void handleTest() {
        try {
            BufferedImage img = ImageIO.read(new File("test.jpg")); // put image in project root

            AsciiConverter converter = new AsciiConverter();
            String ascii = converter.convertToAscii(img, 100);

            outputArea.setText(ascii);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}