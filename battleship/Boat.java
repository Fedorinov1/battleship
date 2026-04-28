package battleship;

public class Boat {
    public int length;
    public int hits;

    public Boat(int length) {
        this.length = length;
        this.hits = 0;
    }

    public void hit() {
        this.hits++;
    }

    public boolean isDestroyed() {
        return hits == length;
    }
}
