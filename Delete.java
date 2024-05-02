import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class Delete {
	
    public static void delete(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, logue antes de excluir a conta.");
            return;
        }
    	    	
        System.out.println("Digite o email da conta que deseja exluir:");
        String email = reader.readLine();

        JsonObject requestJson = Request.createRequest("DELETE_ACCOUNT_CANDIDATE");

        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
    }
}