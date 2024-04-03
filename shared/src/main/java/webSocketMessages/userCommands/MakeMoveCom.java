package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCom extends UserGameCommand {
    int gameID;
    ChessMove move;
    public MakeMoveCom(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }

    public ChessMove getMove() {return move;}

    public int getGameID() {return gameID;}
}
