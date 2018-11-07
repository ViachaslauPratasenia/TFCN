package gui;

import helper.Constants;
import javafx.scene.control.TextField;
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
    private TextArea packageArea;
    private Text xonXoffTF;
    private Text receivedTF;
    private Text sentTF;
    private TextField sourceAddressTF;
    private TextField destinationAddressTF;

    public CommonWindow(SerialPortDAO serialPortDAO) {
        inputArea = new TextArea();
        outputArea = new TextArea();
        packageArea = new TextArea();
        receivedTF = new Text();
        sentTF = new Text();
        xonXoffTF = new Text();
        sourceAddressTF = new TextField();
        destinationAddressTF = new TextField();

        final Separator firstSeparator = new Separator();
        firstSeparator.setPrefWidth(MIN_WIDTH);
        firstSeparator.setValignment(VPos.CENTER);

        final Separator secondSeparator = new Separator();
        secondSeparator.setPrefWidth(MIN_WIDTH);
        secondSeparator.setValignment(VPos.CENTER);

        final Separator thirdSeparator = new Separator();
        thirdSeparator.setPrefWidth(MIN_WIDTH);
        thirdSeparator.setValignment(VPos.CENTER);

        InputWindow inputWindow = new InputWindow(serialPortDAO, inputArea, sentTF, packageArea, destinationAddressTF);
        OutputWindow outputWindow = new OutputWindow(serialPortDAO, outputArea);
        DebugWindow debugWindow = new DebugWindow(serialPortDAO, xonXoffTF, receivedTF, sentTF, sourceAddressTF,
                destinationAddressTF);
        PackageWindow packageWindow = new PackageWindow(serialPortDAO, packageArea);

        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(Constants.PADDING));
        mainLayout.setSpacing(Constants.PADDING);

        mainLayout.getChildren().add(inputWindow.getLayout());
        mainLayout.getChildren().add(firstSeparator);
        mainLayout.getChildren().add(outputWindow.getLayout());
        mainLayout.getChildren().add(secondSeparator);
        mainLayout.getChildren().add(debugWindow.getLayout());
        mainLayout.getChildren().add(thirdSeparator);
        mainLayout.getChildren().add(packageWindow.getLayout());

        scene = new Scene(mainLayout, MIN_WIDTH,  MIN_HEIGHT * 5);
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
