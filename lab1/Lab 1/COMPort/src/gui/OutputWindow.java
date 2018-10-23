package gui;

import serial.SerialPortDAO;
import helper.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class OutputWindow {
    private static final double PADDING = 2;
    private SerialPortDAO serialPortDAO;

    private VBox outputLayout;
    private TextArea outputArea;

    public OutputWindow(SerialPortDAO serialPortDAO, TextArea outputArea) {
        this.outputArea = outputArea;
        this.serialPortDAO = serialPortDAO;
        this.initLayout();
    }

    public VBox getLayout() {
        return this.outputLayout;
    }

    private void initLayout() {
        outputLayout = new VBox();
        outputLayout.setPadding(new Insets(PADDING));
        outputLayout.setSpacing(PADDING);
        outputLayout.setMinSize(Constants.MIN_WIDTH, Constants.MIN_HEIGHT);
        outputLayout.setFillWidth(true);

        Label outputLabel = new Label("Output Area");
        outputLabel.setFont(Constants.FONT);
        outputArea.setEditable(false);
        outputLayout.setAlignment(Pos.CENTER);
        outputLayout.getChildren().add(outputLabel);
        outputLayout.getChildren().add(outputArea);
    }

}
