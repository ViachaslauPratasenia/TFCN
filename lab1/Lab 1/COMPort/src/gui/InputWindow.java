package gui;

import serial.SerialPortDAO;
import helper.*;
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class InputWindow {
    private SerialPortDAO serialPortDAO;

    private VBox inputLayout;
    private TextArea inputArea;
    private Text outputBytes;

    public InputWindow(SerialPortDAO serialPortDAO, TextArea inputArea, Text outputBytes) {
        this.inputArea = inputArea;
        this.outputBytes = outputBytes;
        this.serialPortDAO = serialPortDAO;
        this.initLayout();
    }

    public VBox getLayout() {
        return this.inputLayout;
    }

    private void initLayout() {
        inputLayout = new VBox();
        inputLayout.setPadding(new Insets(Constants.PADDING));
        inputLayout.setSpacing(Constants.PADDING);
        inputLayout.setMinSize(Constants.MIN_WIDTH, Constants.MIN_HEIGHT);
        inputLayout.setFillWidth(true);

        Label inputLabel = new Label("Input Area");
        inputLabel.setFont(Constants.FONT);
        inputLayout.setAlignment(Pos.CENTER);
        inputLayout.getChildren().add(inputLabel);

        inputArea.setWrapText(true);
        inputLayout.getChildren().add(inputArea);

        Button sendButton = new Button("Send");
        sendButton.setFont(Constants.FONT);
        sendButton.setPrefWidth(Constants.MIN_WIDTH);
        sendButton.setOnAction(event -> send());
        inputLayout.getChildren().add(sendButton);
    }

    private void send() {
        try {
            this.serialPortDAO.write(inputArea.getText().getBytes());
            outputBytes.setText(Integer.toString(this.serialPortDAO.getSent()));
            inputArea.clear();
        }
        catch (Exception exception) {
            ExceptionInformationHelper.showException(exception);
        }
    }
}
