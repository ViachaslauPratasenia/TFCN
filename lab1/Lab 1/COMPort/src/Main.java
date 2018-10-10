import gui.CommonWindow;
import helper.FontHelper;
import helper.ThreadHelper;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import serial.SerialPortController;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import helper.ExceptionWarningHelper;


public class Main extends Application  {
    private SerialPortController serialPortController;
    private SerialPortEventListener serialPortEventListener;

    private CommonWindow commonWindow;

    private class SerialPortListener implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte[] readBytes;
                    if(serialPortController.getXonXoffFlowControlMode()) {
                        readBytes = serialPortController.read(event.getEventValue());
                        boolean containsXonXoff = (readBytes[0] == SerialPortController.XOFF_CHAR ||
                                readBytes[0] == SerialPortController.XON_CHAR);
                        if(containsXonXoff) {
                            boolean value = readBytes[0] == SerialPortController.XOFF_CHAR;
                            serialPortController.setXoffState(value);
                            if(value) {
                                ThreadHelper.runOnUIThread(() -> {
                                    commonWindow.getXonXoffTF().setText(CommonWindow.XOFF_IS_ON);
                                    commonWindow.getXonXoffTF().setFont(FontHelper.FONT);
                                    commonWindow.getXonXoffTF().setUnderline(true);
                                    commonWindow.getXonXoffTF().setFill(Color.RED);
                                });
                            } else {
                                ThreadHelper.runOnUIThread(() -> {
                                    commonWindow.getXonXoffTF().setText(CommonWindow.XON_IS_ON);
                                    commonWindow.getXonXoffTF().setFont(FontHelper.FONT);
                                    commonWindow.getXonXoffTF().setUnderline(true);
                                    commonWindow.getXonXoffTF().setFill(Color.GREEN);
                                });
                            }
                        }
                        if(!containsXonXoff && event.getEventValue() > 0) {
                            serialPortController.sendXoff();
                            String result = new String(readBytes) + "\n";
                            ThreadHelper.runOnUIThread(() -> {
                                commonWindow.getOutputArea().appendText(result);
                                commonWindow.getBytesReceivedField().setText(Integer.
                                        toString(serialPortController.getReceived()));
                            });
                            serialPortController.sendXon();
                        }
                    } else {
                        ThreadHelper.runOnUIThread(() -> {
                            commonWindow.getXonXoffTF().setText(CommonWindow.NOT_SUPPORTED);
                            commonWindow.getXonXoffTF().setFont(FontHelper.FONT);
                            commonWindow.getXonXoffTF().setUnderline(true);
                            commonWindow.getXonXoffTF().setFill(Color.GREY);
                        });
                        readBytes = serialPortController.read(event.getEventValue());
                        if (readBytes != null) {
                            String result = new String(readBytes) + "\n";
                            ThreadHelper.runOnUIThread(() -> {
                                commonWindow.getOutputArea().appendText(result);
                                commonWindow.getBytesReceivedField().setText(Integer.
                                        toString(serialPortController.getReceived()));
                            });
                        }
                    }
                } catch (Exception exception) {
                    ExceptionWarningHelper.showException(exception);
                }
            }
        }
    }

    @Override
    public void init() {
        serialPortEventListener = new SerialPortListener();
        serialPortController = new SerialPortController(serialPortEventListener);
        commonWindow = new CommonWindow(serialPortController);
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