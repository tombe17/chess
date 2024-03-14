package client;

import com.google.gson.Gson;
import exception.ResException;
import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        authToken = null;
    }

    public AuthData registerUser(UserData user) throws ResException {
        var path = "/user";
        var auth = this.makeRequest("POST", path, user, AuthData.class);
        authToken = auth.authToken();
        return auth;
    }

    public AuthData loginUser(UserData user) throws ResException {
        var path = "/session";
        var auth = this.makeRequest("POST", path, user, AuthData.class);
        authToken = auth.authToken();
        return auth;
    }

    public void logoutUser() throws ResException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
        System.out.print(authToken);
        authToken = null;
        System.out.print(authToken);
    }

    private <T> T makeRequest(String method, String path, Object req, Class<T> resClass) throws ResException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setRequestProperty("Authorization", authToken);
            if (!method.equals("DELETE")) {
                http.setDoOutput(true);
            }

            writeBody(req, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, resClass);
        } catch (Exception e) {
            throw new ResException(500, e.getMessage());
        }
    }

    private static void writeBody(Object req, HttpURLConnection http) throws IOException {
        if (req != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqInfo = new Gson().toJson(req);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqInfo.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> resClass) throws IOException {
        T res = null;
        if (http.getContentLength() < 0) {
            try (InputStream resBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(resBody);
                if (resClass != null) {
                    res = new Gson().fromJson(reader, resClass);
                }
            }
        }
        return res;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
