import java.util.Set; 
import java.util.Map; 
import java.util.TreeSet; 
import java.util.TreeMap; 

//import javax.swing.*; 

import java.awt.Graphics; 
import java.awt.Graphics2D; 
import java.awt.Color; 

public class Board {
    private int[][] board; 
    private static  int white = 1; 
    private static  int black = 2; 
    private static  int wKing = 3; 
    private static  int bKing = 4; 
    public boolean canBlackJump = false; 

    //constructor
    public Board() {
        board = new int[8][8]; 
        for (int i = 0; i < 3; i++) {
            if (i == 0 || i == 2) {
                for (int j = 1; j < 8; j += 2) {
                    board[j][i] = white; 
                } 
            } else {
                for (int j = 0; j < 8; j += 2) {
                    board[j][i] = white;
                }
            }
        }
        for (int i = 5; i < 8; i++) {
            if (i == 5 || i == 7) {
                for (int j = 0; j < 8; j += 2) {
                    board[j][i] = black; 
                }
            } else {
                for (int j = 1; j < 8; j += 2) {
                    board[j][i] = black;
                }
            }
        }
    }

    public Set<Position> getMoves(int xPos, int yPos) {
        Set<Position> pSet = new TreeSet<>(); 
        if (xPos < 0 || xPos >= 8 || yPos < 0 || yPos >= 8) {
            return pSet; 
        }

        int piece = board[xPos][yPos]; 

        //permissible moves at beginning of game
        if (piece == white) {
            if (board[xPos ][yPos + 1 ] == 0 && xPos  < 8) {
                pSet.add(new Position(xPos + 1, yPos + 1)); 
            }
            if (board[xPos ][yPos + 1] == 0 && xPos  < 8) {
                pSet.add(new Position(xPos - 1, yPos + 1)); 
            }

            if (board[xPos ][yPos + 1] == 0 && xPos - 1 >= 0) {
                pSet.add(new Position(xPos + 1, yPos + 1));
            } 
            if (board[xPos ][yPos + 1] == 0 && xPos - 1 >= 0) {
                pSet.add(new Position(xPos - 1, yPos + 1));

            }  


        } else if (piece == black) {
            if (board[xPos ][yPos - 1] == 0 && xPos + 1 < 8) { 
                pSet.add(new Position(xPos + 1, yPos - 1));
            } 
            if (board[xPos ][yPos - 1] == 0 && xPos + 1 < 8) { 
                pSet.add(new Position(xPos - 1, yPos + 1)); 
            }
            if (board[xPos][yPos - 1] == 0 && xPos  >= 0) {
                pSet.add(new Position(xPos - 1, yPos - 1));
            }

            if (board[xPos][yPos - 1] == 0 && xPos  >= 0) {
                pSet.add(new Position(xPos + 1, yPos - 1));
            }


        } else if (piece == wKing) {   
            //code that behaves similar to black piece
            if (board[xPos][yPos - 1] == 0 && xPos  >= 0) {
                pSet.add(new Position(xPos - 1, yPos - 1));
            }

            if (board[xPos][yPos - 1] == 0 && xPos  >= 0) {
                pSet.add(new Position(xPos + 1, yPos - 1));
            }

          //code that behaves like white piece at white side of board 
        } else if (piece == wKing) {
            if (board[xPos ][yPos + 1 ] == 0 && xPos  < 8) {
                pSet.add(new Position(xPos + 1, yPos + 1)); 
            }
            if (board[xPos ][yPos + 1] == 0 && xPos  < 8) {
                pSet.add(new Position(xPos - 1, yPos + 1)); 
            }

            if (board[xPos ][yPos + 1] == 0 && xPos - 1 >= 0) {
                pSet.add(new Position(xPos + 1, yPos + 1));
            } 
            if (board[xPos ][yPos + 1] == 0 && xPos - 1 >= 0) {
                pSet.add(new Position(xPos - 1, yPos + 1)); 
            }  
        } else if (piece == bKing) {
            //does white-ish moves when it gets to white side of board
            if (board[xPos ][yPos + 1] == 0 && xPos - 1 >= 0) {
                pSet.add(new Position(xPos + 1, yPos + 1));
            } 
            if (board[xPos ][yPos + 1] == 0 && xPos - 1 >= 0) {
                pSet.add(new Position(xPos - 1, yPos + 1));
            }   
        } else if (piece == bKing) {
            if (board[xPos ][yPos - 1] == 0 && xPos + 1 < 8) { 
                pSet.add(new Position(xPos + 1, yPos - 1));
            }
            if (board[xPos ][yPos - 1] == 0 && xPos + 1 < 8) { 
                pSet.add(new Position(xPos - 1, yPos + 1)); 
            }
            if (board[xPos][yPos - 1] == 0 && xPos  >= 0) {
                pSet.add(new Position(xPos - 1, yPos - 1));
            }

            if (board[xPos][yPos - 1] == 0 && xPos  >= 0) {
                pSet.add(new Position(xPos + 1, yPos - 1));
            }
        }
        return pSet; 
    }

