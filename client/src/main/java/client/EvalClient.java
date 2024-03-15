package client;

import exception.ResException;
import model.AuthData;
import model.UserData;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Objects;

public class EvalClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    public EvalClient(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
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
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String register(String... params) throws ResException {
        if (state.equals(State.SIGNEDOUT)) {
            if (params.length == 3) {
                var user = new UserData(params[0], params[1], params[2]);
                AuthData auth = server.registerUser(user);
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
                AuthData auth = server.loginUser(user);
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
        return "In list";
    }

    public String join(String[] params) throws ResException {
        assertSignedIn();
        return "In join";
    }

    public String observe(String[] params) throws ResException {
        assertSignedIn();
        return "In observe";
    }

    public String logout() throws ResException {
        assertSignedIn();
        server.logoutUser();
        state = State.SIGNEDOUT;
        return "logged out. Play again soon!";
    }

    public String commandText() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - quit chess
                    help - list commands
                    """;
        } return """
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

    public String getState() {
        if (state.equals(State.SIGNEDIN)) {
            return "LOGGED IN";
        } else {
            return "LOGGED OUT";
        }
    }
}
