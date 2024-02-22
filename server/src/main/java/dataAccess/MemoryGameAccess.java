package dataAccess;


import chess.ChessGame;
import exception.ResException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

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

    public void updateGame(String playerColor, String username, int gameID) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData newGame;
        if (playerColor == null) {
            return;
        }
        switch (playerColor) {
            case "WHITE":
                newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                games.put(gameID, newGame);
                break;
            case "BLACK":
                newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                games.put(gameID, newGame);
                break;
            default:
                break;
        }
    }

    public Collection<GameData> getAllGames() throws DataAccessException {
        return games.values();
    }

    public void clear() {
        games.clear();
    }
}
