import java.util.List;
import java.util.LinkedList;

public class Undo {
    private Captured begin; 
    private Captured end; 
    private List<Captured> captured; 
    
    //constructor
    public Undo(Captured begin, Captured end, Captured captured) {
        this.begin = begin; 
        this.end = end; 
        this.captured = new LinkedList<>(); 
        if (captured != null) {
            this.captured.add(captured); 
        }
    }
    
    public Captured getBegin() {
        return begin; 
    }
    
    public Captured getEnd() {
        return end; 
    }
    
    public List<Captured> getCaptured() {
        return captured; 
    }
    
    public void add(Captured psn) {
        captured.add(psn);
    }
    
    public void update(Captured c) {
        end = c; 
    }

}
