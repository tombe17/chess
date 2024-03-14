package client;
import ui.EscapeSequences;
import java.util.Scanner;

public class Repl {

    private final EvalClient client;
    public Repl(String serverUrl) {
        client = new EvalClient(serverUrl);
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
}
