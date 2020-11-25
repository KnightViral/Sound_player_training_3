package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sample.entities.MyAudioTrack;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {

    @FXML
    public TabPane tabPane;
    @FXML
    public CheckBox enableSecondOutputChB;
    @FXML
    public Button setOptionsBtn;
    @FXML
    private Label outputL;
    @FXML
    private Label secondOutputL;
    @FXML
    private ComboBox<Mixer.Info> outputCB;
    @FXML
    private ComboBox<Mixer.Info> secondOutputCD;

    List<MyAudioTrack> tracks;

    @FXML
    void initialize(){
        initOutput();
        setOptionsBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initTabs();
            }
        });
    }

    private void initOutput(){
        List<Mixer.Info> list = new ArrayList<>(Arrays.asList(AudioSystem.getMixerInfo()));
        outputCB.getItems().addAll(list);
        secondOutputCD.getItems().addAll(list);
    }

    private void initTabs(){
        if (tracks != null)
            tracks.forEach(MyAudioTrack::close);
        tracks = new ArrayList<>();
        tabPane.getTabs().clear();
        for (String directory : ResourceWorker.getDirectories()) {
            Tab tab = new Tab();
            tab.setText(directory);
            VBox vBox = new VBox();
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(vBox);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            tab.setContent(scrollPane);
            tabPane.getTabs().add(tab);
            for (File file : ResourceWorker.getFiles(directory)) {
                try {
                    MyAudioTrack track = new MyAudioTrack(file.toURI().toURL(), getMixers());

                    ToolBar toolBar = new ToolBar();
                    vBox.getChildren().add(toolBar);
                    toolBar.getItems().add(new Label(file.getName()));
                    Button playBtn = new Button("Play");
                    Button stopBtn = new Button("Stop");
                    playBtn.setAlignment(Pos.TOP_RIGHT);
                    toolBar.getItems().add(playBtn);
                    toolBar.getItems().add(stopBtn);
                    playBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            track.start();
                        }
                    });
                    stopBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            track.stop();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace(); //todo
                }
            }
        }
    }

    private List<Mixer.Info> getMixers(){
        ArrayList<Mixer.Info> res = new ArrayList<>();
        res.add(outputCB.getValue());
        if (enableSecondOutputChB.isSelected())
            res.add(secondOutputCD.getValue());
        return res;
    }

    @FXML
    void onOutputCBAction(){
        outputL.setText(deMessyCode(outputCB.getValue().getName()));
    }

    @FXML
    void onSecondOutputCDAction(){
        secondOutputL.setText(deMessyCode(secondOutputCD.getValue().getName()));
    }

    private static String deMessyCode(String messyCode){
        try {
            return new String(messyCode.getBytes("Windows-1252"), "windows-1251");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); //todo
            return "Error in charset";
        }
    }

}
