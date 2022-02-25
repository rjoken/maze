package maze;

public enum MazeMode {
    DFS(0),
    PRIM(1),
    COMBO(2);

    private int index;

    MazeMode(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static MazeMode byIndex(int index) {
        for(MazeMode m : MazeMode.values()) {
            if(m.index == index) return m;
        }
        throw new IllegalArgumentException("MazeMode index does not exist!");
    }

}
