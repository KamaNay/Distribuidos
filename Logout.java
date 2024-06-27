import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Logout {
    public static String logout(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de sair.");
            return token;
        }

        JsonObject requestJson = Request.createRequest("LOGOUT_CANDIDATE");
        requestJson.addProperty("token", token);

        try {
            String jsonResponse = Request.sendRequest(requestJson, out, in);
            JOptionPane.showMessageDialog(frame, jsonResponse);

            // Limpa o token no lado do cliente
            token = null;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
        }

        return token;
    }
}