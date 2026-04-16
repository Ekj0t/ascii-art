package com.asciiart.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MainController {

    @FXML
    private TextArea outputArea;

    @FXML
    private void handleTest() {
        outputArea.setText("JavaFX is working");
    }
}