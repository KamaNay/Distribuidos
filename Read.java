
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Read {
    public static void read(String token, PrintWriter out, BufferedReader in) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        JsonObject requestJson = Request.createRequest("LOOKUP_ACCOUNT_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        JsonObject responseJson = Request.parseJson(jsonResponse);
        if (responseJson.get("status").getAsString().equals("SUCCESS")) {
            JsonObject dataObject = responseJson.getAsJsonObject("data");
            if (dataObject != null) {
                JsonElement passwordElement = dataObject.get("password");
                JsonElement emailElement = dataObject.get("email");
                JsonElement nameElement = dataObject.get("name");
                if (emailElement != null) {
                    System.out.println("Email: " + emailElement.getAsString());
                }
                if (nameElement != null) {
                    System.out.println("Nome: " + nameElement.getAsString());
                }
                if (passwordElement != null) {
                    System.out.println("Senha: " + passwordElement.getAsString());
                }
            }
        } else {
            System.out
                    .println("Erro ao consultar as informações da conta: " + responseJson.get("status").getAsString());
        }
    }
}
