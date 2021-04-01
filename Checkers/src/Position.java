
public class Position implements Comparable<Position> {
    private int xPos; 
    private int yPos; 
    
    //constructor
    public Position(int xPos, int yPos) {
        this.xPos = xPos; 
        this.yPos = yPos; 
    }
    
    //getter functions
    public int getXPos() {
        return xPos; 
    }
    
    public int getYPos() {
        return yPos; 
    }
    
    //override inherited methods
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true; 
        } 
        
        if (!(o instanceof Position)) {
            return false; 
        }
        
        Position psn = (Position) o; 
        return Integer.compare(xPos, psn.xPos) == 0 && 
                Integer.compare(yPos, psn.yPos) == 0; 
    }
    
    @Override
    public int compareTo(Position psn) {
        if (psn.getXPos() == this.xPos && psn.getYPos() == this.yPos) {
            return 0; 
        } else if (xPos < psn.getXPos()) {
            return 1; 
        } else if (psn.getXPos() == xPos && psn.getYPos() > yPos) {
            return 1; 
        } else {
            return -1; 
        }
    }

}
