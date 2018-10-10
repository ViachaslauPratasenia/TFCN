import gui.CommonWindow;
import helper.Constants;
import helper.ThreadHelper;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import serial.SerialPortDAO;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import helper.ExceptionInformationHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Main extends Application  {
    private SerialPortDAO serialPortDAO;
    private SerialPortEventListener serialPortEventListener;

    private CommonWindow commonWindow;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private class SerialPortListener implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte[] readBytes;
                    if(serialPortDAO.getXonXoffFlowControlMode()) {
                        readBytes = serialPortDAO.read(event.getEventValue());
                        boolean containsXonXoff = (readBytes[0] == SerialPortDAO.XON_CHAR
                                || readBytes[0] == SerialPortDAO.XOFF_CHAR);
                        if(containsXonXoff) {
                            serialPortDAO.setXoffState(readBytes[0] == SerialPortDAO.XOFF_CHAR);
                            if(readBytes[0] == SerialPortDAO.XOFF_CHAR) {
                                ThreadHelper.runOnThread(() -> {
                                    commonWindow.getXonXoffTF().setText(Constants.XOFF_IS_ON);
                                    commonWindow.getXonXoffTF().setFont(Constants.FONT);
                                    commonWindow.getXonXoffTF().setUnderline(true);
                                    commonWindow.getXonXoffTF().setFill(Color.RED);
                                });
                            } else {
                                ThreadHelper.runOnThread(() -> {
                                    commonWindow.getXonXoffTF().setText(Constants.XON_IS_ON);
                                    commonWindow.getXonXoffTF().setFont(Constants.FONT);
                                    commonWindow.getXonXoffTF().setUnderline(true);
                                    commonWindow.getXonXoffTF().setFill(Color.GREEN);
                                });
                            }
                        }
                        if(!containsXonXoff && event.getEventValue() > 0) {
                            serialPortDAO.sendXoff();
                            String result = "User(" + sdf.format(calendar.getTime()) + ") : " +
                                    new String(readBytes) + "\n";
                            ThreadHelper.runOnThread(() -> {
                                commonWindow.getOutputArea().appendText(result);
                                commonWindow.getBytesReceivedField().
                                        setText(Integer.toString(serialPortDAO.getReceived()));
                            });
                            serialPortDAO.sendXon();
                        }
                    } else {
                        ThreadHelper.runOnThread(() -> {
                            commonWindow.getXonXoffTF().setText(Constants.NOT_SUPPORTED);
                            commonWindow.getXonXoffTF().setFont(Constants.FONT);
                            commonWindow.getXonXoffTF().setUnderline(true);
                            commonWindow.getXonXoffTF().setFill(Color.GREY);
                        });
                        readBytes = serialPortDAO.read(event.getEventValue());
                        if (readBytes != null) {
                            String result = "User(" + sdf.format(calendar.getTime()) + ") : " +
                                    new String(readBytes) + "\n";
                            ThreadHelper.runOnThread(() -> {
                                commonWindow.getOutputArea().appendText(result);
                                commonWindow.getBytesReceivedField().
                                        setText(Integer.toString(serialPortDAO.getReceived()));
                            });
                        }
                    }
                } catch (Exception exception) {
                    ExceptionInformationHelper.showException(exception);
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