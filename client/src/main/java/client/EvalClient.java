package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
    private ChessGame.TeamColor currColor = null;

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
        currGame = gamesIndex.get(gameToGet);
        if (teamColor.equals("WHITE")) {
            currColor = ChessGame.TeamColor.WHITE;
        } else {
            currColor = ChessGame.TeamColor.BLACK;
        }


        ws = new WebSocketFacade(serverUrl, notificationHandler);
        var auth = server.getAuth();
        ws.joinGame(auth.authToken(), currGame.gameID(), currColor);

        // var gamePrinter = new PrintBoard(currColor, currGame.game());
        //gamePrinter.print();
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
            currColor = null;

            ws = new WebSocketFacade(serverUrl, notificationHandler);
            var auth = server.getAuth();
            ws.observeGame(auth.authToken(), currGame.gameID());
            //var gamePrinter = new PrintBoard(currColor, currGame.game());
            //gamePrinter.print();
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
        if (params.length >= 2) {
            String startPosString = params[0];
            String endPosString = params[1];

            if (startPosString.length() == 2 && endPosString.length() == 2) {
                //turn into an actual position
                var startPos = makePosition(startPosString);
                var endPos = makePosition(endPosString);
                //add in end promotion if pawn
                ChessPiece.PieceType pieceType = null;
                if (params.length == 3) {
                    String promoType = params[2];
                    switch (promoType) {
                        case "queen" -> pieceType = ChessPiece.PieceType.QUEEN;
                        case "rook" -> pieceType = ChessPiece.PieceType.ROOK;
                        case "knight" -> pieceType = ChessPiece.PieceType.KNIGHT;
                        case "bishop" -> pieceType = ChessPiece.PieceType.BISHOP;
                    }
                }

                if (startPos != null && endPos != null) {
                    //send to server and update game
                    var auth = server.getAuth();
                    ws.makeMove(auth.authToken(), new ChessMove(startPos, endPos, pieceType), currColor, currGame.gameID());
                    return "";
                }
            }
        }
        return "invalid move. Format move as: e2 e4";
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

        var auth = server.getAuth();
        ws.resign(auth.authToken(), currGame.gameID());

        gameState = GameState.FINISHED;
        return "";
    }

    public String redraw() throws ResException {
        assertInGame();
        //refresh game

        var gamePrinter = new PrintBoard(currColor, currGame.game());
        gamePrinter.print();
        return "";
    }

    public String leave() throws ResException {
        assertInGame();

        var auth = server.getAuth();
        ws.leave(auth.authToken(), currGame.gameID());

        gameState = GameState.NOTACTIVE;
        return "";
    }

    public ChessPosition makePosition(String pos) {
        char col = pos.charAt(0);
        char row = pos.charAt(1);
        int colNum;
        switch (col) {
            case 'a' -> colNum = 1;
            case 'b' -> colNum = 2;
            case 'c' -> colNum = 3;
            case 'd' -> colNum = 4;
            case 'e' -> colNum = 5;
            case 'f' -> colNum = 6;
            case 'g' -> colNum = 7;
            case 'h' -> colNum = 8;
            default -> {
                return null;
            }
        }
        int rowNum = row - '0';
        if (rowNum > 8 || rowNum < 1) {
            return null;
        }
        System.out.println("Row: " + rowNum + " Col: " + colNum);
        return new ChessPosition(rowNum, colNum);
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
