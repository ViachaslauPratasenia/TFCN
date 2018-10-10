package gui;

import serial.SerialPortController;
import helper.FontHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class OutputWindow {
    private static final double PADDING = 2;
    private SerialPortController serialPortController;

    private VBox outputLayout;
    private TextArea outputArea;

    public OutputWindow(SerialPortController serialPortController, TextArea outputArea) {
        this.outputArea = outputArea;
        this.serialPortController = serialPortController;
        this.initLayout();
    }

    public VBox getLayout() {
        return this.outputLayout;
    }

    private void initLayout() {
        outputLayout = new VBox();
        outputLayout.setPadding(new Insets(PADDING));
        outputLayout.setSpacing(PADDING);
        outputLayout.setMinSize(CommonWindow.MIN_WIDTH, CommonWindow.MIN_HEIGHT);
        outputLayout.setFillWidth(true);

        Label outputLabel = new Label("Output Area");
        outputLabel.setFont(FontHelper.FONT);
        outputLayout.setAlignment(Pos.CENTER);
        outputLayout.getChildren().add(outputLabel);
        outputLayout.getChildren().add(outputArea);
    }

}
