import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.google.gson.JsonObject;

public class SetJobAvailable {
    public static void set(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de atualizar as informações da conta.");
            return;
        }

        String[] available = { "YES", "NO" };

        JComboBox<String> availableField = new JComboBox<>(available);
        SpinnerNumberModel idModel = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner idField = new JSpinner(idModel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Insira a habilidade que deseja mudar:"));
        panel.add(idField);
        panel.add(new JLabel("Selecione a disponibilidade:"));
        panel.add(availableField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Atualização de Habilidade", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String id = (String) idField.getValue().toString();
            String availableS = (String) availableField.getSelectedItem();

            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("operation", "SET_JOB_AVAILABLE");
            requestJson.addProperty("token", token);
            JsonObject data = new JsonObject();
            data.addProperty("id", id);
            data.addProperty("available", availableS);
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
