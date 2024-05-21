import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class SignupC {
    public static void signup(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        System.out.println("Digite o nome:");
        String name = reader.readLine();

        System.out.println("Digite o nome da empresa:");
        String industry = reader.readLine();

        System.out.println("Digite uma descrição da empresa:");
        String description = reader.readLine();

        int id = 0;

        JsonObject requestJson = Request.createRequest("SIGNUP_RECRUITER");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        data.addProperty("name", name);
        data.addProperty("industry", industry);
        data.addProperty("description", description);
        data.addProperty("id", id);
        requestJson.add("data", data);

        String jsonResponse = Request.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
    }
}