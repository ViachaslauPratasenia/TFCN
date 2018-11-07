package gui;

import serial.SerialPortDAO;
import helper.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import jssc.SerialPort;
import helper.ExceptionInformationHelper;

public class DebugWindow {

    private SerialPortDAO serialPortDAO;
    private VBox debugLayout;

    private Text xonXoffTF;
    private Text receivedTF;
    private Text sentTF;
    private TextField sourceAddressTF;
    private TextField destinationAddressTF;

    public static ComboBox<String> portList;
    private CheckBox xonXoffCheckBox;

    private Button openPortButton;
    private Button closePortButton;
    private Button sendXonButton;
    private Button sendXoffButton;

    private String portName = null;
    private boolean flowControlEnabled = false;
    private boolean portIsOpened = false;

    private byte destination = -1;
    private byte address = -1;

    public DebugWindow(SerialPortDAO serialPortDAO, Text xonXoffTF, Text receivedTF,
                       Text sentTF, TextField sourceAddressTF, TextField destinationAddressTF) {
        this.serialPortDAO = serialPortDAO;
        this.receivedTF = receivedTF;
        this.xonXoffTF = xonXoffTF;
        this.sentTF = sentTF;
        this.sourceAddressTF = sourceAddressTF;
        this.destinationAddressTF = destinationAddressTF;
        this.initLayout();
    }

    public VBox getLayout() {
        return this.debugLayout;
    }

    private void initLayout() {

        debugLayout = new VBox();
        debugLayout.setPadding(new Insets(Constants.PADDING));
        debugLayout.setSpacing(Constants.PADDING);
        debugLayout.setFillWidth(true);
        debugLayout.setMinSize(Constants.MIN_WIDTH, Constants.MIN_HEIGHT + 80);

        Label debugLabel = new Label("Debug Area");
        debugLabel.setFont(Constants.FONT);
        debugLayout.setAlignment(Pos.TOP_CENTER);
        debugLayout.getChildren().add(debugLabel);

        ScrollPane scrollPane = new ScrollPane();
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        GridPane debugGP = new GridPane();
        debugGP.setPadding(new Insets(0));
        debugGP.setHgap(Constants.GAP);
        debugGP.setVgap(Constants.GAP);

        HBox portBox = new HBox();
        ObservableList<String> items = FXCollections.observableArrayList (
                SerialPortDAO.getPortNames());
        portList = new ComboBox<>(items);
        portList.setPromptText("COM ports");
        portList.valueProperty().addListener((observableValue, previous, current) -> portName = current);
        portList.setPrefSize(400,20);
        portBox.getChildren().add(portList);

        Label flowControlLabel = new Label("XON/XOFF control ");
        debugGP.add(flowControlLabel, 0, 0);
        xonXoffCheckBox = new CheckBox();
        xonXoffCheckBox.setSelected(false);
        xonXoffCheckBox.selectedProperty().
                addListener((observableValue, previous, current) -> flowControlEnabled = current);
        debugGP.add(xonXoffCheckBox, 1, 0);

        openPortButton = new Button("Open");
        openPortButton.setPrefWidth((Constants.MIN_WIDTH - Constants.PADDING) / 2);
        openPortButton.setOnAction(event -> open());
        portBox.getChildren().add(openPortButton);

        closePortButton = new Button("Close");
        closePortButton.setPrefWidth((Constants.MIN_WIDTH - Constants.PADDING) / 2);
        closePortButton.setOnAction(event -> close());
        closePortButton.setDisable(this.portIsOpened);
        portBox.getChildren().add(closePortButton);

        sendXonButton = new Button("Send XON");
        sendXonButton.setPrefWidth((Constants.MIN_WIDTH - Constants.PADDING) / 2);
        sendXonButton.setOnAction(event -> sendXon());
        debugGP.add(sendXonButton, 0, 2);


        sendXoffButton = new Button("Send XOFF");
        sendXoffButton.setPrefWidth((Constants.MIN_WIDTH - Constants.PADDING) / 2);
        sendXoffButton.setOnAction(event -> sendXoff());
        debugGP.add(sendXoffButton, 1, 2);

        Label bytesReceivedLabel = new Label("Received: ");
        debugGP.add(bytesReceivedLabel, 0, 3);
        receivedTF.setText("0");
        debugGP.add(receivedTF, 1, 3);

        Label bytesSentLabel = new Label("Sent: ");
        debugGP.add(bytesSentLabel, 0, 4);
        sentTF.setText("0");
        debugGP.add(sentTF, 1, 4);

        Label sourceAddressLabel = new Label("Source address");
        debugGP.add(sourceAddressLabel, 0,5);
        debugGP.add(sourceAddressTF, 1,5);

        Label destinationAddressLabel = new Label("Destination address");
        debugGP.add(destinationAddressLabel, 0,6);
        debugGP.add(destinationAddressTF, 1,6);

        debugLayout.getChildren().add(portBox);
        debugLayout.getChildren().add(debugGP);
        debugLayout.getChildren().add(xonXoffTF);
    }

    private void sendXoff() {
        try {
            this.serialPortDAO.sendXoff();
        } catch(Exception exception) {
            ExceptionInformationHelper.showMessage("Port is not opened");
        }
    }

    private void sendXon() {
        try {
            this.serialPortDAO.sendXon();
        } catch(Exception exception) {
            ExceptionInformationHelper.showMessage("Port is not opened");
        }
    }

    private void open() {
        try {
            try {
                address = Byte.parseByte(sourceAddressTF.getText());
                destination = Byte.parseByte(destinationAddressTF.getText());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Source/destination address must be less than 255");
            }
            if(address == -1 || destination == -1) {
                throw new IllegalArgumentException("You must set source/destination address.");
            }
            if(address == destination) {
                throw new IllegalArgumentException("Source and destination address can't be equal!");
            }
            SerialPort serialPort = new SerialPort(portName);
            this.serialPortDAO.setSerialPort(serialPort, flowControlEnabled);
            this.portIsOpened = !this.portIsOpened;
            sendXonButton.setDisable(!this.flowControlEnabled);
            sendXoffButton.setDisable(!this.flowControlEnabled);
            portList.setDisable(this.portIsOpened);
            xonXoffCheckBox.setDisable(this.portIsOpened);
            openPortButton.setDisable(this.portIsOpened);
            closePortButton.setDisable(!this.portIsOpened);

            if(this.flowControlEnabled) {
                xonXoffTF.setText(Constants.XOFF_IS_ON);
                xonXoffTF.setFont(Constants.FONT);
                xonXoffTF.setUnderline(true);
                xonXoffTF.setFill(Color.DARKRED);
            } else {
                xonXoffTF.setText(Constants.NOT_SUPPORTED);
                xonXoffTF.setFont(Constants.FONT);
                xonXoffTF.setUnderline(true);
                xonXoffTF.setFill(Color.GREY);
            }
        }
        catch (Exception exception) {
            ExceptionInformationHelper.showException(exception);
        }
    }

    private void close() {
        try {
            this.serialPortDAO.closePort();
            this.portIsOpened = !this.portIsOpened;

            receivedTF.setText("0");
            sentTF.setText("0");
            xonXoffTF.setText(null);

            sendXoffButton.setDisable(!this.portIsOpened);
            sendXonButton.setDisable(!this.portIsOpened);
            portList.setDisable(this.portIsOpened);
            xonXoffCheckBox.setDisable(this.portIsOpened);
            openPortButton.setDisable(this.portIsOpened);
            closePortButton.setDisable(!this.portIsOpened);
        }
        catch (Exception exception) {
            ExceptionInformationHelper.showException(exception);
        }
    }
}
