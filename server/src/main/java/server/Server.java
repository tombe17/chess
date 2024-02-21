package server;

import com.google.gson.Gson;
import dataAccess.*;
import exception.ResException;
import model.AuthData;
import model.GameData;
import model.UserData;
import services.GameService;
import services.Message;
import services.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

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
        Spark.post("/game", this::addGame);
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
        AuthData authData = null;
        String json;

        //bad request

        authData = userService.addUser(user);
        //set up the response obj
        res.status(200);
        res.type("application/json");
        json = new Gson().toJson(authData);
        res.body(json);

        return res.body();
    }
    private Object loginUser(Request req, Response res) throws DataAccessException {
//        var user = new Gson().fromJson(req.body(), UserData.class);
//        var memoryUser = userService.getUser(user);
//        if (!Objects.equals(user.password(), memoryUser.password())) { //check password
//           throw new DataAccessException("unauthorized");
//        }
//        var auth = userService.makeAuth(user.username());
//        return new Gson().toJson(auth);
        return null;
    }
    private Object logoutUser(Request req, Response res) throws DataAccessException {
        String authToken = req.headers().toString();
        //String authToken = new Gson().fromJson(req.headers(), );
        //userService.deleteAuth(auth);
        return "";
    }

    private Object listGames(Request req, Response res) throws ResException {
        var game = new Gson().fromJson(req.body(), GameData.class);
        return "listGames";
    }
    private Object addGame(Request req, Response res) throws ResException {
        var game = new Gson().fromJson(req.body(), GameData.class);
        return "addGame";
    }
    private Object joinGame(Request req, Response res) throws ResException {
        //var game = new Gson().fromJson(req.body(), GameData.class);
        return "joinGame";
    }

    private Object deleteAll(Request req, Response res) throws ResException {
        userService.clear();
        gameService.clear();
        return "";
    }

}
