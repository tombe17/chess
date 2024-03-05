package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.memory.MemoryGameAccess;
import dataAccess.mysql.MySqlGameAccess;
import exception.ResException;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GameAccessTest {

    private static String testName;
    private static String userName;
    private static GameData testGame;
    private static int gameID;

    @BeforeAll
    public static void setUp() {
        testName = "gameName";
        userName = "Thomas";
    }

   @BeforeEach
    public void clean() throws Exception {
        var gameAccess = new MySqlGameAccess();
        gameAccess.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameAccess.class, MySqlGameAccess.class})
    void insertGame(Class<? extends GameDAO> gameAccessClass) throws Exception {
        var gameAccess = gameAccessClass.getDeclaredConstructor().newInstance();
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        assertNotNull(testGame);
        assertEquals(gameID, testGame.gameID());

    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameAccess.class, MySqlGameAccess.class})
    void getGame(Class<? extends GameDAO> gameAccessClass) throws Exception {
        var gameAccess = gameAccessClass.getDeclaredConstructor().newInstance();
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        GameData retrievedGame = gameAccess.getGame(gameID);

        assertNotNull(retrievedGame);
        assertEquals(testGame, retrievedGame);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameAccess.class, MySqlGameAccess.class})
    void updateGame(Class<? extends GameDAO> gameAccessClass) throws Exception {
        var gameAccess = gameAccessClass.getDeclaredConstructor().newInstance();
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        gameAccess.updateGame("WHITE", userName, gameID);
        GameData returnGame = gameAccess.getGame(gameID);

        assertNotEquals(testGame, returnGame);
        assertEquals(userName, returnGame.whiteUsername());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameAccess.class, MySqlGameAccess.class})
    void getAllGames(Class<? extends GameDAO> gameAccessClass) throws Exception {
        var gameAccess = gameAccessClass.getDeclaredConstructor().newInstance();
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
        assertEquals(3, retrievedGames.size());

    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameAccess.class, MySqlGameAccess.class})
    void clear(Class<? extends GameDAO> gameAccessClass) throws Exception {
        var gameAccess = gameAccessClass.getDeclaredConstructor().newInstance();
        testGame = gameAccess.insertGame(testName);
        gameID = testGame.gameID();

        gameAccess.clear();
        GameData returnGame = gameAccess.getGame(gameID);

        assertNull(returnGame);
    }
}