package maze;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {
    private static int fps = 30;
    private static int rows = 20;
    private static int cols = 20;
    static final int cellSize = 4;
    static int scaleFactor = 4;

    private MazeMode currentMode = MazeMode.BACKTRACK;
    private Maze maze;
    private List<Image> mazeFrames;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(currentMode.toString());
        doNewMaze(false);
        Group root = new Group();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        Canvas canvas = new Canvas(mazeFrames.get(0).getWidth(), mazeFrames.get(0).getHeight());
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        primaryStage.show();

        EventHandler<KeyEvent> handleKey = new EventHandler<KeyEvent>() {
            /**
             * Handles key events.
             * Pressing F5 will generate a new maze given the selected generation algorithm WITHOUT animation.
             * Pressing F6 will do the same WITH animation
             * Pressing 'X' will export the final maze frame as an image file.
             * Pressing the left and right arrow keys will cycle between maze generation algorithms, changing the selection.
             * @param e - The KeyEvent to be handled.
             */
            @Override
            public void handle(KeyEvent e) {
                if(e.getCode() == KeyCode.F5) {
                    try {
                        doNewMaze(false);
                        doAnimation(gc, mazeFrames);
                        System.out.printf("\nMaze generated in %d iterations.\n", maze.iters);
                        scene.addEventFilter(KeyEvent.KEY_PRESSED, this);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if(e.getCode() == KeyCode.F6) try {
                    System.out.println("\nLoading...");
                    doNewMaze(true);
                    doAnimation(gc, mazeFrames);
                    System.out.printf("\nMaze generated in %d iterations.\n", maze.iters);
                    scene.addEventFilter(KeyEvent.KEY_PRESSED, this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if(e.getCode() == KeyCode.X) {
                    try {
                        exportImage(mazeFrames);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if(e.getCode() == KeyCode.RIGHT) {
                    if(currentMode.getIndex() == MazeMode.values().length-1) {
                        int newIndex = 0;
                        currentMode = MazeMode.byIndex(newIndex);
                    }
                    else {
                        int newIndex = currentMode.getIndex() + 1;
                        currentMode = MazeMode.byIndex(newIndex);
                    }
                    primaryStage.setTitle(currentMode.toString());
                }
                if(e.getCode() == KeyCode.LEFT) {
                    if(currentMode.getIndex() == 0) {
                        int newIndex = MazeMode.values().length-1;
                        currentMode = MazeMode.byIndex(newIndex);
                    }
                    else {
                        int newIndex = currentMode.getIndex() - 1;
                        currentMode = MazeMode.byIndex(newIndex);
                    }
                    primaryStage.setTitle(currentMode.toString());
                }
            }
        };
        scene.addEventFilter(KeyEvent.KEY_PRESSED, handleKey);

    }

    /**
     * Creates a new maze and starts generation based on the currently selected
     * generation algorithm. If "animate" is true, a list of images corresponding
     * to each step of the maze generation will also be generated.
     * @param animate - Whether or not the maze generation should be displayed as an animation frame-by-frame
     */
    private void doNewMaze(boolean animate) {
        maze = new Maze(rows, cols, animate);
        switch(currentMode) {
            case BACKTRACK:
                maze.dfs();
                break;
            case PRIM:
                maze.prim();
                break;
            case COMBO:
                maze.combo();
                break;
            case BTREE:
                maze.btree();
                break;
            case ALDOUSBRODER:
                maze.ab();
                break;
        }
        mazeFrames = maze.getFrames();
    }

    /**
     * Draws each frame in a list of frames to the graphics context (gc) sequentially
     * a constant "frames per second" value.
     * @param gc - The graphics context to draw pixels to.
     * @param frames - The list of images to draw.
     */
    private void doAnimation(GraphicsContext gc, List<Image> frames) {
        AnimatedImage anim = new AnimatedImage(frames);
        anim.setDuration((double)1/fps);

        final long startNanoTime = System.nanoTime();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                gc.drawImage( anim.getFrameAtTime(t), 0, 0 );
                if(anim.getFrameIndexAtTime(t) == frames.size() - 1) {
                    stop();
                }
            }
        }.start();
    }

    /**
     * Exports the last image in a given list of images (frames) as a file in
     * {home}/mazes/maze-{date}.png.
     * @param frames - The list of images to export from.
     */
    private void exportImage(List<Image> frames) {
        if(frames.size() != 0) {
            Image finalImage = frames.get(frames.size() - 1);
            int width = (int)finalImage.getWidth();
            int height = (int)finalImage.getHeight();
            PixelReader pr = finalImage.getPixelReader();
            byte[] buffer = new byte[width * height * 4];
            WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraInstance();
            pr.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

            // get date and time for file name
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyy-HHmmss");
            LocalDateTime now = LocalDateTime.now();
            String path = String.format("%s\\mazes\\maze-%s.png", System.getProperty("user.home"), dtf.format(now));
            File imageFile = new File(path);
            //noinspection ResultOfMethodCallIgnored
            imageFile.getParentFile().mkdirs();

            // try to write the file. access may be denied etc.
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(finalImage, null), "png", imageFile);
                System.out.printf("Successfully dumped maze image to %s", path);
            } catch(IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * Prints usage instructions for this program.
     */
    private static void showUsage() {
        System.out.println("Usage: java {ApplicationName} " +
                "[--rows n] [--cols n] [--scalefactor n] [--fps n]");
        System.out.println("Program controls:");
        System.out.println("F5: Generate and show maze WITHOUT animation.");
        System.out.println("F6: Generate and show maze WITH animation.");
        System.out.println("X:  Export maze image to {home directory}/mazes/.");
        System.out.println("LEFT and RIGHT arrow keys: Cycle between maze generation methods.");
    }

    public static void main(String[] args) {
        List<String> cmd = Arrays.asList(args);
        try {
            if (cmd.contains("--rows")) {
                cols = Integer.parseInt(args[cmd.indexOf("--rows") + 1]);
            }
            if(cmd.contains("--cols")) {
                rows = Integer.parseInt(args[cmd.indexOf("--cols") + 1]);
            }
            if(cmd.contains("--scalefactor")) {
                scaleFactor = Integer.parseInt(args[cmd.indexOf("--scalefactor") + 1]);
            }
            if(cmd.contains("--fps")) {
                fps = Math.min(Integer.parseInt(args[cmd.indexOf("--fps") + 1]), 60);
            }
        }
        catch(Exception e) {
            System.out.println("Your command-line arguments were invalid. Try again.");
            showUsage();
            System.exit(0);
        }
        showUsage();
        launch(args);
    }
}
