package gui;

import convert.Converter;
import javafx.scene.control.*;
import serial.SerialPortDAO;
import helper.*;
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;


public class InputWindow {
    private SerialPortDAO serialPortDAO;

    private VBox inputLayout;
    private TextArea inputArea;
    private Text outputBytes;
    private TextArea packageArea;
    private TextField destination;

    public InputWindow(SerialPortDAO serialPortDAO, TextArea inputArea, Text outputBytes, TextArea packageArea,
                       TextField destination) {
        this.inputArea = inputArea;
        this.outputBytes = outputBytes;
        this.serialPortDAO = serialPortDAO;
        this.packageArea = packageArea;
        this.destination = destination;
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
            if(this.serialPortDAO.getXoffState() && this.serialPortDAO.getXonXoffFlowControlMode()) {
                return;
            }

            ArrayList<byte[]> messages = serialPortDAO.composePackages(
                    inputArea.getText().getBytes(), (byte)Integer.parseInt(destination.getText())
            );

            this.packageArea.clear();

            for(byte[] message : messages) {
                this.packageArea.appendText(Converter.bytesToHex(message));
                this.packageArea.appendText("\n\n");
            }

            this.serialPortDAO.write(messages);

            outputBytes.setText(Integer.toString(this.serialPortDAO.getSent()));

            inputArea.clear();
        }
        catch (Exception exception) {
            ExceptionInformationHelper.showMessage("Port is not opened");
        }
    }
}
