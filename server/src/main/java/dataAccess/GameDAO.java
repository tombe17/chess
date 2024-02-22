package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData insertGame(String gameName) throws DataAccessException; //PUT game in db
    GameData getGame(int gameID) throws DataAccessException; //GET game from db
    void updateGame(int gameID) throws DataAccessException; //UPDATE game in db w/ adding player/move
    Collection<GameData> getAllGames() throws DataAccessException; //GET all games
    void clear(); //DELETE all games
}
