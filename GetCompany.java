import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GetCompany {
    public static void read(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        JsonObject requestJson = Request.createRequest("GET_COMPANY");
        requestJson.addProperty("token", token);

        try {
            String jsonResponse = Request.sendRequest(requestJson, out, in);
            JsonObject responseJson = Request.parseJson(jsonResponse);
            if (responseJson.get("status").getAsString().equals("SUCCESS")) {
                JsonObject dataObject = responseJson.getAsJsonObject("data");
                JsonArray companiesArray = dataObject.getAsJsonArray("company");
                StringBuilder accountInfo = new StringBuilder("Empresas interessadas:\n");
                JsonElement company_size = dataObject.get("company_size");
                if (company_size != null) {
                    accountInfo.append("Número de empresas: ").append(company_size.getAsInt()).append("\n");
                }
                for (JsonElement companyElement : companiesArray) {
                    JsonObject companyObject = companyElement.getAsJsonObject();
                    String name = companyObject.get("name").getAsString();
                    String industry = companyObject.get("industry").getAsString();
                    String email = companyObject.get("email").getAsString();
                    String description = companyObject.get("description").getAsString();
                    accountInfo.append("Nome:").append(name).append(", Industria:").append(industry).append(", email:").append(email)
                        .append(", Descrição:").append(description).append("\n");
                }

                JOptionPane.showMessageDialog(frame, accountInfo.toString());
            }else {
                JOptionPane.showMessageDialog(frame,
                        "Erro ao consultar as informações da conta: " + responseJson.get("status").getAsString());
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
        }
    }
}