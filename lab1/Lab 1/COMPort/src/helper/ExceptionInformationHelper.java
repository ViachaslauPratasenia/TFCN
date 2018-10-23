package helper;

import javafx.scene.control.Alert;
import javafx.scene.text.Font;

public final class ExceptionInformationHelper {
    public static void showException(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Error found!");
        alert.setContentText("Description: " + exception.getMessage());
        alert.showAndWait();
    }
    public static void showMessage(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Error found!");
        alert.setContentText("Description: " + message);
        alert.showAndWait();
    }
}
