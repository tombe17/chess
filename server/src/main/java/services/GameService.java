package services;

import dataAccess.GameDAO;
import model.GameData;

public class GameService {

    private final GameDAO gameAccess;
    public GameService(GameDAO gameAccess) {
        this.gameAccess = gameAccess;
    }

    public GameData makeGame() {
        return null;
    }

    public GameData getGame() {
        return null;
    }

    public GameData joinGame() {
        return null;
    }

    public GameData getAllGames() {
        return null;
    }

    public void clear() {
        gameAccess.clear();
    }

}
