package sample.model;

import javafx.scene.media.MediaPlayer;

public class MPlayer {

    private MediaPlayer mp;

    public MPlayer() {
    }

    public MPlayer(MediaPlayer mp) {
        this.mp = mp;
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }
}
