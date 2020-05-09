package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.animation.FadeIn;
import sample.animation.FadeOut;
import sample.model.MPlayer;

import javax.naming.Binding;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class MediaHomeController {

    @FXML
    private AnchorPane pane;

    @FXML
    private AnchorPane playingtoolPane;

    @FXML
    private HBox mvBoundary;

    @FXML
    private MediaView mv;

    @FXML
    private Label close;

    @FXML
    private Label minimize;

    @FXML
    private Label maximize;

    @FXML
    private JFXSlider mediaSlider;

    @FXML
    private JFXButton playButton;

    @FXML
    private Label playingTime;

    @FXML
    private Label totalTime;

    @FXML
    private JFXButton backwardButton;


    private double customWidthPane,customHeightPane;


    @FXML
    void close(MouseEvent event) {
        Stage stage=(Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void maximize(MouseEvent event) {
        Stage stage=(Stage)((Node) event.getSource()).getScene().getWindow();
        if(stage.isMaximized()){
            stage.setMaximized(false);
            stage.setWidth(customWidthPane);
            stage.setHeight(customHeightPane);
        }else {
            customWidthPane=stage.getWidth();
            customHeightPane=stage.getHeight();
            stage.setMaximized(true);
        }
    }

    @FXML
    void minimize(MouseEvent event) {
        Stage stage=(Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void onDragDetect(MouseEvent event) {
        Stage stage=(Stage)((Node) event.getSource()).getScene().getWindow();
        if(stage.isMaximized()){
            stage.setMaximized(false);
            stage.setWidth(800);
            stage.setHeight(600);
        }
    }


    @FXML
    private JFXButton fastdorwordButton;

    @FXML
    private ImageView fastFsecShows;

    @FXML
    private JFXSlider volumeSlider;

    @FXML
    private Label videoTitle;


    private MPlayer mp;
    private  FadeOut fadeOut;


    @FXML
    void initialize() {


        mediaSlider.setValue(0);
        volumeSlider.setValue(0);
        fadeOut= new FadeOut(playingtoolPane);


        pane.setOnDragOver(e->{
            if(e.getDragboard().hasFiles()){
                e.acceptTransferModes(TransferMode.ANY);
            }
        });




        // For Upload the media File into Stage
        pane.setOnDragDropped(e->{
           if (e.getDragboard().hasFiles() && mp==null){

               mediaSlider.setValue(0);

               List<File> files=e.getDragboard().getFiles();
               videoTitle.setVisible(true);
               videoTitle.setText(files.get(0).getName());
               mp=new MPlayer();
               try {
                   mp.setMp(new MediaPlayer(new Media(files.get(0).toURI().toURL().toString())));
                   mv.setMediaPlayer(mp.getMp());

                   // setting the size of MediaView

                   mv.fitWidthProperty().bind(mvBoundary.widthProperty());
                   mv.fitHeightProperty().bind(mvBoundary.heightProperty());

                   mp.getMp().currentTimeProperty().addListener(new ChangeListener<Duration>() {
                       @Override
                       public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                           mediaSlider.setValue(newValue.toSeconds());
                       }
                   });

                   mediaSlider.maxProperty().bind(Bindings.createDoubleBinding(()-> mp.getMp().getTotalDuration().toSeconds(),mp.getMp().totalDurationProperty()));

               } catch (MalformedURLException ex) {
                   ex.printStackTrace();
               }


               // playing time work

               playingTime.textProperty().bind(Bindings.createStringBinding(()->{
                   Duration duration=mp.getMp().getCurrentTime();
                   return String.format("%02d:%02d:%02d", (int)duration.toHours(),(int) duration.toMinutes()%60 ,(int)duration.toSeconds() %60);
               }, mp.getMp().currentTimeProperty()));

               totalTime.textProperty().bind(Bindings.createStringBinding(()->{
                   Duration duration=mp.getMp().getTotalDuration();
                   return String.format("%02d:%02d:%02d", (int)duration.toHours(),(int) duration.toMinutes()%60 ,(int)duration.toSeconds() %60);
               }, mp.getMp().totalDurationProperty()));

               mp.getMp().currentTimeProperty().addListener(new InvalidationListener() {
                   @Override
                   public void invalidated(Observable observable) {
                       mp.getMp().setOnEndOfMedia(()->{
                           mp.getMp().seek(Duration.seconds(0));
                           mp.getMp().stop();


                           FontAwesomeIconView pauseIcon=new FontAwesomeIconView(FontAwesomeIcon.PLAY);
                           pauseIcon.setGlyphSize(17);
                           pauseIcon.setFill(Paint.valueOf("#ffffff"));
                           pauseIcon.setSmooth(true);
                           playButton.setGraphic(pauseIcon);

                           fadeOut.getStopFadeTransition();
                           playingtoolPane.setVisible(true);
                           playingtoolPane.setOpacity(1.0);

                       });
                   }
               });

           }
           else if(mp.getMp()!=null  && e.getDragboard().hasFiles()){

               mediaSlider.setValue(0);

               mp.getMp().stop();
               if(e.getDragboard().hasFiles()){

                   List<File> files=e.getDragboard().getFiles();
                   videoTitle.setVisible(true);
                   videoTitle.setText(files.get(0).getName());
                   mp=new MPlayer();
                   try {
                       mp.setMp(new MediaPlayer(new Media(files.get(0).toURI().toURL().toString())));
                       mv.setMediaPlayer(mp.getMp());
                       mp.getMp().setAutoPlay(true);

                       FontAwesomeIconView pauseIcon=new FontAwesomeIconView(FontAwesomeIcon.PAUSE);
                       pauseIcon.setGlyphSize(17);
                       pauseIcon.setFill(Paint.valueOf("#ffffff"));
                       pauseIcon.setSmooth(true);
                       playButton.setGraphic(pauseIcon);

                       fadeOut.getFadeTransition();
                       // setting the size of MediaView

                       mv.fitWidthProperty().bind(mvBoundary.widthProperty());
                       mv.fitHeightProperty().bind(mvBoundary.heightProperty());


                       mp.getMp().currentTimeProperty().addListener(new ChangeListener<Duration>() {
                           @Override
                           public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                               mediaSlider.setValue(newValue.toSeconds());
                           }
                       });

                       mediaSlider.maxProperty().bind(Bindings.createDoubleBinding(()-> mp.getMp().getTotalDuration().toSeconds(),mp.getMp().totalDurationProperty()));

                   } catch (MalformedURLException ex) {
                       ex.printStackTrace();
                   }
               }

               // playing time work

               playingTime.textProperty().bind(Bindings.createStringBinding(()->{
                   Duration duration=mp.getMp().getCurrentTime();
                   return String.format("%02d:%02d:%02d", (int)duration.toHours(),(int) duration.toMinutes()%60 ,(int)duration.toSeconds() %60);
               }, mp.getMp().currentTimeProperty()));

               totalTime.textProperty().bind(Bindings.createStringBinding(()->{
                   Duration duration=mp.getMp().getTotalDuration();
                   return String.format("%02d:%02d:%02d", (int)duration.toHours(),(int) duration.toMinutes()%60 ,(int)duration.toSeconds() %60);
               }, mp.getMp().totalDurationProperty()));

               mp.getMp().currentTimeProperty().addListener(new InvalidationListener() {
                   @Override
                   public void invalidated(Observable observable) {
                       mp.getMp().setOnEndOfMedia(()->{
                           mp.getMp().seek(Duration.seconds(0));
                           mp.getMp().stop();

                           FontAwesomeIconView pauseIcon=new FontAwesomeIconView(FontAwesomeIcon.PLAY);
                           pauseIcon.setGlyphSize(17);
                           pauseIcon.setFill(Paint.valueOf("#ffffff"));
                           pauseIcon.setSmooth(true);
                           playButton.setGraphic(pauseIcon);


                           fadeOut.getStopFadeTransition();
                           playingtoolPane.setVisible(true);
                           playingtoolPane.setOpacity(1.0);

                       });
                   }
               });



           }


            volumeSlider.setValue(mp.getMp().getVolume() * 100);


        });


        // For Playing The Media
        playButton.setOnAction(e->{

            if(mp.getMp().getStatus().equals(MediaPlayer.Status.READY) || mp.getMp().getStatus().equals(MediaPlayer.Status.PAUSED) || mp.getMp().getStatus().equals(MediaPlayer.Status.STOPPED)){
                mp.getMp().play();
                fadeOut.getFadeTransition();
                playButton.getScene().setCursor(Cursor.NONE);


                FontAwesomeIconView pauseIcon=new FontAwesomeIconView(FontAwesomeIcon.PAUSE);
                pauseIcon.setGlyphSize(19);
                pauseIcon.setFill(Paint.valueOf("#ffffff"));
                pauseIcon.setSmooth(true);
                playButton.setGraphic(pauseIcon);

            }
            else if(mp.getMp().getStatus().equals(MediaPlayer.Status.PLAYING)){
                mp.getMp().pause();
                playButton.getScene().setCursor(Cursor.DEFAULT);

                fadeOut.getStopFadeTransition();
                playingtoolPane.setVisible(true);
                playingtoolPane.setOpacity(1.0);


                FontAwesomeIconView pauseIcon=new FontAwesomeIconView(FontAwesomeIcon.PLAY);
                pauseIcon.setGlyphSize(17);
                pauseIcon.setFill(Paint.valueOf("#ffffff"));
                pauseIcon.setSmooth(true);
                playButton.setGraphic(pauseIcon);

            }


        });


        // Volume Controll

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mp.getMp().setVolume(newValue.doubleValue() /100);
            }
        });

       mediaSlider.setOnMouseReleased(e->{
          if(mp!=null){
              mp.getMp().seek(Duration.seconds(mediaSlider.getValue()));
          }
       });

        mediaSlider.setOnMousePressed(e->{
            if(mp!=null){
                mp.getMp().seek(Duration.seconds(mediaSlider.getValue()));
            }
        });

        mediaSlider.setOnKeyReleased(e->{
            if(mp!=null) {
                if (e.getCode().equals(KeyCode.RIGHT)) {
                Duration duration=Duration.seconds(mp.getMp().getCurrentTime().toSeconds()+ 30);
                        mp.getMp().seek(duration);
                        mediaSlider.setValue(mp.getMp().getCurrentTime().toSeconds());
                }
            }else if(e.getCode().equals(KeyCode.LEFT)){
                Duration duration=Duration.seconds(mp.getMp().getCurrentTime().toSeconds()- 10);
                mp.getMp().seek(duration);
                mediaSlider.setValue(mp.getMp().getCurrentTime().toSeconds());
            }
        });




        fastdorwordButton.setOnAction(e->{
            Duration time=mp.getMp().getCurrentTime();
           mp.getMp().seek(Duration.seconds(time.toSeconds()+ 30));
           mediaSlider.setValue(time.toSeconds());
            fastFsecShows.setImage(new Image("/sample/assets/icons8-forward-30-80.png"));
           fastFsecShows.setVisible(true);

            if(fastFsecShows.isVisible()){
                PauseTransition pTransition =new PauseTransition(Duration.millis(500));
                pTransition.play();
                pTransition.setOnFinished(ee->{
                    fastFsecShows.setVisible(false);
                });
            }

        });


        backwardButton.setOnAction(e->{
            Duration time=mp.getMp().getCurrentTime();
            mp.getMp().seek(Duration.seconds(time.toSeconds()- 10));
            mediaSlider.setValue(time.toSeconds());
            fastFsecShows.setImage(new Image("/sample/assets/icons8-replay-10-80.png"));
            fastFsecShows.setVisible(true);

            if(fastFsecShows.isVisible()){
                PauseTransition pTransition =new PauseTransition(Duration.millis(500));
                pTransition.play();
                pTransition.setOnFinished(ee->{
                    fastFsecShows.setVisible(false);
                });
            }
        });


        pane.setOnMouseMoved(e->{
            if(mp!=null){
                    if(!playingtoolPane.isVisible() || fadeOut.getStatus().equals(FadeTransition.Status.RUNNING)){
                        fadeOut.getStopFadeTransition();
                        playingtoolPane.setVisible(true);
                        playingtoolPane.setOpacity(1.0);

                        playButton.getScene().setCursor(Cursor.DEFAULT);

                        PauseTransition pauseTransition=new PauseTransition(Duration.seconds(1));
                        pauseTransition.play();
                        pauseTransition.setOnFinished(ee->{
                           if(mp.getMp().getStatus().equals(MediaPlayer.Status.READY) || mp.getMp().getStatus().equals(MediaPlayer.Status.STOPPED) || mp.getMp().getStatus().equals(MediaPlayer.Status.UNKNOWN) || mp.getMp().getStatus().equals(MediaPlayer.Status.PAUSED)){
                               fadeOut.getStopFadeTransition();
                               playingtoolPane.setVisible(true);
                               playingtoolPane.setOpacity(1.0);
                               playButton.getScene().setCursor(Cursor.DEFAULT);
                           }else {
                               fadeOut.getFadeTransition();
                              PauseTransition p=new PauseTransition(Duration.seconds(1.8));
                              p.play();
                              p.setOnFinished(eee->{
                                  playButton.getScene().setCursor(Cursor.NONE);
                              });
                           }
                        });
                    }
                }
        });


        // Full Screen

        pane.setOnKeyReleased(e->{
            Stage stage= (Stage) minimize.getScene().getWindow();
            if(e.getCode().equals(KeyCode.F11)){
                if(stage.isFullScreen()){
                    stage.setFullScreen(false);

                    minimize.setVisible(true);
                    maximize.setVisible(true);
                    close.setVisible(true);
                }
                else {
                    stage.setFullScreen(true);
                    minimize.setVisible(false);
                    maximize.setVisible(false);
                    close.setVisible(false);
                }
            }
            if(e.getCode().equals(KeyCode.ESCAPE)){
                minimize.setVisible(true);
                maximize.setVisible(true);
                close.setVisible(true);
            }
        });
    }

    public void getMediaToHome(MPlayer player,String name){

        if(player!=null){

              videoTitle.setVisible(true);
              videoTitle.setText(name);
              mv.setMediaPlayer(player.getMp());
              mediaSlider.setValue(0);

            mv.fitWidthProperty().bind(mvBoundary.widthProperty());
            mv.fitHeightProperty().bind(mvBoundary.heightProperty());

            player.getMp().currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    mediaSlider.setValue(newValue.toSeconds());
                }
            });

            mediaSlider.maxProperty().bind(Bindings.createDoubleBinding(()-> player.getMp().getTotalDuration().toSeconds(),player.getMp().totalDurationProperty()));

            playingTime.textProperty().bind(Bindings.createStringBinding(()->{
                Duration duration=player.getMp().getCurrentTime();
                return String.format("%02d:%02d:%02d", (int)duration.toHours(),(int) duration.toMinutes()%60 ,(int)duration.toSeconds() %60);
            },player.getMp().currentTimeProperty()));

            totalTime.textProperty().bind(Bindings.createStringBinding(()->{
                Duration duration=player.getMp().getTotalDuration();
                return String.format("%02d:%02d:%02d", (int)duration.toHours(),(int) duration.toMinutes()%60 ,(int)duration.toSeconds() %60);
            }, player.getMp().totalDurationProperty()));

            player.getMp().currentTimeProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    player.getMp().setOnEndOfMedia(()->{
                        player.getMp().seek(Duration.seconds(0));
                        player.getMp().stop();


                        FontAwesomeIconView pauseIcon=new FontAwesomeIconView(FontAwesomeIcon.PLAY);
                        pauseIcon.setGlyphSize(17);
                        pauseIcon.setFill(Paint.valueOf("#ffffff"));
                        pauseIcon.setSmooth(true);
                        playButton.setGraphic(pauseIcon);

                        fadeOut.getStopFadeTransition();
                        playingtoolPane.setVisible(true);
                        playingtoolPane.setOpacity(1.0);

                    });
                }
            });

           mp=player;

        }
    }
}
