package gui;

import serial.SerialPortController;
import helper.FontHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import jssc.SerialPort;
import helper.ExceptionWarningHelper;

public class DebugWindow {
    private static final double GAP = 2;
    private static final double PADDING = 2;

    private SerialPortController serialPortController;
    private VBox debugLayout;

    private Text xonXoffTF;
    private Text receivedTF;
    private Text sentTF;

    private ComboBox<String> portList;
    private CheckBox xonXoffCheckBox;

    private Button openPortButton;
    private Button closePortButton;
    private Button sendXonButton;
    private Button sendXoffButton;

    private String portName = null;
    private boolean flowControlEnabled = false;
    private boolean portIsOpened = false;

    public DebugWindow(SerialPortController serialPortController, Text xonXoffTF, Text receivedTF,
                       Text sentTF) {
        this.serialPortController = serialPortController;
        this.receivedTF = receivedTF;
        this.xonXoffTF = xonXoffTF;
        this.sentTF = sentTF;
        this.initLayout();
    }

    public VBox getLayout() {
        return this.debugLayout;
    }

    private void initLayout() {
        debugLayout = new VBox();
        debugLayout.setPadding(new Insets(PADDING));
        debugLayout.setSpacing(PADDING);
        debugLayout.setFillWidth(true);
        debugLayout.setMinSize(CommonWindow.MIN_WIDTH, CommonWindow.MIN_HEIGHT);

        Label debugLabel = new Label("Debug Area");
        debugLabel.setFont(FontHelper.FONT);
        debugLayout.setAlignment(Pos.TOP_CENTER);
        debugLayout.getChildren().add(debugLabel);

        ScrollPane scrollPane = new ScrollPane();
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        GridPane debugGridPane = new GridPane();
        debugGridPane.setPadding(new Insets(0));
        debugGridPane.setHgap(GAP);
        debugGridPane.setVgap(GAP);

        HBox debugBox = new HBox();
        ObservableList<String> items = FXCollections.observableArrayList (
                SerialPortController.getPortNames());
        portList = new ComboBox<>(items);
        portList.setPromptText("Ports");
        portList.valueProperty().
                addListener((observableValue, previous, current) ->
                        portName = current);
        portList.setPrefSize(400,20);
        debugBox.getChildren().add(portList);

        Label flowControlLabel = new Label("Enable XON/XOFF control ");
        debugGridPane.add(flowControlLabel, 0, 0);
        xonXoffCheckBox = new CheckBox();
        xonXoffCheckBox.setSelected(false);
        xonXoffCheckBox.selectedProperty().
                addListener((observableValue, previous, current) ->
                       flowControlEnabled = current);
        debugGridPane.add(xonXoffCheckBox, 1, 0);

        openPortButton = new Button("Open");
        openPortButton.setPrefWidth((CommonWindow.MIN_WIDTH - PADDING) / 2);
        openPortButton.setOnAction(event -> onOpenPort());
        debugBox.getChildren().add(openPortButton);

        closePortButton = new Button("Close");
        closePortButton.setPrefWidth((CommonWindow.MIN_WIDTH - PADDING) / 2);
        closePortButton.setOnAction(event -> onClosePort());
        closePortButton.setDisable(this.portIsOpened);
        debugBox.getChildren().add(closePortButton);

        sendXoffButton = new Button("Send XOFF");
        sendXoffButton.setPrefWidth((CommonWindow.MIN_WIDTH - PADDING) / 2);
        sendXoffButton.setOnAction(event -> sendXoff());
        debugGridPane.add(sendXoffButton, 0, 2);

        sendXonButton = new Button("Send XON");
        sendXonButton.setPrefWidth((CommonWindow.MIN_WIDTH - PADDING) / 2);
        sendXonButton.setOnAction(event -> sendXon());
        debugGridPane.add(sendXonButton, 1, 2);

        Label bytesReceivedLabel = new Label("Received: ");
        debugGridPane.add(bytesReceivedLabel, 0, 3);
        receivedTF.setText("0");
        debugGridPane.add(receivedTF, 1, 3);

        Label bytesSentLabel = new Label("Sent: ");
        debugGridPane.add(bytesSentLabel, 0, 4);
        sentTF.setText("0");
        debugGridPane.add(sentTF, 1, 4);

        debugLayout.getChildren().add(debugBox);
        debugLayout.getChildren().add(debugGridPane);
        debugLayout.getChildren().add(xonXoffTF);
    }

    private void sendXoff() {
        try {
            this.serialPortController.sendXoff();
        } catch(Exception exception) {
            ExceptionWarningHelper.showException(exception);
        }
    }

    private void sendXon() {
        try {
            this.serialPortController.sendXon();
        } catch(Exception exception) {
            ExceptionWarningHelper.showException(exception);
        }
    }

    private void onOpenPort() {
        try {
            SerialPort serialPort = new SerialPort(portName);
            this.serialPortController.setSerialPort(serialPort, flowControlEnabled);
            this.portIsOpened = !this.portIsOpened;
            openPortButton.setDisable(this.portIsOpened);
            closePortButton.setDisable(!this.portIsOpened);
            sendXonButton.setDisable(!this.flowControlEnabled);
            sendXoffButton.setDisable(!this.flowControlEnabled);
            portList.setDisable(this.portIsOpened);
            xonXoffCheckBox.setDisable(this.portIsOpened);

            if(this.flowControlEnabled) {
                xonXoffTF.setText(CommonWindow.XOFF_IS_ON);
                xonXoffTF.setFont(FontHelper.FONT);
                xonXoffTF.setUnderline(true);
                xonXoffTF.setFill(Color.RED);
            } else {
                xonXoffTF.setText(CommonWindow.NOT_SUPPORTED);
                xonXoffTF.setFont(FontHelper.FONT);
                xonXoffTF.setUnderline(true);
                xonXoffTF.setFill(Color.GREY);
            }
        }
        catch (Exception exception) {
            ExceptionWarningHelper.showException(exception);
        }
    }

    private void onClosePort() {
        try {
            this.serialPortController.closeSerialPort();
            this.portIsOpened = !this.portIsOpened;

            receivedTF.setText("0");
            sentTF.setText("0");
            xonXoffTF.setText("");

            openPortButton.setDisable(this.portIsOpened);
            closePortButton.setDisable(!this.portIsOpened);
            sendXoffButton.setDisable(!this.portIsOpened);
            sendXonButton.setDisable(!this.portIsOpened);
            portList.setDisable(this.portIsOpened);
            xonXoffCheckBox.setDisable(this.portIsOpened);
        }
        catch (Exception exception) {
            ExceptionWarningHelper.showException(exception);
        }
    }
}
