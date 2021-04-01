import javax.swing.*; 
import java.awt.*;  
                                        

import javax.swing.JButton; 
import javax.swing.JPanel; 
import javax.swing.JFrame; 
import javax.swing.SwingUtilities; 

import java.awt.Graphics;  
import java.awt.Color; 
import java.awt.Dimension; 
import java.awt.Point; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.BorderLayout; 

import java.util.List; 
import java.util.LinkedList; 

public class Game<Turn> implements Runnable {
    private Canvas canvas; 
    private Turn turn; 
    private Score score; 
    private Board cBoard = new Board(); 
    private boolean bTurn = true; 
    private boolean notChecker = false; 
    private boolean initPress = false; 
    private boolean notJump = false; 
    private boolean notMove = false; 
    private boolean canWhiteJump = false; 
    private boolean canBlackJump = false; 
    private boolean canWhiteMove = false; 
    private boolean gameOver = false; 
    private boolean blackWin = false; 
    private boolean moreJumps = false; 
    private int finalScore = 0; 
    private int highScore1 = 0; 
    private int highScore2 = 0; 
    private int highScore3 = 0; 
    private int sX; 
    private int sY; 
    private String p1 = ""; 
    private String p2 = ""; 
    private String p3 = ""; 
    private String player1 = "Player Black"; 
    private String player2 = "Player White"; 
    private int width = 50; 
    private int height = 50; 
    private Undo undoTemp; 
    private List<Undo> undo = new LinkedList<>(); 

    
    @SuppressWarnings("serial")
    private class Canvas extends JPanel {
        @Override
        public void paintComponent(Graphics gc) {
            //draw the board
            super.paintComponent(gc);
            for (int i = 0; i < 400; i += 100) {
                for (int j = 0; j < 400; j += 100) {
                    gc.fillRect(i, j, width, height);
                }
            }
            for (int i = 50; i < 400; i += 100) {
                for (int j = 0; j < 400; j += 100) {
                    gc.fillRect(i, j, width, height);
                }
            }
            gc.setColor(Color.PINK);

            for (int i = 0; i < 400; i += 100) {
                for (int j = 50; j < 400; j += 100) {
                    gc.fillRect(i, j, width, height);
                }
            }

            for (int i = 50; i < 400; i += 100) {
                for (int j = 0; j < 400; j += 100) {
                    gc.fillRect(i, j, width, height);
                }
            }

            //draw the board if the game isn't over 
            if (!gameOver) {
                cBoard.drawBoard(gc);
            } else {
                gc.setColor(Color.PINK);
                gc.fillRect(0, 0, 400, 400);
                cBoard.msgGameOver(gc); 
                gc.setColor(Color.BLACK);

                //check win condition
                if (blackWin) {
                    finalScore = cBoard.numBlackPiecesLeft() + 2 * cBoard.numBlackKingsLeft();
                    drawString(gc, player1 + " wins!" + "\nFinal score is:" + finalScore, 180, 200);
                } else {
                    finalScore = cBoard.numWhitePiecesLeft() + 2 * cBoard.numWhiteKingsLeft();
                    drawString(gc, player2 + " wins!" + "\nFinal score is:" + finalScore, 180, 200);
                }
                Game.this.update(); 
            }
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400); 
        }
        public Canvas() {
            super(); 
        }
    }

    //update highScores if needed
    public void update() {
        boolean notWork = true;
        if (notWork && finalScore > highScore1) {
            p3 = p2; 
            highScore3 = highScore2; 
            p2 = p1; 
            highScore2 = highScore1; 
            highScore1 = finalScore; 

            if (blackWin) {
                p1 = player1; 
            } else {
                p1 = player2; 
            }
            notWork = false; 
        } else if (notWork && finalScore > highScore2) {
            p3 = p2; 
            highScore3 = highScore2; 
            highScore2 = finalScore; 
            if (blackWin) {
                p2 = player1; 
            } else {
                p2 = player2; 
            }
            notWork = false; 
        } else if (notWork && finalScore > highScore3) {
            highScore3 = finalScore; 
            if (blackWin) {
                p3 = player1; 
            } else {
                p3 = player2; 
            }
        }

    }
    @SuppressWarnings("serial")
    private class Score extends JPanel {
        @Override
        public void paintComponent(Graphics gc) {
            super.paintComponent(gc);
            gc.setColor(Color.BLACK);
            drawString(gc, "White\n Pieces:\n" + cBoard.numWhitePiecesLeft(), 5, 50); 
            drawString(gc, "Black\n Pieces:\n" + cBoard.numBlackPiecesLeft(), 5, 300); 
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(50, 400);
        }
        //constructor
        public Score() {
            super(); 
        }
    }

    @SuppressWarnings("serial")
    private class Turn extends JPanel {
        @Override
        public void paintComponent(Graphics gc) {
            super.paintComponent(gc);
            if (bTurn) {
                gc.setColor(Color.BLACK);
                gc.fillRect(0, 0, 400, 24);
            } else {
                gc.setColor(Color.WHITE);
                gc.fillRect(0, 0, 400, 24);
            }
            gc.setColor(Color.RED);

            if (notChecker) {
                notChecker = false; 
                gc.drawString("Choose a jump as one's available", 50, 10);
            }
            if (notJump) {
                notJump = false; 
                gc.drawString("Choose a jump as one's available", 50, 10);
            }
            if (notMove) {
                notMove = false; 
                gc.drawString("Invalid move", 50, 10);
            }
            if (canBlackJump || canWhiteJump) {
                canWhiteJump = false; 
                canBlackJump = false; 
                gc.drawString("Must take available jump", 50, 10);
            }

        }
        @Override 
        public Dimension getPreferredSize() {
            return new Dimension(400, 25); 
        }
        public Turn() {
            super(); 
        }
    }

    private void drawString(Graphics g, String s, int x, int y) {
        for (String ln : s.split("\n")) {
            g.drawString(ln, x, y += g.getFontMetrics().getHeight());
        }
    }

    private JPanel makeToolbar() {
        JPanel toolbar = new JPanel();
        JButton undoButton = new JButton("Undo");
        toolbar.add(undoButton);
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (undo.size() > 0) {
                    Undo o = ((LinkedList<Undo>) undo).removeLast();
                    List<Captured> l = o.getCaptured();
                    while (l.size() != 0) {
                        Captured t = l.remove(0);
                        cBoard.add(t.getPsn(), t.getPiece());
                    }
                    cBoard.remove(o.getEnd().getPsn());
                    cBoard.add(o.getBegin().getPsn(), o.getBegin().getPiece());


                    bTurn = !bTurn;
                    initPress = false;
                    moreJumps = false;

                    canvas.repaint();
                    turn.repaint();
                    score.repaint();
                }
            }
        });

        JButton undoClick = new JButton("Reset Piece Selection");
        toolbar.add(undoClick);
        undoClick.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initPress = false;
                turn.repaint();
            }
        });

        return toolbar; 
 
    }

    private class Mouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent m) {
            Point p = m.getPoint();
            int xPos = p.x / 50; 
            int yPos = p.y / 50; 
            if (bTurn) {
                if (!initPress) {
                    if (cBoard.isBlackTile(xPos,yPos)) {
                        // check availability of potential tile
                        if (cBoard.canBlackJump && cBoard.getDest(xPos, yPos).isEmpty()) {
                            canBlackJump = true;
                            turn.repaint();
                        } else {
                            sX = xPos;
                            sY = yPos;
                            initPress = true;
                            turn.repaint();
                        }
                    } else {
                        notChecker = true;
                        turn.repaint();
                    }
                } else {
                    // there are possible jumps
                    if (!cBoard.getDest(sX,sY).isEmpty()) {
                        // the second chosen mouse click is a jump
                        if (cBoard.getDest(sX,sY).containsKey(
                                new Position(xPos, yPos))) {
                            Position psn = cBoard.getDest(sX, sY).get(new Position(xPos, yPos));
                            int takenColor = cBoard.getPiece(psn);
                            int startColor = cBoard.getPiece(new Position(sX, sY));
                            int endColor = cBoard.getPiece(new Position(xPos, yPos));
                            cBoard.movePiece(sX, sY, xPos, yPos);

                            //undo move
                            if (!moreJumps) {
                                undoTemp = new Undo(new Captured(new Position(sX, sY), startColor)
                                        , new Captured(new Position(xPos, yPos), endColor), 
                                        new Captured(psn, takenColor));
                            } else {
                                undoTemp.add(new Captured(psn, takenColor));
                                undoTemp.update(new Captured(new Position(xPos, yPos), endColor));
                            }

                            //check if game is over
                            if (cBoard.numWhitePiecesLeft() == 0) {
                                gameOver = true;
                                blackWin = true;
                            }
                            canvas.repaint();
                            // check if more than one jump's available per turn
                            if (!cBoard.getDest(xPos, yPos).isEmpty()) {
                                sX = xPos;
                                sY = yPos;
                                moreJumps = true;
                            } else {
                                undo.add(undoTemp);
                                moreJumps = false;
                                bTurn = false;
                                initPress = false;
                                turn.repaint();
                            }
                            score.repaint();
                        } else { //must select available jump
                            notJump = true;
                            turn.repaint();
                        }
                    } else { //no permissible jumps
                        if (cBoard.getMoves(sX, sY).contains(
                                new Position(xPos, yPos))) {
                            int startColor = cBoard.getPiece(new Position(sX, sY));
                            int endColor = cBoard.getPiece(new Position(xPos, yPos));
                            undoTemp = new Undo(new Captured(new Position(sX, sY), startColor), 
                                    new Captured(new Position(xPos, yPos), endColor), null);
                            undo.add(undoTemp);
                            cBoard.movePiece(sX, sY, xPos, yPos);
                            canvas.repaint();
                            bTurn = false;
                            initPress = false;
                            turn.repaint();
                            score.repaint();
                        } else {
                            notMove = true;
                            turn.repaint();
                        }
                    }
                }
            } else {
                if (!initPress) {
                    if (cBoard.isWhiteTile(xPos,yPos)) {
                        // check availability of potential tile
                        if (cBoard.canWhiteMove() && cBoard.getDest(xPos, yPos).isEmpty()) {
                            canWhiteMove = true;
                            turn.repaint();
                        } else {
                            sX = xPos;
                            sY = yPos;
                            initPress = true;
                            turn.repaint();
                        }
                    } else {
                        notChecker = true;
                        turn.repaint();
                    }
                } else { //prioritize jump
                    // there are possible jumps
                    if (!cBoard.getDest(sX,sY).isEmpty()) {
                        // selected second mouse click is a jump
                        if (cBoard.getDest(sX,sY).containsKey(
                                new Position(xPos, yPos))) {
                            Position psn = cBoard.getDest(sX, sY).get(new Position(xPos, yPos));
                            int takenColor = cBoard.getPiece(psn);
                            int startColor = cBoard.getPiece(new Position(sX, sY));
                            int endColor = cBoard.getPiece(new Position(xPos, yPos));
                            cBoard.movePiece(sX, sY, xPos, yPos);

                            //undo a move
                            if (!moreJumps) {
                                undoTemp = new Undo(new Captured(new Position(sX, sY), startColor)
                                        , new Captured(new Position(xPos, yPos), endColor), 
                                        new Captured(psn, takenColor));
                            } else {
                                undoTemp.add(new Captured(psn, takenColor));
                                undoTemp.update(new Captured(new Position(xPos, yPos), endColor));
                            }

                            //check if game is over
                            if (cBoard.numBlackPiecesLeft() == 0) {
                                gameOver = true;
                                blackWin = false;
                            }
                            canvas.repaint();
                            // if more jumps
                            if (!cBoard.getDest(xPos, yPos).isEmpty()) {
                                sX = xPos;
                                sY = yPos;
                                moreJumps = true;
                            } else {
                                undo.add(undoTemp);
                                moreJumps = false;
                                bTurn = true;
                                initPress = false;
                                turn.repaint();
                            }
                            score.repaint();
                        } else { //select a jump because one is available
                            notJump = true;
                            turn.repaint();
                        }
                    } else {  //no possible jumps available 
                        if (cBoard.getMoves(sX, sY).contains(
                                new Position(xPos, yPos))) {
                            int startColor = cBoard.getPiece(new Position(sX, sY));
                            int endColor = cBoard.getPiece(new Position(xPos, yPos));
                            undoTemp = new Undo(new Captured(new Position(sX, sY), startColor), 
                                    new Captured(new Position(xPos, yPos), endColor), null);
                            undo.add(undoTemp);
                            cBoard.movePiece(sX, sY, xPos, yPos);
                            canvas.repaint();
                            bTurn = true;
                            initPress = false;
                            turn.repaint();
                            score.repaint();
                        } else {
                            notMove = true;
                            turn.repaint();
                        }
                    }
                }
            }

        }
    }
    
    /*
     * Description: One of the clickable options in the initial game screen that
     *              describes what the Checkers game is and gives instructions
     *              on how to play it. 
     */
    private JLabel instructions() {
        JLabel i = new JLabel("<html> <b> Checkers Instructions: "
                + "</b> <br> <br>"
                + "Checkers is a popular two-player board game. <br>"
                + "In checkers, a normal piece moves diagonally in one direction. <br>"
                + "A king makes bizarre moves - play to find out how! <br>"
                + "A piece is crowned king if it reaches the last row of the <br>"
                + "opponents' side of the board.<br>"
                + "A piece is captured if an opponent piece jumps over it.<br>"
                + "One can jump once, twice, or even thrice in one turn. <br>"
                + "If a jump's available, one MUST take it. <br>"
                + "Redo a move if at an impasse without permissible moves. <br>"
                + "The color bar at the top of the board displays players' turns. <br>"
                + "Once a piece is clicked, the next click determines its destination. <br>"
                + "A winner is found when they've captured all the opponents' pieces. <br>"
                + "The winning score is calculated by the adding the number of pieces <br>"
                + "captured plus twice the number of kings. <br>"
                + "<br> <br>"
                + "Good luck and have fun! </html>", SwingConstants.CENTER);
        return i; 
    }

    public void run() {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());

        canvas = this.new Canvas();
        turn = this.new Turn();
        score = this.new Score();


        // mouse listener
        Mouse mouseListener = new Mouse();
        canvas.addMouseListener(mouseListener); 

        // game panel
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());

        gamePanel.add(turn, BorderLayout.PAGE_START);
        gamePanel.add(canvas, BorderLayout.CENTER);
        gamePanel.add(makeToolbar(), BorderLayout.PAGE_END);
        gamePanel.add(score, BorderLayout.EAST);
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(gamePanel);

        // instructions panel
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BorderLayout());
        JLabel instruction = instructions();
        instructionsPanel.add(instruction, BorderLayout.CENTER);


        //ask players to input their names at beginning of game
        player1 = JOptionPane.showInputDialog("Player 1 Name (Black Color):");
        player2 = JOptionPane.showInputDialog("Player 2 Name (White Color):");

        // adding JPanes to the game window
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Instructions", instructionsPanel);
        tabbedPane.addTab("Game", wrapperPanel);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Description: The main method which starts and runs the game. It initializes the GUI elements 
     *              specified in the Game class and runs the Checkers  game.
     * Compilation: javac Game
     * Execution:   java Game
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game<>());
    }

}

