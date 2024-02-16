package server;

import com.google.gson.Gson;
import exception.ResException;
import model.GameData;
import model.UserData;
import services.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    private final UserService userService;
    public Server() {
        userService = new UserService();
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

    private Object addUser(Request req, Response res) throws ResException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        return "addUser";
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
        return "deleteAll";
    }

}
