import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class Login {
	
	public static String login(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
    	
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        JsonObject requestJson = Request.createRequest("LOGIN_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        requestJson.add("data", data);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
        JsonObject response = Request.parseJson(jsonResponse);
        
        String status = response.get("status").getAsString();

        switch (status) {
            case "SUCCESS":
                return response.getAsJsonObject("data").get("token").getAsString();
            case "INVALID_PASSWORD":
                System.out.println("Senha inválida.");
                break;
            case "USER_NOT_FOUND":
                System.out.println("Usuário não encontrado.");
                break;
            default:
                System.out.println("Erro no login.");
        }
        return null;
    }   
}
