import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Station{
    private byte monitor;
    private byte sourceAddr;
    private byte destinationAddr;
    private boolean requestForSend = false;

    private String[] initDestinations = { "1", "10", "100"};
    private String currentDestination = "";

    private TextArea taInput;
    private TextArea taOutput;
    private TextArea taToken;
    private Text txtSource;
    private Button btnSend;
    private Button btnStart;

    Station(byte source, byte dest,byte monitor){
        this.sourceAddr = source;
        this.destinationAddr = dest;
        this.monitor = monitor;

        txtSource = new Text();

        switch (source){
            case 1:
                txtSource.setText("Source: 1");
                break;
            case 10:
                txtSource.setText("Source: 10");
                break;
            case 100:
                txtSource.setText("Source: 100");
                break;
        }
        this.monitor = monitor;
    }

    public byte getSourceAddr(){
        return sourceAddr;
    }

    public byte getDestinationAddr(){
        return destinationAddr;
    }

    public byte getMonitor(){
        return monitor;
    }

    public Button getButtonSend(){
        return btnSend;
    }

    public Button getButtonStart(){
        return btnStart;
    }

    public TextArea getTaOutput(){
        return taOutput;
    }

    public TextArea getTaToken(){
        return taToken;
    }
    public TextArea getTaInput(){
        return taInput;
    }

    public String getCurrentDestination() {
        return currentDestination;
    }

    public void setDestinationAddr(byte dest){
        destinationAddr = dest;
    }

    public void setRequest() {
        if (taInput.getText().length() > 0) {
            requestForSend = true;
        }
    }

    public void resetRequest() {
        requestForSend = false;
    }

    public boolean requestStatus() {
        return requestForSend;
    }

    public void start(Stage stage) {
        try {
            VBox debugLayout = new VBox();
            debugLayout.setPadding(new Insets(5));
            debugLayout.setSpacing(5);
            debugLayout.setFillWidth(true);
            debugLayout.setAlignment(Pos.TOP_CENTER);

            Label labelInput = new Label("Input");
            debugLayout.getChildren().add(labelInput);

            taInput = new TextArea();
            taInput.setPrefWidth(300);
            taInput.setMaxHeight(50);
            debugLayout.getChildren().add(taInput);

            btnSend = new Button("Send");
            btnSend.setDisable(true);
            btnSend.setMinHeight(30);
            btnSend.setPrefWidth(300);
            debugLayout.getChildren().add(btnSend);

            Label labelDestAddress = new Label("Destination");
            debugLayout.getChildren().add(labelDestAddress);

            ToggleGroup destinationsList = new ToggleGroup();
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            RadioButton button0 = new RadioButton(initDestinations[0]);
            button0.setPadding(new Insets(2,25,2,2));
            button0.setToggleGroup(destinationsList);

            RadioButton button1 = new RadioButton(initDestinations[1]);
            button1.setPadding(new Insets(2,25,2,2));
            button1.setToggleGroup(destinationsList);

            RadioButton button2 = new RadioButton(initDestinations[2]);
            button2.setPadding(new Insets(2,25,2,2));
            button2.setToggleGroup(destinationsList);

            /*destinationsList.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    RadioButton button = (RadioButton)destinationsList.getSelectedToggle();
                    currentDestination = button.getText();
                }
            });*/

            destinationsList.selectedToggleProperty().addListener(listener -> {
                RadioButton button = (RadioButton)destinationsList.getSelectedToggle();
                currentDestination = button.getText();
            });

            hBox.getChildren().addAll(button0, button1, button2);
            debugLayout.getChildren().add(hBox);

            final Separator separatorOne = new Separator();
            separatorOne.setPrefWidth(300);
            separatorOne.setValignment(VPos.CENTER);
            debugLayout.getChildren().add(separatorOne);

            Label labelOutput = new Label("Output");
            debugLayout.getChildren().add(labelOutput);

            taOutput = new TextArea();
            taOutput.setEditable(false);
            taOutput.setPrefHeight(100);
            taOutput.setWrapText(true);
            debugLayout.getChildren().add(taOutput);

            final Separator separatorTwo = new Separator();
            separatorTwo.setPrefWidth(300);
            separatorTwo.setValignment(VPos.CENTER);
            debugLayout.getChildren().add(separatorTwo);

            Label labelDebug = new Label("Debug: ");
            debugLayout.getChildren().add(labelDebug);

            debugLayout.getChildren().add(txtSource);

            taToken = new TextArea();
            taToken.setEditable(false);
            taToken.setPrefHeight(50);
            debugLayout.getChildren().add(taToken);

            btnStart = new Button("Start");
            btnStart.setMinHeight(30);
            btnStart.setPrefWidth(300);
            if(monitor == 1)
                debugLayout.getChildren().add(btnStart);

            Scene sceneInput = new Scene(debugLayout, 300, 450);
            stage.setScene(sceneInput);
            stage.setResizable(false);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}