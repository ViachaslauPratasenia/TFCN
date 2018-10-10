package gui;

import helper.Constants;
import serial.SerialPortDAO;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static helper.Constants.*;

public class CommonWindow {

    private Scene scene;

    private TextArea inputArea;
    private TextArea outputArea;
    private Text xonXoffTF;
    private Text receivedTF;
    private Text sentTF;

    public CommonWindow(SerialPortDAO serialPortDAO) {
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

        InputWindow inputWindow = new InputWindow(serialPortDAO, inputArea, sentTF);
        OutputWindow outputWindow = new OutputWindow(serialPortDAO, outputArea);
        DebugWindow debugWindow = new DebugWindow(serialPortDAO, xonXoffTF, receivedTF, sentTF);

        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(Constants.PADDING));
        mainLayout.setSpacing(Constants.PADDING);

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
