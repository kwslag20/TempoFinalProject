package MusicPlayer;

import javafx.fxml.FXML;

public class MasterController {
    IntermediaryPlayer intermediaryPlayer = new IntermediaryPlayer();
    @FXML public void handlePlay(){
        intermediaryPlayer.play();
    }
    @FXML public void handlePause(){
        intermediaryPlayer.pause();
    }
    @FXML public void handleResume(){
        intermediaryPlayer.resume();
    }
}
