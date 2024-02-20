package services;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

public class UserService {
    final private UserDAO userAccess;

    public UserService(UserDAO userAccess) {
        this.userAccess = userAccess;
    }

    public UserData getUser(UserData user) throws DataAccessException {
        System.out.println("In US - getting user");
        return userAccess.getUser(user.username());
    }

    public UserData addUser(UserData user) throws DataAccessException {
        System.out.println("in UserService - adding user");
        return userAccess.insertUser(user);
    }

    public void clear() {
        userAccess.clear();
    }
}
