package dataAccess.mysql;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import exception.ResException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserAccess implements UserDAO {

    private final SQLHelper sql = new SQLHelper();
    public MySqlUserAccess() throws SQLException, ResException, DataAccessException {
        String[] createStatements = {
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
        sql.configureDatabase(createStatements);
    }
    @Override
    public UserData insertUser(UserData user) throws ResException {
        var statement = "INSERT into user (username, password, email) VALUES (?, ?, ?)";
        var hashedPass = hashPassword(user.password());
        sql.executeUpdate(statement, user.username(), hashedPass, user.email());
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
        sql.executeUpdate(statement);
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

}
