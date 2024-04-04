package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCom extends UserGameCommand {
    int gameID;
    ChessGame.TeamColor teamColor;
    ChessMove move;
    public MakeMoveCom(String authToken, int gameID, ChessGame.TeamColor teamColor, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.move = move;
    }

    public ChessMove getMove() {return move;}

    public int getGameID() {return gameID;}
    public ChessGame.TeamColor getTeamColor() {return teamColor;}
}
