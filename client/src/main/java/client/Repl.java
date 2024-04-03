package client;
import client.websocket.NotificationHandler;
import ui.EscapeSequences;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler {

    private final EvalClient client;
    public Repl(String serverUrl) {
        client = new EvalClient(serverUrl, this);
    }

    public void run() {
        System.out.println(EscapeSequences.EMPTY + EscapeSequences.BLACK_QUEEN + "Welcome to chess! Type \"help\" to get started." + EscapeSequences.BLACK_QUEEN + EscapeSequences.EMPTY);
        client.help();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }

    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_WHITE + "[" + client.getState() + "] " + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(Notification notification) {
        System.out.println("Printing notification");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + notification.getMessage());
        printPrompt();
    }
    @Override
    public void loadGame(LoadGameMessage message) {
        //get game and then print it
        System.out.println("Printing game");
        new PrintBoard(message.getTeamColor(), message.getGame().game()).print();

        printPrompt();
    }
    @Override
    public void error(ErrorMessage error) {
        System.out.println("Sending error");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + error.getMessage());
        printPrompt();
    }
}
