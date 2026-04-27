package ce326.hw3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* GUI class is the implementation of Connect4 GUI. Some of the Game class
 * functionality was transfered here in order to make the game more accessible.
 * Unfortunately, this resulted to some bugs during the Optimal Tree rebuilt
 * that was implemented into game.nextMove() method and was deleted in order
 * to seperate game.nextMovePlayer() and game.nextMoveAI()
 */

public class GUI extends JFrame implements ActionListener {

    JList<String> historyList;
    JScrollPane historyScrollPane;
    JPanel menuPanel, gamePanel, historyPanel;
    JButton[][] buttons;
    JButton startButton, starterButton, exitButton, historyButton;
    JLabel menuLabel;
    History history;
    Game game;
    char starter;
    int difficulty;
    
    // static definition of Player and AI piece Icons respectively
    private static final ImageIcon PLAYER_ICON;
    private static final ImageIcon AI_ICON;
    private static final String HISTORY_FILE_PATH = "historyRecord.txt";
    
    static {
        ImageIcon playerIcon = null;
        ImageIcon aiIcon = null;
        
        try {
            playerIcon = new ImageIcon(GUI.class.getResource("player.png"));
            aiIcon = new ImageIcon(GUI.class.getResource("ai.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        PLAYER_ICON = playerIcon;
        AI_ICON = aiIcon;
    }



    public GUI() {
        // Window Properties
        setTitle("Connect Four");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //History
        history = new History();
        historyList = new JList<>(new String[]{});  // Initialize with an empty array
        historyScrollPane = new JScrollPane(historyList);
        historyScrollPane.setBounds(330, 150, 200, 200);
        
        // initialize historyPanel
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("Game History"), BorderLayout.NORTH);
        add(historyPanel);
        historyPanel.setVisible(false);             // history is hidden when the window opens

        // AI starts by default
        starter = 'a';

        // horizontal menu bar
        menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        menuPanel.add(new JLabel("Welcome to Connect Four"));

        // Start button starts a new game
        startButton = new JButton("New Game");
        startButton.addActionListener(this);
        menuPanel.add(startButton);

        // Starter button selects the starter (current game is excluded)
        starterButton = new JButton("1st Player");
        starterButton.addActionListener(this);
        menuPanel.add(starterButton);

        // Exit from the game
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        menuPanel.add(exitButton);

        // Display history
        historyButton = new JButton("History");
        historyButton.addActionListener(this);
        menuPanel.add(historyButton);
        
        // menuPanel is on the top of the window
        add(menuPanel, BorderLayout.NORTH);

        // Initialize board
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(Board.ROWS, Board.COLUMNS));

        //initialize buttons grid
        buttons = initializeButtons();
        for(int row = 0; row < Board.ROWS; row++){
            for(int col = 0; col < Board.COLUMNS; col++){
                gamePanel.add(buttons[row][col]);
            }
        }

        // gamePanel is in the center of the window.
        add(gamePanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // This method empties the board that may has \
    // pieces from the previous game.
    public void emptyBoard(){
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLUMNS; col++) {
                buttons[row][col].setIcon(null);
            }
        }
    }

    public void decideDifficulty(){
        // Display the 3 options in a pop up
        Object[] options = {"Trivial", "Medium", "Hard"};
        int selectedOption = JOptionPane.showOptionDialog(
            null,
            "Please select a difficulty level:",
            "Difficulty Level",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);
        
        // set difficulty based on user selection
        if (selectedOption == 0)
            difficulty = 1;
        else if (selectedOption == 1)
            difficulty = 3;
        else if (selectedOption == 2)
            difficulty = 5;
    }

    public void startGame() {
        // display difficulty selection dialog
        decideDifficulty();

        // reset the game
        game = new Game(starter, difficulty, buttons);
    
        // empty the board
        emptyBoard();
    
        // show the game panel
        add(gamePanel, BorderLayout.CENTER);
        gamePanel.setVisible(true);
        historyPanel.setVisible(false);
    
        // if the AI starts, put AI's piece
        if (game.starter == 'a')
            putAiGUI();
    }

    // Returns a list of Strings (file's lines).
    // Used to return the games recorded.
    private List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // Project HISTORY_FILE_PATH contents to the GUI window
    public void showHistory() {
        // Read the contents of the historyRecord.txt file
        List<String> historyRecords = readFile(HISTORY_FILE_PATH);
    
        // Clear previous history panel and initialize it
        historyPanel.removeAll();
        historyPanel.add(new JLabel("Game History"), BorderLayout.NORTH);
    
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String record : historyRecords) {
            listModel.addElement(record); // Add each record to the listModel
        }
        historyList.setModel(listModel);
    
        // Make list scrollable
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        add(historyPanel, BorderLayout.CENTER);
    }    

