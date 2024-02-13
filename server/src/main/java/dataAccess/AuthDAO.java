package dataAccess;

//import javax.xml.crypto.Data;
import model.AuthData;

public interface AuthDAO {
    void insertAuth(String username) throws DataAccessException; //PUT auth in db
    AuthData getAuth(String username) throws DataAccessException; //GET auth token from db
    void deleteAuth(String username) throws DataAccessException; //DELETE given auth
    void clear(); //DELETE all auth

}
