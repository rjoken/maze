package maze;
import java.util.*;

import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;

@SuppressWarnings("unused")
class Maze {
    int iters;
    private int rows;
    private int cols;
    private int currentRow = 0;
    private int currentCol = 0;
    private boolean animate;
    private List<Image> frames;
    private Random rng;
    private List<Pair<Integer, Integer>> borderCells;
    Stack<Pair<Integer, Integer>> stack;
    private int[][][] matrix;

    /**
     * Class constructor for Maze.
     * @param r - Number of rows.
     * @param c - Number of columns.
     * @param a - Whether or not the maze should be animated when displaying.
     */
    Maze(int r, int c, boolean a) {
        animate = a;
        rows = r;
        cols = c;
        matrix = new int[rows][cols][5]; // L, R, U, D, visited?
        rng = new Random();
        frames = new LinkedList<>();
        borderCells = new LinkedList<>();
        stack = new Stack<>();
        iters = 0;
    }

    Maze(int r, int c) {
        this(r, c, false);
    }

    /**
     * Depth-first search maze generation algorithm (recursive backtracking)
     */
    void dfs() {
        iters++;
        currentRow = rng.nextInt(rows - 1);
        currentCol = rng.nextInt(cols - 1);
        stack.push(new Pair<>(currentRow, currentCol));
        while(!stack.empty()) {
            iters++;
            matrix[currentRow][currentCol][4] = 1; // mark visited
            List<Character> possible = new ArrayList<>();
            if ((currentCol > 0) && (matrix[currentRow][currentCol - 1][4] == 0)) {
                /* if it is not the first column and the column to the left has not
                   already been visited:
                   has possible left connection
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
                stack.push(new Pair<>(currentRow, currentCol));
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

    /**
     * Maze generation algorithm based on Prim's minimum spanning tree algorithm (unweighted).
     */
    void prim() {
        iters++;
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

    /**
     * Maze generation algorithm using aspects of both DFS and PRIM.
     */
    void combo() {
        iters++;
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
                /* if it is not the first column and the column to the left has not
                   already been visited:
                   has possible left connection
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

    /**
     * Maze generation algorithm using a binary tree.
     */
    void btree() {
        List<Pair<Integer,Integer>> unvisitedCells = new LinkedList<>();
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                unvisitedCells.add(new Pair<>(i, j));
            }
        }
        while(!unvisitedCells.isEmpty()) {
            iters++;
            // randomly select a cell
            int cellSelection = rng.nextInt(unvisitedCells.size());
            currentRow = unvisitedCells.get(cellSelection).getKey();
            currentCol = unvisitedCells.get(cellSelection).getValue();
            // mark as visited
            matrix[currentRow][currentCol][4] = 1;
            unvisitedCells.remove(cellSelection);
            List<Character> possible = new ArrayList<>();
            if (currentCol < cols - 1) {
                possible.add('R');
            }
            if (currentRow > 0) {
                possible.add('U');
            }
            if(!possible.isEmpty()) {
                int selection = rng.nextInt(possible.size());
                if (possible.get(selection) == 'R') {
                    // mark cell as having right connection
                    matrix[currentRow][currentCol][1] = 1;
                    // mark left connected cell as having left connection
                    currentCol++;
                    matrix[currentRow][currentCol][0] = 1;
                }
                if (possible.get(selection) == 'U') {
                    // mark cell as having up connection
                    matrix[currentRow][currentCol][2] = 1;
                    // mark down connected cell as having down connection
                    currentRow--;
                    matrix[currentRow][currentCol][3] = 1;
                }
            }
            if(animate) {
                frames.add(generateFrame(currentRow, currentCol));
            }
        }
        setEntryExit();
    }

    /**
     * Maze generation based on the Aldous-Broder algorithm (random walk)
     */
    void ab() {
        currentRow = rng.nextInt(rows);
        currentCol = rng.nextInt(cols);
        matrix[currentRow][currentCol][4] = 1;
        while(!allCellsVisited(matrix)) {
            iters++;
            List<Character> possible = new ArrayList<>();
            if (currentCol > 0) {
                // has possible left connection
                possible.add('L');
            }
            if (currentCol < cols - 1) {
                // has possible right connection
                possible.add('R');
            }
            if (currentRow > 0) {
                // has possible up connection
                possible.add('U');
            }
            if (currentRow < rows - 1) {
                // has possible down connection
                possible.add('D');
            }
            if (!possible.isEmpty()) {
                // if there are possible paths from this cell, select one
                int selection = rng.nextInt(possible.size());
                if (possible.get(selection) == 'L') {
                    // if the left cell is unvisited, connect
                    if(matrix[currentRow][currentCol - 1][4] == 0) {
                        // mark cell as having left connection
                        matrix[currentRow][currentCol][0] = 1;
                        // mark left connected cell as having right connection
                        currentCol--;
                        matrix[currentRow][currentCol][1] = 1;
                    }
                    else {
                        currentCol--;
                    }
                }
                if (possible.get(selection) == 'R') {
                    // if the right cell is unvisited, connect
                    if(matrix[currentRow][currentCol + 1][4] == 0) {
                        // mark cell as having right connection
                        matrix[currentRow][currentCol][1] = 1;
                        // mark right connected cell as having left connection
                        currentCol++;
                        matrix[currentRow][currentCol][0] = 1;
                    }
                    else {
                        currentCol++;
                    }
                }
                if (possible.get(selection) == 'U') {
                    // if the above cell is unvisited, connect
                    if(matrix[currentRow - 1][currentCol][4] == 0) {
                        // mark cell as having up connection
                        matrix[currentRow][currentCol][2] = 1;
                        // mark up connected cell as having down connection
                        currentRow--;
                        matrix[currentRow][currentCol][3] = 1;
                    }
                    else {
                        currentRow--;
                    }
                }
                if (possible.get(selection) == 'D') {
                    // if the below cell is unvisited, connect
                    if(matrix[currentRow + 1][currentCol][4] == 0) {
                        // mark cell as having down connection
                        matrix[currentRow][currentCol][3] = 1;
                        // mark down connected cell as having up connection
                        currentRow++;
                        matrix[currentRow][currentCol][2] = 1;
                    }
                    else {
                        currentRow++;
                    }
                }
                matrix[currentRow][currentCol][4] = 1;
            }
            if(animate) {
                frames.add(generateFrame(currentRow, currentCol));
            }
        }
        setEntryExit();
    }

    /**
     * Returns true if all cells in the 3d array 'maze' have been visited,
     * false otherwise.
     * @param maze - the maze array to evaluate
     * @return boolean
     */
    private boolean allCellsVisited(int maze[][][]) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(maze[i][j][4] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Set the entrance and exit points of the maze.
     */
    private void setEntryExit() {
        // maze entry
        matrix[0][0][2] = 1;
        // maze exit
        matrix[rows - 1][cols - 1][3] = 1;

        frames.add(generateFrame(-1, -1));
    }

    /**
     * Returns a single image showing the current state of the maze.
     * The current cell will be highlighted in red.
     * @param r - The row of the current cell being explored.
     * @param c - The column of the current cell being explored.
     * @return Image
     */
    private Image generateFrame(int r, int c) {
        int cellSize = Main.cellSize;
        int scaleFactor = Main.scaleFactor;
        WritableImage canvas = new WritableImage(cols*cellSize + (cellSize/2), rows*cellSize + (cellSize/2));
        PixelWriter writer = canvas.getPixelWriter();
        Pair<Integer, Integer> p;
        for(int i = 0; i < canvas.getWidth(); i++) {
            for(int j = 0; j < canvas.getHeight(); j++) {
                writer.setColor(i, j, Color.rgb(0,0,255));
            }
        }
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                int[] cellData = matrix[row][col];
                p = new Pair<>(row, col);
                if (row == r && col == c) {
                    for (int k = 1; k < cellSize - 1; k++) {
                        // colour all except the outer 10 pixels of this cell RED
                        for (int m = 1; m < cellSize - 1; m++) {
                            writer.setColor(cellSize * col + k + 1, cellSize * row + m + 1, Color.RED);
                        }

                        if (cellData[0] == 1) {
                            // if left connection, set leftmost pixels to RED
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + 1, cellSize * row + m + 1, Color.RED);
                            }
                        }
                        if (cellData[1] == 1) {
                            // if right connection, set rightmost pixels to RED
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + (cellSize - 1) + 1, cellSize * row + m + 1, Color.RED);
                            }
                        }
                        if (cellData[2] == 1) {
                            // if up connection, set topmost pixels to RED
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + m + 1, cellSize * row + 1, Color.RED);
                            }
                        }
                        if (cellData[3] == 1) {
                            // if down connection, set bottommost pixels to RED
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + m + 1, cellSize * row + (cellSize - 1) + 1, Color.RED);
                            }
                        }

                    }
                } else if (borderCells.contains(p) || stack.contains(p)) {
                    for (int k = 1; k < cellSize - 1; k++) {
                        // colour all except the outer 10 pixels of this cell GREEN
                        for (int m = 1; m < cellSize - 1; m++) {
                            writer.setColor(cellSize * col + k + 1, cellSize * row + m + 1, Color.GREEN);
                        }

                        if (cellData[0] == 1) {
                            // if left connection, set leftmost pixels to GREEN
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + 1, cellSize * row + m + 1, Color.GREEN);
                            }
                        }
                        if (cellData[1] == 1) {
                            // if right connection, set rightmost pixels to GREEN
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + (cellSize - 1) + 1, cellSize * row + m + 1, Color.GREEN);
                            }
                        }
                        if (cellData[2] == 1) {
                            // if up connection, set topmost pixels to GREEN
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + m + 1, cellSize * row + 1, Color.GREEN);
                            }
                        }
                        if (cellData[3] == 1) {
                            // if down connection, set bottommost pixels to GREEN
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + m + 1, cellSize * row + (cellSize - 1) + 1, Color.GREEN);
                            }
                        }

                    }
                } else {
                    for (int k = 1; k < cellSize - 1; k++) {
                        // colour all except the outer 10 pixels of this cell WHITE
                        for (int m = 1; m < cellSize - 1; m++) {
                            writer.setColor(cellSize * col + k + 1, cellSize * row + m + 1, Color.WHITE);
                        }

                        if (cellData[0] == 1) {
                            // if left connection, set leftmost pixels to white
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + 1, cellSize * row + m + 1, Color.WHITE);
                            }
                        }
                        if (cellData[1] == 1) {
                            // if right connection, set rightmost pixels to white
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + (cellSize - 1) + 1, cellSize * row + m + 1, Color.WHITE);
                            }
                        }
                        if (cellData[2] == 1) {
                            // if up connection, set topmost pixels to white
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + m + 1, cellSize * row + 1, Color.WHITE);
                            }
                        }
                        if (cellData[3] == 1) {
                            // if down connection, set bottommost pixels to white
                            for (int m = 1; m < cellSize - 1; m++) {
                                writer.setColor(cellSize * col + m + 1, cellSize * row + (cellSize - 1) + 1, Color.WHITE);
                            }
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

    /**
     * Takes an image and returns the same image resized based on a given scalar S.
     * @param input - input image.
     * @param S - scalar.
     * @return - Newly scaled image.
     */
    private Image resample(Image input, int S) {
        final int W = (int) input.getWidth();
        final int H = (int) input.getHeight();

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

    /**
     * @return - The list of images containing all frames of the maze generation.
     */
    List<Image> getFrames() {
        return frames;
    }
}

