package ce326.hw3;

import java.util.ArrayList;
import javax.swing.JButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;


/* This is the code that implements a Game in Connect4. Note that
 * many methods like nextMove() have been removed in order to implement
 * GUI and some of the class's functionality was transfered to the GUI
 * class.
 */

public class Game {
    char winner;                                                // 'p' for PLAYER, 'a' for AI, 't' for TIE, '-' not yet decided
    char starter;
    char turn;
    int difficulty;                                             // defines game difficulty
    ArrayList<Integer> moveSequence;                            // contains the move sequence, first to last
    OptimalTree decisionTree;
    JButton[][] buttons;
    LocalDateTime gameTimeStart, gameTimeEnd;

    public Game(int difficulty, JButton[][] buttons){
        winner = '-';
        turn = 'a';
        starter = 'a';
        this.difficulty = difficulty;
        this.buttons = buttons;
        moveSequence = new ArrayList<Integer>();
        gameTimeStart = LocalDateTime.now();
        gameTimeEnd = null;
        decisionTree = new OptimalTree(new MaximizerNode(new Board()));
        decisionTree.rebuild(difficulty);
    }

    public Game(char starter, int difficulty, JButton[][] buttons){
        winner = '-';
        this.turn = starter;
        this.starter = starter;
        this.difficulty = difficulty;
        this.buttons = buttons;
        moveSequence = new ArrayList<Integer>();
        gameTimeStart = LocalDateTime.now();
        gameTimeEnd = null;
        if(turn == 'a')
            decisionTree = new OptimalTree(new MaximizerNode(new Board()));
        else
            decisionTree = new OptimalTree(new MinimizerNode(new Board()));
        decisionTree.rebuild(difficulty);
    }

    public char getWinner() {
        return winner;
    }

    public void setWinner(char winner) {
        this.winner = winner;
    }

    public void setStartingPlayer(char starter){
        turn = starter;
    }

    public void swapTurn(){
        turn = (turn == 'p') ? 'a' : 'p';
    }

    /* Sets root to a maximizer since when player plays the root
     * is a minimizer. Player cannot use the algorithm though :(
     */
    public int nextMovePlayer(int column){
        decisionTree.rebuild(difficulty);
        int row = decisionTree.getRoot().board.getRow(column);
        decisionTree.setRoot(decisionTree.getRoot().getChild(column));
        recordMove(column);
        if(decisionTree.getRoot().board.someoneHasWon() || decisionTree.getRoot().board.someoneHasWon())
            setWinner(decisionTree.getRoot().board.winner);
        return row;
    }

    /* Sets the tree to a minimizer but doesn't matter since
     * nextPlayerMove will set root to a maximizer again.
     */
    public int nextMoveAI() {
        decisionTree.rebuild(difficulty);
        JButton b = null;
        // Disable the buttons
        for (int row = 0; row < Board.ROWS; row++) {
            for(int col = 0; col < Board.COLUMNS; col++){
                b = buttons[row][col];
                b.setEnabled(false);
            }
        }
        
        decisionTree.minMax();
        int aiMove = decisionTree.optimalPath().get(0);
        decisionTree.setRoot(decisionTree.getRoot().getChild(aiMove));
        recordMove(aiMove);
    
        if(decisionTree.getRoot().board.someoneHasWon() || decisionTree.getRoot().board.someoneHasWon()){
            setWinner(decisionTree.getRoot().board.winner);
            return aiMove;
        }
        // Enable the buttons
        for (int row = 0; row < Board.ROWS; row++) {
            for(int col = 0; col < Board.COLUMNS; col++){
                b = buttons[row][col];
                b.setEnabled(true);
            }
        }
        return aiMove;
    }
    
    // Used to record moves int a game
    void recordMove(int column){
        moveSequence.add(column);
    }

    // Returns true if game is over (winner decided or board is full).
    // Returns false if game is not over.
    public boolean hasEnded(){
        return decisionTree.getRoot().board.someoneHasWon() | decisionTree.getRoot().board.isBoardFull();
    }

    // The game has ended with a winner
    public void end(){
        gameTimeEnd = LocalDateTime.now();
    }

    // Converts difficulty to the corresponding String
    public String difficultyString(){
        return (difficulty == 1) ? "Trivial" : (difficulty == 3) ? "Medium" : "Hard";
    }

    // Converts winner to the corresponding String
    public String winneString(){
        return (winner == 'p') ? "P" : (winner == 'a') ? "AI" : "TIE" ; 
    }

    @Override
    public String toString(){
        String str = "";
        str += gameTimeStart.toString() + " - " + gameTimeEnd.toString();
        str += " L: " + difficultyString();
        str += " W:" + winneString();
        return str;
    }

    // creates a file object given a filepath
    public static void insertStringAtBeginning(String filePath, String text) {
        try {
            File file = new File(filePath);
            insertStringAtBeginning(file, text);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // inserts a String (which is game's info) 
    // in a file (which is probably historyRecord file).
    public static void insertStringAtBeginning(File file, String text) {
        try {
            if (file.exists()) {
                // Read the existing content of the file
                StringBuilder fileContent = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));

                // Read content already in the file
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    fileContent.append(currentLine).append("\n");
                }
                reader.close();

                // Write the new content with the inserted string
                FileWriter writer = new FileWriter(file);
                writer.write(text + "\n");                      // first write new game
                writer.write(fileContent.toString());           // then write old content
                writer.close();
            } else {
                // If the file doesn't exist, create a new file and write the string
                FileWriter writer = new FileWriter(file);
                writer.write(text);
                writer.close();
            }

            System.out.println("String inserted at the beginning of the file successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Used to pring the moves that happened during the game
    // one by one
    public void printMoveSequence(){
        System.out.println("");
        char notStarter = (starter == 'p') ? 'a' : 'p';                         // defined as starter's opponent
        System.out.println("Move Sequence for this Game was: ");
        for(int i = 0; i < moveSequence.size(); i++)
            if(i % 2 == 0)
                System.out.println(starter + " " + moveSequence.get(i));        // moves in indicies 0, 2, 4, 6 ... belong to the starter
            else
                System.out.println(notStarter + " " + moveSequence.get(i));     // moves in indicies 1, 3, 5, 7 ... belong to the notStarter
    }

    // Assume winner is decided
    // Used for Console gameplay (test before gui was implemented)
    public void printWinner(){
        if (winner == 'p')
            System.out.println("Winner: Player");
        else if(winner == 'a')
            System.out.println("Winner: AI");
        else
            System.out.println("Winner: None");
    }

}
