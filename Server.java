import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Server extends Thread {
    private Socket clientSocket;
    private BufferedWriter fileWriter;
    private static final String DATABASE_FILE = "user_database.txt";

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
                        case "SIGNUP_CANDIDATE":
                            handleSignup(requestJson, out);
                            break;
                        case "UPDATE_ACCOUNT_CANDIDATE":
                            handleUpdate(requestJson, out);
                            break;
                        case "DELETE_ACCOUNT_CANDIDATE":
                            handleDelete(requestJson, out);
                            break;
                        case "LOGOUT_CANDIDATE":
                            handleLogout(requestJson, out);
                            break;
                        case "LOOKUP_ACCOUNT_CANDIDATE":
                            handleLookup(requestJson, out);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (fileWriter != null) fileWriter.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
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

    private void handleSignup(JsonObject requestJson, PrintWriter out) throws IOException {
        JsonObject data = requestJson.getAsJsonObject("data");
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();
        String name = data.get("name").getAsString();

        List<User> users = readUserDatabase();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                // Usuário já existe
                JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");
                System.out.println(responseJson + "userexists");
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Usuário não encontrado, pode ser cadastrado
        int id = generateId();
        User newUser = new User(email, password, name, id);
        users.add(newUser);
        writeUserDatabase(users);

        // Cadastro realizado com sucesso
        JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "SUCCESS", "");
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
        int id = data.get("id").getAsInt();

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

        List<User> users = readUserDatabase();
        for (User user : users) {
            if (user.getId() == id) {
                // Remove o usuário da lista
                users.remove(user);
                writeUserDatabase(users);

                // Exclusão realizada com sucesso
                JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "SUCCESS", "");
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "USER_NOT_FOUND", "");
        out.println(Request.toJsonString(responseJson));
    }

    private List<User> readUserDatabase() throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                User user = new User(parts[0], parts[1], parts[2], Integer.valueOf(parts[3]));
                users.add(user);
            }
        } catch (FileNotFoundException e) {
            // Arquivo não existe, retorna lista vazia
        }
        return users;
    }

    private void writeUserDatabase(List<User> users) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            for (User user : users) {
                String userString = user.getEmail() + "," + user.getPassword() + "," + user.getName() + "," + user.getId();
                bw.write(userString);
                bw.newLine();
            }
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
                responseJson.add("data", data);  // Adicione esta linha
                out.println(Request.toJsonString(responseJson));
                return;
            }
        }

        // Usuário não encontrado
        JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "USER_NOT_FOUND", "");
        out.println(Request.toJsonString(responseJson));
    }


    

    public static void main(String[] args) {
        int serverPort = 21234;

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
