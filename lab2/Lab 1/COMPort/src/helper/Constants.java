package helper;

import javafx.scene.text.Font;

public class Constants {
    public static final Font FONT = new Font("Arial",18);
    public static final double MIN_WIDTH = 430;
    public static final double MIN_HEIGHT = 200;
    public static final String XON_IS_ON = "Sending data is allowed";
    public static final String XOFF_IS_ON = "Sending data is restricted";
    public static final String NOT_SUPPORTED = "XON/XOFF is not supported";
    public static final double GAP = 2;
    public static final double PADDING = 5;
    public static final byte XON_CHAR = 17;
    public static final byte XOFF_CHAR = 19;


    public static final int PACKAGE_DATA_OFFSET = 4;
    public static final int PACKAGE_DATA_SIZE_OFFSET = 3;
    public static final int PACKAGE_SOURCE_OFFSET = 2;
    public static final int PACKAGE_DESTINATION_OFFSET = 1;
    public static final int PACKAGE_FLAG_OFFSET = 0;
    public static final int FCS_DEFAULT = 255;
    public static final int PACKAGE_DATA_LENGTH = 251;
    public static final byte PACKAGE_BEGINNING_FLAG = 'a';

    public enum PackageState {
        GOOD,
        DAMAGED,
        WRONG_DESTINATION_ADDRESS,
        NONE
    };
}
