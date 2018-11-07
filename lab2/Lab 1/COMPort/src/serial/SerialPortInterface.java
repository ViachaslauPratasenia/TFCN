package serial;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.ArrayList;

public interface SerialPortInterface {
    void setSerialPort(SerialPort serialPort, byte address, boolean xonXoffFlowControlEnable) throws SerialPortException;
    boolean closePort() throws SerialPortException;
    boolean sendXon(byte destination) throws SerialPortException;
    boolean sendXoff(byte destination) throws SerialPortException;
    byte[] read(int byteCount) throws SerialPortException;
    boolean write(ArrayList<byte[]> messages) throws SerialPortException;
}
