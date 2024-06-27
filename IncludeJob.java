import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class IncludeJob {
    public static void read(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de consultar as informações da conta.");
            
        }

        String[] skills = {"NodeJs", "JavaScript", "Java", "C", "HTML", "CSS", "React", "React Native",
                "TypeScript", "Ruby"};
        String[] yn = { "YES", "NO" };

        JComboBox<String> skillField = new JComboBox<>(skills);
        SpinnerNumberModel experienceModel = new SpinnerNumberModel(0, 0, 100, 1); // Valor inicial, mínimo, máximo, incremento
        JSpinner experienceField = new JSpinner(experienceModel);
        JComboBox<String> availableField = new JComboBox<>(yn);
        JComboBox<String> searchableField = new JComboBox<>(yn);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Selecione a habilidade:"));
        panel.add(skillField);
        panel.add(new JLabel("Diga o nível de experiencia desejado:"));
        panel.add(experienceField);
        panel.add(new JLabel("Selecione a disponibilidade:"));
        panel.add(availableField);
        panel.add(new JLabel("Selecione a disponibilidade da busca:"));
        panel.add(searchableField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Incluir habilidade", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String skill = (String) skillField.getSelectedItem();
            String experience = (String) experienceField.getValue().toString(); // Obtém o valor do JSpinner como inteiro
            String available = (String) availableField.getSelectedItem();
            String searchable = (String) searchableField.getSelectedItem();

            JsonObject requestJson = Request.createRequest("INCLUDE_JOB");
            JsonObject data = new JsonObject();
            data.addProperty("skill", skill);
            data.addProperty("experience", experience);
            data.addProperty("available", available);
            data.addProperty("searchable", searchable);
            requestJson.add("data", data);
            requestJson.addProperty("token", token);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                JOptionPane.showMessageDialog(frame, jsonResponse);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}