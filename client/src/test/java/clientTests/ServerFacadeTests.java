package clientTests;

import client.ServerFacade;
import exception.ResException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static final UserData user = new UserData("Thomas", "pass123", "tom@tom.com");
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterEach
    void clear() throws ResException {
        facade.clearAll();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerGood() throws ResException {
        var auth = facade.registerUser(user);
        Assertions.assertTrue(auth.authToken().length() > 10);
    }

    @Test
    public void registerBad() {
        Assertions.assertThrows(ResException.class, () -> facade.registerUser(new UserData("Bob", null, "bob@bob.com")));
    }

    @Test
    public void loginGood() throws ResException {
        facade.registerUser(user);
        var auth = facade.loginUser(user);
        Assertions.assertTrue(auth.authToken().length() > 10);
    }

    @Test
    public void loginBad() throws ResException {
        facade.registerUser(user);
        Assertions.assertThrows(ResException.class, () -> facade.loginUser(new UserData("Thomas", "wrongpassword", null)));
    }

    @Test
    public void logoutGood() throws ResException {
        facade.registerUser(user);
        facade.loginUser(user);
        facade.logoutUser();
        Assertions.assertNull(facade.getAuth());
    }

    @Test
    public void logoutBad() {
        Assertions.assertThrows(ResException.class, () -> facade.createGame("badGameReq"));
    }

    @Test
    public void createGameGood() throws ResException {
        facade.registerUser(user);
        facade.loginUser(user);
        var id = facade.createGame("myGame");
        Assertions.assertNotNull(id);
    }

    @Test
    public void createGameBad() {
        Assertions.assertThrows(ResException.class, () -> facade.createGame("myGame"));
    }

    @Test
    public void joinGameGood() throws ResException {
        facade.registerUser(user);
        facade.loginUser(user);
        facade.createGame("myGame");
        facade.joinGame("WHITE", 1);

        var games = facade.listGames();
        GameData joinedGame = null;
        for (GameData game : games) {
            if (game.gameID() == 1) {
                joinedGame = game;
            }
        }
        Assertions.assertNotNull(joinedGame);
        Assertions.assertEquals(joinedGame.whiteUsername(), user.username());
    }

    @Test
    public void joinGameBad() {
        Assertions.assertThrows(ResException.class, () -> facade.joinGame(null, 2));
    }

    @Test
    public void listGamesGood() throws ResException {
        facade.registerUser(user);
        facade.loginUser(user);
        facade.createGame("game1");
        facade.createGame("game2");

        var games = facade.listGames();

        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void listGamesBad() {
        Assertions.assertThrows(ResException.class, () -> facade.listGames());
    }

    @Test
    public void clearDB() throws ResException {
        facade.registerUser(user);
        facade.loginUser(user);
        facade.createGame("game1");
        facade.clearAll();

        Assertions.assertThrows(ResException.class, () -> facade.loginUser(user));
    }
}
