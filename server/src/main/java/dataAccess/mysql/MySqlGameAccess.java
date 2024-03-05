package dataAccess.mysql;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.GameDAO;
import exception.ResException;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlGameAccess implements GameDAO {

    public MySqlGameAccess() throws SQLException, ResException, DataAccessException {
        configureDatabase();
    }
    @Override
    public GameData insertGame(String gameName) throws ResException {
        var statement = "INSERT into game (gameName, game) VALUES (?, ?)";
        var board = new ChessBoard();
        board.resetBoard();
        var chessGame = new ChessGame();
        chessGame.setBoard(board);
        var gameJson = new Gson().toJson(chessGame);

        var id = executeUpdate(statement, gameName, gameJson);
        var game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(id, null, null, gameName, game);
    }

    @Override
    public GameData getGame(int gameID) throws ResException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void updateGame(String playerColor, String username, int gameID) throws DataAccessException, ResException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement;
            if (Objects.equals(playerColor, "BLACK")) {
                statement = "UPDATE game SET blackUsername = ? WHERE gameID =?";
            } else if (Objects.equals(playerColor, "WHITE")) {
                statement = "UPDATE game SET whiteUsername = ? WHERE gameID =?";
            } else {
                return;
            }
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ResException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws ResException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void clear() throws ResException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameString = rs.getString("game");
        var game = new Gson().fromJson(gameString, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private int executeUpdate(String statement, Object... params) throws ResException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
             `gameID` int NOT NULL AUTO_INCREMENT,
             `whiteUsername` varchar(256),
             `blackUsername` varchar(256),
             `gameName` varchar(256) NOT NULL,
             `game` TEXT DEFAULT NULL,
             PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws ResException, DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
