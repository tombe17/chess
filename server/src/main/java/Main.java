import chess.*;
import dataAccess.mysql.MySqlUserAccess;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {
            int port;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            } else {
                port = 8080;
            }

            var server = new Server().run(port);
            System.out.printf("Listening on port %d%n", port);
        } catch (Throwable ex) {
            System.out.printf("Can't start server: %s%n", ex.getMessage());
        }

    }
}