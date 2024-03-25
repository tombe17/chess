package client.websocket;

import com.sun.nio.sctp.NotificationHandler;
import exception.ResException;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");


        } catch (URISyntaxException ex) {
            throw new ResException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
