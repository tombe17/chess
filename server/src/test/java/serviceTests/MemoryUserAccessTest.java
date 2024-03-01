package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.memory.MemoryUserAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUserAccessTest {

    private static MemoryUserAccess memoryAccess;
    private static UserData testData;
    @BeforeAll
    public static void setUp() {
        testData = new UserData("Thomas", "pass123", "tom@boh.boh");
        memoryAccess = new MemoryUserAccess();
    }

    @Test
    void insertUser() throws DataAccessException {
        memoryAccess.insertUser(testData);

        UserData theUser = memoryAccess.getUser("Thomas");

        assertNotNull(theUser);
        assertEquals("Thomas", theUser.username());
        assertEquals("pass123", theUser.password());
        assertEquals("tom@boh.boh", theUser.email());
    }

    @Test
    void getUser() throws DataAccessException {
        memoryAccess.insertUser(testData);
        UserData testUser = memoryAccess.getUser("Thomas");
        assertNotNull(testUser);
        assertEquals("Thomas", testUser.username());
    }
    @Test
    void invalidGetUser() throws DataAccessException {
        memoryAccess.insertUser(testData);
        UserData testUser = memoryAccess.getUser("George");

        assertNull(testUser);
    }
    @Test
    void clear() throws DataAccessException {
        memoryAccess.insertUser(testData);
        memoryAccess.clear();
        UserData testUser = memoryAccess.getUser("Thomas");
        assertNull(testUser);
    }
}