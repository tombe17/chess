package client;

import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResException;
import model.AuthData;
import model.GameData;
import model.UserData;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class EvalClient {
    private final ServerFacade server;
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private GameState gameState = GameState.NOTACTIVE;

    private GameData currGame = null;
    private String currColor = null;

    public HashMap<String, GameData> gamesIndex = new HashMap<>();
    public EvalClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) throws ResException {
        var words = input.toLowerCase().split(" ");
        var cmd = (words.length > 0) ? words[0] : "help";
        var params = Arrays.copyOfRange(words, 1, words.length);
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "move" -> makeMove(params);
            case "show" -> showMoves(params);
            case "redraw" -> redraw();
            case "resign" -> resign();
            case "leave" -> leave();
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String register(String... params) throws ResException {
        if (state.equals(State.SIGNEDOUT)) {
            if (params.length == 3) {
                var user = new UserData(params[0], params[1], params[2]);
                server.registerUser(user);
                state = State.SIGNEDIN;
                return "Welcome " + user.username() + "!";
            }
        }
        return "failed to register";
    }

    public String login(String[] params) throws ResException {
        if (state.equals(State.SIGNEDOUT)) {
            if (params.length == 2) {
                var user = new UserData(params[0], params[1], null);
                server.loginUser(user);
                state = State.SIGNEDIN;
                return "Welcome back " + user.username() + "!";
            }
        }
        return "Failed to login";
    }

    public String create(String[] params) throws ResException {
        assertSignedIn();
        if (params.length == 1) {
            var name = params[0];
            var gameID = server.createGame(name);
            return "Created game: " + name + " with ID: " + gameID;
        }
        return "failed to create";
    }

    public String list() throws ResException {
        assertSignedIn();
        var games = server.listGames();
        StringBuilder printGames = new StringBuilder();
        int i = 1;
        String num;
        gamesIndex.clear();
        for (GameData game : games) {
            num = Integer.toString(i);
            gamesIndex.put(num, game);

            printGames.append(num).append(". ").append(game.gameName()).append(" [").append(game.whiteUsername()).append(", ").
                    append(game.blackUsername()).append("]\n");
            i++;
        }
        return printGames.toString();
    }

    public String join(String[] params) throws ResException {
        assertSignedIn();
        String teamColor = null;
        if (params.length > 2 || params.length == 0) {
            return "failed to join";
        }
        if (gamesIndex.isEmpty()) {
            return "list games before joining";
        }
        if (params.length == 2) {
            teamColor = params[1].toUpperCase();

            if (!teamColor.equals("WHITE") && !teamColor.equals("BLACK")) {
                throw new ResException(400, "Bad request");
            }
        }
        if (params.length == 1) {
            return observe(params);
        }

        var gameToGet = params[0];
        var gameID = gamesIndex.get(gameToGet).gameID();
        server.joinGame(teamColor, gameID);
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        var auth = server.getAuth();
        ws.joinGame(auth.username());

        currGame = gamesIndex.get(gameToGet);
        currColor = teamColor;
        var gamePrinter = new PrintBoard(teamColor, currGame.game());
        gamePrinter.print();
        gameState = GameState.PLAYING;
        return "";
    }

    public String observe(String[] params) throws ResException {
        assertSignedIn();
        if (params.length == 1) {
            if (gamesIndex.isEmpty()) {
                return "list games before joining";
            }
            var gameToGet = params[0];
            var gameID = gamesIndex.get(gameToGet).gameID();
            server.joinGame(null, gameID);

            currGame = gamesIndex.get(gameToGet);
            currColor = "OBSERVER";
            var gamePrinter = new PrintBoard("OBSERVER", currGame.game());
            gamePrinter.print();
            gameState = GameState.OBSERVING;
            return "";
        }
        return "failed to observe";
    }

    public String logout() throws ResException {
        assertSignedIn();
        server.logoutUser();
        state = State.SIGNEDOUT;
        return "logged out. Play again soon!";
    }

    public String makeMove(String[] params) throws ResException {
        assertPlaying();
        if (params.length == 2) {
            String startPos = params[0];
            String endPos = params[1];

            return "made move " + startPos + " to " + endPos;
        }
        return "failed to move";
    }

    public String showMoves(String[] params) throws ResException {
        assertPlaying();
        if (params.length == 1) {
            String pos = params[0];

            return "Showing moves for " + pos;
        }
        return "failed to show moves";
    }

    public String resign() throws ResException {
        assertPlaying();
        gameState = GameState.NOTACTIVE;
        return "in resign";
    }

    public String redraw() throws ResException {
        assertInGame();
        var gamePrinter = new PrintBoard(currColor, currGame.game());
        gamePrinter.print();
        return "in redraw";
    }

    public String leave() throws ResException {
        assertInGame();
        gameState = GameState.NOTACTIVE;
        return "in leave";
    }

    public String commandText() {

        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - quit chess
                    help - list commands
                    """;
        } else if (gameState == GameState.OBSERVING) {
            return """
                    redraw - the chess board
                    leave - the game
                    help - list commands
                    """;
        } else if (gameState == GameState.PLAYING) {
            return """
                    move <START_POS> <END_POS> - a piece
                    show <POS> - the moves for a given position
                    redraw - the chess board
                    leave - the game
                    resign - forfeit the game
                    help - list commands
                    """;
        }
        return """
                create <NAME> - new game
                list - all games
                join <ID> [WHITE|BLACK|<empty>] - a game
                observe <ID> - a game
                logout - when done
                quit - quit chess
                help - list commands
                """;
    }

    public String help() {
        String commands = commandText();
        String[] lines = commands.split("\n");
        for (String line : lines) {
            String[] words = line.trim().split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (i == 0) {
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + words[i] + " ");
                } else if (Objects.equals(words[i], "-")) {
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + words[i] + " ");
                }else {
                    System.out.print(words[i] + " ");
                }
            }
            System.out.println(); // Print a newline after each line
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        return "";
    }

    private void assertSignedIn() throws ResException {
        if (state == State.SIGNEDOUT) {
            throw new ResException(400, "You must sign in");
        }
    }

    private void assertInGame() throws ResException {
        if (gameState == GameState.NOTACTIVE || gameState == GameState.FINISHED) {
            throw new ResException(400, "Game is not active");
        }
    }

    private void assertPlaying() throws ResException {
        if (gameState != GameState.PLAYING) {
            throw new ResException(400, "You must be playing for that action");
        }
    }

    public String getState() {
        if (state.equals(State.SIGNEDIN)) {
            return "LOGGED IN";
        } else {
            return "LOGGED OUT";
        }
    }
}