    public boolean jump(int piece, int xA, int yA, int xB, int yB, int xC, int yC) {

        if (xC >= 8 || xC < 0 || yC >= 8 || yC < 0 || board[xC][yC] != 0) {
            return false;
        }

        if (piece == white || piece == wKing) {
            if (board[xB][yB] == black || board[xB][yB] == bKing) {
                return true;
            }
        } else {
            if (board[xB][yB] == white || board[xB][yB] == wKing) {
                return true;
            }
        }
        return false;
    }

    //a map of piece destinations
    public Map<Position, Position> getDest(int xPos, int yPos) {
        Map<Position, Position> pMap = new TreeMap<>();
        int piece = board[xPos][yPos]; 
        if (piece == white || piece == wKing) {
            if (jump(white, xPos, yPos, xPos + 1, yPos + 1, xPos + 2, yPos + 2)) {
                pMap.put(new Position(xPos + 2, yPos + 2), new Position(xPos + 1, yPos + 1));
            }
            if (jump(white, xPos, yPos, xPos - 1, yPos + 1, xPos - 2, yPos + 2)) {
                pMap.put(new Position(xPos - 2, yPos + 2), new Position(xPos - 1, yPos + 1));
            }

            //control king jumps
            if (piece == wKing) {
                if (jump(wKing, xPos, yPos, xPos - 1, yPos - 1, xPos - 2, yPos - 2)) {
                    pMap.put(new Position(xPos - 2, yPos - 2), new Position(xPos - 1, yPos - 1));
                }
                if (jump(wKing, xPos, yPos, xPos + 1, yPos - 1, xPos + 2, yPos - 2)) {
                    pMap.put(new Position(xPos + 2, yPos - 2), new Position(xPos + 1, yPos - 1));
                }
            }
        }
        if (piece == black || piece == bKing) {
            if (jump(black, xPos, yPos, xPos - 1, yPos - 1, xPos - 2, yPos - 2)) {
                pMap.put(new Position(xPos - 2, yPos - 2), new Position(xPos - 1, yPos - 1));
            }
            if (jump(black, xPos, yPos, xPos + 1, yPos - 1, xPos + 2, yPos - 2)) {
                pMap.put(new Position(xPos + 2, yPos - 2), new Position(xPos + 1, yPos - 1));
            }

            //control king jumps
            if (piece == bKing) {
                if (jump(bKing, xPos, yPos, xPos + 1, yPos + 1, xPos + 2, yPos + 2)) {
                    pMap.put(new Position(xPos + 2, yPos + 2), new Position(xPos + 1, yPos + 1));
                }
                if (jump(bKing, xPos, yPos, xPos - 1, yPos + 1, xPos - 2, yPos + 2)) {
                    pMap.put(new Position(xPos - 2, yPos + 2), new Position(xPos - 1, yPos + 1));
                }
            }

        }
        return pMap;

    }

