package maze;

import javafx.scene.image.Image;

import java.util.List;

// https://gamedevelopment.tutsplus.com/tutorials/introduction-to-javafx-for-game-development--cms-23835

public class AnimatedImage {
    private Image[] frames;
    private double duration;

    public AnimatedImage(List<Image> images) {
        frames = new Image[images.size()];
        for(int i = 0; i < frames.length; i++) {
            frames[i] = images.get(i);
        }
    }

    public Image getFrame(int i) {
        if(i > frames.length)
            return frames[frames.length - 1];
        if(i < 0)
            return frames[0];
        else
            return frames[i];
    }

    public Image getFrameAtTime(double time) { // get the frame to be displayed at a given point in time
        int index = (int)((time % (frames.length * duration)) / duration);
        return frames[index];
    }

    public int getFrameIndexAtTime(double time) {
        return (int) ((time % (frames.length * duration)) / duration);
    }

    public void setDuration(double d) {
        duration = d;
    }

    public double getDuration() {
        return duration;
    }

    public int size() {
        return frames.length;
    }
}
