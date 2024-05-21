import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LoginC {
    public static String login(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        JsonObject requestJson = Request.createRequest("LOGIN_RECRUITER");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        requestJson.add("data", data);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        JsonObject responseJson = Request.parseJson(jsonResponse);
        if (responseJson.get("status").getAsString().equals("SUCCESS")) {
            JsonObject dataObject = responseJson.getAsJsonObject("data");
            if (dataObject != null) {
                JsonElement tokenElement = dataObject.get("token");
                if (tokenElement != null) {
                    token = tokenElement.getAsString();
                } else {
                    System.out.println("Token não encontrado na resposta");
                }
            }
        }
        System.out.println(jsonResponse);
        return token;
    }
}
