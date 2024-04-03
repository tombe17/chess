package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends ServerMessage {

    GameData game;
    ChessGame.TeamColor teamColor;
    public LoadGameMessage(ServerMessageType type, GameData game, ChessGame.TeamColor teamColor) {
        super(type);
        this.game = game;
        this.teamColor = teamColor;
    }

    public GameData getGame() {return game;}

    public ChessGame.TeamColor getTeamColor() {return teamColor;}

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
