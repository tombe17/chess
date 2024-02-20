package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    final private UserDAO userAccess;
    final private AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public UserData getUser(UserData user) throws DataAccessException {
        System.out.println("In US - getting user");
        return userAccess.getUser(user.username());
    }

    public UserData addUser(UserData user) throws DataAccessException {
        System.out.println("in UserService - adding user");
        return userAccess.insertUser(user);
    }

    public AuthData makeAuth(String username) throws DataAccessException {
        System.out.println("in US - making auth");
        return authAccess.insertAuth(username);
    }

    public void deleteAuth(AuthData auth) throws DataAccessException {
        System.out.println("in US - deleting auth");
        authAccess.deleteAuth(auth.authToken());
    }

    public void clear() {
        userAccess.clear();
        authAccess.clear();
    }

}
