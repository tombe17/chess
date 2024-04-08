package dataAccess.mysql;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import exception.ResException;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthAccess implements AuthDAO {
    private final SQLHelper sql = new SQLHelper();
    public MySqlAuthAccess() throws ResException {
        try {
            String[] createStatements = {
                    """
            CREATE TABLE IF NOT EXISTS auth (
             `authToken` varchar(256) NOT NULL,
             `username` varchar(256) NOT NULL,
             PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            };
            sql.configureDatabase(createStatements);
        } catch (SQLException | DataAccessException e) {
            throw new ResException(500, e.getMessage());
        }
    }
    @Override
    public AuthData insertAuth(String username) throws DataAccessException, ResException {
        var statement = "INSERT into auth (authToken, username) VALUES (?, ?)";
        String newToken = UUID.randomUUID().toString();

        sql.executeUpdate(statement, newToken, username);
        return new AuthData(newToken, username);
    }

    @Override
    public AuthData getAuth(String authToken) throws ResException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws ResException {
        var statement = "DELETE FROM auth WHERE authToken =?";
        sql.executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws ResException {
        var statement = "TRUNCATE auth";
        sql.executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }
}
