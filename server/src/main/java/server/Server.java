package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.memory.MemoryAuthAccess;
import dataAccess.memory.MemoryGameAccess;
import dataAccess.memory.MemoryUserAccess;
import dataAccess.mysql.MySqlAuthAccess;
import dataAccess.mysql.MySqlGameAccess;
import dataAccess.mysql.MySqlUserAccess;
import exception.ResException;
import model.*;
import services.GameService;
import services.Message;
import services.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.sql.SQLException;

public class Server {

    private final UserService userService;
    private final GameService gameService;

    public Server() {
        UserDAO userDataAccess;
        AuthDAO authDataAccess;
        GameDAO gameDataAccess;

        try {
            userDataAccess = new MySqlUserAccess();
        } catch (SQLException | ResException | DataAccessException e) {
            userDataAccess = new MemoryUserAccess();
        }
        try {
            authDataAccess = new MySqlAuthAccess();
        } catch (ResException e) {
            authDataAccess = new MemoryAuthAccess();
        }
        try {
            gameDataAccess = new MySqlGameAccess();
        } catch (SQLException | ResException | DataAccessException e) {
            gameDataAccess = new MemoryGameAccess();
        }

        userService = new UserService(userDataAccess, authDataAccess);
        gameService = new GameService(gameDataAccess);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::deleteAll);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(ResException.class, this::handleExceptions);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void handleExceptions(ResException exc, Request req, Response res) {
        res.status(exc.getStatus());
        res.body(new Gson().toJson(new Message(exc.getMessage())));
    }

    private Object registerUser(Request req, Response res) throws ResException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        String json;

        AuthData authData = userService.addUser(user);

        res.status(200);
        res.type("application/json");
        json = new Gson().toJson(authData);
        res.body(json);

        return res.body();
    }
    private Object loginUser(Request req, Response res) throws ResException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        user = userService.getUser(user);
        AuthData auth = userService.makeAuth(user.username());

        res.status(200);
        res.body(new Gson().toJson(auth));
        return res.body();
    }
    private Object logoutUser(Request req, Response res) throws ResException {
        String authToken = req.headers("Authorization");
        AuthData auth = userService.getAuth(authToken);
        userService.deleteAuth(auth);
        res.status(200);
        return "";
    }

    private Object listGames(Request req, Response res) throws ResException {
        String authToken = req.headers("Authorization");
        userService.getAuth(authToken);
        var games = new ListGamesResult(gameService.getAllGames());
        res.status(200);
        return new Gson().toJson(games);
    }
    private Object createGame(Request req, Response res) throws ResException {
        String authToken = req.headers("Authorization");
        userService.getAuth(authToken);

        var gameName = new Gson().fromJson(req.body(), CreateChessRequest.class);
        var game = gameService.makeGame(gameName.gameName());
        String gameIDString = Integer.toString(game.gameID());
        var gameRes = new CreateChessResult(gameIDString);
        res.status(200);

        res.body(new Gson().toJson(gameRes));
        return res.body();

    }
    private Object joinGame(Request req, Response res) throws ResException {
        AuthData auth = userService.getAuth(req.headers("Authorization"));
        var gameReq = new Gson().fromJson(req.body(), JoinGameRequest.class);

        gameService.joinGame(gameReq, auth.username());
        res.status(200);
        return new Gson().toJson(res.body());
    }

    private Object deleteAll(Request req, Response res) throws ResException {
        userService.clear();
        gameService.clear();
        return "";
    }

}
