package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCom extends UserGameCommand {

    int gameID;
    ChessGame.TeamColor playerColor;
    public JoinPlayerCom(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }


}
