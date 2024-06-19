import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Delete {
    public static void delete(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de excluir a conta.");
            return;
        }

        JTextField emailField = new JTextField(20);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Digite o endereço de email para excluir a conta:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Excluir Conta", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();

            JsonObject requestJson = Request.createRequest("DELETE_ACCOUNT_CANDIDATE");

            JsonObject data = new JsonObject();
            data.addProperty("email", email);
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