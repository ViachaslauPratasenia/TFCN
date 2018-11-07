package helper;

import javafx.application.Platform;

public class ThreadHelper {
    public static void runOnThread(Runnable task) {
        if(Platform.isFxApplicationThread()) task.run();
        else Platform.runLater(task);
    }
}
