package dataAccess;

import model.GameData;

public interface GameDAO {
    void insertGame(GameData game) throws DataAccessException; //PUT game in db
    GameData getGame(String gameID) throws DataAccessException; //GET game from db
    void updateGame(String gameID) throws DataAccessException; //UPDATE game in db w/ adding player/move
    GameData getAllGames() throws DataAccessException; //GET all games
    void clear(); //DELETE all games
}
