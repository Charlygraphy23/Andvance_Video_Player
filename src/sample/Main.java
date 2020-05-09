package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene =new Scene(FXMLLoader.load(getClass().getResource("view/splashview.fxml")));
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        primaryStage.alwaysOnTopProperty();
        primaryStage.getIcons().setAll(new Image("/sample/assets/Media-windows-media-player-icon.png"));

        }


    public static void main(String[] args) {
        launch(args);
    }
}
