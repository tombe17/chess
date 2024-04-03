package webSocketMessages.userCommands;

public class LeaveCom extends UserGameCommand {

    int gameID;
    public LeaveCom(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }

    public int getGameID() {return gameID;}
}
