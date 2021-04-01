import static org.junit.Assert.*; 
import org.junit.Test; 
import java.util.Map; 
import java.util.Set; 
import java.util.TreeMap; 

public class BoardTest {
    //fields 
    private  final int white = 1;
    private  final int black = 2;
    private  final int wKing = 3;
    private  final int bKing = 4;
    
   //test getMap
    @Test
    public void testPositionGetMap() {
        Position p = new Position(1, 1);
        Position p1 = new Position(1, 1);
        Map<Position, Position> map = new TreeMap<>(); 
        map.put(p, p1); 
        assertEquals(map.get(new Position(1, 1)), p1); 
    }
    
    //test piece movement - black 
    @Test
    public void testBlackMove() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(2, 2), black);
        Set<Position> pSet = board.getMoves(2, 2);
        assertTrue(pSet.contains(new Position(3, 1)));
        assertTrue(pSet.contains(new Position(1, 1)));
        assertEquals(pSet.size(), 3); 
    }
    
    @Test
    public void testBlackMoveRt() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(6, 4), black);
        Set<Position> pSet = board.getMoves(6, 4);
        assertTrue(pSet.contains(new Position(5, 3))); 
        assertEquals(pSet.size(), 3); 
    }
    
    @Test
    public void testBlackMoveLt() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(1, 4), black);
        Set<Position> pSet = board.getMoves(1, 4); 
        assertTrue(pSet.contains(new Position(2, 3))); 
        assertEquals(pSet.size(), 3);
    }
    
    @Test
    public void testBlackMoveBlocked() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(2, 2), black);
        board.add(new Position(3, 3), black);
        board.add(new Position(1, 3), black);
        Set<Position> pSet = board.getMoves(2, 2); 
        assertTrue(pSet.contains(new Position(1, 1))); 
        assertEquals(pSet.size(), 3);  
    }
    
    //test piece movement - white 
    @Test
    public void testWhiteMove() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(4, 4), white);
        Set<Position> pSet = board.getMoves(4, 4);
        assertTrue(pSet.contains(new Position(5, 5))); 
        assertTrue(pSet.contains(new Position(3, 5))); 
        assertEquals(pSet.size(), 2); 
    }
    
    @Test
    public void testWhiteMoveRt() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(6, 4), white);
        Set<Position> pSet = board.getMoves(6, 4);
        assertTrue(pSet.contains(new Position(5, 5))); 
        assertEquals(pSet.size(), 2);
    }
    
    @Test
    public void testWhiteMoveLt() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(1, 4), white);
        Set<Position> pSet = board.getMoves(1, 4); 
        assertTrue(pSet.contains(new Position(2, 5))); 
        assertEquals(pSet.size(), 2); 
    }
    
    @Test
    public void testWhiteMoveBlocked() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(2, 2), white);
        board.add(new Position(3, 3), black);
        board.add(new Position(1, 3), black);
        Set<Position> pSet = board.getMoves(2, 2); 
        assertEquals(pSet.size(), 2);  
    }
    
    //test coronation 
    @Test
    public void testBlackCoronation() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(1, 1), black); 
        board.movePiece(1, 1, 0, 0);
        assertEquals(board.getPiece(new Position(0, 0)), bKing); 
    }
    
    @Test
    public void testWhiteCoronation() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(6, 6), white);
        board.movePiece(6, 6, 7, 7);
        assertEquals(board.getPiece(new Position(7, 7)), wKing); 
        
    }
    
    //test capture 
    @Test
    public void testCapture() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(2, 2), white);
        board.add(new Position(1, 1), black);
        board.movePiece(1, 1, 3, 3);
        assertEquals(board.getPiece(new Position(2, 2)), 0); 
    }
    
    //test game over
    @Test
    public void testGameOver() {
        Board board = new Board(); 
        board.clear(); 
        board.add(new Position(1, 1), white);
        board.add(new Position(2, 2), black);
        board.movePiece(2, 2, 0, 0);
        assertTrue(board.isGameOver()); 
    }
}
