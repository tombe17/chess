package server.websocket;



import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String userName;
    public Integer gameID;
    public Session session;

    public Connection(String userName, Integer gameID, Session session) {
        this.userName = userName;
        this.gameID = gameID;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
