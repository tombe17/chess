package dataAccess.memory;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthAccess implements AuthDAO {

    final private HashMap<String, AuthData> auths = new HashMap<>();
    public AuthData insertAuth(String username) throws DataAccessException {
        System.out.println("inserting auth into memory");

        String newToken = UUID.randomUUID().toString();
        var authData = new AuthData(newToken, username);

        auths.put(newToken, authData);
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    public void clear() {
        auths.clear();
    }
}
