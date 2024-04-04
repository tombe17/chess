package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import exception.ResException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import services.GameService;
import services.UserService;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCom;
import webSocketMessages.userCommands.JoinPlayerCom;
import webSocketMessages.userCommands.MakeMoveCom;
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
    public void onMessage(Session session, String message) throws IOException, ResException, InvalidMoveException {
        //figure out which action it is then based on then perform a function
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, JoinPlayerCom.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, JoinObserverCom.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCom.class), session);
        }
    }

    private void joinPlayer(JoinPlayerCom cmd, Session session) throws IOException, ResException {
        System.out.println("In join Player WS");
        connections.add(cmd.getAuthString(), cmd.getGameID(), session);

        //broadcast notification
        var auth = userService.getAuth(cmd.getAuthString());
        var mes = String.format("%s joined as %s", auth.username(), cmd.getPlayerColor());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
        connections.broadcast(cmd.getAuthString(), cmd.getGameID(), notification, ServerMessage.ServerMessageType.NOTIFICATION);

        //load game for root client
        var notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameService.getGame(cmd.getGameID()), cmd.getPlayerColor());
        session.getRemote().sendString(notifyLoad.toString());
    }

    private void joinObserver(JoinObserverCom cmd, Session session) throws ResException, IOException {
        System.out.println("In join Player WS");
        connections.add(cmd.getAuthString(), cmd.getGameID(), session);

        var notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameService.getGame(cmd.getGameID()), null);
        session.getRemote().sendString(notifyLoad.toString());

        var auth = userService.getAuth(cmd.getAuthString());
        var mes = String.format("%s joined as an observer", auth.username());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
        connections.broadcast(cmd.getAuthString(), cmd.getGameID(), notification, ServerMessage.ServerMessageType.NOTIFICATION);
    }

    private void makeMove(MakeMoveCom cmd, Session session) throws ResException, InvalidMoveException, IOException {
        System.out.println("In MakeMove WS");
        //first validate move if good then update game, then broadcast
        gameService.makeMove(cmd);

        var notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameService.getGame(cmd.getGameID()), null);
        connections.broadcast("", cmd.getGameID(), notifyLoad, ServerMessage.ServerMessageType.LOAD_GAME);

        var auth = userService.getAuth(cmd.getAuthString());
        var mes = String.format("%s moved", cmd.getTeamColor());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
        connections.broadcast(cmd.getAuthString(), cmd.getGameID(), notification, ServerMessage.ServerMessageType.NOTIFICATION);
    }
}
