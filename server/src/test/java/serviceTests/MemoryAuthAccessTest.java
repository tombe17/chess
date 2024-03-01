package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.memory.MemoryAuthAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAuthAccessTest {

    private static MemoryAuthAccess memoryAccess;
    private static String username;
    @BeforeAll
    public static void setUp() {
        memoryAccess = new MemoryAuthAccess();
        username = "Thomas";
    }
    @Test
    void insertAuth() throws DataAccessException {

        AuthData authTest = memoryAccess.insertAuth(username);

        assertNotNull(authTest);
        assertNotNull(authTest.authToken());
        assertEquals(username, authTest.username());
    }

    @Test
    void getAuth() throws DataAccessException {
        AuthData authData = memoryAccess.insertAuth(username);
        AuthData authTest = memoryAccess.getAuth(authData.authToken());

        assertNotNull(authTest);
        assertEquals("Thomas", authTest.username());
    }

    @Test
    void invalidGetAuth() throws DataAccessException {
        memoryAccess.insertAuth(username);
        AuthData authTest = memoryAccess.getAuth(username);

        assertNull(authTest);
    }

    @Test
    void deleteAuth() throws DataAccessException {
        AuthData authTest = memoryAccess.insertAuth(username);
        memoryAccess.deleteAuth(authTest.authToken());

        assertNull(memoryAccess.getAuth(authTest.authToken()));
    }

    @Test
    void clear() throws DataAccessException {
        AuthData authData = memoryAccess.insertAuth(username);
        memoryAccess.clear();
        AuthData authTest = memoryAccess.getAuth(authData.authToken());
        assertNull(authTest);
    }
}