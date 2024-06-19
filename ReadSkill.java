import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ReadSkill {
    public static void read(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        String[] skills = { "NodeJs", "JavaScript", "Java", "C", "HTML", "CSS", "React", "React Native",
                "TypeScript", "Ruby" };

        JComboBox<String> skillField = new JComboBox<>(skills);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Escolha a habilidade que deseja buscar:"));
        panel.add(skillField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Buscar Habilidade", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String skill = (String) skillField.getSelectedItem();

            JsonObject requestJson = Request.createRequest("LOOKUP_SKILL");

            JsonObject data = new JsonObject();
            data.addProperty("skill", skill);
            requestJson.addProperty("token", token);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                System.out.println(jsonResponse);
                JsonObject responseJson = Request.parseJson(jsonResponse);
                if (responseJson.get("status") != null && responseJson.get("status").getAsString().equals("SUCCESS")) {
                    JsonObject dataObject = responseJson.getAsJsonObject("data");
                    if (dataObject != null) {
                        StringBuilder accountInfo = new StringBuilder("Informações da habilidade:\n");
                        JsonElement skillElement = dataObject.get("skill");
                        JsonElement expElement = dataObject.get("experience");
                        JsonElement idElement = dataObject.get("id");

                        if (skillElement != null) {
                            accountInfo.append("Habilidade: ").append(skillElement.getAsString()).append("\n");
                        }
                        if (expElement != null) {
                            accountInfo.append("Experiencia: ").append(expElement.getAsInt()).append("\n");
                        }
                        if (idElement != null) {
                            accountInfo.append("Id: ").append(idElement.getAsInt()).append("\n");
                        }

                        JOptionPane.showMessageDialog(frame, accountInfo.toString());
                    }
                } else {
                    String statusMessage = (responseJson.get("status") != null)
                            ? responseJson.get("status").getAsString()
                            : "desconhecido";
                    JOptionPane.showMessageDialog(frame, "Erro ao consultar as informações da conta: " + statusMessage);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}