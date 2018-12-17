import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    private static final int DELAY = 1000;
    private static final int PERIOD = 1000;
    private static final String MARKER = "*";

    private Timer _timer;
    private Package _package;
    private byte addresses[] = { 1 , 10, 100 };


    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(new Station(addresses[0],addresses[1],(byte)1));
        stations.add(new Station(addresses[1],addresses[2], (byte)0));
        stations.add(new Station(addresses[2], addresses[0], (byte)0));

        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        Stage stage3 = new Stage();

        stage1.setX(400);
        stage1.setY(200);
        stage2.setX(800);
        stage2.setY(200);
        stage3.setX(1200);
        stage3.setY(200);

        stations.get(0).start(stage1);
        stations.get(1).start(stage2);
        stations.get(2).start(stage3);

        _timer = new Timer();

        stations.get(0).getButtonStart().setOnAction((event) -> {
            _package = new Package();
            for (int i = 0; i < 3; i++) {
                int currentStation = i;

                _timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stationRoutine(stations.get(currentStation), _package);
                    }
                }, DELAY, PERIOD);

                stations.get(0).getButtonStart().setDisable(true);
                stations.get(0).getButtonSend().setDisable(false);
                stations.get(1).getButtonSend().setDisable(false);
                stations.get(2).getButtonSend().setDisable(false);
            }
        });

        stations.get(0).getTaToken().textProperty().addListener(observable -> sendPacket(stations.get(0)));
        stations.get(1).getTaToken().textProperty().addListener(observable -> sendPacket(stations.get(1)));
        stations.get(2).getTaToken().textProperty().addListener(observable -> sendPacket(stations.get(2)));

        stations.get(0).getButtonSend().setOnAction(event -> stations.get(0).setRequest());
        stations.get(1).getButtonSend().setOnAction(event -> stations.get(1).setRequest());
        stations.get(2).getButtonSend().setOnAction(event -> stations.get(2).setRequest());
    }

    public static void stationRoutine(Station station, Package tokenPackage){
        try {
            if (tokenPackage.getControl() == 0) {
                Helper.runOnUIThread(() -> station.getTaToken().setText(MARKER));
                tokenPackage.setSource(station.getSourceAddr());
                tokenPackage.setDestination(station.getDestinationAddr());
                tokenPackage.setMonitor(station.getMonitor());
            } else {
                Helper.runOnUIThread(()->{
                    if (tokenPackage.getDestination() == station.getSourceAddr()) {
                        String data = "";
                        data += (char)tokenPackage.getData();

                        tokenPackage.setStatus((byte) 1);
                        station.getTaOutput().appendText(data);
                    }
                    if (tokenPackage.getStatus() == 1) {
                        tokenPackage.setControl((byte) 0);
                        tokenPackage.freeData();
                        tokenPackage.setStatus((byte) 0);
                    }
                });
            }
            Thread.sleep(DELAY);
            Helper.runOnUIThread(() -> station.getTaToken().setText(""));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Station station) {
        if (!station.getTaInput().getText().equals("")
                && !station.getCurrentDestination().equals("")
                && station.requestStatus()
                && _package.getControl() == 0
                && _package != null) {
            if (station.getTaToken().getText().equals("*")) {
                if (station.getCurrentDestination().equals("1")) {
                    station.setDestinationAddr(addresses[0]);
                }
                if (station.getCurrentDestination().equals("10")) {
                    station.setDestinationAddr(addresses[1]);
                }
                if (station.getCurrentDestination().equals("100")) {
                    station.setDestinationAddr(addresses[2]);
                }

                _package.setControl((byte) 1);
                _package.setDestination(station.getDestinationAddr());
                _package.setSource(station.getSourceAddr());
                _package.setMonitor(station.getMonitor());

                String reduced = station.getTaInput().getText().substring(1);
                _package.setData(station.getTaInput().getText().getBytes()[0]);
                station.getTaInput().setText(reduced);

                if (reduced.length() == 0) {
                    station.resetRequest();
                }
            }
        }
    }
}