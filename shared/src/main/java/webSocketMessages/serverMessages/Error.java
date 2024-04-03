package webSocketMessages.serverMessages;

public class Error extends ServerMessage {

    String errorMessage;
    public Error(ServerMessageType type, String message) {
        super(type);
        errorMessage = message;
    }

    public String getMessage() {
        return errorMessage;
    }
}
