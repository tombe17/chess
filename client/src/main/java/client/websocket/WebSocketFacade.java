package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResException;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCom;
import webSocketMessages.userCommands.JoinPlayerCom;
import webSocketMessages.userCommands.MakeMoveCom;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
                    switch (message.getServerMessageType()) {
                        case LOAD_GAME -> notificationHandler.loadGame(new Gson().fromJson(s, LoadGameMessage.class));
                        case NOTIFICATION -> notificationHandler.notify(new Gson().fromJson(s, Notification.class));
                        case ERROR -> notificationHandler.error(new Gson().fromJson(s, ErrorMessage.class));
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("ws opened");
    }

    public void joinGame(String authToken, Integer gameID, ChessGame.TeamColor playerColor) throws ResException {
        try {
            var joinCom = new JoinPlayerCom(authToken, gameID, playerColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(joinCom));
        } catch (IOException ex) {
            throw new ResException(500, ex.getMessage());
        }
    }

    public void observeGame(String authToken, Integer gameID) throws ResException {
        try {
            var observeCom = new JoinObserverCom(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(observeCom));
        } catch (IOException e) {
            throw new ResException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, ChessMove chessMove, ChessGame.TeamColor currColor, int gameID) throws ResException {
        try {
            var moveCom = new MakeMoveCom(authToken, gameID, currColor, chessMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCom));
        } catch (IOException e) {
            throw new ResException(500, e.getMessage());
        }
    }
}
