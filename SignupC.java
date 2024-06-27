import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SignupC {
    public static void signup(JFrame frame, PrintWriter out, BufferedReader in) {
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField nameField = new JTextField(20);
        JTextField industryField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Digite o endereço de email:"));
        panel.add(emailField);
        panel.add(new JLabel("Digite a senha:"));
        panel.add(passwordField);
        panel.add(new JLabel("Digite o nome:"));
        panel.add(nameField);
        panel.add(new JLabel("Digite o nome da empresa:"));
        panel.add(industryField);
        panel.add(new JLabel("Digite uma descrição da empresa:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Cadastro de Recrutador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String industry = industryField.getText();
            String description = descriptionField.getText();
            int id = 0;

            JsonObject requestJson = Request.createRequest("SIGNUP_RECRUITER");
            JsonObject data = new JsonObject();
            data.addProperty("email", email);
            data.addProperty("password", password);
            data.addProperty("name", name);
            data.addProperty("industry", industry);
            data.addProperty("description", description);
            data.addProperty("id", id);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                JOptionPane.showMessageDialog(frame, jsonResponse);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}