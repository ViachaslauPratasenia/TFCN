package helper;

import javafx.scene.control.Alert;
import javafx.scene.text.Font;

public final class ExceptionWarningHelper {
    public static void showException(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exception error");
        alert.setHeaderText("Exception found!");
        alert.setContentText("Description: " + exception.getMessage());
        alert.showAndWait();
    }
}
