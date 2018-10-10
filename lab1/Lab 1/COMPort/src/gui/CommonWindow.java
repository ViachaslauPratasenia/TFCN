package gui;

import serial.SerialPortController;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CommonWindow {
    public static final double MIN_WIDTH = 420;
    public static final double MIN_HEIGHT = 240;
    public static final String XON_IS_ON = "Sending data is allowed";
    public static final String XOFF_IS_ON = "Sending data is restricted";
    public static final String NOT_SUPPORTED = "XON/XOFF flow control is not supported";

    private Scene scene;

    private TextArea inputArea;
    private TextArea outputArea;
    private Text xonXoffTF;
    private Text receivedTF;
    private Text sentTF;

    public CommonWindow(SerialPortController serialPortController) {
        inputArea = new TextArea();
        outputArea = new TextArea();
        receivedTF = new Text();
        sentTF = new Text();
        xonXoffTF = new Text();

        final Separator firstSeparator = new Separator();
        firstSeparator.setPrefWidth(MIN_WIDTH);
        firstSeparator.setValignment(VPos.CENTER);

        final Separator secondSeparator = new Separator();
        secondSeparator.setPrefWidth(MIN_WIDTH);
        secondSeparator.setValignment(VPos.CENTER);

        InputWindow inputWindow = new InputWindow(serialPortController, inputArea, sentTF);
        OutputWindow outputWindow = new OutputWindow(serialPortController, outputArea);
        DebugWindow debugWindow = new DebugWindow(serialPortController, xonXoffTF, receivedTF, sentTF);

        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(5));
        mainLayout.setSpacing(5);

        mainLayout.getChildren().add(inputWindow.getLayout());
        mainLayout.getChildren().add(firstSeparator);
        mainLayout.getChildren().add(outputWindow.getLayout());
        mainLayout.getChildren().add(secondSeparator);
        mainLayout.getChildren().add(debugWindow.getLayout());

        scene = new Scene(mainLayout, MIN_WIDTH,  MIN_HEIGHT * 3);
    }

    public TextArea getOutputArea() {
        return this.outputArea;
    }

    public Text getBytesReceivedField() {
        return this.receivedTF;
    }

    public Text getXonXoffTF() {
        return this.xonXoffTF;
    }

    public Scene getScene() {
        return this.scene;
    }
}
