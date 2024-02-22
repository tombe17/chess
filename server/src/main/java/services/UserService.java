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

    public UserData getUser(UserData user) throws ResException {
        System.out.println("In US - getting user");
        try {
            UserData userInMemory = userAccess.getUser(user.username());
            if (userInMemory != null) {
                return userInMemory;
            } else {
                throw new ResException(401, "Error: Unauthorized");
            }
        } catch (DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }

    }

    public AuthData addUser(UserData user) throws ResException {
        System.out.println("in UserService - adding user");
        AuthData authData = null;
        try {
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

    public AuthData makeAuth(String username) throws ResException {
        System.out.println("in US - making auth");
        try {
            return authAccess.insertAuth(username);
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }

    }

    public AuthData getAuth(String authToken) throws ResException {
        System.out.println("in US - getting auth");
        try {
            return authAccess.getAuth(authToken);
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }
    }

    public void deleteAuth(AuthData auth) throws ResException {
        System.out.println("in US - deleting auth");
        try {
            authAccess.deleteAuth(auth.authToken());
        } catch(DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }
    }

    public void clear() {
        userAccess.clear();
        authAccess.clear();
    }

}
