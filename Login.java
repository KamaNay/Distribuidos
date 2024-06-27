import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Login {
    public static String login(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Digite o endereço de email:"));
        panel.add(emailField);
        panel.add(new JLabel("Digite a senha:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            JsonObject requestJson = Request.createRequest("LOGIN_CANDIDATE");
            JsonObject data = new JsonObject();
            data.addProperty("email", email);
            data.addProperty("password", password);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                JsonObject responseJson = Request.parseJson(jsonResponse);
                if (responseJson.get("status").getAsString().equals("SUCCESS")) {
                    JsonObject dataObject = responseJson.getAsJsonObject("data");
                    if (dataObject != null) {
                        JsonElement tokenElement = dataObject.get("token");
                        if (tokenElement != null) {
                            token = tokenElement.getAsString();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Token não encontrado na resposta");
                        }
                        GetCompany.read(frame, out, in, token);
                    }
                }

                JOptionPane.showMessageDialog(frame, jsonResponse);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
            }
        }
        return token;
    }
}