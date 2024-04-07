package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import exception.ResException;
import model.GameState;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import services.GameService;
import services.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;
    private final GameService gameService;
    private final HashMap<Integer, GameState> gameStates = new HashMap<>();

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResException, InvalidMoveException, DataAccessException {
        //figure out which action it is then based on then perform a function
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, JoinPlayerCom.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, JoinObserverCom.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCom.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, ResignCom.class), session);
            case LEAVE -> leave(new Gson().fromJson(message, LeaveCom.class), session);
        }
    }

    private void joinPlayer(JoinPlayerCom cmd, Session session) throws IOException, ResException {
        try {
            var auth = userService.getAuth(cmd.getAuthString());
            var game = gameService.getGame(cmd.getGameID());

            if (game != null) {
                //if the game isn't already there then add to map
                if (!gameStates.containsKey(cmd.getGameID())) {
                    gameStates.put(cmd.getGameID(), GameState.ACTIVE);
                }

                var nameToCheck = cmd.getPlayerColor() == ChessGame.TeamColor.WHITE ? game.whiteUsername() : game.blackUsername();
                if (nameToCheck != null) {
                    if (nameToCheck.equals(auth.username())) {
                        connections.add(auth.username(), cmd.getGameID(), session);

                        var mes = String.format("%s joined as %s", auth.username(), cmd.getPlayerColor());
                        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
                        connections.broadcast(auth.username(), cmd.getGameID(), notification);
                        //load game for root client
                        var notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, cmd.getPlayerColor());
                        session.getRemote().sendString(notifyLoad.toString());
                    } else { //player there
                        var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, String.format("%s is taken.", cmd.getPlayerColor()));
                        session.getRemote().sendString(error.toString());
                    }
                } else { //http request failed
                    var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Empty game.");
                    session.getRemote().sendString(error.toString());
                }
            } else { //bad game ID
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Game ID");
                session.getRemote().sendString(error.toString());
            }
        } catch (ResException e) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(error.toString());
        }
    }

    private void joinObserver(JoinObserverCom cmd, Session session) throws ResException, IOException {
        try {
            var auth = userService.getAuth(cmd.getAuthString());
            var game = gameService.getGame(cmd.getGameID());
            if (game != null) {
                connections.add(auth.username(), cmd.getGameID(), session);

                var notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, null);
                session.getRemote().sendString(notifyLoad.toString());

                var mes = String.format("%s joined as an observer", auth.username());
                var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
                connections.broadcast(auth.username(), cmd.getGameID(), notification);
            } else {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Game ID");
                session.getRemote().sendString(error.toString());
            }
        } catch (ResException e) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(error.toString());
        }
    }

    private void makeMove(MakeMoveCom cmd, Session session) throws ResException, IOException {
        //first validate move if good then update game, then broadcast
        try {
            if (gameStates.get(cmd.getGameID()) != GameState.FINISHED) {
                var auth = userService.getAuth(cmd.getAuthString());
                var gameData = gameService.getGame(cmd.getGameID());
                var teamColor = cmd.getTeamColor();

                if (teamColor == null) {
                    if (auth.username().equals(gameData.whiteUsername())) {
                        teamColor = ChessGame.TeamColor.WHITE;
                    } else if (auth.username().equals(gameData.blackUsername())) {
                        teamColor = ChessGame.TeamColor.BLACK;
                    }
                }

                gameService.makeMove(cmd, teamColor);
                var mes = String.format("%s moved %s", auth.username(), moveToString(cmd.getMove()));
                var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
                connections.broadcast(auth.username(), cmd.getGameID(), notification);

                var notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameService.getGame(cmd.getGameID()), ChessGame.TeamColor.BLACK);
                connections.getConnection(gameData.blackUsername()).session.getRemote().sendString(notifyLoad.toString());
                notifyLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameService.getGame(cmd.getGameID()), null);
                connections.broadcast(gameData.blackUsername(), cmd.getGameID(), notifyLoad);

                ChessGame.TeamColor oppTeam;
                String oppTeamUser;
                if (teamColor == ChessGame.TeamColor.WHITE) {
                    oppTeam = ChessGame.TeamColor.BLACK;
                    oppTeamUser = gameData.blackUsername();
                } else {
                    oppTeam = ChessGame.TeamColor.WHITE;
                    oppTeamUser = gameData.whiteUsername();
                }

                if (gameData.game().isInCheckmate(oppTeam)) {
                    mes = String.format("%s was checkmated!", oppTeamUser);
                    notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
                    connections.broadcast("", cmd.getGameID(), notification);
                    gameStates.replace(cmd.getGameID(), GameState.FINISHED);
                } else if (gameData.game().isInStalemate(oppTeam)) {
                    mes = "Stalemate!";
                    notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
                    connections.broadcast("", cmd.getGameID(), notification);
                    gameStates.replace(cmd.getGameID(), GameState.FINISHED);
                } else if (gameData.game().isInCheck(oppTeam)) {
                    mes = String.format("%s is in check!", oppTeamUser);
                    notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
                    connections.broadcast(auth.username(), cmd.getGameID(), notification);
                }
            } else {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is finished, can't move.");
                session.getRemote().sendString(error.toString());
            }
        } catch (InvalidMoveException e) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(error.toString());
        }
    }

    private void resign(ResignCom cmd, Session session) throws ResException, IOException {
        if (gameStates.get(cmd.getGameID()) != GameState.FINISHED) {
            //broadcast then remove their connection
            var auth = userService.getAuth(cmd.getAuthString());
            var mes = String.format("%s resigned.", auth.username());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
            connections.broadcast("", cmd.getGameID(), notification);
            connections.remove(auth.username());

            gameStates.replace(cmd.getGameID(), GameState.FINISHED);
        } else {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is finished, can't move.");
            session.getRemote().sendString(error.toString());
        }
    }

    private void leave(LeaveCom cmd, Session session) throws ResException, IOException, DataAccessException {
        var auth = userService.getAuth(cmd.getAuthString());
        var game = gameService.getGame(cmd.getGameID());
        String teamColor;
        if (Objects.equals(game.whiteUsername(), auth.username())) {
            teamColor = "WHITE";
        } else {
            teamColor = "BLACK";
        }
        gameService.playerLeave(cmd, teamColor);

        //broadcast then remove their connection
        var mes = String.format("%s left.", auth.username());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, mes);
        connections.broadcast(auth.username(), cmd.getGameID(), notification);
        connections.remove(auth.username());
    }

    public String moveToString(ChessMove move) {
        String moveString = "";

        moveString = addPosString(move.getStartPosition(), moveString);
        moveString = moveString + " to ";
        moveString = addPosString(move.getEndPosition(), moveString);

        return moveString;
    }

    public String addPosString(ChessPosition position, String string) {
        int rowNum = position.getRow();
        var colNum = position.getColumn();

        switch (colNum) {
            case 1 -> string = string + "a";
            case 2 -> string = string + "b";
            case 3 -> string = string + "c";
            case 4 -> string = string + "d";
            case 5 -> string = string + "e";
            case 6 -> string = string + "f";
            case 7 -> string = string + "g";
            case 8 -> string = string + "h";
        }
        string = string + rowNum;
        return string;
    }
}
