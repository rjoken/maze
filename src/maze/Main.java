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
import java.nio.ByteBuffer;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {
    private static final int fps = 60;
    private static final int rows = 20;
    private static final int cols = 20;
    private MazeMode currentMode = MazeMode.DFS;
    private Maze maze;
    private List<Image> mazeFrames;

    @Override
    public void start(Stage primaryStage) throws Exception{
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
            @Override
            public void handle(KeyEvent e) {
                if(e.getCode() == KeyCode.F5) {
                    try {
                        doNewMaze(false);
                        doAnimation(gc);
                        System.out.printf("\nIters: %d", maze.iters);
                        scene.addEventFilter(KeyEvent.KEY_PRESSED, this);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if(e.getCode() == KeyCode.F6) try {
                    System.out.printf("\nLoading...");
                    doNewMaze(true);
                    doAnimation(gc);
                    System.out.printf("\nIters: %d", maze.iters);
                    scene.addEventFilter(KeyEvent.KEY_PRESSED, this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if(e.getCode() == KeyCode.X) {
                    try {
                        if(mazeFrames.size() != 0) {
                            Image finalImage = mazeFrames.get(mazeFrames.size() - 1);
                            int width = (int)finalImage.getWidth();
                            int height = (int)finalImage.getHeight();
                            PixelReader pr = finalImage.getPixelReader();
                            byte[] buffer = new byte[width * height * 4];
                            WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraInstance();
                            pr.getPixels(0, 0, width, height, format, buffer, 0, width * 4);
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyy-HHmmss");
                            LocalDateTime now = LocalDateTime.now();
                            String path = String.format("%s\\mazes\\maze-%s.png", System.getProperty("user.home"), dtf.format(now));
                            File imageFile = new File(path);
                            imageFile.getParentFile().mkdirs();
                            try {
                                ImageIO.write(SwingFXUtils.fromFXImage(finalImage, null), "png", imageFile);
                                System.out.printf("Successfully dumped maze image to %s", path);
                            } catch(IOException ex) {
                                ex.printStackTrace();
                            }

                        }
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

    private void doNewMaze(boolean animate) throws Exception{
        maze = new Maze(rows, cols, animate);
        switch(currentMode) {
            case DFS:
                maze.dfs();
                break;
            case PRIM:
                maze.prim();
                break;
            case COMBO:
                maze.combo();
                break;
        }
        mazeFrames = maze.getFrames();
    }

    private void doAnimation(GraphicsContext gc) {
        AnimatedImage anim = new AnimatedImage(mazeFrames);
        anim.setDuration((double)1/fps);

        final long startNanoTime = System.nanoTime();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                gc.drawImage( anim.getFrameAtTime(t), 0, 0 );
                if(anim.getFrameIndexAtTime(t) == mazeFrames.size() - 1) {
                    stop();
                }
            }
        }.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
