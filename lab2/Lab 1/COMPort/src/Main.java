import gui.CommonWindow;
import static helper.Constants.*;
import helper.ThreadHelper;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import serial.SerialPortDAO;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import helper.ExceptionInformationHelper;

public class Main extends Application  {
    private SerialPortDAO serialPortDAO;
    private SerialPortEventListener serialPortEventListener;

    private CommonWindow commonWindow;

    private class SerialPortListener implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte[] readBytes = serialPortDAO.read(event.getEventValue());

                    // if the received package was not damaged during transmission
                    // and was meant for this station
                    if(serialPortDAO.getPackageState() == PackageState.GOOD) {
                        if(serialPortDAO.getXonXoffFlowControlMode()) {

                            boolean containsXonXoff = (readBytes[0] == XOFF_CHAR ||
                                    readBytes[0] == XON_CHAR);
                            if(containsXonXoff) {
                                boolean value = readBytes[0] == XOFF_CHAR;
                                serialPortDAO.setXoffState(value);

                                if(value) {
                                    ThreadHelper.runOnThread(() -> {
                                        commonWindow.getXonXoffTF().setText(XOFF_IS_ON);
                                        commonWindow.getXonXoffTF().setFill(Color.RED);
                                    });
                                } else {
                                    ThreadHelper.runOnThread(() -> {
                                        commonWindow.getXonXoffTF().setText(XON_IS_ON);
                                        commonWindow.getXonXoffTF().setFill(Color.DARKRED);
                                    });
                                }
                            }

                            if(!containsXonXoff && event.getEventValue() > 0) {
                                serialPortDAO.sendXoff(serialPortDAO.getSenderAddress());

                                String result = new String(readBytes);
                                ThreadHelper.runOnThread(() -> {
                                    commonWindow.getOutputArea().appendText(result);
                                    commonWindow.getBytesReceivedField().setText(Integer.
                                            toString(serialPortDAO.getReceived()));
                                });

                                serialPortDAO.sendXon(serialPortDAO.getSenderAddress());
                            }
                        } else {
                            ThreadHelper.runOnThread(() -> {
                                commonWindow.getXonXoffTF().setText(NOT_SUPPORTED);
                                commonWindow.getXonXoffTF().setFill(Color.GREY);
                            });

                            if (readBytes != null) {
                                String result = new String(readBytes);
                                ThreadHelper.runOnThread(() -> {
                                    commonWindow.getOutputArea().appendText(result);
                                    commonWindow.getBytesReceivedField().setText(Integer.
                                            toString(serialPortDAO.getReceived()));
                                });
                            }
                        }
                    }
                } catch (Exception exception) {
                    ThreadHelper.runOnThread(() -> ExceptionInformationHelper.showException(exception));
                }
            }
        }
    }

    @Override
    public void init() {
        serialPortEventListener = new SerialPortListener();
        serialPortDAO = new SerialPortDAO(serialPortEventListener);
        commonWindow = new CommonWindow(serialPortDAO);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("COM");
        primaryStage.setResizable(false);
        primaryStage.setScene(commonWindow.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}