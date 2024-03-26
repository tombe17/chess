package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        //figure out which action it is then based on then perform a function
        System.out.print(message);
//        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
//        switch (cmd.getCommandType()) {
//            case JOIN_PLAYER -> joinPlayer(cmd.getAuthString(), session);
//        }
    }

    private void joinPlayer(String authToken, Session session) throws IOException {
        System.out.print("name joined the game");
    }
}
