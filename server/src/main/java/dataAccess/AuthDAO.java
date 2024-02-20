package dataAccess;

//import javax.xml.crypto.Data;
import model.AuthData;

public interface AuthDAO {
    AuthData insertAuth(String username) throws DataAccessException; //PUT auth in db
    AuthData getAuth(String authToken) throws DataAccessException; //GET auth token from db
    void deleteAuth(String authToken) throws DataAccessException; //DELETE given auth
    void clear(); //DELETE all auth

}
