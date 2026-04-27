package ce326.hw3;

import java.util.ArrayList;

public class History {
    ArrayList<Game> gameList;
    
    public History(){
        gameList = new ArrayList<>();
    }

    public ArrayList<Game> getGameList() {
        return gameList;
    }

    public void addGame(Game game) {
        gameList.add(game);
    }
    
    public String[] historyToString(){
        int size = gameList.size();
        String[] historyStr = new String[size];
        
        for(int i = 0; i < size; i++){
            Game game = gameList.get(i);
            historyStr[i] = game.toString();
        }
        return historyStr;
    }
}
