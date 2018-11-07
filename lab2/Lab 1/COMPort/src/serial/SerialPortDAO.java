package serial;

import convert.Converter;
import helper.ExceptionInformationHelper;
import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.Arrays;

import static helper.Constants.*;

public class SerialPortDAO implements SerialPortInterface{
    private SerialPort serialPort = null;
    private SerialPortEventListener serialPortEventListener;
    private boolean xonXoffFlowControlEnable = false;
    private boolean xoffIsSet = true;
    private int received;
    private int sent;

    private byte address = 0;
    private byte senderAddress = 0;
    private PackageState packageState = PackageState.NONE;

    public SerialPortDAO(SerialPortEventListener serialPortEventListener) {
        this.serialPortEventListener = serialPortEventListener;
    }

    private static byte[] stuffBytes(byte[] source) {
        byte[] result = new byte[source.length + 1];
        int readIndex = 0;
        int writeIndex = 1;
        int codeIndex = 0;
        byte code = 1;

        while(readIndex < source.length) {
            if(source[readIndex] == PACKAGE_BEGINNING_FLAG) {
                result[codeIndex] = code;
                code = 1;
                codeIndex = writeIndex++;
                readIndex++;
            } else {
                result[writeIndex++] = source[readIndex++];
                code++;
                if(code == 0xFF) {
                    result[codeIndex] = code;
                    code = 1;
                    codeIndex = writeIndex++;
                }
            }
        }

        result[codeIndex] = code;

        return result;
    }

    private static byte[] unStuffBytes(byte[] source) {
        byte[] result = new byte[source.length - 1];

        int read_index = 0;
        int write_index = 0;
        byte code;
        byte i;

        while(read_index < source.length) {
            code = source[read_index];

            if(read_index + code > source.length && code != 1) {
                result[write_index] = PACKAGE_BEGINNING_FLAG;
                break;
            }

            read_index++;

            for(i = 1; i < code; i++) {
                result[write_index++] = source[read_index++];
            }
            if(code != 0xFF && read_index != source.length) {
                result[write_index++] = PACKAGE_BEGINNING_FLAG;
            }
        }

        return result;
    }

    @Override
    public void setSerialPort(SerialPort serialPort, byte address, boolean xonXoffFlowControlEnable) throws SerialPortException {
        this.serialPort = serialPort;
        this.xonXoffFlowControlEnable = xonXoffFlowControlEnable;
        serialPort.openPort();
        this.address = address;
        serialPort.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        serialPort.addEventListener(this.serialPortEventListener);
    }

    @Override
    public boolean closePort() throws SerialPortException{
        boolean result = this.serialPort.closePort();
        this.serialPort = null;
        this.address = 0;
        sent = 0;
        received = 0;
        return result;
    }

    public byte[] decomposePackage(byte[] source) {
        if(serialPort == null) {
            throw new NullPointerException("The value of param serialPort can not bu null!");
        }

        if(source.length < 5) {
            throw new IllegalArgumentException("Package was damaged. It's size is too small!");
        }

        byte[] tempBytes = new byte[source.length - 1];
        for(int i = 0, j = 1; j < source.length; i++, j++) {
            tempBytes[i] = source[j];
        }

        tempBytes = unStuffBytes(tempBytes);

        byte[] result = new byte[tempBytes.length + 1];
        result[0] = source[0];

        for(int i = 1, j = 0; j < tempBytes.length; i++, j++) {
            result[i] = tempBytes[j];
        }

        return result;
    }

    public ArrayList<byte[]> composePackages(byte[] source, byte destinationAddress) {
        if(this.address == destinationAddress) {
            throw new IllegalArgumentException("Source and destination address can't be equal!");
        }

        if(serialPort == null) {
            throw new NullPointerException("The value of param serialPort can not bu null!");
        }

        ArrayList<byte[]> result = new ArrayList<>();

        int amountOfPackages = (int)Math.ceil((double)source.length / (double)PACKAGE_DATA_LENGTH);

        int undistributedDataSize = source.length;
        int sourceIndex = 0;

        for(int i = 0; i < amountOfPackages; i++) {
            int currentDataSize =
                    (undistributedDataSize - PACKAGE_DATA_LENGTH) < 0 ? undistributedDataSize : PACKAGE_DATA_LENGTH;

            undistributedDataSize -= PACKAGE_DATA_LENGTH;

            // 4 - amount of other package's bytes, except the beginning flag
            byte[] iterationPackage = new byte[currentDataSize + 4];

            iterationPackage[PACKAGE_DESTINATION_OFFSET - 1] = destinationAddress;
            iterationPackage[PACKAGE_SOURCE_OFFSET - 1] = this.address;
            iterationPackage[PACKAGE_DATA_SIZE_OFFSET- 1] = (byte)currentDataSize;
            for(int j = PACKAGE_DATA_OFFSET - 1; j < currentDataSize + PACKAGE_DATA_OFFSET - 1; j++, sourceIndex++) {
                iterationPackage[j] = source[sourceIndex];
            }
            iterationPackage[iterationPackage.length - 1] = (byte)FCS_DEFAULT;

            iterationPackage = stuffBytes(iterationPackage);

            byte[] completePackage = new byte[iterationPackage.length + 1];
            completePackage[PACKAGE_FLAG_OFFSET] = PACKAGE_BEGINNING_FLAG;

            for(int j = 0, k = 1; j < iterationPackage.length; j++, k++) {
                completePackage[k] = iterationPackage[j];
            }

            result.add(completePackage);
        }

        return result;
    }

