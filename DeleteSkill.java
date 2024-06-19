import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteSkill {
    public static void delete(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de excluir uma habilidade.");
            return;
        }

        String[] skills = {"NodeJs", "JavaScript", "Java", "C", "HTML", "CSS", "React", "React Native",
                "TypeScript", "Ruby"};

        JComboBox<String> skillField = new JComboBox<>(skills);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Escolha a habilidade que deseja excluir:"));
        panel.add(skillField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Excluir Habilidade", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String skill = (String) skillField.getSelectedItem();

            JsonObject requestJson = Request.createRequest("DELETE_SKILL");

            JsonObject data = new JsonObject();
            data.addProperty("skill", skill);
            requestJson.addProperty("token", token);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                JOptionPane.showMessageDialog(frame, jsonResponse);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
            }
        }
    }
}