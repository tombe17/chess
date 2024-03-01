package dataAccess.memory;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserAccess implements UserDAO {

    final private HashMap<String, UserData> users = new HashMap<>();
    public UserData insertUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
        System.out.println("inserting user into memory");
        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }
}
