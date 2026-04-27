package com.asciiart.ui;

import com.asciiart.core.AsciiConverter;
import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.TextField;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainController {

    @FXML private TextArea outputArea;
    @FXML private Slider resolutionSlider;
    @FXML private Slider contrastSlider;
    @FXML private Slider brightnessSlider;
    @FXML private CheckBox invertCheckbox;
    @FXML private ImageView previewImageView;

    @FXML private TextField resolutionField;
    @FXML private TextField contrastField;
    @FXML private TextField brightnessField;

    private final AsciiConverter converter = new AsciiConverter();

    private File currentFile;
    private BufferedImage currentImage;

    private final PauseTransition delay = new PauseTransition(Duration.millis(200));

    @FXML
    private void initialize() {
        setupDragAndDrop();
        setupLivePreview();
        bindSliderWithField();
    }

    private void bindSliderWithField() {

        bind(resolutionSlider, resolutionField, 0);
        bind(contrastSlider, contrastField, 2);
        bind(brightnessSlider, brightnessField, 2);
    }

    private void bind(Slider slider, TextField field, int decimals) {

        // Slider → Text
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            field.setText(String.format("%." + decimals + "f", newVal.doubleValue()));
        });

        // Initial value
        field.setText(String.format("%." + decimals + "f", slider.getValue()));

        // Text → Slider
        field.setOnAction(e -> updateSliderFromField(slider, field));
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) updateSliderFromField(slider, field);
        });
    }

    private void updateSliderFromField(Slider slider, TextField field) {
        try {
            double value = Double.parseDouble(field.getText());
            slider.setValue(value);
        } catch (Exception ignored) {
            field.setText(String.valueOf(slider.getValue()));
        }
    }

    @FXML
    private void handleReset() {

        resolutionSlider.setValue(150);
        contrastSlider.setValue(0.8);
        brightnessSlider.setValue(0);
        invertCheckbox.setSelected(false);

        triggerUpdate(); // refresh preview
    }

    @FXML
    private void resetResolution() {
        resolutionSlider.setValue(150);
        triggerUpdate();
    }

    @FXML
    private void resetContrast() {
        contrastSlider.setValue(0.8);
        triggerUpdate();
    }

    @FXML
    private void resetBrightness() {
        brightnessSlider.setValue(0);
        triggerUpdate();
    }

    // =========================
    // Upload
    // =========================
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

    private void processImage(File file) {
        try {
            currentFile = file;
            currentImage = ImageIO.read(file);

            updateAscii();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // Drag & Drop
    // =========================
    private void setupDragAndDrop() {

        outputArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
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

    // =========================
    // Live Preview Setup
    // =========================
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

    // =========================
    // ASCII + Image Update
    // =========================
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

        updatePreviewImage();
    }

    // =========================
    // Image Preview (processed)
    // =========================
    private void updatePreviewImage() {
        if (currentImage == null) return;

        double contrast = contrastSlider.getValue();
        double brightness = brightnessSlider.getValue();
        boolean invert = invertCheckbox.isSelected();

        BufferedImage processed = processImagePreview(
                currentImage,
                contrast,
                brightness,
                invert
        );

        previewImageView.setImage(SwingFXUtils.toFXImage(processed, null));
    }

    private BufferedImage processImagePreview(
            BufferedImage original,
            double contrast,
            double brightness,
            boolean invert
    ) {

        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb = original.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                double rn = r / 255.0;
                double gn = g / 255.0;
                double bn = b / 255.0;

                // Contrast
                rn = Math.pow(rn, contrast);
                gn = Math.pow(gn, contrast);
                bn = Math.pow(bn, contrast);

                // Brightness
                rn += brightness;
                gn += brightness;
                bn += brightness;

                // Invert
                if (invert) {
                    rn = 1 - rn;
                    gn = 1 - gn;
                    bn = 1 - bn;
                }

                // Clamp
                rn = Math.max(0, Math.min(1, rn));
                gn = Math.max(0, Math.min(1, gn));
                bn = Math.max(0, Math.min(1, bn));

                int newR = (int) (rn * 255);
                int newG = (int) (gn * 255);
                int newB = (int) (bn * 255);

                int newRGB = (newR << 16) | (newG << 8) | newB;
                output.setRGB(x, y, newRGB);
            }
        }

        return output;
    }

    // =========================
    // Save ASCII as Image
    // =========================
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

    // =========================
    // ASCII → Image Renderer
    // =========================
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