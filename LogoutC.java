import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class LogoutC {
    public static String logout(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de sair.");
            return token;
        }

        JsonObject requestJson = Request.createRequest("LOGOUT_RECRUITER");
        requestJson.addProperty("token", token);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);

        // Limpa o token no lado da empresa
        token = null;
        return token;
    }
}