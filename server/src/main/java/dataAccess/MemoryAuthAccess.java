package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Random;

public class MemoryAuthAccess implements AuthDAO {

    private String newAuthToken;
    final private HashMap<String, AuthData> auths = new HashMap<>();
    public AuthData insertAuth(String username) throws DataAccessException {
        newAuthToken = generateString(new Random(), "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 8);
        var authData = new AuthData(newAuthToken, username);
        System.out.println("inserting auth into memory");
        auths.put(newAuthToken, authData);
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

    public static String generateString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
