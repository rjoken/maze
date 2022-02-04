package maze;
import java.io.IOException;
import java.util.*;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Maze {
    int rows;
    int cols;
    int currentRow = 0;
    int currentCol = 0;
    boolean animate = false;
    List<Image> frames;
    Boolean done = false;
    Random rng;
    Stack<Pair<Integer, Integer>> stack;


    int[][][] matrix;

    public Maze(int r, int c, boolean a) {
        animate = a;
        rows = r;
        cols = c;
        matrix = new int[rows][cols][5]; // L, R, U, D, visited?
        stack = new Stack<>();
        stack.push(new Pair<>(currentRow, currentCol));
        rng = new Random();
        frames = new LinkedList<>();
    }

    public Maze(int r, int c) {
        this(r, c, false);
    }

    public void dfs() throws IOException {
        while(!stack.empty()) {
            //System.out.printf("%d, %d", currentRow, currentCol);
            matrix[currentRow][currentCol][4] = 1; // mark visited
            List<Character> possible = new ArrayList<>();
            if ((currentCol > 0) && (matrix[currentRow][currentCol - 1][4] == 0)) {
                /** if it is not the first column and the column to the left has not
                 *  already been visited:
                 *  has possible left connection
                 */
                possible.add('L');
            }
            if ((currentCol < cols - 1) && (matrix[currentRow][currentCol + 1][4] == 0)) {
                // has possible right connection
                possible.add('R');
            }
            if ((currentRow > 0) && (matrix[currentRow - 1][currentCol][4] == 0)) {
                // has possible up connection
                possible.add('U');
            }
            if ((currentRow < rows - 1) && (matrix[currentRow + 1][currentCol][4] == 0)) {
                // has possible down connection
                possible.add('D');
            }
            if (!possible.isEmpty()) {
                // if there are possible paths from this cell
                stack.push(new Pair<>(currentRow, currentCol));
                int selection = rng.nextInt(possible.size());
                if (possible.get(selection) == 'L') {
                    // mark cell as having left connection
                    matrix[currentRow][currentCol][0] = 1;
                    // mark left connected cell as having right connection
                    currentCol--;
                    matrix[currentRow][currentCol][1] = 1;
                }
                if (possible.get(selection) == 'R') {
                    // mark cell as having right connection
                    matrix[currentRow][currentCol][1] = 1;
                    // mark right connected cell as having left connection
                    currentCol++;
                    matrix[currentRow][currentCol][0] = 1;
                }
                if (possible.get(selection) == 'U') {
                    // mark cell as having up connection
                    matrix[currentRow][currentCol][2] = 1;
                    // mark up connected cell as having down connection
                    currentRow--;
                    matrix[currentRow][currentCol][3] = 1;
                }
                if (possible.get(selection) == 'D') {
                    // mark cell as having up connection
                    matrix[currentRow][currentCol][3] = 1;
                    // mark up connected cell as having down connection
                    currentRow++;
                    matrix[currentRow][currentCol][2] = 1;
                }
            } else {
                Pair<Integer, Integer> stackTop = stack.pop();
                currentRow = stackTop.getKey();
                currentCol = stackTop.getValue();
            }
            if(animate)
                frames.add(generateFrame(currentCol, currentRow));
        }
        setEntryExit();
    }

    public void prim() {
        throw new NotImplementedException();
    }

    void setEntryExit() throws IOException{
        // maze entry
        matrix[0][0][0] = 1;
        // maze exit
        matrix[rows - 1][cols - 1][1] = 1;

        frames.add(generateFrame(-1, -1));
    }

    Image generateFrame(int c, int r) throws IOException {
        int cellSize = 5;
        int scaleFactor = 6;
        WritableImage canvas = new WritableImage(cols*cellSize, rows*cellSize);
        PixelWriter writer = canvas.getPixelWriter();

        for(int i = 0; i < canvas.getWidth(); i++) {
            for(int j = 0; j < canvas.getHeight(); j++) {
                writer.setColor(i, j, Color.DARKBLUE);
            }
        }
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                int[] cellData = matrix[i][j];
                if(i == r && j == c) {
                    for(int k = 1; k < cellSize-1; k++) {
                        // colour all except the outer 10 pixels of this cell RED
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*i+k, cellSize*j+m, Color.RED);
                        }
                        if(cellData[0] == 1) {
                            // if left connection, set leftmost pixels to RED
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i+m, cellSize*j, Color.RED);
                            }
                        }
                        if(cellData[1] == 1) {
                            // if right connection, set rightmost pixels to RED
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i+m, cellSize*j+(cellSize-1), Color.RED);
                            }
                        }
                        if(cellData[2] == 1) {
                            // if up connection, set topmost pixels to RED
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i, cellSize*j+m, Color.RED);
                            }
                        }
                        if(cellData[3] == 1) {
                            // if down connection, set bottommost pixels to RED
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i+(cellSize-1), cellSize*j+m, Color.RED);
                            }
                        }
                    }
                }

                else {
                    for(int k = 1; k < cellSize-1; k++) {
                        // colour all except the outer 10 pixels of this cell white
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*i+k, cellSize*j+m, Color.WHITE);
                        }
                        if(cellData[0] == 1) {
                            // if left connection, set leftmost pixels to white
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i+m, cellSize*j, Color.WHITE);
                            }
                        }
                        if(cellData[1] == 1) {
                            // if right connection, set rightmost pixels to white
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i+m, cellSize*j+(cellSize-1), Color.WHITE);
                            }
                        }
                        if(cellData[2] == 1) {
                            // if up connection, set topmost pixels to white
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i, cellSize*j+m, Color.WHITE);
                            }
                        }
                        if(cellData[3] == 1) {
                            // if down connection, set bottommost pixels to white
                            for(int m = 1; m < cellSize-1; m++) {
                                writer.setColor(cellSize*i+(cellSize-1), cellSize*j+m, Color.WHITE);
                            }
                        }
                    }
                }

            }
        }
        return resample(canvas, scaleFactor);
    }

    private Image resample(Image input, int scaleFactor) {
        final int W = (int) input.getWidth();
        final int H = (int) input.getHeight();
        final int S = scaleFactor;

        WritableImage output = new WritableImage(
                W * S,
                H * S
        );

        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < S; dy++) {
                    for (int dx = 0; dx < S; dx++) {
                        writer.setArgb(x * S + dx, y * S + dy, argb);
                    }
                }
            }
        }

        return output;
    }

    public List<Image> getFrames() {
        return frames;
    }

}

