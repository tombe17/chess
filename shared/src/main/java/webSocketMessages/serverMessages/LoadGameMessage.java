package webSocketMessages.serverMessages;

import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends ServerMessage {

    GameData game;
    public LoadGameMessage(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
