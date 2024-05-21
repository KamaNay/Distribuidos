import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class UpdateC {
    public static void update(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de atualizar a conta.");
            return;
        }

        System.out.println("Digite o novo endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a nova senha:");
        String password = reader.readLine();

        System.out.println("Digite o novo nome:");
        String name = reader.readLine();

        System.out.println("Digite o novo nome da empresa:");
        String industry = reader.readLine();

        System.out.println("Digite a nova descrição:");
        String description = reader.readLine();

        int id = 0;

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("operation", "UPDATE_ACCOUNT_RECRUITER");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        data.addProperty("name", name);
        data.addProperty("industry", industry);
        data.addProperty("description", description);
        data.addProperty("id", id);
        requestJson.add("data", data);

        out.println(requestJson.toString());
        String jsonResponse = in.readLine();
        System.out.println(jsonResponse);
    }
}