    // This method corresponds to the AI's
    // move in the GUI
    public void putAiGUI(){
        int column = game.nextMoveAI();
        int row = game.decisionTree.getRoot().board.getRow(column);
        buttons[row+1][column].setIcon(AI_ICON);
        buttons[row+1][column].repaint();
    }

    // This method corresponds to the Player's
    // move in the GUI
    public void putPlayerGUI(int column){
        // if the move is valid, update the board and check for a win
        // switch to the next player and update the current player icon
        int row = game.nextMovePlayer(column);
        buttons[row][column].setIcon(PLAYER_ICON);
        buttons[row][column].repaint();
    }

    // Perform the corresponding action accordingly
    // to the event.
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        int column;
        /* Start game */
        if (source == startButton) {
            startGame();
        }
        else if(source == starterButton){
            // create a custom dialog with two radio buttons to choose from
            JDialog dialog = new JDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setModal(true);
            dialog.setTitle("Starter");

            // Panel to select starter. Default is AI.
            JPanel starterPanel = new JPanel(new GridLayout(2, 1));

            JRadioButton aiButton = new JRadioButton("AI");         // button for starter = ai
            JRadioButton playerButton = new JRadioButton("Player"); // button for starter = player

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(aiButton);
            buttonGroup.add(playerButton);

            starterPanel.add(aiButton);
            starterPanel.add(playerButton);

            dialog.add(starterPanel);

            // show the dialog and wait for user input
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            // set the value of the starter field based on the user's choice
            if(aiButton.isSelected()) 
                starter = 'a'; // AI starts
            else if(playerButton.isSelected()) 
                starter = 'p'; // Player starts
        }
        else if(source == exitButton){
            System.exit(0);
        }
        else if (source == historyButton) {
            // Display the history panel
            showHistory();
            gamePanel.setVisible(false);
            historyPanel.setVisible(true);    
        } 
        else{
            // get the button that was clicked and its column
            JButton button = (JButton) e.getSource();
            Board board = game.decisionTree.getRoot().board;
            column = Integer.parseInt(button.getActionCommand());
            
            // attempt to make the move in the given column
            if(board.isColumnFull(column))
                return;

            // if the move is valid, update the board and check for a win
            putPlayerGUI(column);
            
            // check end of game
            if (game.hasEnded()){
                game.end();                     // game over
                history.gameList.add(game);     // record game in history
                Game.insertStringAtBeginning(HISTORY_FILE_PATH, game.toString());
                return;                         // return, don't let AI move
            }

            // if game has not ended, it is AI's turn
            putAiGUI();
            
            // check for end of game
            if (game.hasEnded()){
                game.end();                     // game over
                history.gameList.add(game);     // record game in history
                Game.insertStringAtBeginning(HISTORY_FILE_PATH, game.toString());
                return;
            }
        }
        return;
    }

    // This method creates the JButton[][] array
    // and the JButton objects.
    public JButton[][] initializeButtons(){
        buttons = new JButton[Board.ROWS][Board.COLUMNS];
        for(int row = 0; row < Board.ROWS; row++){
            for(int column = 0; column < Board.COLUMNS; column++){
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 30));
                button.addActionListener(this);
                button.setActionCommand(""+column);
                button.setIcon(null);
                buttons[row][column] = button;
            }
        }
        return buttons;
    }
}

