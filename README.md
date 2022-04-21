# mazegen
## Software for generating, solving, and analysing mazes
### Overview
This software package contains two components: the maze generation software, and the maze solving software. The maze generation software implements five different algorithms each with different properties, advantages, and disadvantages.
The five algorithms included are:
- Recursive Backtracking
- Prim's Algorithm
- Growing Tree Algorithm
- Binary Tree Algorithm
- Aldous-Broder Algorithm

### Using the Maze Generation Software
The maze generation software requires Java to be installed on the target machine on which it is to be run. A compiled JAR file can be obtained from the [Releases Page](https://github.com/rjoken/maze/releases). The current version is v1.0.3.

The source has been tested and built using IntelliJ IDEA 2018.3.4.

The JAR file may be run using the following command, provided that Java is installed on the target machine:
`java -jar PATH-TO-MAZE.JAR`

The Java executable supports a variety of runtime arguments:
- `--rows [n]`: The number of rows for maze generation. Default is 20.
- `--cols [n]`: The number of columns for maze generation. Default is 20.
- `--fps [n]`: The speed at which the maze generation animation should be displayed. Default is 30.
- `--scalefactor [n]`: A multiplier by which to scale the image output (helps to scale down large maze sizes or scale up small maze sizes). Default is 4.

Once open, the maze generation algorithms may be cycled using the arrow keys. In order to generate a maze without an animation, press F5. In order to generate a maze with an animation, press F6. To Export the generated maze as an image file (placed in your home directory/mazes), press 'X'.

### Using the Maze Solving Python Script
The release folder also contains a Python 3 script for solving mazes. The script is contained inside the "solver" folder and can be executed using the following commands:

`pip install -r requirements.txt`

`python solve.py -i PATH-TO-IMAGEFILE.PNG`

An output image should be generated in the same directory as the Python script showing the solution to the maze.
Like the Java program, the Python script also accepts some command-line arguments:
- `-h`: Show program usage instructions.
- `-d`: Show debug information.
- `-o <outputfile.txt>`: Write maze statistic information to a file.
