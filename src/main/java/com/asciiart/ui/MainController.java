package com.asciiart.ui;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;
import com.asciiart.core.AsciiConverter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainController {

    @FXML
    private TextArea outputArea;

    private final AsciiConverter converter = new AsciiConverter();

    @FXML private Slider resolutionSlider;
    @FXML private Slider contrastSlider;
    @FXML private Slider brightnessSlider;
    @FXML private CheckBox invertCheckbox;

    private PauseTransition delay = new PauseTransition(Duration.millis(200));

    private File currentFile;
    private BufferedImage currentImage;

    @FXML
    private void initialize() {
        setupDragAndDrop();
        setupLivePreview();
    }

    private void setupLivePreview() {

        resolutionSlider.valueProperty().addListener((obs, o, n) -> triggerUpdate());
        contrastSlider.valueProperty().addListener((obs, o, n) -> triggerUpdate());
        brightnessSlider.valueProperty().addListener((obs, o, n) -> triggerUpdate());
        invertCheckbox.selectedProperty().addListener((obs, o, n) -> triggerUpdate());
    }

    private void triggerUpdate() {
        delay.setOnFinished(e -> updateAscii());
        delay.playFromStart();
    }

    private void updateAscii() {
        if (currentImage == null) return;

        int width = (int) resolutionSlider.getValue();
        double contrast = contrastSlider.getValue();
        double brightness = brightnessSlider.getValue();
        boolean invert = invertCheckbox.isSelected();

        String ascii = converter.convertToAscii(
                currentImage,
                width,
                contrast,
                brightness,
                invert
        );

        outputArea.setText(ascii);
    }

    @FXML
    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            processImage(file);
        }
    }

    private void setupDragAndDrop() {
        outputArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            }
            event.consume();
        });

        outputArea.setOnDragDropped(event -> {
            var files = event.getDragboard().getFiles();
            if (!files.isEmpty()) {
                processImage(files.get(0));
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void processImage(File file) {
        try {
            currentFile = file;
            currentImage = ImageIO.read(file);

            updateAscii();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (currentFile == null) return;

            BufferedImage asciiImage = asciiToImage(outputArea.getText());

            FileChooser fileChooser = new FileChooser();

            String name = currentFile.getName();
            String base = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;

            fileChooser.setInitialFileName(base + "_ascii.jpg");
            fileChooser.setInitialDirectory(currentFile.getParentFile());

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JPG Image", "*.jpg")
            );

            File saveFile = fileChooser.showSaveDialog(new Stage());

            if (saveFile != null) {
                ImageIO.write(asciiImage, "jpg", saveFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 FIXED rendering (FontMetrics based)
    private BufferedImage asciiToImage(String ascii) {

        String[] lines = ascii.split("\n");

        BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = temp.createGraphics();

        Font font = new Font("Consolas", Font.PLAIN, 10);
        g2d.setFont(font);

        FontMetrics metrics = g2d.getFontMetrics();

        int charWidth = metrics.charWidth('A');
        int charHeight = metrics.getHeight();

        int width = lines[0].length() * charWidth;
        int height = lines.length * charHeight;

        g2d.dispose();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.setFont(font);

        int y = metrics.getAscent();

        for (String line : lines) {
            g.drawString(line, 0, y);
            y += charHeight;
        }

        g.dispose();
        return img;
    }
}