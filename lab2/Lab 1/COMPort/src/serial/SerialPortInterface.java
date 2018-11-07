package serial;

import jssc.SerialPort;
import jssc.SerialPortException;

public interface SerialPortInterface {
    void setSerialPort(SerialPort serialPort, boolean xonXoffFlowControlEnable) throws SerialPortException;
    boolean closePort() throws SerialPortException;
    boolean sendXon() throws SerialPortException;
    boolean sendXoff() throws SerialPortException;
    byte[] read(int byteCount) throws SerialPortException;
    boolean write(byte[] source) throws SerialPortException;
}
