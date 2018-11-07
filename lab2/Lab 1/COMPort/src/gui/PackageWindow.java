package gui;

import helper.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import serial.SerialPortDAO;

public class PackageWindow {
    private static final double PADDING = 2;
    private SerialPortDAO serialPortDAO;

    private VBox packageLayout;
    private TextArea packageArea;

    public PackageWindow(SerialPortDAO serialPortDAO, TextArea packageArea) {
        this.packageArea = packageArea;
        this.serialPortDAO = serialPortDAO;
        this.initPackageWindowLayout();
    }

    public VBox getLayout() {
        return this.packageLayout;
    }

    private void initPackageWindowLayout() {
        packageLayout = new VBox();
        packageLayout.setPadding(new Insets(PADDING));
        packageLayout.setSpacing(PADDING);
        packageLayout.setMinSize(Constants.MIN_WIDTH, Constants.MIN_HEIGHT);
        packageLayout.setFillWidth(true);

        Label packageLabel = new Label("Package Area");
        packageLabel.setFont(Constants.FONT);
        packageArea.setEditable(false);
        packageLayout.setAlignment(Pos.CENTER);
        packageLayout.getChildren().add(packageLabel);
        packageLayout.getChildren().add(packageArea);
    }
}
