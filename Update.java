import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class Update {
    public static void update(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, logue antes de atualizar a conta.");
            return;
        }
    	   	
        System.out.println("Digite seu novo endereço de email:");
        String email = reader.readLine();

        if (email == null) {
            System.out.println("Inválido");
            return;
        }

        System.out.println("Digite sua nova senha:");
        String password = reader.readLine();

        if (password == null) {
            System.out.println("Inválido");
            return;
        }

        System.out.println("Digite seu novo nome:");
        String name = reader.readLine();

        if (name == null) {
            System.out.println("Inválido");
            return;
        }

        JsonObject requestJson = Request.createRequest("UPDATE_ACCOUNT_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        data.addProperty("name", name);
        data.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
    }
}