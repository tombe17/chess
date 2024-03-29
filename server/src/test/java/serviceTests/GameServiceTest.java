package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.memory.MemoryGameAccess;
import dataAccess.mysql.MySqlGameAccess;
import exception.ResException;
import model.GameData;
import model.JoinGameRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.GameService;

import java.sql.SQLException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    static GameService service;
    static String nameTest;
    static String badName;
    static int badID;
    static String username;

    @BeforeAll
    static void setUp() throws SQLException, ResException, DataAccessException {
        service = new GameService(new MySqlGameAccess());
        nameTest = "BestGame";
        badName = "";
        badID = 3117;
        username = "Tyler";
    }

    @BeforeEach
    void before() throws ResException {
        service.clear();
    }

    @Test
    void makeGame() throws ResException {
        GameData gameTest = service.makeGame(nameTest);

        assertNotNull(gameTest);
    }

    @Test
    void invalidMakeGame() {
        assertThrows(ResException.class, () -> service.makeGame(badName));
    }

    @Test
    void getGame() throws ResException {
        GameData gameTest = service.makeGame(nameTest);
        GameData retrievedGame = service.getGame(gameTest.gameID());

        assertNotNull(retrievedGame);
        assertEquals(gameTest, retrievedGame);
    }

    @Test
    void invalidGetGame() throws ResException {
        service.makeGame(nameTest);
        GameData retrievedGame = service.getGame(badID);

        assertNull(retrievedGame);
    }

    @Test
    void joinGame() throws ResException {
        GameData gameTest = service.makeGame(nameTest);

        service.joinGame(new JoinGameRequest("WHITE", gameTest.gameID()), username);
        GameData retrievedGame = service.getGame(gameTest.gameID());

        assertEquals(username, retrievedGame.whiteUsername());
    }

    @Test
    void invalidJoinGame() throws ResException {
        GameData gameTest = service.makeGame(nameTest);

        service.joinGame(new JoinGameRequest("WHITE", gameTest.gameID()), username);
        assertThrows(ResException.class, () -> service.joinGame(new JoinGameRequest("WHITE", gameTest.gameID()), username));
    }

    @Test
    void getAllGames() throws ResException {
        var games = new HashSet<GameData>();
        GameData testGame = service.makeGame(nameTest);
        games.add(testGame);
        testGame = service.makeGame("game2");
        games.add(testGame);
        testGame = service.makeGame("game3");
        games.add(testGame);
        var retrievedGames = service.getAllGames();

        assertNotNull(retrievedGames);
        assertEquals(games.size(), retrievedGames.size());

    }

    @Test
    void clear() throws ResException {
        GameData testGame = service.makeGame(nameTest);
        service.clear();

        assertNull(service.getGame(testGame.gameID()));
    }
}