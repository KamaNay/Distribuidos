import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChooseCandidate {
    public static void read(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        SpinnerNumberModel idModel = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner idField = new JSpinner(idModel);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Escolha o candidato desejado:"));
        panel.add(idField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Buscar Candidato", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = (String) idField.getValue().toString();

            JsonObject requestJson = Request.createRequest("CHOOSE_CANDIDATE");

            JsonObject data = new JsonObject();
            data.addProperty("id_user", id);
            requestJson.addProperty("token", token);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                JsonObject responseJson = Request.parseJson(jsonResponse);
                if (responseJson.get("status") != null && responseJson.get("status").getAsString().equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(frame, "Candidato selecionado");
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