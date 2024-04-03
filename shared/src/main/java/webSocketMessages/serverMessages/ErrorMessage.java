package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage {

    String errorMessage;
    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        errorMessage = message;
    }

    public String getMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
