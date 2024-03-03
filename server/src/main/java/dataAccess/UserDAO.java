package dataAccess;

import exception.ResException;
import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    UserData insertUser(UserData user) throws DataAccessException, ResException; //PUT a user into db
    UserData getUser(String username) throws DataAccessException, SQLException, ResException; //GET a user from db
    void clear() throws ResException; //delete all data
}
