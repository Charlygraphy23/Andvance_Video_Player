package sample.animation;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeOut {

    private FadeTransition fadeTransition=null;

    public FadeOut(Node e) {
      fadeTransition=new FadeTransition(Duration.seconds(1.5),e);
      fadeTransition.setFromValue(e.getOpacity());
      fadeTransition.setToValue(0);
      fadeTransition.setOnFinished(ee->{
          e.setVisible(false);
      });

    }

    public void getFadeTransition() {
       fadeTransition.play();

    }

    public void getStopFadeTransition() {
        fadeTransition.stop();

    }
    public FadeTransition.Status getStatus() {
       return fadeTransition.getStatus();
    }
}
