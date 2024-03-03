package dataAccess.mysql;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import exception.ResException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserAccess implements UserDAO {

    public MySqlUserAccess() throws SQLException, ResException, DataAccessException {
        configureDatabase();
    }
    @Override
    public UserData insertUser(UserData user) throws ResException {
        var statement = "INSERT into user (username, password, email) VALUES (?, ?, ?)";
        var hashedPass = hashPassword(user.password());
        executeUpdate(statement, user.username(), hashedPass, user.email());
        return new UserData(user.username(), hashedPass, user.email());
    }

    @Override
    public UserData getUser(String username) throws ResException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws ResException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var pass = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, pass, email);
    }

    String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.encode(password);
    }

    public boolean verifyUser(String username, String providedPassword) throws ResException {
        var hashedPassword = getUser(username).password();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(providedPassword, hashedPassword);
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
            CREATE TABLE IF NOT EXISTS user (
             `id` int NOT NULL AUTO_INCREMENT,
             `username` varchar(256) NOT NULL,
             `password` varchar(256) NOT NULL,
             `email` varchar(256) NOT NULL,
             PRIMARY KEY (`id`)
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
