package dataAccess;

//import javax.xml.crypto.Data;
import exception.ResException;
import model.AuthData;

public interface AuthDAO {
    AuthData insertAuth(String username) throws DataAccessException, ResException; //PUT auth in db
    AuthData getAuth(String authToken) throws DataAccessException, ResException; //GET auth token from db
    void deleteAuth(String authToken) throws DataAccessException, ResException; //DELETE given auth
    void clear() throws ResException; //DELETE all auth

}
