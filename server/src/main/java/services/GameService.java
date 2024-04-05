package services;

import chess.ChessGame;
import chess.InvalidMoveException;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import exception.ResException;
import model.GameData;
import model.JoinGameRequest;
import webSocketMessages.userCommands.LeaveCom;
import webSocketMessages.userCommands.MakeMoveCom;

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
            if (gameName == null || gameName.isEmpty()) {
                throw new ResException(400, "Error: bad request");
            }
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

    public void makeMove(MakeMoveCom moveCmd) throws ResException, InvalidMoveException {
        System.out.println("In makeMove GS");
        ChessGame game = getGame(moveCmd.getGameID()).game();
        //make sure it's your turn
        if (moveCmd.getTeamColor() == game.getTeamTurn()) {
            //validate move
            game.makeMove(moveCmd.getMove());
            gameAccess.makeMove(game, moveCmd.getGameID());
        } else {
            System.out.println("Not your turn");
        }
    }

    public void playerLeave(LeaveCom cmd, String teamColor) throws DataAccessException, ResException {
        gameAccess.updateGame(teamColor, null, cmd.getGameID());
    }

    public Collection<GameData> getAllGames() throws ResException {
        System.out.println("In GS - getGames");
        try {
            return gameAccess.getAllGames();
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }
    }

    public void clear() throws ResException {
        gameAccess.clear();
    }

}
