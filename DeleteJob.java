import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteJob {
    public static void delete(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de excluir uma habilidade.");
            return;
        }

        SpinnerNumberModel idModel = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner idField = new JSpinner(idModel);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Escolha a habilidade que deseja buscar:"));
        panel.add(idField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Excluir Habilidade", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int id = (int) idField.getValue();

            JsonObject requestJson = Request.createRequest("DELETE_JOB");

            JsonObject data = new JsonObject();
            data.addProperty("id", id);
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