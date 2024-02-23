package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import exception.ResException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.function.Executable;

import java.util.Objects;

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
            if (userInMemory == null || !Objects.equals(user.password(), userInMemory.password())) {
                throw new ResException(401, "Error: Unauthorized");
            } else {
                return userInMemory;
            }
        } catch (DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }

    }

    public AuthData addUser(UserData user) throws ResException {
        System.out.println("in UserService - adding user");
        AuthData authData;
        try {
            if (userAccess.getUser(user.username()) == null) {
                if (user.username() == null || user.password() == null || user.email() == null) {
                    throw new ResException(400, "Error: bad request");
                }
                userAccess.insertUser(user);
                authData = authAccess.insertAuth(user.username());
                return authData;

            } else {
                throw new ResException(403, "Error: already taken");
            }

        } catch (DataAccessException e) {
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
            AuthData auth = authAccess.getAuth(authToken);
            if (auth == null) {
                throw new ResException(401, "Error: unauthorized");
            } else {
                return auth;
            }
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
