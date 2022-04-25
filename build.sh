mkdir class
javac -d class src/maze/*.java
jar cfm maze.jar manifest.txt -C class maze
java -jar maze.jar