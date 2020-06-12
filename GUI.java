  
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.Color;
import java.awt.*;

/**
 * User interface 
 * @author Rebecca
 *
 */

public class GUI extends JFrame {

 
	private static final long serialVersionUID = 1L;
	private int nRows = 8;
    private int nCols = 8;
    private JPanel[][] board = new JPanel[nRows][nCols];
    private Container container;
    private JPanel pan;
    private JButton newGame;
    private JButton exitGame;
    private static int fromRow = -1;
    private static int fromCol = -1;
    private static int toRow = -1;
    private static int toCol = -1;
    private JLabel status = new JLabel();
    private JLabel playerStatus = new JLabel();
    private Model model;
    private int[][] boardState;
    public boolean moveMade = false;
    private int playerID;
    private int activePlayer;
    private JPanel separator;
    private ObjectOutputStream OoS;
    private boolean resigned = false;

    static final int empty = 0, red = 1, red_king = 3, black = 2, black_king = 4; 
    
    /**
     *  Model, playerID, ObjectOutputStream passed to constructor
     */
    public GUI(Model model, int playerID, ObjectOutputStream OoS) {
        this.model = model;
        this.playerID = playerID;
        this.OoS = OoS;
        initUI();
    }
    public int getFromRow(){
        return fromRow;
    }
    public int getFromCol(){
        return fromCol;
    }
    public int getToRow(){
        return toCol;
    }
    public int getToCol(){
        return toCol;
    }
    /**
     * User interface components initialised, action listener added to buttons
     */
    public void initUI() {
        container = new JPanel();
        board = createBoard();
        pan = new JPanel();
        separator = new JPanel();
        newGame = new JButton("New Game");
        newGame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a) {
                newGame.setEnabled(false);
                try {
                    OoS.writeObject("newGame");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 
            }
        });
        exitGame = new JButton("Exit");
        exitGame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try {
                    OoS.writeObject("resigned");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        separator.add(newGame);
        separator.add(exitGame);
        newGame.setEnabled(false);
        separator.setSize(100,100);
        container.setSize(750, 600);
        container.setLayout(new GridLayout(8, 8, 1, 1));
        pan.setSize(750, 150);
        setLayout(new BorderLayout());
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playerStatus.setText("Player number: " + playerID);

        pan.add(playerStatus);
        pan.add(separator);
        pan.add(status);
        add(container, BorderLayout.CENTER);
        add(pan, BorderLayout.SOUTH);

        container.setVisible(true);
        setVisible(true);
    }

    public int getPlayerID() {
        return playerID;
    }
    public void startGame() {
        boardState = model.getBoard();
        repaint();
    }
    /**
     * Board updated with player jump options, ensures newGame button disabled, updates text
     */
    public void updateBoard() {
        this.boardState = model.getBoard();
        if (model.ifCanJump()) {
            setStatus("You must jump again " + model.getActivePlayer());
        }
        if (playerID == model.getActivePlayer()) {
            playerStatus.setText("It's " + playerID + ", turn");
        } else {
            playerStatus.setText("Player number: " + playerID);
        }
        if (model.checkGameOver() || model.isResigned()) {
            endGame();
        }
        if (model.isGameInProgress()) {
            newGame.setEnabled(false);
        }
        repaint();
    }
    /**
     * If player resigns game, method enabled 
     */
    public void endGame() {
        model.endGame();
        newGame.setEnabled(true);
        if (model.checkWin()) {
            setStatus("Game over! Player: " + model.getWinner() + " is the winner");
        }
        if (model.checkDraw()) {
            setStatus("Game over! It's a draw.");
        }
        if (model.isResigned()) {
            setStatus("Game over, player has resigned.");
        }
        repaint();
    }

    /**
     * Model updated, sent to client
     * @param model
     */
    public void updateModel(Model model) {
        this.model = model;
        this.boardState = model.getBoard();
        this.activePlayer = model.getActivePlayer();
        repaint();
    }

    public void setStatus(String s) {
        status.setText(s);
    }
    public int getActivePlayer() {
        return activePlayer;
    }

    /**
     * JPanel board array created
     * @return board
     */
    public JPanel[][] createBoard() {
        JPanel[][] board = new JPanel[nRows][nCols];
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                Board panel = new Board(row, col);
                board[row][col] = panel;
                container.add(board[row][col]);
            }
        }
        return board;
    }

    /**
     * JPanel class, implements mouse listener, uses Graphics to create board/counters 
     */
    public class Board extends JPanel implements MouseListener {
       
		private static final long serialVersionUID = 1L;
		private int row;
        private int col;

        public Board(int row, int col) {
            this.row = row;
            this.col = col;
            addMouseListener(this);
        }
        public int getRow() {
            return row;
        }
        public int getCol() {
            return col;
        }
        /**
         * Create board/counter graphics, available moves highlighted
         */
        public void paintComponent(Graphics g) {
            Move[] moves = model.checkLegalMoves(model.getActivePlayer());
            if (row % 2 == col % 2) {
                g.setColor(Color.green);
            } else {
                g.setColor(Color.white);
            }
            if (boardState[row][col] == 0 && row % 2 == col % 2) {
                g.setColor(Color.green);
            }
            g.fillRect(0, 0, getWidth(), getHeight());

            if (boardState[row][col] == black) {
                g.setColor(Color.black);
                g.fillOval(0, 0, getWidth(), getHeight());
            } else if (boardState[row][col] == red) {
                g.setColor(Color.red);
                g.fillOval(0, 0, getWidth(), getHeight());
            } else if (boardState[row][col] == red_king) {
                g.setColor(Color.red);
                g.fillOval(0, 0, getWidth(), getHeight());
                g.setColor(Color.cyan);
                g.drawString("K", getHeight() / 2, getWidth() / 2);
            } else if (boardState[row][col] == black_king) {
                g.setColor(Color.black);
                g.fillOval(0, 0, getWidth(), getHeight());
                g.setColor(Color.cyan);
                g.drawString("K", getHeight() / 2, getWidth() / 2);
            }
            /**
             *  Movable counters highlighted, legal moves for counter highlighted
             */
            if (moves == null ) {
                return;
            } else {
                if (model.isGameInProgress()) {

                    for (Move move : moves) {
                            if (move.fromRow == row && move.fromCol == col) {
                                g.setColor(Color.BLACK);
                                g.drawRoundRect(0,0, getWidth(), getHeight(), 20,20);
                            }
                    }
            }   
            if (fromRow >= 0) {
                for (Move move : moves) {
                    if ((move.toRow == row && move.toCol == col) && (move.fromRow == fromRow && move.fromCol == fromCol)) {
                        g.setColor(Color.ORANGE);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                }
            }
            }

        }
        /**
         * MouseEvent for counter selection/moves, legal moves for player updated, sent to server to update model
         */
        public void mousePressed(MouseEvent e) {
            if (model.getActivePlayer() == playerID) {
                Board selectedSpace = (Board) e.getSource();
                int row = selectedSpace.getRow();
                int col = selectedSpace.getCol();
                int piece = model.getPiece(row, col);
                System.out.println("Piece: " + piece + " row:" + row + " col:" + col);
                Move[] moves = model.checkLegalMoves(model.getActivePlayer());

                /**
                 * Move selection logic, checks for active player, checks if board space empty/contains counter, legal moves verified
                 */
                if (piece == model.getActivePlayer() || piece == black_king || piece == red_king) {
                    for (Move move : moves) {
                        System.out.println(move);
                    }
                    status.setText("Counter at Row: " + row + " Column: " + col + " selected");
                    System.out.println("Counter selected");
                    fromRow = row;
                    fromCol = col;
                    System.out.println("Repainting in Progress");
                    repaint();
                } else if (row % 2 == col % 2) {                         
                    status.setText("Space selected at Row: " + row + " Column: " + col + " Please select again to confirm!");
                    System.out.println("Square selected");
                    toRow = row;
                    toCol = col;
                    for (Move move : moves) {
                        if ((move.fromCol == fromCol && move.fromRow == fromRow) && (move.toRow == toRow && move.toCol == toCol)) {
                                try {
                                    System.out.println("Player " + playerID + " is selecting a move");
                                    OoS.writeObject(move);
                                    OoS.flush();
                                    OoS.reset();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }   
                        }
                    }
                }
            } else {
                setStatus("Please wait your turn.");
            }

        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }
}