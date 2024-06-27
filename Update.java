import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Update {
    public static void update(JFrame frame, PrintWriter out, BufferedReader in, String token) {

        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de atualizar as informações da conta.");
            return;
        }
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField nameField = new JTextField(20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Digite o novo endereço de email:"));
        panel.add(emailField);
        panel.add(new JLabel("Digite a nova senha:"));
        panel.add(passwordField);
        panel.add(new JLabel("Digite o novo nome:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Atualização de Conta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            int id = 0;

            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("operation", "UPDATE_ACCOUNT_CANDIDATE");
            requestJson.addProperty("token", token);
            JsonObject data = new JsonObject();
            data.addProperty("email", email);
            data.addProperty("password", password);
            data.addProperty("name", name);
            data.addProperty("id", id);
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