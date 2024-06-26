import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SearchJob {
    public static void searchJob(JFrame frame, PrintWriter out, BufferedReader in, String token) {
        if (token == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        String[] skills = { "NodeJs", "JavaScript", "Java", "C", "HTML", "CSS", "React", "React Native",
                "TypeScript", "Ruby" };
        String[] filters = { "AND", "OR", "" };
        String[] options = { "skill", "experience", "ambos" };

        JComboBox<String> skillField = new JComboBox<>(skills);
        JComboBox<String> filterField = new JComboBox<>(filters);
        JComboBox<String> optionsField = new JComboBox<>(options);
        SpinnerNumberModel experienceModel = new SpinnerNumberModel(0, 0, 100, 1); // Valor inicial, mínimo, máximo,
                                                                                   // incremento
        JSpinner experienceField = new JSpinner(experienceModel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Selecione o que deseja enviar:"));
        panel.add(optionsField);
        panel.add(new JLabel("Selecione a habilidade:"));
        panel.add(skillField);
        panel.add(new JLabel("\n"));
        panel.add(filterField);
        panel.add(new JLabel("Diga o nível de experiencia desejado:"));
        panel.add(experienceField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Buscar Vagas", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && optionsField.getSelectedItem().equals("ambos")) {
            String skill = (String) skillField.getSelectedItem();
            String filter = (String) filterField.getSelectedItem();
            int experience = (int) experienceField.getValue();

            JsonObject requestJson = Request.createRequest("SEARCH_JOB");

            JsonObject data = new JsonObject();
            data.addProperty("skill", skill);
            data.addProperty("filter", filter);
            data.addProperty("experience", experience);
            requestJson.addProperty("token", token);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                System.out.println(jsonResponse);
                JsonObject responseJson = Request.parseJson(jsonResponse);
                if (responseJson.get("status").getAsString().equals("SUCCESS")) {
                    JsonObject dataObject = responseJson.getAsJsonObject("data");
                    JsonArray jobsArray = dataObject.getAsJsonArray("jobset");
                    StringBuilder accountInfo = new StringBuilder("Informações das vagas:\n");

                    JsonElement jobset_sizeElement = dataObject.get("jobset_size");
                    if (jobset_sizeElement != null) {
                        accountInfo.append("Número de vagas: ").append(jobset_sizeElement.getAsInt()).append("\n");
                    }

                    for (JsonElement jobElement : jobsArray) {
                        JsonObject jobObject = jobElement.getAsJsonObject();
                        String skillR = jobObject.get("skill").getAsString();
                        int experienceR = jobObject.get("experience").getAsInt();
                        int id = jobObject.get("id").getAsInt();
                        accountInfo.append("Habilidade: ").append(skillR).append(", Experiência: ").append(experienceR)
                                .append(", id: ").append(id).append("\n");
                    }

                    JOptionPane.showMessageDialog(frame, accountInfo.toString());
                } else {
                    String statusMessage = (responseJson.get("status") != null)
                            ? responseJson.get("status").getAsString()
                            : "desconhecido";
                    JOptionPane.showMessageDialog(frame, "Informações: " + statusMessage);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao enviar a solicitação: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (result == JOptionPane.OK_OPTION && optionsField.getSelectedItem().equals("skill")) {
            String skill = (String) skillField.getSelectedItem();

            JsonObject requestJson = Request.createRequest("SEARCH_JOB");

            JsonObject data = new JsonObject();
            data.addProperty("skill", skill);
            requestJson.addProperty("token", token);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                System.out.println(jsonResponse);
                JsonObject responseJson = Request.parseJson(jsonResponse);
                if (responseJson.get("status").getAsString().equals("SUCCESS")) {
                    JsonArray jobsArray = responseJson.getAsJsonArray("jobset");
                    StringBuilder accountInfo = new StringBuilder("Informações das vagas:\n");

                    JsonElement jobset_sizeElement = responseJson.get("jobset_size");
                    if (jobset_sizeElement != null) {
                        accountInfo.append("Número de vagas: ").append(jobset_sizeElement.getAsInt()).append("\n");
                    }

                    for (JsonElement jobElement : jobsArray) {
                        JsonObject jobObject = jobElement.getAsJsonObject();
                        String skillR = jobObject.get("skill").getAsString();
                        int experienceR = jobObject.get("experience").getAsInt();
                        int id = jobObject.get("id").getAsInt();
                        accountInfo.append("Habilidade: ").append(skillR).append(", Experiência: ").append(experienceR)
                                .append(", id: ").append(id).append("\n");
                    }

                    JOptionPane.showMessageDialog(frame, accountInfo.toString());
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
        } else if (result == JOptionPane.OK_OPTION && optionsField.getSelectedItem().equals("experience")) {
            int experience = (int) experienceField.getValue();

            JsonObject requestJson = Request.createRequest("SEARCH_JOB");

            JsonObject data = new JsonObject();
            data.addProperty("experience", experience);
            requestJson.addProperty("token", token);
            requestJson.add("data", data);

            try {
                String jsonResponse = Request.sendRequest(requestJson, out, in);
                System.out.println(jsonResponse);
                JsonObject responseJson = Request.parseJson(jsonResponse);
                if (responseJson.get("status").getAsString().equals("SUCCESS")) {
                    JsonArray jobsArray = responseJson.getAsJsonArray("jobset");
                    StringBuilder accountInfo = new StringBuilder("Informações das vagas:\n");

                    JsonElement jobset_sizeElement = responseJson.get("jobset_size");
                    if (jobset_sizeElement != null) {
                        accountInfo.append("Número de vagas: ").append(jobset_sizeElement.getAsInt()).append("\n");
                    }

                    for (JsonElement jobElement : jobsArray) {
                        JsonObject jobObject = jobElement.getAsJsonObject();
                        String skillR = jobObject.get("skill").getAsString();
                        int experienceR = jobObject.get("experience").getAsInt();
                        int id = jobObject.get("id").getAsInt();
                        accountInfo.append("Habilidade: ").append(skillR).append(", Experiência: ").append(experienceR)
                                .append(", id: ").append(id).append("\n");
                    }

                    JOptionPane.showMessageDialog(frame, accountInfo.toString());
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
