package sample.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeIn {

    private FadeTransition fadeTransition=null;

    public FadeIn(Node e) {
        fadeTransition=new FadeTransition(Duration.seconds(1),e);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setOnFinished(ee->{
            e.setVisible(true);
        });
    }

    public void getFadeTransition() {
        fadeTransition.play();
    }

    public void getStopFadeTransition() {
        fadeTransition.stop();
    }

}
