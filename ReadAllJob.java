import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ReadAllJob {
    public static void read(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        JsonObject requestJson = Request.createRequest("LOOKUP_JOBSET");
        requestJson.addProperty("token", token);

        try {
            String jsonResponse = Request.sendRequest(requestJson, out, in);
            JsonObject responseJson = Request.parseJson(jsonResponse);
            if (responseJson.get("status").getAsString().equals("SUCCESS")) {
                JsonObject dataObject = responseJson.getAsJsonObject("data");
                JsonArray jobsArray = dataObject.getAsJsonArray("skills");
                StringBuilder accountInfo = new StringBuilder("Informações das habilidades:\n");

                JsonElement jobset_sizeElement = dataObject.get("jobset_size");
                if (jobset_sizeElement != null) {
                    accountInfo.append("Número de vagas: ").append(jobset_sizeElement.getAsInt()).append("\n");
                }

                for (JsonElement jobElement : jobsArray) {
                    JsonObject jobObject = jobElement.getAsJsonObject();
                    String skill = jobObject.get("skill").getAsString();
                    int experience = jobObject.get("experience").getAsInt();
                    accountInfo.append("Habilidade: ").append(skill).append(", Experiência: ").append(experience)
                            .append("\n");
                }

                JOptionPane.showMessageDialog(frame, accountInfo.toString());
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Erro ao consultar as informações da conta: " + responseJson.get("status").getAsString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
        }
    }
}