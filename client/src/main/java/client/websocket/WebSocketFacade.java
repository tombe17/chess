package client.websocket;

import com.google.gson.Gson;
import exception.ResException;
import webSocketMessages.serverMessages.ServerMessage;

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
                    notificationHandler.notify(message);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.print("ws opened");
    }

    public void joinGame(String username) throws ResException {
        try {
            this.session.getBasicRemote().sendText(username + " joined");
        } catch (IOException ex) {
            throw new ResException(500, ex.getMessage());
        }
    }
}