    //check if white can make a move
    public boolean canWhiteMove() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == white || board[i][j] == wKing) {
                    if (!getDest(i, j).isEmpty()) {
                        return true; 
                    }
                }
            }
        }
        return false; 
    }

    //check if black can make a move
    public boolean canBlackMove() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == black || board[i][j] == bKing) {
                    if (!getDest(i, j).isEmpty()) {
                        return true; 
                    }
                }
            }
        }
        return false; 
    }

    //actually make the pieces move
    public void movePiece(int x, int y, int x1, int y1) {
        int piece = board[x][y]; 
        //populate the board with moved pieces
        board[x1][y1] = piece; 
        board[x][y] = 0; //original position now empty
        int xDiff = Math.abs(x - x1); 
        if (xDiff == 2) {
            int newX = (x + x1) / 2; 
            int newY = (y + y1) / 2; 
            board[newX][newY] = 0; 
        }

        //create kings if criteria fits 
        boolean whiteCrowned = (y1 == 7 && piece == white); 
        boolean blackCrowned = (y1 == 0 && piece == black); 
        if (whiteCrowned) {
            board[x1][y1] = wKing; 
        } else if (blackCrowned) {
            board[x1][y1] = bKing; 
        }

    }

    //check which piece is on an occupied tile 
    public boolean isWhiteTile(int xPos, int yPos) {
        return board[xPos][yPos] == white || board[xPos][yPos] == wKing; 
    }

    public boolean isBlackTile(int xPos, int yPos) {
        return board[xPos][yPos] == black || board[xPos][yPos] == bKing; 
    }

    //add or remove pieces in the map
    public int getPiece(Position psn) {
        return board[psn.getXPos()][psn.getYPos()]; 
    }

    public void add(Position psn, int piece) {
        board[psn.getXPos()][psn.getYPos()] = piece; 
    }

    public void remove(Position psn) {
        board[psn.getXPos()][psn.getYPos()] = 0; 
    }

    //draw everything to the Board
    public void drawBoard(Graphics gc0) {
        Graphics2D gc2 = (Graphics2D) gc0; 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == black) {
                    gc2.setColor(Color.BLACK);
                    gc2.fillOval(i * 50, j * 50, 50, 50);
                } else if (board[i][j] == white) {
                    gc2.setColor(Color.WHITE);
                    gc2.fillOval(i * 50, j * 50, 50, 50);
                } else if (board[i][j] == bKing) {
                    gc2.setColor(Color.BLACK);
                    gc2.fillArc(i * 50, j * 50, 50, 50, 30, 30);
                } else if (board[i][j] == wKing) {
                    gc2.setColor(Color.WHITE);
                    gc2.fillArc(i * 50, j * 50, 50, 50, 30, 30);
                }

            }
        }
    }

    //check number of White pieces remaining
    public int numWhitePiecesLeft() {
        int count = 0; 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == white || board[i][j] == wKing) {
                    count++; 
                }
            }
        }
        return count; 
    }

    public int numWhiteKingsLeft() {
        int count = 0; 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == wKing) {
                    count++;
                }
            }
        }
        return count; 
    }

    //check number of Black pieces remaining 
    public int numBlackPiecesLeft() {
        int count = 0; 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == black || board[i][j] == bKing) {
                    count++; 
                }
            }
        }
        return count; 
    }

    public int numBlackKingsLeft() {
        int count = 0; 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == bKing) {
                    count++;
                }
            }
        }
        return count; 
    }

    //clear board
    public void clear() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = 0; 
            }
        }
    }


    //check whether the game is over - i.e when a player is out of pieces
    public boolean isGameOver() {
        if (numBlackPiecesLeft() == 0 || numWhitePiecesLeft() == 0) {
            return true; 
        } 
        return false; 
    }

    //display message that the game is over 
    public void msgGameOver(Graphics gc0) {
        Graphics2D gc2 = (Graphics2D) gc0; 
        gc2.setColor(Color.RED); 
        gc2.drawString("GAME OVER! X(", 180, 200);
    }

}
