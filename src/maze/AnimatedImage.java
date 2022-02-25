package maze;

import javafx.scene.image.Image;

import java.util.List;

// https://gamedevelopment.tutsplus.com/tutorials/introduction-to-javafx-for-game-development--cms-23835

 @SuppressWarnings({"unused", "SameParameterValue"})
 class AnimatedImage {
    private Image[] frames;
    private double duration;

    AnimatedImage(List<Image> images) {
        frames = new Image[images.size()];
        for(int i = 0; i < frames.length; i++) {
            frames[i] = images.get(i);
        }
    }

    Image getFrame(int i) {
        if(i > frames.length)
            return frames[frames.length - 1];
        if(i < 0)
            return frames[0];
        else
            return frames[i];
    }

    Image getFrameAtTime(double time) { // get the frame to be displayed at a given point in time
        int index = (int)((time % (frames.length * duration)) / duration);
        return frames[index];
    }

    int getFrameIndexAtTime(double time) {
        return (int) ((time % (frames.length * duration)) / duration);
    }

    void setDuration(double d) {
        duration = d;
    }

    double getDuration() {
        return duration;
    }

    int size() {
        return frames.length;
    }
}
