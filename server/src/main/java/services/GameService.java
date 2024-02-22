package services;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import exception.ResException;
import model.GameData;
import model.JoinGameRequest;

import java.util.Collection;
import java.util.Objects;

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

    public GameData getGame(int gameID) throws ResException {
        try {
            return gameAccess.getGame(gameID);
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }

    }

    public void joinGame(JoinGameRequest gameReq, String username) throws ResException {
        try {
            GameData game = getGame(gameReq.gameID());
            if (game == null) {
                throw new ResException(400,"Error: bad request");
            } else {
                if (Objects.equals(gameReq.playerColor(), "WHITE") && game.whiteUsername() != null) {
                    throw new ResException(403, "Error: already taken");
                } else if (Objects.equals(gameReq.playerColor(), "BLACK") && game.blackUsername() != null) {
                    throw new ResException(403, "Error: already taken");
                }


                gameAccess.updateGame(gameReq.playerColor(), username, gameReq.gameID());
            }
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }
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
