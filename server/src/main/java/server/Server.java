package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryUserAccess;
import dataAccess.UserDAO;
import exception.ResException;
import model.GameData;
import model.UserData;
import services.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    private final UserService userService;
    private final UserDAO userAccess = new MemoryUserAccess();
    public Server() {
        userService = new UserService(userAccess);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::deleteAll);
        Spark.post("/user", this::addUser);
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
    }

    private Object addUser(Request req, Response res) throws ResException, DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        //first check if user is there, if not then add them else throw the exception
        var userCheck = userService.getUser(user);
        if (userCheck != null) {
            throw new DataAccessException("User already exists.");
        }
        user = userService.addUser(user);
        //then create an auth token and return that
        return new Gson().toJson(user);
    }
    private Object loginUser(Request req, Response res) throws ResException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        return "loginUser";
    }
    private Object logoutUser(Request req, Response res) throws ResException {
        return "logoutUser";
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
        return "deleteAll";
    }

}
