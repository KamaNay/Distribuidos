import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Server extends Thread {
    private Socket clientSocket;
    private BufferedWriter fileWriter;
    private static final String URL = "jdbc:mysql://localhost:3306/SD?user=root&password=8infinito";

    public Server(Socket clientSoc, BufferedWriter writer) {
        clientSocket = clientSoc;
        fileWriter = writer;
        start();
    }

    private static List<String> invalidatedTokens = new ArrayList<>();

    public static class JWTValidator {
        private static final String TOKEN_KEY = "DISTRIBUIDOS";
        private static final Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
        private static final JWTVerifier verifier = JWT.require(algorithm).build();

        public static String generateToken(int id, String role) {
            return JWT.create()
                    .withClaim("id", id)
                    .withClaim("role", role)
                    .sign(algorithm);
        }

        public static int getIdClaim(String token) throws JWTVerificationException {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("id").asInt();
        }

        public static String getRoleClaim(String token) throws JWTVerificationException {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("role").asString();
        }
    }

    private static class Experience {
        private int id_user;
        private int id_skill;
        private int experience;
        private int id;

        public Experience(int id_user, int id_skill, int experience, int id) {
            this.id_user = id_user;
            this.id_skill = id_skill;
            this.experience = experience;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }

        public int getId_user() {
            return id_user;
        }

        @SuppressWarnings("unused")
        public void setId_user(int id_user) {
            this.id_user = id_user;
        }

        public int getId_skill() {
            return id_skill;
        }

        @SuppressWarnings("unused")
        public void setId_skill(int id_skill) {
            this.id_skill = id_skill;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

    }

    private static class Skill {
        private String skill;
        private int id;

        public Skill(String skill, int id) {
            this.skill = skill;
            this.id = id;
        }

        public String getSkill() {
            return skill;
        }

        public int getId() {
            return id;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }
    }

    private static class SkillSet {
        private String skill;
        private int experience;
        int id;

        public SkillSet(String skill, int experience, int id) {
            this.skill = skill;
            this.experience = experience;
            this.id = id;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public int getId() {
            return id;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

    }

    private static class ExperienceJ {
        private int id_company;
        private int id_skill;
        private int experience;
        private int id;

        public ExperienceJ(int id_company, int id_skill, int experience, int id) {
            this.id_company = id_company;
            this.id_skill = id_skill;
            this.experience = experience;
            this.id = id;
        }

        public int getId_company() {
            return id_company;
        }

        @SuppressWarnings("unused")
        public void setId_company(int id_company) {
            this.id_company = id_company;
        }

        public int getId_skill() {
            return id_skill;
        }

        @SuppressWarnings("unused")
        public void setId_skill(int id_skill) {
            this.id_skill = id_skill;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public int getId() {
            return id;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }

    }

    private static class Job {
        private String skill;
        private int id;

        public Job(String skill, int id) {
            this.skill = skill;
            this.id = id;
        }

        public String getSkill() {
            return skill;
        }

        public int getId() {
            return id;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }
    }

    private static class JobSet {
        private String skill;
        private int experience;
        private int id;

        public JobSet(String skill, int experience, int id) {
            this.skill = skill;
            this.experience = experience;
            this.id = id;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public int getId() {
            return id;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }
    }

    private static class User {
        private String email;
        private String password;
        private String name;
        private int id;

        public User(String email, String password, String name, int id) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.id = id;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    private static class Company {
        private String email;
        private String password;
        private String name;
        private String industry;
        private String description;
        private int id;

        public Company(String email, String password, String name, int id, String industry, String description) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.industry = industry;
            this.description = description;
            this.id = id;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @SuppressWarnings("unused")
        public void setId(int id) {
            this.id = id;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }

        public String getIndustry() {
            return industry;
        }

        public String getDescription() {
            return description;
        }

        public int getId() {
            return id;
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            boolean running = true;
            while (running) {
                String jsonMessage = in.readLine();

                if (jsonMessage != null && jsonMessage.equalsIgnoreCase("sair")) {
                    running = false;
                    continue;
                }

                if (jsonMessage != null) {
                    fileWriter.write("recebido: " + jsonMessage);
                    fileWriter.newLine();
                    fileWriter.flush();

                    JsonObject requestJson = Request.parseJson(jsonMessage);
                    String operation = requestJson.get("operation").getAsString();

                    switch (operation) {
                        case "LOGIN_CANDIDATE":
                            handleLogin(requestJson, out);
                            break;
                        case "LOGIN_RECRUITER":
                            handleLoginC(requestJson, out);
                            break;
                        case "SIGNUP_CANDIDATE":
                            handleSignup(requestJson, out);
                            break;
                        case "SIGNUP_RECRUITER":
                            handleSignupC(requestJson, out);
                            break;
                        case "UPDATE_ACCOUNT_CANDIDATE":
                            handleUpdate(requestJson, out);
                            break;
                        case "UPDATE_ACCOUNT_RECRUITER":
                            handleUpdateC(requestJson, out);
                            break;
                        case "DELETE_ACCOUNT_CANDIDATE":
                            handleDelete(requestJson, out);
                            break;
                        case "DELETE_ACCOUNT_RECRUITER":
                            handleDeleteC(requestJson, out);
                            break;
                        case "LOGOUT_CANDIDATE":
                            handleLogout(requestJson, out);
                            break;
                        case "LOGOUT_RECRUITER":
                            handleLogoutC(requestJson, out);
                            break;
                        case "LOOKUP_ACCOUNT_CANDIDATE":
                            handleLookup(requestJson, out);
                            break;
                        case "LOOKUP_ACCOUNT_RECRUITER":
                            handleLookupC(requestJson, out);
                            break;
                        case "INCLUDE_SKILL":
                            handleIncludeS(requestJson, out);
                            break;
                        case "LOOKUP_SKILL":
                            handleLookupS(requestJson, out);
                            break;
                        case "LOOKUP_SKILLSET":
                            handleLookupSS(requestJson, out);
                            break;
                        case "DELETE_SKILL":
                            handleDeleteS(requestJson, out);
                            break;
                        case "UPDATE_SKILL":
                            handleUpdateS(requestJson, out);
                            break;

                        case "INCLUDE_JOB":
                            handleIncludeJ(requestJson, out);
                            break;
                        case "LOOKUP_JOB":
                            handleLookupJ(requestJson, out);
                            break;
                        case "LOOKUP_JOBSET":
                            handleLookupJS(requestJson, out);
                            break;
                        case "DELETE_JOB":
                            handleDeleteJ(requestJson, out);
                            break;
                        case "UPDATE_JOB":
                            handleUpdateJ(requestJson, out);
                            break;

                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (fileWriter != null)
                    fileWriter.close();
                if (clientSocket != null && !clientSocket.isClosed())
                    clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    private void handleUpdateJ(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        JsonObject data = requestJson.getAsJsonObject("data");
        int id_job = data.get("id").getAsInt();
        String newSkill = data.get("skill").getAsString();
        int newExperience = data.get("experience").getAsInt();
        int id;

        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("operation", "UPDATE_JOB");
            responseJson.addProperty("status", "INVALID_TOKEN");
            responseJson.add("data", new JsonObject());
            fileWriter.write("enviado: " + responseJson + "invalidtoken");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(responseJson.toString());
            return;
        }

        List<JobSet> jobsets = readJobSetDatabase();
        List<Job> jobs = readJobDatabase();
        List<ExperienceJ> experiences = readExperienceJDatabase();
        for (Job job : jobs) {
            for (ExperienceJ experience : experiences) {
                for (JobSet jobset : jobsets) {
                    if (job.getId() == id_job && experience.getId_company() == id
                            && job.getId() == experience.getId_skill()) {
                        job.setSkill(newSkill);
                        jobs.add(job);
                        writeJobDatabase(jobs);

                        experience.setExperience(newExperience);
                        experiences.add(experience);
                        writeExperienceJDatabase(experiences);

                        jobset.setSkill(newSkill);
                        jobset.setExperience(newExperience);
                        jobsets.add(jobset);
                        writeJobSetDatabase(jobsets);

                        JsonObject responseJson = new JsonObject();
                        responseJson.addProperty("operation", "UPDATE_JOB");
                        responseJson.addProperty("status", "SUCCESS");
                        responseJson.add("data", new JsonObject());
                        fileWriter.write("enviado: " + responseJson + "success");
                        fileWriter.newLine();
                        fileWriter.flush();
                        out.println(responseJson.toString());
                        return;
                    }
                }
            }
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("operation", "UPDATE_JOB");
        responseJson.addProperty("status", "JOB_NOT_FOUND");
        responseJson.add("data", new JsonObject());
        fileWriter.write("enviado: " + responseJson + "jobnotfound");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(responseJson.toString());
    }

    private void handleDeleteJ(JsonObject requestJson, PrintWriter out) throws IOException {
        JsonObject data = requestJson.getAsJsonObject("data");
        int id_job = data.get("id").getAsInt();

        String getId = "SELECT * FROM jobs WHERE id = ?";
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getId);) {

            preparedStatement.setInt(1, id_job);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int id_skill = rs.getInt("id");
                String deleteExpJ = "DELETE FROM experienceJ WHERE id_skill = ?";

                try (PreparedStatement preparedStatement2 = connection.prepareStatement(deleteExpJ)) {

                    preparedStatement2.setInt(1, id_skill);
                    int rowsAffected = preparedStatement2.executeUpdate();

                    if (rowsAffected > 0) {
                        // Exclusão realizada com sucesso
                        JsonObject responseJson = Request.createResponse("DELETE_JOB", "SUCCESS", "");
                        fileWriter.write("enviado: " + responseJson + "success");
                        fileWriter.newLine();
                        fileWriter.flush();
                        out.println(Request.toJsonString(responseJson));
                    } else {
                        // Usuário não encontrado
                        JsonObject responseJson = Request.createResponse("DELETE_JOB", "JOB_NOT_FOUND", "");
                        fileWriter.write("enviado: " + responseJson + "jobnotfound");
                        fileWriter.newLine();
                        fileWriter.flush();
                        out.println(Request.toJsonString(responseJson));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JsonObject responseJson = Request.createResponse("DELETE_JOB", "ERROR", "");
                    fileWriter.write("enviado: " + responseJson + "error");
                    fileWriter.newLine();
                    fileWriter.flush();
                    out.println(Request.toJsonString(responseJson));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String deleteJ = "DELETE FROM jobs WHERE id = ?";

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteJ)) {

            preparedStatement.setInt(1, id_job);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Exclusão realizada com sucesso
                JsonObject responseJson = Request.createResponse("DELETE_JOB", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_JOB", "JOB_NOT_FOUND", "");
                fileWriter.write("enviado: " + responseJson + "jobnotfound");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_JOB", "ERROR", "");
            fileWriter.write("enviado: " + responseJson + "error");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
        }

        String deleteJS = "DELETE FROM jobset WHERE id = ?";

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteJS)) {

            preparedStatement.setInt(1, id_job);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Exclusão realizada com sucesso
                JsonObject responseJson = Request.createResponse("DELETE_JOB", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_JOB", "JOB_NOT_FOUND", "");
                fileWriter.write("enviado: " + responseJson + "jobnotfound");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_JOB", "ERROR", "");
            fileWriter.write("enviado: " + responseJson + "error");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
        }
    }

    private void handleLookupJS(JsonObject requestJson, PrintWriter out) throws SQLException, IOException {
        List<JsonObject> jobsList = new ArrayList<>();

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM jobset");
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            JsonObject jobData = new JsonObject();
            jobData.addProperty("skill", rs.getString("skill")); // Ajuste o nome da coluna conforme necessário
            jobData.addProperty("experience", rs.getInt("experience")); // Ajuste o nome da coluna conforme necessário
            jobsList.add(jobData);
        }

        JsonObject responseJson = Request.createResponse("LOOKUP_JOBSET", "SUCCESS", "");
        responseJson.add("skills", new Gson().toJsonTree(jobsList));
        responseJson.addProperty("jobset_size", jobsList.size());

        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLookupJ(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        JsonObject data = requestJson.getAsJsonObject("data");
        int id;
        int id_job = data.get("id").getAsInt();

        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOOKUP_JOB", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "invalidtoken");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        List<Job> jobs = readJobDatabase();
        List<ExperienceJ> experiences = readExperienceJDatabase();
        for (Job job : jobs) {
            for (ExperienceJ experience : experiences) {
                if (job.getId() == id_job && experience.getId_company() == id
                        && job.getId() == experience.getId_skill()) {
                    // Habilidade encontrado, retorna os dados do usuário
                    data.addProperty("skill", job.getSkill());
                    data.addProperty("id", job.getId());
                    data.addProperty("experience", experience.getExperience());

                    JsonObject responseJson = Request.createResponse("LOOKUP_JOB", "SUCCESS", "");
                    responseJson.add("data", data); // Adicione esta linha
                    fileWriter.write("enviado: " + responseJson + "success");
                    fileWriter.newLine();
                    fileWriter.flush();
                    out.println(Request.toJsonString(responseJson));
                    return;
                }
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("LOOKUP_JOB", "JOB_NOT_FOUND", "");
        fileWriter.write("enviado: " + responseJson + "jobnotfound");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleIncludeJ(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String token = requestJson.get("token").getAsString();

        String skillName = data.get("skill").getAsString();
        int experience = data.get("experience").getAsInt();

        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("INCLUDE_JOB", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "invalidtoken");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        List<Job> jobDataset = readJobDataset();
        List<ExperienceJ> experiences = readExperienceJDatabase();
        List<JobSet> jobsets = readJobSetDatabase();
        List<Job> jobs = readJobDatabase();
        for (Job job : jobs) {
            if (job.getSkill().equals(skillName)) {
                // Skill já existe
                JsonObject responseJson = Request.createResponse("INCLUDE_JOB", "JOB_EXISTS", "");
                fileWriter.write("enviado: " + responseJson + "jobexists");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        for (Job job : jobDataset) {
            if (job.getSkill().equals(skillName)) {
                // Skill não encontrado e existente no database, pode ser cadastrado
                int id_company = id;
                int id_skill = generateJobId();
                Job newJob = new Job(skillName, id_skill);
                ExperienceJ newExperience = new ExperienceJ(id_company, id_skill, experience, id_skill);
                jobs.add(newJob);
                experiences.add(newExperience);
                writeJobDatabase(jobs);
                writeExperienceJDatabase(experiences);

                JobSet newJobSet = new JobSet(skillName, experience, id_skill);
                jobsets.add(newJobSet);
                writeJobSetDatabase(jobsets);

                // Cadastro realizado com sucesso
                JsonObject responseJson = Request.createResponse("INCLUDE_JOB", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Skill não existe
        JsonObject responseJson = Request.createResponse("INCLUDE_JOB", "SKILL_NOT_EXISTS", "");
        fileWriter.write("enviado: " + responseJson + "skillnotexists");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private int generateJobId() {
        int id;

        List<Job> jobs = readJobDatabase();
        if (jobs.isEmpty()) {
            id = 1;
            return id;
        }
        Job lastSkill = jobs.get(jobs.size() - 1);
        return id = lastSkill.getId() + 1;
    }

    private List<ExperienceJ> readExperienceJDatabase() {
        List<ExperienceJ> experiences = new ArrayList<>();
        String query = "SELECT id_company, id_skill, experience, id FROM experienceJ";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id_company = resultSet.getInt("id_company");
                int id_skill = resultSet.getInt("id_skill");
                int exp = resultSet.getInt("experience");
                int id = resultSet.getInt("id");
                ExperienceJ experience = new ExperienceJ(id_company, id_skill, exp, id);
                experiences.add(experience);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return experiences;
    }

    private void writeExperienceJDatabase(List<ExperienceJ> experiences) {
        String query = "INSERT INTO experienceJ (id_company, id_skill, experience, id) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE id_company=VALUES(id_company), id_skill=VALUES(id_skill), experience=VALUES(experience)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (ExperienceJ experience : experiences) {
                preparedStatement.setInt(1, experience.getId_company());
                preparedStatement.setInt(2, experience.getId_skill());
                preparedStatement.setInt(3, experience.getExperience());
                preparedStatement.setInt(4, experience.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private void writeJobDatabase(List<Job> jobs) {
        String query = "INSERT INTO jobs (skill, id) VALUES (?, ?) ON DUPLICATE KEY UPDATE skill=VALUES(skill)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Job job : jobs) {
                preparedStatement.setString(1, job.getSkill());
                preparedStatement.setInt(2, job.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private List<Job> readJobDataset() {
        List<Job> jobs = new ArrayList<>();
        String query = "SELECT skill, id FROM jobdataset";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String skill_name = resultSet.getString("skill");
                int id = resultSet.getInt("id");
                Job job = new Job(skill_name, id);
                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return jobs;
    }

    private void writeJobSetDatabase(List<JobSet> jobsets) {
        String query = "INSERT INTO jobset (skill, experience, id) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE skill=VALUES(skill), experience=VALUES(experience)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (JobSet jobset : jobsets) {
                preparedStatement.setString(1, jobset.getSkill());
                preparedStatement.setInt(2, jobset.getExperience());
                preparedStatement.setInt(3, jobset.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private List<JobSet> readJobSetDatabase() {
        List<JobSet> jobsets = new ArrayList<>();
        String query = "SELECT skill, experience, id FROM jobset";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String skill = resultSet.getString("skill");
                int experience = resultSet.getInt("experience");
                int id = resultSet.getInt("id");
                JobSet jobset = new JobSet(skill, experience, id);
                jobsets.add(jobset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return jobsets;
    }

    private List<Job> readJobDatabase() {
        List<Job> jobs = new ArrayList<>();
        String query = "SELECT skill, id FROM jobs";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String skill_name = resultSet.getString("skill");
                int id = resultSet.getInt("id");
                Job job = new Job(skill_name, id);
                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return jobs;
    }

    private void handleUpdateS(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        JsonObject data = requestJson.getAsJsonObject("data");
        String skillName = data.get("skill").getAsString();
        String newSkill = data.get("newSkill").getAsString();
        int newExperience = data.get("experience").getAsInt();
        int id;

        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("operation", "UPDATE_SKILL");
            responseJson.addProperty("status", "INVALID_TOKEN");
            responseJson.add("data", new JsonObject());
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(responseJson.toString());
            return;
        }

        List<SkillSet> skillsets = readSkillSetDatabase();
        List<Skill> skills = readSkillDatabase();
        List<Experience> experiences = readExperienceDatabase();
        for (Skill skill : skills) {
            for (Experience experience : experiences) {
                for (SkillSet skillset : skillsets) {
                    if (skill.getSkill().equals(skillName) && experience.getId_user() == id
                            && skill.getId() == experience.getId_skill() && skillset.getSkill().equals(skillName)) {
                        skill.setSkill(newSkill);
                        skills.add(skill);
                        writeSkillDatabase(skills);

                        experience.setExperience(newExperience);
                        experiences.add(experience);
                        writeExperienceDatabase(experiences);

                        skillset.setSkill(newSkill);
                        skillset.setExperience(newExperience);
                        skillsets.add(skillset);
                        writeSkillSetDatabase(skillsets);

                        JsonObject responseJson = new JsonObject();
                        responseJson.addProperty("operation", "UPDATE_SKILL");
                        responseJson.addProperty("status", "SUCCESS");
                        responseJson.add("data", new JsonObject());
                        fileWriter.write("enviado: " + responseJson + "success");
                        fileWriter.newLine();
                        fileWriter.flush();
                        out.println(responseJson.toString());
                        return;
                    }
                }
            }
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("operation", "UPDATE_SKILL");
        responseJson.addProperty("status", "SKILL_NOT_FOUND");
        responseJson.add("data", new JsonObject());
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(responseJson.toString());
    }

    private void handleDeleteS(JsonObject requestJson, PrintWriter out) throws IOException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String skillName = data.get("skill").getAsString();

        String getId = "SELECT * FROM skills WHERE skill = ?";
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getId);) {

            preparedStatement.setString(1, skillName);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int id_skill = rs.getInt("id");
                String deleteExp = "DELETE FROM experience WHERE id_skill = ?";

                try (PreparedStatement preparedStatement2 = connection.prepareStatement(deleteExp)) {

                    preparedStatement2.setInt(1, id_skill);
                    int rowsAffected = preparedStatement2.executeUpdate();

                    if (rowsAffected > 0) {
                        // Exclusão realizada com sucesso
                        JsonObject responseJson = Request.createResponse("DELETE_SKILL", "SUCCESS", "");
                        fileWriter.write("enviado: " + responseJson + "success");
                        fileWriter.newLine();
                        fileWriter.flush();
                        out.println(Request.toJsonString(responseJson));
                    } else {
                        // Usuário não encontrado
                        JsonObject responseJson = Request.createResponse("DELETE_SKILL", "SKILL_NOT_FOUND", "");
                        fileWriter.write("enviado: " + responseJson + "success");
                        fileWriter.newLine();
                        fileWriter.flush();
                        out.println(Request.toJsonString(responseJson));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JsonObject responseJson = Request.createResponse("DELETE_SKILL", "ERROR", "");
                    fileWriter.write("enviado: " + responseJson + "success");
                    fileWriter.newLine();
                    fileWriter.flush();
                    out.println(Request.toJsonString(responseJson));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String deleteS = "DELETE FROM skills WHERE skill = ?";

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteS)) {

            preparedStatement.setString(1, skillName);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Exclusão realizada com sucesso
                JsonObject responseJson = Request.createResponse("DELETE_SKILL", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_SKILL", "SKILL_NOT_FOUND", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_SKILL", "ERROR", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
        }

        String deleteSS = "DELETE FROM skillset WHERE skill = ?";

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteSS)) {

            preparedStatement.setString(1, skillName);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Exclusão realizada com sucesso
                JsonObject responseJson = Request.createResponse("DELETE_SKILL", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_SKILL", "SKILL_NOT_FOUND", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_SKILL", "ERROR", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
        }
    }

    private void handleLookupSS(JsonObject requestJson, PrintWriter out) throws SQLException, IOException {
        List<JsonObject> skillsList = new ArrayList<>();

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM skillset");
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            JsonObject skillData = new JsonObject();
            skillData.addProperty("skill", rs.getString("skill")); // Ajuste o nome da coluna conforme necessário
            skillData.addProperty("experience", rs.getInt("experience")); // Ajuste o nome da coluna conforme necessário
            skillsList.add(skillData);
        }

        JsonObject responseJson = Request.createResponse("LOOKUP_SKILLSET", "SUCCESS", "");
        responseJson.add("skills", new Gson().toJsonTree(skillsList));
        responseJson.addProperty("skillset_size", skillsList.size());

        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLookupS(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        JsonObject data = requestJson.getAsJsonObject("data");
        int id;
        String skillName = data.get("skill").getAsString();

        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOOKUP_SKILL", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        List<Skill> skills = readSkillDatabase();
        List<Experience> experiences = readExperienceDatabase();
        for (Skill skill : skills) {
            for (Experience experience : experiences) {
                if (skill.getSkill().equals(skillName) && experience.getId_user() == id
                        && skill.getId() == experience.getId_skill()) {
                    // Habilidade encontrado, retorna os dados do usuário
                    data.addProperty("skill", skill.getSkill());
                    data.addProperty("id", skill.getId());
                    data.addProperty("experience", experience.getExperience());

                    JsonObject responseJson = Request.createResponse("LOOKUP_SKILL", "SUCCESS", "");
                    responseJson.add("data", data); // Adicione esta linha
                    fileWriter.write("enviado: " + responseJson + "success");
                    fileWriter.newLine();
                    fileWriter.flush();
                    out.println(Request.toJsonString(responseJson));
                    return;
                }
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("LOOKUP_SKILL", "SKILL_NOT_FOUND", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleIncludeS(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String token = requestJson.get("token").getAsString();

        String skillName = data.get("skill").getAsString();
        int experience = data.get("experience").getAsInt();

        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("INCLUDE_SKILL", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        List<Skill> skillDataset = readSkillDataset();
        List<Experience> experiences = readExperienceDatabase();
        List<SkillSet> skillsets = readSkillSetDatabase();
        List<Skill> skills = readSkillDatabase();
        for (Skill skill : skills) {
            if (skill.getSkill().equals(skillName)) {
                // Skill já existe
                JsonObject responseJson = Request.createResponse("INCLUDE_SKILL", "SKILL_EXISTS", "");
                fileWriter.write("enviado: " + responseJson + "skillexists");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        for (Skill skill : skillDataset) {
            if (skill.getSkill().equals(skillName)) {
                // Skill não encontrado e existente no database, pode ser cadastrado
                int id_user = id;
                int id_skill = generateSkillId();
                Skill newSkill = new Skill(skillName, id_skill);
                Experience newExperience = new Experience(id_user, id_skill, experience, id_skill);
                skills.add(newSkill);
                experiences.add(newExperience);
                writeSkillDatabase(skills);
                writeExperienceDatabase(experiences);

                SkillSet newSkillSet = new SkillSet(skillName, experience, id_skill);
                skillsets.add(newSkillSet);
                writeSkillSetDatabase(skillsets);

                // Cadastro realizado com sucesso
                JsonObject responseJson = Request.createResponse("INCLUDE_SKILL", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Skill não existe
        JsonObject responseJson = Request.createResponse("INCLUDE_SKILL", "SKILL_NOT_EXISTS", "");
        fileWriter.write("enviado: " + responseJson + "skillnotexists");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private int generateSkillId() {
        int id;

        List<Skill> skills = readSkillDatabase();
        if (skills.isEmpty()) {
            id = 1;
            return id;
        }
        Skill lastSkill = skills.get(skills.size() - 1);
        return id = lastSkill.getId() + 1;
    }

    private List<Server.Experience> readExperienceDatabase() {
        List<Experience> experiences = new ArrayList<>();
        String query = "SELECT id_user, id_skill, experience, id FROM experience";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id_user = resultSet.getInt("id_user");
                int id_skill = resultSet.getInt("id_skill");
                int exp = resultSet.getInt("experience");
                int id = resultSet.getInt("id");
                Experience experience = new Experience(id_user, id_skill, exp, id);
                experiences.add(experience);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return experiences;
    }

    private void writeExperienceDatabase(List<Server.Experience> experiences) {
        String query = "INSERT INTO experience (id_user, id_skill, experience, id) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE id_user=VALUES(id_user), id_skill=VALUES(id_skill), experience=VALUES(experience)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Experience experience : experiences) {
                preparedStatement.setInt(1, experience.getId_user());
                preparedStatement.setInt(2, experience.getId_skill());
                preparedStatement.setInt(3, experience.getExperience());
                preparedStatement.setInt(4, experience.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private void writeSkillDatabase(List<Skill> skills) {
        String query = "INSERT INTO skills (skill, id) VALUES (?, ?) ON DUPLICATE KEY UPDATE skill=VALUES(skill)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Skill skill : skills) {
                preparedStatement.setString(1, skill.getSkill());
                preparedStatement.setInt(2, skill.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private List<Server.Skill> readSkillDataset() {
        List<Skill> skills = new ArrayList<>();
        String query = "SELECT skill, id FROM skilldataset";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String skill_name = resultSet.getString("skill");
                int id = resultSet.getInt("id");
                Skill skill = new Skill(skill_name, id);
                skills.add(skill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return skills;
    }

    private void writeSkillSetDatabase(List<SkillSet> skillsets) {
        String query = "INSERT INTO skillset (skill, experience, id) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE skill=VALUES(skill), experience=VALUES(experience)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (SkillSet skillset : skillsets) {
                preparedStatement.setString(1, skillset.getSkill());
                preparedStatement.setInt(2, skillset.getExperience());
                preparedStatement.setInt(3, skillset.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private List<SkillSet> readSkillSetDatabase() {
        List<SkillSet> skillsets = new ArrayList<>();
        String query = "SELECT skill, experience, id FROM skillset";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String skill = resultSet.getString("skill");
                int experience = resultSet.getInt("experience");
                int id = resultSet.getInt("id");
                SkillSet skillset = new SkillSet(skill, experience, id);
                skillsets.add(skillset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return skillsets;
    }

    private List<Skill> readSkillDatabase() {
        List<Skill> skills = new ArrayList<>();
        String query = "SELECT skill, id FROM skills";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String skill_name = resultSet.getString("skill");
                int id = resultSet.getInt("id");
                Skill skill = new Skill(skill_name, id);
                skills.add(skill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return skills;
    }

    private void handleLogin(JsonObject requestJson, PrintWriter out) throws IOException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();

        List<User> users = readUserDatabase();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                if (user.getPassword().equals(password)) {
                    // Usuário autenticado com sucesso
                    String token = JWTValidator.generateToken(user.getId(), user.getEmail());

                    JsonObject responseJson = Request.createResponse("LOGIN_CANDIDATE", "SUCCESS", token);
                    fileWriter.write("enviado: " + responseJson);
                    fileWriter.newLine();
                    fileWriter.flush();
                    out.println(Request.toJsonString(responseJson));
                    return;
                }
            }
        }
        // Caso ocorra qualquer erro de senha ou email
        JsonObject responseJson = Request.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLoginC(JsonObject requestJson, PrintWriter out) throws IOException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();

        List<Company> companies = readCompanyDatabase();
        for (Company company : companies) {
            if (company.getEmail().equals(email)) {
                if (company.getPassword().equals(password)) {
                    // Empresa autenticada com sucesso
                    String token = JWTValidator.generateToken(company.getId(), company.getEmail());

                    JsonObject responseJson = Request.createResponse("LOGIN_RECRUITER", "SUCCESS", token);
                    fileWriter.write("enviado: " + responseJson);
                    fileWriter.newLine();
                    fileWriter.flush();
                    out.println(Request.toJsonString(responseJson));
                    return;
                }
            }
        }
        // Caso ocorra qualquer erro de senha ou email
        JsonObject responseJson = Request.createResponse("LOGIN_RECRUITER", "INVALID_LOGIN", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleSignup(JsonObject requestJson, PrintWriter out) throws IOException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();
        String name = data.get("name").getAsString();

        List<User> users = readUserDatabase();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                // Cliente já existe
                JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");
                fileWriter.write("enviado: " + responseJson + "userexists");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Cliente não encontrado, pode ser cadastrado
        int id = generateUserId();
        User newUser = new User(email, password, name, id);
        users.add(newUser);
        writeUserDatabase(users);

        // Cadastro realizado com sucesso
        JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "SUCCESS", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleSignupC(JsonObject requestJson, PrintWriter out) throws IOException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();
        String name = data.get("name").getAsString();
        String industry = data.get("industry").getAsString();
        String description = data.get("description").getAsString();

        List<Company> companies = readCompanyDatabase();
        for (Company company : companies) {
            if (company.getEmail().equals(email)) {
                // Empresa já existe
                JsonObject responseJson = Request.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");
                fileWriter.write("enviado: " + responseJson + "userexists");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Empresa não encontrada, pode ser cadastrado
        int id = generateUserId();
        Company newCompany = new Company(email, password, name, id, industry, description);
        companies.add(newCompany);
        writeCompanyDatabase(companies);

        // Cadastro realizado com sucesso
        JsonObject responseJson = Request.createResponse("SIGNUP_RECRUITER", "SUCCESS", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private int generateUserId() throws IOException {
        int id;

        List<User> users = readUserDatabase();
        if (users.isEmpty()) {
            id = 1;
            return id;
        }
        User lastUser = users.get(users.size() - 1);
        return id = lastUser.getId() + 1;
    }

    private void handleUpdate(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        JsonObject data = requestJson.getAsJsonObject("data");
        String newEmail = data.get("email").getAsString();
        String password = data.get("password").getAsString();
        String name = data.get("name").getAsString();
        int id;

        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("operation", "UPDATE_ACCOUNT_CANDIDATE");
            responseJson.addProperty("status", "INVALID_TOKEN");
            responseJson.add("data", new JsonObject());
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(responseJson.toString());
            return;
        }

        List<User> users = readUserDatabase();
        for (User user : users) {
            if (user.getId() == id) {
                user.setEmail(newEmail);
                user.setPassword(password);
                user.setName(name);
                writeUserDatabase(users);

                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("operation", "UPDATE_ACCOUNT_CANDIDATE");
                responseJson.addProperty("status", "SUCCESS");
                responseJson.add("data", new JsonObject());
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(responseJson.toString());
                return;
            }
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("operation", "UPDATE_ACCOUNT_CANDIDATE");
        responseJson.addProperty("status", "USER_NOT_FOUND");
        responseJson.add("data", new JsonObject());
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(responseJson.toString());
    }

    private void handleUpdateC(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        JsonObject data = requestJson.getAsJsonObject("data");
        String newEmail = data.get("email").getAsString();
        String password = data.get("password").getAsString();
        String name = data.get("name").getAsString();
        String industry = data.get("industry").getAsString();
        String description = data.get("description").getAsString();
        int id;

        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("operation", "UPDATE_ACCOUNT_RECRUITER");
            responseJson.addProperty("status", "INVALID_TOKEN");
            responseJson.add("data", new JsonObject());
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(responseJson.toString());
            return;
        }

        List<Company> companies = readCompanyDatabase();
        for (Company company : companies) {
            if (company.getId() == id) {
                company.setEmail(newEmail);
                company.setPassword(password);
                company.setName(name);
                company.setIndustry(industry);
                company.setDescription(description);
                writeCompanyDatabase(companies);

                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("operation", "UPDATE_ACCOUNT_RECRUITER");
                responseJson.addProperty("status", "SUCCESS");
                responseJson.add("data", new JsonObject());
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(responseJson.toString());
                return;
            }
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("operation", "UPDATE_ACCOUNT_RECRUITER");
        responseJson.addProperty("status", "USER_NOT_FOUND");
        responseJson.add("data", new JsonObject());
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(responseJson.toString());
    }

    private void handleDelete(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        String deleteSQL = "DELETE FROM users WHERE id = ?";

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Exclusão realizada com sucesso
                JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "USER_NOT_FOUND", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "ERROR", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
        }
    }

    private void handleDeleteC(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_RECRUITER", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        String deleteSQL = "DELETE FROM companies WHERE id = ?";

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Exclusão realizada com sucesso
                JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_RECRUITER", "SUCCESS", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_RECRUITER", "USER_NOT_FOUND", "");
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_RECRUITER", "ERROR", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Driver JDBC não encontrado.", e);
        }
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "email VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "name VARCHAR(255) NOT NULL)";
        String createCompaniesTableSQL = "CREATE TABLE IF NOT EXISTS companies (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "email VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "name VARCHAR(255) NOT NULL," +
                "industry VARCHAR(255) NOT NULL," +
                "description VARCHAR(255) NOT NULL)";
        String createSkillsTableSQL = "CREATE TABLE IF NOT EXISTS skills (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "skill VARCHAR(255) NOT NULL)";
        String createSkillSetTableSQL = "CREATE TABLE IF NOT EXISTS skillset (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "skill VARCHAR(255) NOT NULL, " +
                "experience INT NOT NULL)";
        String createExperienceTableSQL = "CREATE TABLE IF NOT EXISTS experience (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_user INT NOT NULL, " +
                "experience INT NOT NULL, " +
                "id_skill INT NOT NULL)";
        String createJobsTableSQL = "CREATE TABLE IF NOT EXISTS jobs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "skill VARCHAR(255) NOT NULL)";
        String createJobSetTableSQL = "CREATE TABLE IF NOT EXISTS jobset (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "skill VARCHAR(255) NOT NULL, " +
                "experience INT NOT NULL)";
        String createExperienceJTableSQL = "CREATE TABLE IF NOT EXISTS experienceJ (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_company INT NOT NULL, " +
                "experience INT NOT NULL, " +
                "id_skill INT NOT NULL)";

        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {

            statement.execute(createUsersTableSQL);
            System.out.println("Tabela 'users' criada com sucesso.");
            statement.execute(createCompaniesTableSQL);
            System.out.println("Tabela 'companies' criada com sucesso");
            statement.execute(createSkillsTableSQL);
            System.out.println("Tabela'skills' criada com sucesso.");
            statement.execute(createSkillSetTableSQL);
            System.out.println("Tabela'skillset' criada com sucesso.");
            statement.execute(createExperienceTableSQL);
            System.out.println("Tabela'experience' criada com sucesso.");
            statement.execute(createJobsTableSQL);
            System.out.println("Tabela'jobs' criada com sucesso.");
            statement.execute(createJobSetTableSQL);
            System.out.println("Tabela'jobset' criada com sucesso.");
            statement.execute(createExperienceJTableSQL);
            System.out.println("Tabela'experienceJ' criada com sucesso.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao criar a tabelas.");
        }
    }

    public List<User> readUserDatabase() {
        List<User> users = new ArrayList<>();
        String query = "SELECT email, password, name, id FROM users";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");
                int id = resultSet.getInt("id");
                User user = new User(email, password, name, id);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return users;
    }

    public List<Company> readCompanyDatabase() {
        List<Company> companies = new ArrayList<>();
        String query = "SELECT email, password, name, industry, description, id FROM companies";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");
                String industry = resultSet.getString("industry");
                String description = resultSet.getString("description");
                int id = resultSet.getInt("id");
                Company company = new Company(email, password, name, id, industry, description);
                companies.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return companies;
    }

    public void writeUserDatabase(List<User> users) {
        String query = "INSERT INTO users (email, password, name, id) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE email=VALUES(email), password=VALUES(password), name=VALUES(name)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (User user : users) {
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getName());
                preparedStatement.setInt(4, user.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    public void writeCompanyDatabase(List<Company> companies) {
        String query = "INSERT INTO companies (email, password, name, industry, description, id) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE email=VALUES(email),password=VALUES(password), name=VALUES(name), industry=VALUES(industry), description=VALUES(description)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Company company : companies) {
                preparedStatement.setString(1, company.getEmail());
                preparedStatement.setString(2, company.getPassword());
                preparedStatement.setString(3, company.getName());
                preparedStatement.setString(4, company.getIndustry());
                preparedStatement.setString(5, company.getDescription());
                preparedStatement.setInt(6, company.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private void handleLogout(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        try {
            JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOGOUT_CANDIDATE", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        // Adiciona o token à lista de tokens inválidos
        invalidatedTokens.add(token);

        // Logout realizado com sucesso
        JsonObject responseJson = Request.createResponse("LOGOUT_CANDIDATE", "SUCCESS", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLogoutC(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        try {
            JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOGOUT_RECRUITER", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        // Adiciona o token à lista de tokens inválidos
        invalidatedTokens.add(token);

        // Logout realizado com sucesso
        JsonObject responseJson = Request.createResponse("LOGOUT_RECRUITER", "SUCCESS", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLookup(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        List<User> users = readUserDatabase();
        for (User user : users) {
            if (user.getId() == id) {
                // Usuário encontrado, retorna os dados do usuário
                JsonObject data = new JsonObject();
                data.addProperty("email", user.getEmail());
                data.addProperty("name", user.getName());
                data.addProperty("password", user.getPassword());
                data.addProperty("id", user.getId());

                JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "SUCCESS", "");
                responseJson.add("data", data); // Adicione esta linha
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "USER_NOT_FOUND", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLookupC(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_RECRUITER", "INVALID_TOKEN", "");
            fileWriter.write("enviado: " + responseJson + "success");
            fileWriter.newLine();
            fileWriter.flush();
            out.println(Request.toJsonString(responseJson));
            return;
        }

        List<Company> companies = readCompanyDatabase();
        for (Company company : companies) {
            if (company.getId() == id) {
                // Usuário encontrado, retorna os dados do usuário
                JsonObject data = new JsonObject();
                data.addProperty("email", company.getEmail());
                data.addProperty("name", company.getName());
                data.addProperty("password", company.getPassword());
                data.addProperty("industry", company.getIndustry());
                data.addProperty("description", company.getDescription());
                data.addProperty("id", company.getId());

                JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_RECRUITER", "SUCCESS", "");
                responseJson.add("data", data); // Adicione esta linha
                fileWriter.write("enviado: " + responseJson + "success");
                fileWriter.newLine();
                fileWriter.flush();
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_RECRUITER", "USER_NOT_FOUND", "");
        fileWriter.write("enviado: " + responseJson + "success");
        fileWriter.newLine();
        fileWriter.flush();
        out.println(Request.toJsonString(responseJson));
    }

    public static void main(String[] args) {
        int serverPort = 21234;
        createTables();

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("server_log.txt", true));
                ServerSocket serverSocket = new ServerSocket(serverPort)) {

            System.out.println("Servidor iniciado na porta " + serverPort);

            while (true) {
                System.out.println("Aguardando conexão...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket);

                new Server(clientSocket, fileWriter);
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
