package serviceTests;

import dataAccess.UserDAO;
import dataAccess.memory.MemoryUserAccess;
import dataAccess.mysql.MySqlUserAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserAccessTest {

    private static UserData testData;
    @BeforeAll
    public static void setUp() {
        testData = new UserData("Thomas", "pass123", "tom@boh.boh");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserAccess.class, MySqlUserAccess.class})
    void insertUser(Class<? extends UserDAO> userAccessClass) throws Exception {
        var userAccess = userAccessClass.getDeclaredConstructor().newInstance();
        userAccess.insertUser(testData);

        UserData theUser = userAccess.getUser("Thomas");

        assertNotNull(theUser);
        assertEquals("Thomas", theUser.username());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserAccess.class, MySqlUserAccess.class})
    void getUser(Class<? extends UserDAO> userAccessClass) throws Exception {
        var userAccess = userAccessClass.getDeclaredConstructor().newInstance();
        userAccess.insertUser(testData);
        UserData testUser = userAccess.getUser("Thomas");
        assertNotNull(testUser);
        assertEquals("Thomas", testUser.username());
    }
    @ParameterizedTest
    @ValueSource(classes = {MemoryUserAccess.class, MySqlUserAccess.class})
    void invalidGetUser(Class<? extends UserDAO> userAccessClass) throws Exception {
        var userAccess = userAccessClass.getDeclaredConstructor().newInstance();
        userAccess.insertUser(testData);
        UserData testUser = userAccess.getUser("George");

        assertNull(testUser);
    }
    @ParameterizedTest
    @ValueSource(classes = {MemoryUserAccess.class, MySqlUserAccess.class})
    void clear(Class<? extends UserDAO> userAccessClass) throws Exception {
        var userAccess = userAccessClass.getDeclaredConstructor().newInstance();

        userAccess.insertUser(testData);
        userAccess.clear();
        UserData testUser = userAccess.getUser("Thomas");
        assertNull(testUser);
    }
}