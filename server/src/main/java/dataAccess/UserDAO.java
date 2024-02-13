package dataAccess;

import model.UserData;

public interface UserDAO {
    void insertUser(UserData user) throws DataAccessException; //PUT a user into db
    UserData getUser(String username) throws DataAccessException; //GET a user from db
    void clear(); //delete all data
}
