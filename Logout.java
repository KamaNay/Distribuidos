import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class Logout {
    
    public static void logout(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
        JsonObject requestJson = Request.createRequest("LOGOUT_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
        JsonObject response = Request.parseJson(jsonResponse);

        String status = response.get("status").getAsString();

        switch (status) {
            case "SUCCESS":
                System.out.println("Logout realizado com sucesso.");
                break;
            case "INVALID_TOKEN":
                System.out.println("Token inválido. Logout falhou.");
                break;
            default:
                System.out.println("Erro durante o logout.");
        }
    }
}
