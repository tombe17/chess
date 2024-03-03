package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import dataAccess.memory.MemoryAuthAccess;
import dataAccess.memory.MemoryUserAccess;
import dataAccess.mysql.MySqlAuthAccess;
import dataAccess.mysql.MySqlUserAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AuthAccessTest {

    private static String username;
    @BeforeAll
    public static void setUp() {
        username = "Thomas";
    }
    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthAccess.class, MySqlAuthAccess.class})
    void insertAuth(Class<? extends AuthDAO> authAccessClass) throws Exception {
        var authAccess = authAccessClass.getDeclaredConstructor().newInstance();
        AuthData authTest = authAccess.insertAuth(username);

        assertNotNull(authTest);
        assertNotNull(authTest.authToken());
        assertEquals(username, authTest.username());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthAccess.class, MySqlAuthAccess.class})
    void getAuth(Class<? extends AuthDAO> authAccessClass) throws Exception {
        var authAccess = authAccessClass.getDeclaredConstructor().newInstance();
        AuthData authData = authAccess.insertAuth(username);
        AuthData authTest = authAccess.getAuth(authData.authToken());

        assertNotNull(authTest);
        assertEquals("Thomas", authTest.username());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthAccess.class, MySqlAuthAccess.class})
    void invalidGetAuth(Class<? extends AuthDAO> authAccessClass) throws Exception {
        var authAccess = authAccessClass.getDeclaredConstructor().newInstance();
        authAccess.insertAuth(username);
        AuthData authTest = authAccess.getAuth(username);

        assertNull(authTest);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthAccess.class, MySqlAuthAccess.class})
    void deleteAuth(Class<? extends AuthDAO> authAccessClass) throws Exception {
        var authAccess = authAccessClass.getDeclaredConstructor().newInstance();
        AuthData authTest = authAccess.insertAuth(username);
        authAccess.deleteAuth(authTest.authToken());

        assertNull(authAccess.getAuth(authTest.authToken()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthAccess.class, MySqlAuthAccess.class})
    void clear(Class<? extends AuthDAO> authAccessClass) throws Exception {
        var authAccess = authAccessClass.getDeclaredConstructor().newInstance();
        AuthData authData = authAccess.insertAuth(username);
        authAccess.clear();
        AuthData authTest = authAccess.getAuth(authData.authToken());
        assertNull(authTest);
    }
}