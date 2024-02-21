package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import exception.ResException;
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

    public AuthData addUser(UserData user) throws ResException {
        System.out.println("in UserService - adding user");
        AuthData authData = null;
        try {
            userAccess.getUser(user.username());
            //if we do get them throw it
            if (userAccess.getUser(user.username()) == null) {
                //no user so we can add them
                if (user.username() == null || user.password() == null || user.email() == null) {
                    throw new ResException(400, "Error: bad request");
                }
                userAccess.insertUser(user);
                authData = authAccess.insertAuth(user.username());
                return authData;

            } else {
                throw new ResException(403, "Error: already taken");
            }

        } catch (DataAccessException e) { //500 error
            throw new ResException(500, e.getMessage());
        }
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
