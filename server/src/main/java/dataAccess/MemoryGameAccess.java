package dataAccess;


import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameAccess implements GameDAO {

    private int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();
    public GameData insertGame(GameData game) throws DataAccessException {
        game = new GameData(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
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
