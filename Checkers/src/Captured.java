
public class Captured {
    private int x; 
    private int y; 
    private int piece; 
    private Position psn; 
    
    //constructor
    public Captured(Position p, int piece) {
        this.x = p.getXPos(); 
        this.y = p.getYPos();
        psn = p; 
        this.piece = piece; 
    }

    //getter functions 
    public int getXPos() {
        return x; 
    }
    
    public int getYPos() {
        return y; 
    }
    
    public Position getPsn() {
        return psn; 
    }
    
    public int getPiece() {
        return piece; 
    }
}
