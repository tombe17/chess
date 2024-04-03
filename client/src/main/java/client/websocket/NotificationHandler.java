package client.websocket;

import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ErrorMessage;

public interface NotificationHandler {
    void notify(Notification notification);
    void loadGame(LoadGameMessage message);
    void error(ErrorMessage error);
}
