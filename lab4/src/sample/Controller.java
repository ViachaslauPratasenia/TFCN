package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import static sample.Constants.*;

public class Controller {
    private byte dataBuffer = 0;

    @FXML
    private TextArea inputArea;
    @FXML
    private TextArea outputArea;
    @FXML
    private TextArea debugArea;
    @FXML
    private Button sendButton;

    private class SendTask extends Task<Void> {
        @Override
        protected Void call() {
            runOnUIThread(() -> {
                inputArea.setEditable(false);
                sendButton.setDisable(true);
            });

            byte[] message = inputArea.getText().getBytes();

            for (byte symbol: message) {
                int counter = 0;
                boolean isSending = true;
                StringBuilder collisions = new StringBuilder();

                while(isSending) {
                    while (isChannelBusy());

                    sendSymbol(symbol);

                    try {
                        Thread.sleep(COLLISION_DURATION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(isCollision()) {
                        collisions.append(SYMBOL);
                        counter += 1;

                        if (counter > MAX_NUMBER_COLLISION) {
                            debugArea.appendText(collisions + "/n");
                            isSending = false;
                        } else {
                            try {
                                Thread.sleep(computeDelay(counter));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        runOnUIThread(() -> {
                            outputArea.appendText((char)symbol + "\n");
                            debugArea.appendText(collisions + "\n");
                        });
                        isSending = false;
                    }
                }
            }
            runOnUIThread(() -> {
                inputArea.setEditable(true);
                sendButton.setDisable(false);
            });
            return null;
        }
    }

    @FXML
    protected void send(ActionEvent event){
        new Thread(new SendTask()).start();
    }

    private int computeDelay(int n) {
        return (int)Math.round(Math.random() * Math.pow(2, Math.min(n, MAX_NUMBER_COLLISION)));
    }

    private static void runOnUIThread(Runnable task) {
        if(task == null) throw new NullPointerException("Param task can not be null");

        if(Platform.isFxApplicationThread()) task.run();
        else Platform.runLater(task);
    }

    private void sendSymbol(byte data) {
        this.dataBuffer = data;
    }

    private boolean isChannelBusy() {
        return (System.currentTimeMillis() % 2) == 1;
    }

    private boolean isCollision() {
        return (System.currentTimeMillis() % 2) == 1;
    }
}
