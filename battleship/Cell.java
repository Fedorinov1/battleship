package battleship;

public class Cell {
    public boolean hasBoat;
    public boolean isShot;
    public Boat boat;

    public Cell() {
        this.hasBoat = false;
        this.isShot = false;
        this.boat = null;
    }

    public void shoot() {
        if (isShot) return;

        isShot = true;

        if (hasBoat && boat != null) {
            boat.hit();
        }
    }
    
    public boolean isHit() {
        return isShot && hasBoat;
    }
}
