package dataAccess;

import chess.ChessGame;
import exception.ResException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData insertGame(String gameName) throws DataAccessException, ResException; //PUT game in db
    GameData getGame(int gameID) throws DataAccessException, ResException; //GET game from db
    void updateGame(String playerColor, String username, int gameID) throws DataAccessException, ResException; //UPDATE game in db w/ adding player/move
    Collection<GameData> getAllGames() throws DataAccessException, ResException; //GET all games
    void clear() throws ResException; //DELETE all games

    void makeMove(ChessGame game, int gameID) throws ResException;
}
