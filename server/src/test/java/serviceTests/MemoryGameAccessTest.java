package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.memory.MemoryGameAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MemoryGameAccessTest {

    private static MemoryGameAccess gameAccess;
    private static String testName;
    private static String userName;
    private static GameData testGame;
    private static int gameID;

    @BeforeAll
    public static void setUp() {
        gameAccess = new MemoryGameAccess();
        testName = "gameName";
        userName = "Thomas";
    }
    @Test
    void insertGame() throws DataAccessException {
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        assertNotNull(testGame);
        assertEquals(gameID, testGame.gameID());

    }

    @Test
    void getGame() throws DataAccessException {
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        GameData retrievedGame = gameAccess.getGame(gameID);

        assertNotNull(retrievedGame);
        assertEquals(testGame, retrievedGame);
    }

    @Test
    void updateGame() throws DataAccessException {
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        gameAccess.updateGame("WHITE", userName, gameID);
        GameData returnGame = gameAccess.getGame(gameID);

        assertNotEquals(testGame, returnGame);
        assertEquals(userName, returnGame.whiteUsername());
    }

    @Test
    void getAllGames() throws DataAccessException {
        var games = new HashSet<GameData>();
        testGame = gameAccess.insertGame("game1");
        games.add(testGame);
        testGame = gameAccess.insertGame("game2");
        games.add(testGame);
        testGame = gameAccess.insertGame("game3");
        games.add(testGame);
        var retrievedGames = gameAccess.getAllGames();

        assertNotNull(games);
        assertNotNull(retrievedGames);
        assertTrue(games.containsAll(retrievedGames) && retrievedGames.containsAll(games));

    }

    @Test
    void clear() throws DataAccessException {
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        gameAccess.clear();
        GameData returnGame = gameAccess.getGame(gameID);

        assertNull(returnGame);
    }
}