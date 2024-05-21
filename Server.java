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
                    fileWriter.write(jsonMessage);
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
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
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
                    System.out.println(responseJson);
                    out.println(Request.toJsonString(responseJson));
                    return;
                }
            }
        }
        // Caso ocorra qualquer erro de senha ou email
        JsonObject responseJson = Request.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
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
                    System.out.println(responseJson);
                    out.println(Request.toJsonString(responseJson));
                    return;
                }
            }
        }
        // Caso ocorra qualquer erro de senha ou email
        JsonObject responseJson = Request.createResponse("LOGIN_RECRUITER", "INVALID_LOGIN", "");
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
                // Empresa já existe
                JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");
                System.out.println(responseJson + "userexists");
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Empresa não encontrado, pode ser cadastrado
        int id = generateId();
        User newUser = new User(email, password, name, id);
        users.add(newUser);
        writeUserDatabase(users);

        // Cadastro realizado com sucesso
        JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "SUCCESS", "");
        System.out.println(responseJson + "success");
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
                // Usuário já existe
                JsonObject responseJson = Request.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");
                System.out.println(responseJson + "userexists");
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Empresa não encontrada, pode ser cadastrado
        int id = generateId();
        Company newCompany = new Company(email, password, name, id, industry, description);
        companies.add(newCompany);
        writeCompanyDatabase(companies);

        // Cadastro realizado com sucesso
        JsonObject responseJson = Request.createResponse("SIGNUP_RECRUITER", "SUCCESS", "");
        System.out.println(responseJson + "success");
        out.println(Request.toJsonString(responseJson));
    }

    private int generateId() throws IOException {
        int id;

        List<User> users = readUserDatabase();
        if (users.isEmpty()) {
            id = 0;
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
                out.println(responseJson.toString());
                return;
            }
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("operation", "UPDATE_ACCOUNT_CANDIDATE");
        responseJson.addProperty("status", "USER_NOT_FOUND");
        responseJson.add("data", new JsonObject());
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
                out.println(responseJson.toString());
                return;
            }
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("operation", "UPDATE_ACCOUNT_RECRUITER");
        responseJson.addProperty("status", "USER_NOT_FOUND");
        responseJson.add("data", new JsonObject());
        out.println(responseJson.toString());
    }

    private void handleDelete(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
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
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "USER_NOT_FOUND", "");
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "ERROR", "");
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
                out.println(Request.toJsonString(responseJson));
            } else {
                // Usuário não encontrado
                JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_RECRUITER", "USER_NOT_FOUND", "");
                out.println(Request.toJsonString(responseJson));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_RECRUITER", "ERROR", "");
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

        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {

            statement.execute(createUsersTableSQL);
            System.out.println("Tabela 'users' criada com sucesso.");
            statement.execute(createCompaniesTableSQL);
            System.out.println("Tabela 'companies' criada com sucesso");

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
            out.println(Request.toJsonString(responseJson));
            return;
        }

        // Adiciona o token à lista de tokens inválidos
        invalidatedTokens.add(token);

        // Logout realizado com sucesso
        JsonObject responseJson = Request.createResponse("LOGOUT_CANDIDATE", "SUCCESS", "");
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLogoutC(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        try {
            JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOGOUT_RECRUITER", "INVALID_TOKEN", "");
            out.println(Request.toJsonString(responseJson));
            return;
        }

        // Adiciona o token à lista de tokens inválidos
        invalidatedTokens.add(token);

        // Logout realizado com sucesso
        JsonObject responseJson = Request.createResponse("LOGOUT_RECRUITER", "SUCCESS", "");
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLookup(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
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
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "USER_NOT_FOUND", "");
        out.println(Request.toJsonString(responseJson));
    }

    private void handleLookupC(JsonObject requestJson, PrintWriter out) throws IOException {
        String token = requestJson.get("token").getAsString();
        int id;
        try {
            id = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_RECRUITER", "INVALID_TOKEN", "");
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
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_RECRUITER", "USER_NOT_FOUND", "");
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
