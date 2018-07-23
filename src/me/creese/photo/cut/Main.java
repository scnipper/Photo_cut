

package me.creese.photo.cut;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/main.fxml"));
        primaryStage.setTitle("Photo cut");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

    }


    public static void main(String[] args) {
        launch(args);
    }
}
