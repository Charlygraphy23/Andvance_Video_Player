package sample.controller;

import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import sample.animation.FadeIn;
import sample.model.MPlayer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class SplashScreenController extends Window {


    @FXML
    private ImageView image;

    @FXML
    private Label importButton;

    private  MPlayer player;

    private double xOffset,yOffset;
    private Node root;
    private FXMLLoader loader;
 //   private File file;

    @FXML
    void initialize() {
        player=new MPlayer();

        ScaleTransition sc=new ScaleTransition(Duration.millis(600),image);
        sc.setByX(0);
        sc.setByY(0);
        sc.setToX(2);
        sc.setToY(2);
        sc.play();

        sc.setOnFinished(e->{
            sc.setDuration(Duration.millis(100));
            sc.setByX(2);
            sc.setByY(2);
            sc.setToX(1.4);
            sc.setToY(1.4);
            sc.play();
            sc.setOnFinished(ee->{
                sc.setByX(1.4);
                sc.setByY(1.4);
                sc.setToX(2);
                sc.setToY(2);
                sc.play();
            });
        });

        FadeIn fadeIn=new FadeIn(importButton);
        fadeIn.getFadeTransition();


        importButton.setOnMouseClicked(e->{

            FileChooser chooser=new FileChooser();
            File file=chooser.showOpenDialog(this);
            if(file!=null){
                try {
                    player.setMp(new MediaPlayer(new Media(file.toURI().toURL().toString())));

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }

            Scene scene= null;
            try {
                loader=new FXMLLoader(getClass().getResource("/sample/view/homeview.fxml"));
                scene = new Scene(loader.load());
                System.out.println(player);

               if(player.getMp()!=null){
                   MediaHomeController homeController=loader.getController();
                   homeController.getMediaToHome(player,file.getName());
               }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.getIcons().setAll(new Image("/sample/assets/Media-windows-media-player-icon.png"));
            

            stage.show();
            Stage s1=(Stage) importButton.getScene().getWindow();
            s1.hide();


            root=scene.getRoot();
            Bounds rootBounds=root.getBoundsInLocal();

            double width,height;

            width=stage.getWidth() -rootBounds.getWidth();
            height=stage.getHeight() - rootBounds.getHeight();

            Bounds prefBound=getprefBounds(root);

            stage.setMinWidth(prefBound.getWidth() + width);
            stage.setMinHeight(prefBound.getHeight() + height);

            //grab your root here
            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });

            //move around here
            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

        });

    }

    // end of Initialize
    //
    //
    //
    //


    private Bounds getprefBounds(Node root) {

        double prefWidth, prfHeight;

        Orientation bias=root.getContentBias();

        if(bias==Orientation.HORIZONTAL){
            prfHeight=root.prefHeight(-1);
            prefWidth=root.prefWidth(prfHeight);
        }
        else if(bias==Orientation.VERTICAL){
            prefWidth=root.prefWidth(-1);
            prfHeight=root.prefHeight(prefWidth);
        }
        else {
            prfHeight=root.prefHeight(-1);
            prefWidth=root.prefWidth(-1);
        }

        return new BoundingBox(0,0,prefWidth,prfHeight);
    }
}
