import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Read {
    public static void read(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        JsonObject requestJson = Request.createRequest("LOOKUP_ACCOUNT_CANDIDATE");
        requestJson.addProperty("token", token);

        try {
            String jsonResponse = Request.sendRequest(requestJson, out, in);
            JsonObject responseJson = Request.parseJson(jsonResponse);
            if (responseJson.get("status").getAsString().equals("SUCCESS")) {
                JsonObject dataObject = responseJson.getAsJsonObject("data");
                if (dataObject != null) {
                    StringBuilder accountInfo = new StringBuilder("Informações da Conta:\n");
                    JsonElement emailElement = dataObject.get("email");
                    JsonElement nameElement = dataObject.get("name");
                    JsonElement passwordElement = dataObject.get("password");
                    JsonElement idElement = dataObject.get("id");

                    if (emailElement != null) {
                        accountInfo.append("Email: ").append(emailElement.getAsString()).append("\n");
                    }
                    if (nameElement != null) {
                        accountInfo.append("Nome: ").append(nameElement.getAsString()).append("\n");
                    }
                    if (passwordElement != null) {
                        accountInfo.append("Senha: ").append(passwordElement.getAsString()).append("\n");
                    }
                    if (idElement != null) {
                        accountInfo.append("Id: ").append(idElement.getAsInt()).append("\n");
                    }

                    JOptionPane.showMessageDialog(frame, accountInfo.toString());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Erro ao consultar as informações da conta: " + responseJson.get("status").getAsString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
        }
    }
}