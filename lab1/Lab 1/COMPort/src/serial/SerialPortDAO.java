package serial;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.Arrays;

public class SerialPortDAO implements SerialPortInterface{
    public static final byte XON_CHAR = 17;
    public static final byte XOFF_CHAR = 19;

    private SerialPort serialPort = null;
    private SerialPortEventListener serialPortEventListener;
    private boolean xonXoffFlowControlEnable = false;
    private boolean XoffIsSet = true;
    private int received;
    private int sent;

    @Override
    public void setSerialPort(SerialPort serialPort, boolean XonXoffFlowControlEnable) throws SerialPortException {
        this.serialPort = serialPort;
        this.xonXoffFlowControlEnable = XonXoffFlowControlEnable;
        serialPort.openPort();
        serialPort.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        serialPort.addEventListener(this.serialPortEventListener);
    }

    public SerialPortDAO(SerialPortEventListener serialPortEventListener) {
        this.serialPortEventListener = serialPortEventListener;
    }

    @Override
    public boolean closePort() throws SerialPortException{
        boolean result = this.serialPort.closePort();
        this.serialPort = null;
        sent = 0;
        received = 0;
        return result;
    }

    @Override
    public boolean sendXon() throws SerialPortException{
        return this.serialPort.writeByte(SerialPortDAO.XON_CHAR);
    }

    @Override
    public boolean sendXoff() throws SerialPortException {
        return this.serialPort.writeByte(SerialPortDAO.XOFF_CHAR);
    }

    @Override
    public byte[] read(int byteCount) throws SerialPortException {
        byte[] result = this.serialPort.readBytes(byteCount);
        if(this.xonXoffFlowControlEnable) {
            if (result[0] != XON_CHAR && result[0] != XOFF_CHAR) {
                received += result.length;
            }
        } else
            received += result.length;
        return result;
    }

    @Override
    public boolean write(byte[] source) throws SerialPortException  {
        boolean result = false;
        if(!(this.xonXoffFlowControlEnable && this.XoffIsSet)) {
            result = this.serialPort.writeBytes(source);
            if (result) this.sent += source.length;
        }
        return result;
    }

    public int getReceived() {
        return received;
    }

    public int getSent() {
        return sent;
    }

    public void setXoffState(boolean value) {
        this.XoffIsSet = value;
    }

    public boolean getXonXoffFlowControlMode() {
        return this.xonXoffFlowControlEnable;
    }

    public static ArrayList<String> getPortNames() {
        return new ArrayList<>(Arrays.asList(
                SerialPortList.getPortNames(
                )));
    }
}
