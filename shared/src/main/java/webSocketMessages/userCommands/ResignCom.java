package webSocketMessages.userCommands;

public class ResignCom extends UserGameCommand {

    int gameID;
    public ResignCom(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }

    public int getGameID() {return gameID;}
}
