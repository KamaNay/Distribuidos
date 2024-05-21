import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ReadC {
        public static void read(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        JsonObject requestJson = Request.createRequest("LOOKUP_ACCOUNT_RECRUITER");
        requestJson.addProperty("token", token);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        JsonObject responseJson = Request.parseJson(jsonResponse);
        if (responseJson.get("status").getAsString().equals("SUCCESS")) {
            JsonObject dataObject = responseJson.getAsJsonObject("data");
            if (dataObject != null) {
            	JsonElement passwordElement = dataObject.get("password");
                JsonElement emailElement = dataObject.get("email");
                JsonElement nameElement = dataObject.get("name");
                JsonElement industryElement = dataObject.get("indsutry");
                JsonElement descriptionElement = dataObject.get("description");
                JsonElement idElement = dataObject.get("id");
                if (emailElement != null) {
                    System.out.println("Email: " + emailElement.getAsString());
                }
                if (nameElement != null) {
                    System.out.println("Nome: " + nameElement.getAsString());
                }
                if (passwordElement != null) {
                	System.out.println("Senha: " + passwordElement.getAsString());
                }
                if (industryElement != null) {
                	System.out.println("Empresa: " + industryElement.getAsString());
                }
                if (descriptionElement != null) {
                	System.out.println("Descrição: " + descriptionElement.getAsString());
                }
                if (idElement != null) {
                	System.out.println("Id: " +  idElement.getAsInt());
                }
            }
        } else {
            System.out.println("Erro ao consultar as informações da conta: " + responseJson.get("status").getAsString());
        }
    }
}