package services;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import exception.ResException;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final GameDAO gameAccess;
    public GameService(GameDAO gameAccess) {
        this.gameAccess = gameAccess;
    }

    public GameData makeGame(String gameName) throws ResException {
        System.out.println("in GS - addGame");
        try {
            return gameAccess.insertGame(gameName);
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }
    }

    public GameData getGame() {
        return null;
    }

    public GameData joinGame() {
        return null;
    }

    public Collection<GameData> getAllGames() throws ResException {
        System.out.println("In GS - getGames");
        try {
            return gameAccess.getAllGames();
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }
    }

    public void clear() {
        gameAccess.clear();
    }

}
