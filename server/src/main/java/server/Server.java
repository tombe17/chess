package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthAccess;
import dataAccess.MemoryUserAccess;
import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import exception.ResException;
import model.AuthData;
import model.GameData;
import model.UserData;
import services.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.lang.reflect.Type;
import java.util.Objects;

public class Server {

    private final UserService userService;
    private final UserDAO userAccess = new MemoryUserAccess();
    private final AuthDAO authAccess = new MemoryAuthAccess();
    public Server() {
        userService = new UserService(userAccess, authAccess);
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
            throw new DataAccessException("already taken");
        }
        user = userService.addUser(user);
        //then create an auth token and return that
        var auth = userService.makeAuth(user.username());
        return new Gson().toJson(auth);
    }
    private Object loginUser(Request req, Response res) throws ResException, DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        var memoryUser = userService.getUser(user);
        if (!Objects.equals(user.password(), memoryUser.password())) { //check password
           throw new DataAccessException("unauthorized");
        }
        var auth = userService.makeAuth(user.username());
        return new Gson().toJson(auth);
    }
    private Object logoutUser(Request req, Response res) throws ResException, DataAccessException {
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
        return "";
    }

}
