
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Model implements Serializable {
   
	/** 
     * Draughts game model
     * @author Rebecca
     * 
     */
    static final int empty = 0, red = 1, red_king = 3, black = 2, black_king = 4; 
    private static final long serialVersionUID = 1L;
    public int[][] board; 
    private Lock lock = new ReentrantLock(); 
    private int activePlayer;
    private boolean gameInProgress;
    private Move[] moves;
    private int winner;
    private boolean resigned;
    private boolean canJump = false;

    public Model() {
        board = new int[8][8];
    }

    public boolean ifCanJump() {
        return canJump;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    public boolean isResigned() {
        return resigned;
    }

    public void setResigned(boolean resigned) {
        this.resigned = resigned;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }
    /**
     * Starts game, initialises board, checks for legal moves, sets active player 
     */
    public void startGame() {
        initialBoard();
        resigned = false;
        gameInProgress = true;
        activePlayer = black;
        moves = checkLegalMoves(getActivePlayer());
    }
    /**
     * Ends game, sets boolean, returns active player to start
     */
    public void endGame() {
        gameInProgress = false;
        activePlayer = black;
    }
    /**
     * Establishes game over state
     * @return
     */
    public boolean checkGameOver() {
        if (checkWin()) {
            return true;
        }
        if (checkDraw()) {
            return true;
        }
        return false;
    }
    /**
     * Establishes draw state
     * @return
     */
    public boolean checkDraw() {
        Move[] redMoves = checkLegalMoves(red);
        Move[] blackMoves = checkLegalMoves(black);
        if (blackMoves.length < 0 && redMoves.length < 0) {
            return true;
        }
        return false;
    }
    /**
     * Establishes winner, tallies counters, if player counter tally 0 declared winner
     * @return
     */
    public boolean checkWin() {
        int blackCounters = 0;
        int redCounters = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == red || board[row][col] == red_king) {
                    redCounters++;
                } else if (board[row][col] == black || board[row][col] == black_king) {
                    blackCounters++;
                }
            }
        }
        if (redCounters == 0) {
            setWinner(black);
            return true;
        } else if (blackCounters == 0) {
            setWinner(red);
            return true;
        }
        return false;
    }
    public int getPiece(int row, int col) {
        return board[row][col];
    }
    public Move[] getMoves() {
        return moves;
    }
    /**
     * Initialise board
     */
    public void initialBoard() {
        for (int row=0; row < 8; row++) {
            for (int col=0; col < 8; col++) {
                if (row%2 == col%2) {
                    if (row < 3) {
                        board[row][col] = black;
                    } else if (row > 4) {
                        board[row][col] = red;
                    } else {
                        board[row][col] = empty;
                    }
                } else {
                    board[row][col] = empty;
                }
            }
        }
    }
    public int[][] getBoard() {
        return board;
    }
    /**
     * Verifies legal moves, if legal move jump, jumped piece removed
     * Counter removed from current position, moved to new position
     * @param fRow
     * @param fCol
     * @param tRow
     * @param tCol
     * @return
     */
    public boolean makeMove(int fRow, int fCol, int tRow, int tCol) {
        moves = checkLegalMoves(getActivePlayer());
        if (moves == null) {
            return false;
        }
        for (Move move : moves) {
            if (move.fromCol == fCol && move.fromRow == fRow) {
                if (move.toRow == tRow && move.toCol == tCol) {
                    if (tRow == 0 || tRow == 7) {
                        if (board[fRow][fCol] == red) {
                            board[fRow][fCol] = red_king;
                        } else {
                            board[fRow][fCol] = black_king;
                        }            
                    }
                    if (move.isJump()) {
                        int jCol = (fCol + tCol) /2;
                        int jRow = (fRow + tRow) /2;
                        board[jRow][jCol] = empty;
                    } 
                    board[tRow][tCol] = board[fRow][fCol];
                    board[fRow][fCol] = empty; 
                    System.out.println("Move completed!");
                    return true;
                } 
            }
        }
        System.out.println("Not valid!");
        return false;
    }

    /**
     * Checks for legal moves for current player
     * @param player
     * @return
     */
    public Move[] checkLegalMoves(int player) {
        ArrayList<Move> moves = new ArrayList<Move>();
        int playerKing;
        if (player == black) {
            playerKing = black_king;
        } else {
            playerKing = red_king;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player || board[row][col] == playerKing) {
                    if (checkJump(player, row, col, row+1, col+1, row+2, col+2))
                        moves.add(new Move(row, col, row+2, col+2));
                    if (checkJump(player, row, col, row-1, col+1, row-2, col+2))
                        moves.add(new Move(row, col, row-2, col+2));
                    if (checkJump(player, row, col, row+1, col-1, row+2, col-2))
                        moves.add(new Move(row, col, row+2, col-2));
                    if (checkJump(player, row, col, row-1, col-1, row-2, col-2))
                        moves.add(new Move(row, col, row-2, col-2));
                }
            }
        } 
        if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canMove(player,row,col,row+1,col+1))
                            moves.add(new Move(row,col,row+1,col+1));
                        if (canMove(player,row,col,row-1,col+1))
                            moves.add(new Move(row,col,row-1,col+1));
                        if (canMove(player,row,col,row+1,col-1))
                            moves.add(new Move(row,col,row+1,col-1));
                        if (canMove(player,row,col,row-1,col-1))
                            moves.add(new Move(row,col,row-1,col-1));
                    }
                }
            }
        }
        if (moves.size() == 0) {
            return null;
        } else {
            Move[] legalMoves = new Move[moves.size()];
            for (int i=0; i<moves.size(); i++) {
                legalMoves[i] = moves.get(i);
            }
            return legalMoves;
        }
    }
    /**
     * Checks for multiple jumps
     * @param player
     * @param row
     * @param col
     * @return
     */
    public Move[] getJumpsFrom(int player, int row, int col) {
        if (player != red && player != black)
            return null;
        int playerKing;  // The constant representing a King belonging to player.
        if (player == red)
            playerKing = red_king;
        else
            playerKing = black_king;
        ArrayList<Move> moves = new ArrayList<Move>();  // The legal jumps will be stored in this list.
        if (board[row][col] == player || board[row][col] == playerKing) {
            if (checkJump(player, row, col, row+1, col+1, row+2, col+2))
                moves.add(new Move(row, col, row+2, col+2));
            if (checkJump(player, row, col, row-1, col+1, row-2, col+2))
                moves.add(new Move(row, col, row-2, col+2));
            if (checkJump(player, row, col, row+1, col-1, row+2, col-2))
                moves.add(new Move(row, col, row+2, col-2));
            if (checkJump(player, row, col, row-1, col-1, row-2, col-2))
                moves.add(new Move(row, col, row-2, col-2));
        }
        if (moves.size() == 0)
            return null;
        else {
            Move[] moveArray = new Move[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }
    }
    /**
     * Current position, to be jumped position and landing position calculated, returns true if legal jump
     * @param row1
     * @param col1
     * @param row2
     * @param col2
     * @param row3
     * @param col3
     * @return
     */
    public boolean checkJump(int player, int row1, int col1, int row2, int col2, int row3, int col3) {
        if (row3 < 0 || row3 >= 8 || col3 < 0 || col3 >=8) {
            return false; 
        }
        if (board[row3][col3] != empty) {
            return false; 
        }
        if (player == black) {
            if (board[row1][col1] == black && row3 < row1) {
                return false; 
            }
            if (board[row2][col2] != red && board[row2][col2] != red_king) {
                return false; 
            }
            return true; 
        } else {
            if (board[row1][col1] == red && row3 > row1) {
                return false; 
            }
            if (board[row2][col2] != black && board[row2][col2] != black_king) {
                return false; 
            }
            return true; 
        }
    }

    /**
     * Establishes whether player can make move
     * @param player
     * @param fR
     * @param fC
     * @param tR
     * @param tC
     * @return
     */
    public boolean canMove(int player, int fRow, int fCol, int tRow, int tCol) {

        if (tRow < 0 || tRow >=8 || tCol < 0 || tCol >=8) {
            return false;
        }

        if (board[tRow][tCol] != empty) {
            return false;
        }
        if (player == red) {
            if (board[fRow][fCol] == red && tRow > fRow)
                return false; 
            return true; 
        }
        else {
            if (board[fRow][fCol] == black && tRow < fRow)
                return false; 
            return true; 
        }
    }

    public int getActivePlayer() {
        return activePlayer;
    }
    public void setActivePlayer(int player) {
        activePlayer = player;
    }

    /**
     * Populate moves array with moves, switches player
     */
     public void switchPlayer() {

        if (activePlayer == red) {
            activePlayer = black;
        } else {
            activePlayer = red;
        }
        moves = checkLegalMoves(activePlayer);
    }
    public boolean isGameInProgress() {
        return gameInProgress;
    }
    public void lock() {
        lock.lock();
    }
    public void unlock() {
        lock.unlock();
    }
    /**
     * Prints board
     */
    public String toString() {
        String output = "";
        for (int row=0; row< 8; row++) {
            output += row + " |";
            for (int col=0; col<8; col++) {
                output += board[row][col] + "|";
            }
            output += "\n";
        }
        return output;
    }

   
}