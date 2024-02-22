package server;

import com.google.gson.Gson;
import dataAccess.*;
import exception.ResException;
import model.*;
import services.GameService;
import services.Message;
import services.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Collection;
import java.util.Objects;

public class Server {

    private final UserService userService;
    private final GameService gameService;

    public Server() {
        final UserDAO userAccess = new MemoryUserAccess();
        final AuthDAO authAccess = new MemoryAuthAccess();
        final GameDAO gameAccess = new MemoryGameAccess();
        userService = new UserService(userAccess, authAccess);
        gameService = new GameService(gameAccess);
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
        //set up the response obj
        res.status(200);
        res.type("application/json");
        json = new Gson().toJson(authData);
        res.body(json);

        return res.body();
    }
    private Object loginUser(Request req, Response res) throws ResException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        var userInMemory = userService.getUser(user);
        if (!Objects.equals(user.password(), userInMemory.password())) { //check password
           throw new ResException(401, "Error: unauthorized");
        }
        var auth = userService.makeAuth(user.username());
        res.status(200);
        res.body(new Gson().toJson(auth));
        return res.body();
    }
    private Object logoutUser(Request req, Response res) throws ResException {
        String authToken = req.headers("Authorization");
        AuthData auth = userService.getAuth(authToken);
        if (auth == null) {
            throw new ResException(401, "Error: unauthorized");
        } else {
            userService.deleteAuth(auth);
            res.status(200);
            return "";
        }
    }

    private Object listGames(Request req, Response res) throws ResException {
        String authToken = req.headers("Authorization");
        AuthData auth = userService.getAuth(authToken);
        if(auth == null) {
            throw new ResException(401, "Error: unauthorized");
        } else {
            var games = new ListGamesResponse(gameService.getAllGames());
            res.status(200);
            return new Gson().toJson(games);
        }
    }
    private Object createGame(Request req, Response res) throws ResException {
        String authToken = req.headers("Authorization");
        AuthData auth = userService.getAuth(authToken);
        if(auth == null) {
            throw new ResException(401, "Error: unauthorized");
        } else {
            var gameName = new Gson().fromJson(req.body(), CreateChessRequest.class);
            var game = gameService.makeGame(gameName.gameName());
            String gameIDString = Integer.toString(game.gameID());
            var gameRes = new CreateChessResponse(gameIDString);
            res.status(200);

            res.body(new Gson().toJson(gameRes));
            return res.body();
        }
    }
    private Object joinGame(Request req, Response res) throws ResException {
        AuthData auth = userService.getAuth(req.headers("Authorization"));
        if (auth == null) {
            throw new ResException(401, "Error: unauthorized");
        } else {
            var gameReq = new Gson().fromJson(req.body(), JoinGameRequest.class);

            gameService.joinGame(gameReq, auth.username());
            res.status(200);
            return new Gson().toJson(res.body());
        }
    }

    private Object deleteAll(Request req, Response res) throws ResException {
        userService.clear();
        gameService.clear();
        return "";
    }

}
