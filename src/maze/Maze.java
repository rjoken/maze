package maze;
import java.io.IOException;
import java.util.*;

import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class Maze {
    private static final int cellSize = 4;
    int iters;
    private int rows;
    private int cols;
    private int currentRow = 0;
    private int currentCol = 0;
    private boolean animate;
    private List<Image> frames;
    private Random rng;
    private Stack<Pair<Integer, Integer>> stack;
    private List<Pair<Integer, Integer>> borderCells;
    private int[][][] matrix;

    public Maze(int r, int c, boolean a) {
        animate = a;
        rows = r;
        cols = c;
        matrix = new int[rows][cols][5]; // L, R, U, D, visited?
        rng = new Random();
        frames = new LinkedList<>();
        borderCells = new LinkedList<>();
        iters = 0;
    }

    public Maze(int r, int c) {
        this(r, c, false);
    }

    public void dfs() throws IOException {
        currentRow = rng.nextInt(rows - 1);
        currentCol = rng.nextInt(cols - 1);
        stack = new Stack<>();
        stack.push(new Pair<>(currentRow, currentCol));
        while(!stack.empty()) {
            iters++;
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
                frames.add(generateFrame(currentRow, currentCol));
        }
        setEntryExit();
    }

    public void prim() throws IOException {
        currentRow = rng.nextInt(rows - 1);
        currentCol = rng.nextInt(cols - 1);
        matrix[currentRow][currentCol][4] = 1;
        if ((currentCol > 0) && (matrix[currentRow][currentCol - 1][4] == 0)) {
            // has possible left connection
            borderCells.add(new Pair<>(currentRow, currentCol - 1));
        }
        if ((currentCol < cols - 1) && (matrix[currentRow][currentCol + 1][4] == 0)) {
            // has possible right connection
            borderCells.add(new Pair<>(currentRow, currentCol + 1));
        }
        if ((currentRow > 0) && (matrix[currentRow - 1][currentCol][4] == 0)) {
            // has possible up connection
            borderCells.add(new Pair<>(currentRow - 1, currentCol));
        }
        if ((currentRow < rows - 1) && (matrix[currentRow + 1][currentCol][4] == 0)) {
            // has possible down connection
            borderCells.add(new Pair<>(currentRow + 1, currentCol));
        }
        while(!borderCells.isEmpty()) {
            iters++;
            // select a random bordercell
            int borderSelection = rng.nextInt(borderCells.size());
            List<Pair<Pair<Integer, Integer>, Character>> neighbours = new ArrayList<>();
            currentRow = borderCells.get(borderSelection).getKey();
            currentCol = borderCells.get(borderSelection).getValue();
            if (currentCol > 0) {
                // has possible left connection
                // if the cell to the left is already in the maze, add it to list of neighbours
                if(matrix[currentRow][currentCol - 1][4] == 1) {
                    neighbours.add(new Pair<>(new Pair<>(currentRow, currentCol - 1), 'R'));
                }
                // else, add it to the border cells
                else {
                    if(!borderCells.contains(new Pair<>(currentRow, currentCol - 1)))
                        borderCells.add(new Pair<>(currentRow, currentCol - 1));
                    //matrix[currentRow][currentCol - 1][4] = 1;
                }
            }
            if (currentCol < cols - 1) {
                // has possible right connection
                // if the cell to the right is already in the maze, add it to the list of neighbours
                if(matrix[currentRow][currentCol + 1][4] == 1) {
                    neighbours.add(new Pair<>(new Pair<>(currentRow, currentCol + 1), 'L'));
                }
                // else, add it to the border cells
                else {
                    if(!borderCells.contains(new Pair<>(currentRow, currentCol + 1)))
                        borderCells.add(new Pair<>(currentRow, currentCol + 1));
                    //matrix[currentRow][currentCol + 1][4] = 1;
                }
            }
            if (currentRow > 0)  {
                // has possible up connection
                // if the cell above is already in the maze, add it to the list of neighbours
                if(matrix[currentRow - 1][currentCol][4] == 1) {
                    neighbours.add(new Pair<>(new Pair<>(currentRow - 1, currentCol), 'D'));
                }
                // else, add it to the border cells
                else {
                    if(!borderCells.contains(new Pair<>(currentRow - 1, currentCol)))
                        borderCells.add(new Pair<>(currentRow - 1, currentCol));
                    //matrix[currentRow - 1][currentCol][4] = 1;
                }
            }
            if (currentRow < rows - 1) {
                // has possible down connection
                // if the cell below is already in the maze, add it to the list of neighbours
                if(matrix[currentRow + 1][currentCol][4] == 1) {
                    neighbours.add(new Pair<>(new Pair<>(currentRow + 1, currentCol), 'U'));
                }
                // else, add it to the border cells
                else {
                    if(!borderCells.contains(new Pair<>(currentRow + 1, currentCol)))
                        borderCells.add(new Pair<>(currentRow + 1, currentCol));
                    //matrix[currentRow + 1][currentCol][4] = 1;
                }
            }

            // choose a random neighbour
            int neighbourSelection = rng.nextInt(neighbours.size());
            // remove the wall connecting the selected neighbour with the current cell
            Character direction = neighbours.get(neighbourSelection).getValue();
            if(direction == 'L') {
                // mark current borderCell as having right connection, and neighbour as having left connection
                matrix[currentRow][currentCol][1] = 1;
                matrix[currentRow][currentCol + 1][0]  = 1;
            }
            if(direction == 'R') {
                // mark current borderCell as having left connection, and neighbour as having right connection
                matrix[currentRow][currentCol][0] = 1;
                matrix[currentRow][currentCol - 1][1]  = 1;
            }
            if(direction == 'U') {
                // mark current borderCell as having down connection, and neighbour as having up connection
                matrix[currentRow][currentCol][3] = 1;
                matrix[currentRow + 1][currentCol][2]  = 1;
            }
            if(direction == 'D') {
                // mark current borderCell as having up connection, and neighbour as having down connection
                matrix[currentRow][currentCol][2] = 1;
                matrix[currentRow - 1][currentCol][3]  = 1;
            }
            // mark borderCell as being visited
            matrix[currentRow][currentCol][4] = 1;
            // remove borderCell from borderCell list
            borderCells.remove(borderSelection);
            // generate frame
            if(animate)
                frames.add(generateFrame(currentRow, currentCol));
        }
        setEntryExit();
    }

    public void combo() throws IOException {
        // start by randomly adding one cell to the list of border cells
        currentRow = rng.nextInt(rows - 1);
        currentCol = rng.nextInt(cols - 1);
        matrix[currentRow][currentCol][4] = 1; // mark visited
        borderCells.add(new Pair<>(currentRow, currentCol));
        // repeat until border cells list is empty
        while(!borderCells.isEmpty()) {
            iters++;
            // choose a cell from the border cells, carve a passage to a random unvisited neighbour
            // 50-50 chance that it will choose either the newest or a random member of the border list
            int die = rng.nextInt(2);
            int borderSelection;
            if(die == 0) {
                //break through via the newest member of the list
                borderSelection = borderCells.size() - 1;
            }
            else {
                //select a random member of the list
                borderSelection = rng.nextInt(borderCells.size());
            }
            currentRow = borderCells.get(borderSelection).getKey();
            currentCol = borderCells.get(borderSelection).getValue();
            List<Character> possible = new ArrayList<>();
            // get possible connections
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
                // add the selected neighbour to the border cells.
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
                borderCells.add(new Pair<>(currentRow, currentCol));
                matrix[currentRow][currentCol][4] = 1;
            } else {
                // if there are no unvisited neighbours, remove cell from border
                borderCells.remove(borderSelection);
            }

            if(animate) {
                frames.add(generateFrame(currentRow, currentCol));
            }
        }
        setEntryExit();
    }

    private void setEntryExit() throws IOException{
        // maze entry
        matrix[0][0][0] = 1;
        // maze exit
        matrix[rows - 1][cols - 1][1] = 1;

        frames.add(generateFrame(-1, -1));
    }

    private Image generateFrame(int r, int c) throws IOException {

        int scaleFactor = 4;
        WritableImage canvas = new WritableImage(cols*cellSize + (cellSize/2), rows*cellSize + (cellSize/2));
        PixelWriter writer = canvas.getPixelWriter();
        Pair<Integer, Integer> p;
        for(int i = 0; i < canvas.getWidth(); i++) {
            for(int j = 0; j < canvas.getHeight(); j++) {
                writer.setColor(i, j, Color.DARKBLUE);
            }
        }
        int row = 0;
        int col = 0;
        for(int i = 0; i < rows * cols; i++, col++) {
            if(col == cols) {
                col = 0;
                row = row + 1;
            }
            int[] cellData = matrix[row][col];
            p = new Pair<>(row,col);
            if(row == r && col == c) {
                for(int k = 1; k < cellSize-1; k++) {
                    // colour all except the outer 10 pixels of this cell RED
                    for(int m = 1; m < cellSize-1; m++) {
                        writer.setColor(cellSize*row+k+1, cellSize*col+m+1, Color.RED);
                    }
                    if(cellData[0] == 1) {
                        // if left connection, set leftmost pixels to RED
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+m+1, cellSize*col+1, Color.RED);
                        }
                    }
                    if(cellData[1] == 1) {
                        // if right connection, set rightmost pixels to RED
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+m+1, cellSize*col+(cellSize-1)+1, Color.RED);
                        }
                    }
                    if(cellData[2] == 1) {
                        // if up connection, set topmost pixels to RED
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+1, cellSize*col+m+1, Color.RED);
                        }
                    }
                    if(cellData[3] == 1) {
                        // if down connection, set bottommost pixels to RED
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+(cellSize-1), cellSize*col+m, Color.RED);
                        }
                    }
                }
            }
            else if(borderCells.contains(p)) {
                for(int k = 1; k < cellSize-1; k++) {
                    // colour all except the outer 10 pixels of this cell GREEN
                    for(int m = 1; m < cellSize-1; m++) {
                        writer.setColor(cellSize*row+k+1, cellSize*col+m+1, Color.GREEN);
                    }
                    if(cellData[0] == 1) {
                        // if left connection, set leftmost pixels to GREEN
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+m+1, cellSize*col+1, Color.GREEN);
                        }
                    }
                    if(cellData[1] == 1) {
                        // if right connection, set rightmost pixels to GREEN
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+m+1, cellSize*col+(cellSize-1)+1, Color.GREEN);
                        }
                    }
                    if(cellData[2] == 1) {
                        // if up connection, set topmost pixels to GREEN
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+1, cellSize*col+m+1, Color.GREEN);
                        }
                    }
                    if(cellData[3] == 1) {
                        // if down connection, set bottommost pixels to GREEN
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+(cellSize-1)+1, cellSize*col+m+1, Color.GREEN);
                        }
                    }
                }
            }
            else {
                for(int k = 1; k < cellSize-1; k++) {
                    // colour all except the outer 10 pixels of this cell white
                    for(int m = 1; m < cellSize-1; m++) {
                        writer.setColor(cellSize*row+k+1, cellSize*col+m+1, Color.WHITE);
                    }
                    if(cellData[0] == 1) {
                        // if left connection, set leftmost pixels to white
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+m+1, cellSize*col+1, Color.WHITE);
                        }
                    }
                    if(cellData[1] == 1) {
                        // if right connection, set rightmost pixels to white
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+m+1, cellSize*col+(cellSize-1)+1, Color.WHITE);
                        }
                    }
                    if(cellData[2] == 1) {
                        // if up connection, set topmost pixels to white
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+1, cellSize*col+m+1, Color.WHITE);
                        }
                    }
                    if(cellData[3] == 1) {
                        // if down connection, set bottommost pixels to white
                        for(int m = 1; m < cellSize-1; m++) {
                            writer.setColor(cellSize*row+(cellSize-1)+1, cellSize*col+m+1, Color.WHITE);
                        }
                    }
                }
            }
            // entrance & exit stuff

            if(r == -1 && c == -1) {
                writer.setColor(2, 0, Color.WHITE);
                writer.setColor(3, 0, Color.WHITE);
                writer.setColor((int)canvas.getWidth() - 3, (int)canvas.getHeight() - 1, Color.WHITE);
                writer.setColor((int)canvas.getWidth() - 4, (int)canvas.getHeight() - 1, Color.WHITE);
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

