import java.io.Serializable;

/**
     * Move class establishes Move objects
     *@author Rebecca
     */
    public class Move implements Serializable{
   
	private static final long serialVersionUID = 1L;
		int fromRow, fromCol;
        int toRow, toCol;

        public Move(int fRow, int fCol, int tRow, int tCol) {
            this.fromRow = fRow;
            this.fromCol = fCol;
            this.toRow = tRow;
            this.toCol = tCol;
        }   
        public boolean isJump() {
            return (fromRow - toRow == 2 || fromRow - toRow == -2);
        }
        public String toString() {
            return "Move from r" + fromRow +", c"+ fromCol + " to r" + toRow +", c"+ toCol;
        }
    }