    @Override
    public boolean sendXon(byte destination) throws SerialPortException{
        if(destination < 0) {
            throw new IllegalArgumentException("The value of param destination is invalid!");
        }
        byte[] xon = { XON_CHAR };
        byte[] xonPackage = this.composePackages(xon, destination).get(0);
        return this.serialPort.writeBytes(xonPackage);
    }

    @Override
    public boolean sendXoff(byte destination) throws SerialPortException {
        if(destination < 0) {
            throw new IllegalArgumentException("The value of param destination is invalid!");
        }

        byte[] xoff = { XOFF_CHAR };
        byte[] xoffPackage = composePackages(xoff, destination).get(0);
        return this.serialPort.writeBytes(xoffPackage);
    }

    @Override
    public byte[] read(int byteCount) throws SerialPortException {
        if(serialPort == null) {
            throw new NullPointerException("The value of param serialPort can not bu null!");
        }

        byte[] result = this.serialPort.readBytes(byteCount);

        //check beginning flag
        if (result[0] != PACKAGE_BEGINNING_FLAG) {
            this.packageState = PackageState.DAMAGED;
            return result;
        }

        byte[] decomposedSource;

        try {
            //decompose package
            decomposedSource = decomposePackage(result);
        } catch (Exception exception) {
            this.packageState = PackageState.DAMAGED;
            ExceptionInformationHelper.showException(exception);
            return result;
        }

        //check FCS value
        if(!this.checkFCS(decomposedSource)) {
            this.packageState = PackageState.DAMAGED;
            return decomposedSource;
        }

        //check destination address
        if(this.address != decomposedSource[PACKAGE_DESTINATION_OFFSET]) {
            this.packageState = PackageState.WRONG_DESTINATION_ADDRESS;
            return decomposedSource;
        }

        this.senderAddress = decomposedSource[PACKAGE_SOURCE_OFFSET];

        byte[] data = this.retrievePackageData(decomposedSource);

        if(this.xonXoffFlowControlEnable) {
            if (data[0] != XON_CHAR && data[0] != XOFF_CHAR) {
                this.received += result.length;
            }
        } else {
            this.received += result.length;
        }

        packageState = PackageState.GOOD;

        return data;
    }

    private byte[] retrievePackageData(byte[] source) {
        if(serialPort == null) {
            throw new NullPointerException("The value of param serialPort can not bu null!");
        }

        int dataSize = Converter.byteToUnsignedInt(source[PACKAGE_DATA_SIZE_OFFSET]);

        byte[] data = new byte[dataSize];

        for(int i = PACKAGE_DATA_OFFSET, j = 0; j < dataSize; i++, j++) {
            data[j] = source[i];
        }

        return data;
    }

    private boolean checkFCS(byte[] message) {
        return message[message.length - 1] == (byte)FCS_DEFAULT;
    }

    @Override
    public boolean write(ArrayList<byte[]> messages) throws SerialPortException  {
        if(serialPort == null) {
            throw new NullPointerException("The value of param serialPort can not bu null!");
        }

        boolean result = false;

        if(!(this.xoffIsSet && this.xonXoffFlowControlEnable)) {
            for(byte[] message : messages) {
                result = this.serialPort.writeBytes(message);

                if (result) {
                    this.sent += message.length;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
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
        this.xoffIsSet = value;
    }

    public boolean getXoffState(){ return xoffIsSet;}

    public boolean getXonXoffFlowControlMode() {
        return this.xonXoffFlowControlEnable;
    }

    public byte getAddress() {
        return address;
    }

    public void setAddress(byte address) {
        this.address = address;
    }

    public byte getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(byte senderAddress) {
        this.senderAddress = senderAddress;
    }

    public PackageState getPackageState() {return packageState;}

    public static ArrayList<String> getPortNames() {
        return new ArrayList<>(Arrays.asList(
                SerialPortList.getPortNames(
                )));
    }
}
