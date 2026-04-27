package ce326.hw3;

public class Board {
    char[][] boardPieces;                                       // 'p' Player, 'a' AI, '-' Blank
    char winner;
    boolean hasThree;

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;

    Board(){
        hasThree = false;
        boardPieces = new char[ROWS][COLUMNS];
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++)
                boardPieces[i][j] = '-';
        }
    }

    Board(char [][]boardPieces/*,int[] sum*/){
        hasThree = false;
        this.boardPieces = boardPieces;
        winner = '-';
    }

    public boolean hasThreeInARow(){
        return hasThree;
    }

    /* Returns true if someone has connected 4 on this board. */
    public boolean someoneHasWon(){
        return (winner == 'a' || winner == 'p');
    }
    /* Returns true if board is full and false otherwise */
    public boolean isBoardFull(){
        for(int i = 0; i < COLUMNS; i++){
            if(!isColumnFull(i))
                return false;
        }
        return true;
    }

    /* Cheks if a column on a board is full. */
    public boolean isColumnFull(int column){
        return (boardPieces[0][column] == 'a' || boardPieces[0][column] == 'p'); 
    }

    /* Puts the piece of the player in turn to the selected column.
     * Returns true for success.
     * Returns false for failure. (Full column)
     */
    public char[][] putPieceInColumn(char turn, int column){
        int i = 0;
        
        if(boardPieces[0][column] != '-')
            return boardPieces; // or null
        
        char[][] boardPiecesNew = new char[ROWS][COLUMNS];

        for(i=0; i<ROWS;i++){
            for(int j=0;j<COLUMNS;j++)
                boardPiecesNew [i][j] = boardPieces[i][j];
        }

        i--;
        while(boardPiecesNew[i][column] != '-' && i > -1)
            i--;
        
        boardPiecesNew[i][column] = turn;
        
        return boardPiecesNew;
    }

    public int play(char pov, int column) {
        int row = -1;
        for (int i = ROWS - 1; i >= 0; i--) {
            if (boardPieces[i][column] == '-') {
                boardPieces[i][column] = pov;
                row = i;
                break;
            }
        }
        return row;
    }

    public int getRow(int column){
        int row = Board.ROWS - 1;
        for(; row > -1; row--){
            if(boardPieces[row][column] == '-')
                break;
        }
        return row;
    }

    /* Prints boardPieces 2D array by row. */
    public void printBoard(){
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++)
                System.out.printf("|" + boardPieces[i][j]+"| ");
            System.out.println("");
        }
    }

    /* Evaluates the whole Board for the given pov.  */
    public int evaluateBoard(char pov){
        int type = 0;
        int[] sum = {0, 0, 0, 0};
        for(int k = 0; k < 4; k++)
            sum[k] = 0;
        int result = 0;
        for(type = 0; type < 4; type++){
            for(int i = 0; i < ROWS; i ++){
                if(type != 0 && i > ROWS - 4)
                    continue;
                for(int j = 0; j < COLUMNS; j++){
                    if((type == 0 || type == 3) && j > COLUMNS - 4)
                        continue;
                    if(type == 2 && j < 3)
                        continue;
                    sum[type] += evaluateQuadruple(i, j, type, pov);
                }
            }
            result += sum[type];
        }
        return result;
    }

    /* Evaluate a quadruple's value starting from the (1) top and (2) leftmost place.
     * type defines a quadruples kind:
     * type 0 for horizontal quadruple
     * type 1 for vertical quadruple
     * type 2 for top diagonal quadruple
     * type 3 for bottom diagonal duadruple
     */
    public int evaluateQuadruple(int row, int column, int type, char pov){
        int numOfPieces = isPlayersQuadruple(row, column, type, pov);       // how many pieces in the quadruple
        char enemy = (pov == 'a') ? 'p' : 'a';
        int sign = Integer.signum(numOfPieces);

        numOfPieces = Math.abs(numOfPieces);

        if(numOfPieces == 0)
            return 0;
        switch (numOfPieces) {
            case 1:                                                         // for 1 piece-quadruple
                return sign * 1;                                                   // return 1 piece-quadruple value
            case 2:                                                         // for 2 piece-quadruple
                return sign * 4;                                                   // return 2 piece-quadruple value and so on...
            case 3:
                hasThree = true;
                return sign * 16;
            case 4:
                winner = (sign == 1) ? pov : enemy;
                return sign *  10000;       
            default:
                return 0;  
        }
    }

    /* type 0 for horizontal quadruple
     * type 1 for vertical quadruple
     * type 2 for top diagonal quadruple
     * type 3 for bottom diagonal duadruple
     */
    public int isPlayersQuadruple(int row, int column, int type, char pov){
        switch (type) {
            case 0:
                return isPlayersQuadrupleHorizontal(row, column, pov);
            case 1:
                return isPlayersQuadrupleVertical(row, column, pov);
            case 2:
                return isPlayersQuadrupleTopDiagonal(row, column, pov);
            case 3:
                return isPlayersQuadrupleBottomDiagonal(row, column, pov);
            default:
                System.out.println("Invalid type of quadruple");
                return -2;
        }
    }

    /* Return a player's pieces in a horizontal quadruple.
     * Return value: > 0 if at least a player's piece in
     * quadruple and no enemy pieces as well.
     * Return value: 0 if no piece in that quadruple. (4 blanks)
     * Return value: -1 if at least one enemy piece in the quadruple.
     */
    public int isPlayersQuadrupleHorizontal(int row, int column, char pov){
        int povPiecesCounter = 0;
        int enemyPiecesCounter = 0;
        int j = column;
        char enemy = (pov == 'a') ? 'p' : 'a';                  // configure enemy piece

        if(column > COLUMNS - 4)                                // quadruple will go out of bounds
            return 0;

        for(j = column; j < column + 4; j++){
            if(boardPieces[row][j] == pov)                      // pov's piece foind in quadruple?
                povPiecesCounter++;
            if(boardPieces[row][j] == enemy){                     // enemy piece found in quadruple?
                enemyPiecesCounter++;
                break;
            }
        }
        if(enemyPiecesCounter == 0)                             // if no enemy piece found return pov piece number
            return povPiecesCounter;
        if(povPiecesCounter != 0)                               // if both kind of pieces found return 0
            return 0;
        
        enemyPiecesCounter = 0;
        
        for(j = column; j < column + 4; j++){
            if(boardPieces[row][j] == enemy)                    // enemy piece found in quadruple?
                enemyPiecesCounter++;
            if(boardPieces[row][j] == pov)                      // pov's piece also found in quadruple?
                return 0;
        }
        return -enemyPiecesCounter;
    }

    /* Return a pov's pieces in a vertical quadruple.
     * Return value: > 0 if at least a pov's piece in
     * quadruple and no enemy pieces as well.
     * Return value: 0 if no piece in that quadruple. (4 blanks)
     * Return value: -1 if at least one enemy piece in the quadruple.
     */
    public int isPlayersQuadrupleVertical(int row, int column, char pov){
        int povPiecesCounter = 0;
        int enemyPiecesCounter = 0;
        int i = row;
        char enemy = (pov == 'a') ? 'p' : 'a';                  // configure enemy piece

        if(row > ROWS - 4)                                      // quadruple will go out of bounds
            return 0;                                           // so will go out of bounds

        for(i = row; i < row + 4; i++){
            if(boardPieces[i][column] == pov)                   // pov's piece foind in quadruple?
                povPiecesCounter++;
            if(boardPieces[i][column] == enemy){                     // enemy piece found in quadruple?
                enemyPiecesCounter++;
                break;
            }
        }
        if(enemyPiecesCounter == 0)                             // if no enemy piece found return pov piece number
            return povPiecesCounter;
        if(povPiecesCounter != 0)                               // if both kind of pieces found return 0
            return 0;
        
        enemyPiecesCounter = 0;
        
        for(i = row; i < row + 4; i++){
            if(boardPieces[i][column] == enemy)                 // enemy piece found in quadruple?
                enemyPiecesCounter++;
            if(boardPieces[i][column] == pov)                   // pov's piece also found in quadruple?
                return 0;
        }
        return -enemyPiecesCounter;
    }
    
    /* Diagonal quadruples start from the top piece and go
     * either left for top diagonal quadruples or right for
     *  bottom diagonal quadruples
     */

    /* Diagonal quadruples go start from top right and end bottom left */
    public int isPlayersQuadrupleTopDiagonal(int row, int column, char pov){
        int povPiecesCounter = 0;
        int enemyPiecesCounter = 0;
        int i = row;
        int j = column;
        char enemy = (pov == 'a') ? 'p' : 'a';                  // configure enemy piece
        
        if(row > ROWS - 4 || column < 3)                        // quadruple will go out of bounds
            return 0;
        for(i = row, j = column; i < row + 4; i++, j--){        // always calculate for the top piece
            if(boardPieces[i][j] == pov)                        // pov's piece foind in quadruple?
                povPiecesCounter++;
            if(boardPieces[i][j] == enemy){                     // enemy piece found in quadruple?
                enemyPiecesCounter++;
                break;
            }
        }
        
        if(enemyPiecesCounter == 0)                             // if no enemy piece found return pov piece number
            return povPiecesCounter;
        if(povPiecesCounter != 0)                               // if both kind of pieces found return 0
            return 0;
        
        enemyPiecesCounter = 0;
        
        for(i = row, j = column; i < row + 4; i++, j--){
            if(boardPieces[i][j] == enemy)                      // enemy piece found in quadruple?
                enemyPiecesCounter++;
            if(boardPieces[i][j] == pov)                        // pov's piece also found in quadruple?
                return 0;
        }
        return -enemyPiecesCounter;
    }

    /* DIagonal bottom quadruples start from top left and end bottom right */
    public int isPlayersQuadrupleBottomDiagonal(int row, int column, char pov){
        int povPiecesCounter = 0;
        int enemyPiecesCounter = 0;
        int i = row;
        int j = column;
        char enemy = (pov == 'a') ? 'p' : 'a';                  // configure enemy piece
        
        if(row > ROWS - 4 || column > COLUMNS - 4)              // quadruple will go out of bounds
            return 0;

        for(i = row, j = column; i < row + 4; i++, j++){
            if(boardPieces[i][j] == pov)                        // pov's piece found in quadruple?
                povPiecesCounter++;
            if(boardPieces[i][j] == enemy){                     // enemy piece found in quadruple?
                enemyPiecesCounter++;
                break;
            }
        }

        if(enemyPiecesCounter == 0)                             // if no enemy piece found return pov piece number
            return povPiecesCounter;
        if(povPiecesCounter != 0)                               // if both kind of pieces found return 0
            return 0;
        
        enemyPiecesCounter = 0;

        for(i = row, j = column; i < row + 4; i++, j++){
            if(boardPieces[i][j] == enemy)                      // enemy piece found in quadruple?
                enemyPiecesCounter++;
            if(boardPieces[i][j] == pov)                        // pov's piece also found in quadruple?
                return 0;
        }
        return -enemyPiecesCounter;
    }
}
