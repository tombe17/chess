package webSocketMessages.userCommands;

public class JoinObserverCom extends UserGameCommand {

    int gameID;
    public JoinObserverCom(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

    public Integer getGameID() {return gameID;}
}
