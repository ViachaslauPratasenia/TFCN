package gui;

import serial.SerialPortController;
import helper.*;
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class InputWindow {
    private static final double PADDING = 5;

    private SerialPortController serialPortController;

    private VBox inputLayout;
    private TextArea inputArea;
    private Text outputBytes;

    public InputWindow(SerialPortController serialPortController, TextArea inputArea, Text outputBytes) {
        this.inputArea = inputArea;
        this.outputBytes = outputBytes;
        this.serialPortController = serialPortController;
        this.initLayout();
    }

    public VBox getLayout() {
        return this.inputLayout;
    }

    private void initLayout() {
        inputLayout = new VBox();
        inputLayout.setPadding(new Insets(PADDING));
        inputLayout.setSpacing(PADDING);
        inputLayout.setMinSize(CommonWindow.MIN_WIDTH, CommonWindow.MIN_HEIGHT);
        inputLayout.setFillWidth(true);

        Label inputLabel = new Label("Input Area");
        inputLabel.setFont(FontHelper.FONT);
        inputLayout.setAlignment(Pos.CENTER);
        inputLayout.getChildren().add(inputLabel);

        inputArea.setWrapText(true);
        inputLayout.getChildren().add(inputArea);

        Button sendButton = new Button("Send");
        sendButton.setFont(FontHelper.FONT);
        sendButton.setPrefWidth(CommonWindow.MIN_WIDTH);
        sendButton.setOnAction(event -> send());
        inputLayout.getChildren().add(sendButton);
    }

    private void send() {
        try {
            this.serialPortController.write(inputArea.getText().getBytes());
            outputBytes.setText(Integer.toString(this.serialPortController.getSent()));
            inputArea.clear();
        }
        catch (Exception exception) {
            ExceptionWarningHelper.showException(exception);
        }
    }
}
