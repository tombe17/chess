package server.websocket;

import com.google.gson.Gson;
import exception.ResException;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import services.GameService;
import services.UserService;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCom;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    UserService userService;
    GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResException {
        //figure out which action it is then based on then perform a function
        System.out.println(message);
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, JoinPlayerCom.class), session);
        }
    }

    private void joinPlayer(JoinPlayerCom cmd, Session session) throws IOException, ResException {
        System.out.println("In join Player WS");
        connections.add(cmd.getAuthString(), cmd.getGameID(), session);

        //broadcast notification
        var mes = String.format("%s joined as %s", cmd.getAuthString(), cmd.getPlayerColor());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
        connections.broadcast(cmd.getAuthString(), cmd.getGameID(), notification);

        //load game for root client
        var notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameService.getGame(cmd.getGameID()));
        session.getRemote().sendString(notifyLoad.toString());
    }
}
