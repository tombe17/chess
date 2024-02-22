package dataAccess;


import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameAccess implements GameDAO {

    private int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();
    public GameData insertGame(String gameName) throws DataAccessException {
        var game = new GameData(nextId++, null, null, gameName, new ChessGame());
        games.put(game.gameID(),game);
        return game;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public void updateGame(int gameID) throws DataAccessException {

    }

    public Collection<GameData> getAllGames() throws DataAccessException {
        return games.values();
    }

    public void clear() {
        games.clear();
    }
}
