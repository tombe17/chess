package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.memory.MemoryAuthAccess;
import dataAccess.memory.MemoryUserAccess;
import dataAccess.mysql.MySqlAuthAccess;
import dataAccess.mysql.MySqlUserAccess;
import exception.ResException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.UserService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    static UserData testUser;
    static UserData newUser;
    static UserData badUser;
    static UserService services;
    static String badAuth;
    @BeforeAll
    static void setUp() throws SQLException, ResException, DataAccessException {
        testUser = new UserData("Thomas", "pass123", "tom@boh.boh");
        newUser = new UserData("Newbie", "skills", "new@new.com");
        badUser = new UserData(null, null, "email@foo.com");
        badAuth = "idk";
        services = new UserService(new MySqlUserAccess(), new MySqlAuthAccess());
    }

    @BeforeEach
    void before() throws ResException {
        services.clear();
    }

    @Test
    void addUser() throws ResException {
        AuthData auth = services.addUser(testUser);

        assertNotNull(auth);
    }

    @Test
    void invalidAddUser() {
        assertThrows(ResException.class, () -> services.addUser(badUser));

    }
    @Test
    void getUser() throws ResException {
        services.addUser(testUser);
        UserData retrievedUser = services.getUser(testUser);

        assertNotNull(retrievedUser);
        assertEquals(testUser.username(), retrievedUser.username());
    }

    @Test
    void invalidGetUser() throws ResException {
        services.addUser(testUser);

        assertThrows(ResException.class, () -> services.getUser(badUser));
    }

    @Test
    void makeAuth() throws ResException {
        AuthData authTest = services.makeAuth(testUser.username());

        assertNotNull(authTest);
    }

    @Test
    void getAuth() throws ResException {
        AuthData authTest = services.makeAuth(testUser.username());
        AuthData retrievedAuth = services.getAuth(authTest.authToken());

        assertNotNull(retrievedAuth);
        assertEquals(authTest, retrievedAuth);
    }

    @Test
    void InvalidGetAuth() {
        assertThrows(ResException.class, () -> services.getAuth(badAuth));
    }

    @Test
    void deleteAuth() throws ResException {
        AuthData authTest = services.makeAuth(testUser.username());
        services.deleteAuth(authTest);

        assertThrows(ResException.class, () -> services.getAuth(testUser.username()));
    }

    @Test
    void clearUser() throws ResException {
        services.addUser(testUser);
        services.clear();
        assertThrows(ResException.class, () -> services.getUser(testUser));
    }

    @Test
    void clearAuth() throws ResException {
        services.makeAuth(newUser.username());
        services.clear();
        assertThrows(ResException.class, () -> services.getAuth(newUser.username()));
    }
}