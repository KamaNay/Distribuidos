import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class UpdateSkill {
    public static void update(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de atualizar as informações da conta.");
            return;
        }

        String[] skills = { "NodeJs", "JavaScript", "Java", "C", "HTML", "CSS", "React", "React", "Native",
                "TypeScript", "Ruby" };

        JComboBox<String> skillField = new JComboBox<>(skills);
        JComboBox<String> newSkillField = new JComboBox<>(skills);
        SpinnerNumberModel experienceModel = new SpinnerNumberModel(0, 0, 100, 1); // Valor inicial, mínimo, máximo, incremento
        JSpinner experienceField = new JSpinner(experienceModel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Selecione a habilidade que deseja mudar:"));
        panel.add(skillField);
        panel.add(new JLabel("Selecione a nova habilidade:"));
        panel.add(newSkillField);
        panel.add(new JLabel("Insira o novo nível de experiencia:"));
        panel.add(experienceField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Atualização de Habilidade", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String skill = (String) skillField.getSelectedItem();
            String newSkill = (String) newSkillField.getSelectedItem();
            String experience = (String) experienceField.getValue().toString();

            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("operation", "UPDATE_SKILL");
            requestJson.addProperty("token", token);
            JsonObject data = new JsonObject();
            data.addProperty("skill", skill);
            data.addProperty("newSkill", newSkill);
            data.addProperty("experience", experience);
            requestJson.add("data", data);

            out.println(requestJson.toString());
            try {
                String jsonResponse = in.readLine();
                JOptionPane.showMessageDialog(frame, jsonResponse);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}