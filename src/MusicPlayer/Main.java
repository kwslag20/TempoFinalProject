/*
 * File: Main.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 * ---------------------------
 * Edited From: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 *
 */

package MusicPlayer;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.lang.Exception;


public class Main extends Application{
    /**
     * Creates a scene containing a simple IDE. The IDE has tab with CodeAreas
     * and two menus： File and Edit
     *
     * @param stage A background window that contains the scene
     */
    @Override
    public void start(Stage stage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Main.fxml"));
        Parent root = loader.load();

        MasterController mc = loader.getController();
        stage.setOnCloseRequest((e) -> stage.close());

        Scene scene = new Scene(root, 250, 60);
        stage.setTitle("Project 18");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Launches an instance of class Main
     */
    public static void main(String args[]){
        launch(args);
    }
}