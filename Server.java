import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Server extends Thread {

	private Socket cSocket;
	private BufferedWriter fileWriter;
	private static final String DATABASE_FILE = "user_database.txt";

	public Server(Socket cSoc, BufferedWriter writer) {
		cSocket = cSoc;
		fileWriter = writer;
		start();
	}

	public static class JWTValidator {
		private final static String TOKEN_KEY = "DISTRIBUIDOS";
		private final static Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
		private final static JWTVerifier verifier = JWT.require(algorithm).build();

		public static String generateToken(String id, String role) {
			return JWT.create()
					.withClaim("id", id)
					.withClaim("role", role)
					.sign(algorithm);
		}

		public static String getIdClaim(String token) throws JWTVerificationException {
			DecodedJWT jwt = verifier.verify(token);
			return jwt.getClaim("id").asString();
		}

		public String getRoleClaim(String token) throws JWTVerificationException {
			DecodedJWT jwt = verifier.verify(token);
			return jwt.getClaim("role").asString();
		}
	}

	private static class User {
		private String email;
		private String password;
		private String name;

		public User(String email, String password, String name) {
			this.email = email;
			this.password = password;
			this.name = name;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setPassword(String password) {
			this.password = password;
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
	}

	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;

		try {
			in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
			out = new PrintWriter(cSocket.getOutputStream(), true);

			boolean running = true;
			while (running) {
				String jsonMessage = in.readLine();

				if (jsonMessage != null) {
					fileWriter.write(jsonMessage);
					fileWriter.newLine();
					fileWriter.flush();

					JsonObject requestJson = Request.parseJson(jsonMessage);
					String operation = requestJson.get("operation").getAsString();

					switch (operation) {
						case "LOGIN_CANDIDATE":
							login(requestJson, out);
							break;
						case "SIGNUP_CANDIDATE":
							singup(requestJson, out);
							break;
						case "UPDATE_ACCOUNT_CANDIDATE":
							update(requestJson, out);
							break;
						case "DELETE_ACCOUNT_CANDIDATE":
							delete(requestJson, out);
							break;

						case "LOGOUT_CANDIDATE":
							logout(requestJson, out);
							break;

						case "LOOKUP_ACCOUNT_CANDIDATE":
							System.out.println("aaaaaaa");
							readUserData(requestJson, out);
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
				if (cSocket != null && !cSocket.isClosed())
					cSocket.close();
			} catch (IOException e) {
				System.err.println("Erro ao finalizar: " + e.getMessage());
			}
		}
	}

	private void login(JsonObject requestJson, PrintWriter out) throws IOException {
		JsonObject data = requestJson.getAsJsonObject("data");
		String email = data.get("email").getAsString();
		String password = data.get("password").getAsString();

		List<User> users = readUserDatabase();
		for (User user : users) {
			if (user.getEmail().equals(email)) {
				if (user.getPassword().equals(password)) {
					// Usuário autenticado
					String token = JWTValidator.generateToken(user.getEmail(), user.getEmail());
					JsonObject responseJson = Request.createResponse("LOGIN_CANDIDATE", "SUCCESS", token);
					out.println(Request.toJsonString(responseJson));
					return;
				} else {
					// Senha incorreta
					JsonObject responseJson = Request.createResponse("LOGIN_CANDIDATE", "INVALID_PASSWORD", "");
					out.println(Request.toJsonString(responseJson));
					return;
				}
			}
		}
		// Usuário inexistente
		JsonObject responseJson = Request.createResponse("LOGIN_CANDIDATE", "USER_NOT_FOUND", "");
		out.println(Request.toJsonString(responseJson));
	}

	private void logout(JsonObject requestJson, PrintWriter out) throws IOException {
		JsonObject data = requestJson.getAsJsonObject("data");
		String token = data.get("token").getAsString();

		try {
			@SuppressWarnings("unused")
			String userId = JWTValidator.getIdClaim(token);
			// Aqui você poderia realizar ações de logout, se necessário

			// Envie uma resposta de sucesso
			JsonObject responseJson = Request.createResponse("LOGOUT_CANDIDATE", "SUCCESS", "");
			out.println(Request.toJsonString(responseJson));
		} catch (JWTVerificationException e) {
			// Se houver um problema com o token, envie uma resposta de falha
			JsonObject responseJson = Request.createResponse("LOGOUT_CANDIDATE", "INVALID_TOKEN", "");
			out.println(Request.toJsonString(responseJson));
		}
	}

	private void readUserData(JsonObject requestJson, PrintWriter out) throws IOException {
		String token = requestJson.getAsJsonObject("data").get("token").getAsString();
        String email;
        try {
            email = JWTValidator.getIdClaim(token);
        } catch (JWTVerificationException e) {
            JsonObject responseJson = Request.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
            out.println(Request.toJsonString(responseJson));
            return;
        }

        List<User> users = readUserDatabase();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                // Usuário encontrado, retorna os dados do usuário
                JsonObject data = new JsonObject();
                data.addProperty("email", user.getEmail());
                data.addProperty("name", user.getName());
                data.addProperty("password", user.getPassword());

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

	private void singup(JsonObject requestJson, PrintWriter out) throws IOException {
		JsonObject data = requestJson.getAsJsonObject("data");
		String email = data.get("email").getAsString();
		String password = data.get("password").getAsString();
		String name = data.get("name").getAsString();

		List<User> users = readUserDatabase();
		for (User user : users) {
			if (user.getEmail().equals(email)) {
				// Usuário já existe
				JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");
				out.println(Request.toJsonString(responseJson));
				return;
			}
		}

		// Usuário não encontrado
		User newUser = new User(email, password, name);
		users.add(newUser);
		writeUserDatabase(users);

		// Cadastro efetuado
		JsonObject responseJson = Request.createResponse("SIGNUP_CANDIDATE", "SUCCESS", "");
		out.println(Request.toJsonString(responseJson));
	}

	private void update(JsonObject requestJson, PrintWriter out) throws IOException {
		JsonObject data = requestJson.getAsJsonObject("data");
		String token = data.get("token").getAsString();

		String userId = JWTValidator.getIdClaim(token);
		List<User> users = readUserDatabase();

		Optional<User> optionalUser = users.stream()
				.filter(user -> user.getEmail().equals(userId))
				.findFirst();
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			if (data.has("email")) {
				user.setEmail(data.get("email").getAsString());
			}
			if (data.has("password")) {
				user.setPassword(data.get("password").getAsString());
			}
			if (data.has("name")) {
				user.setName(data.get("name").getAsString());
			}

			writeUserDatabase(users);

			token = "";

			JsonObject responseJson = Request.createResponse("UPDATE_ACCOUNT_CANDIDATE", "SUCCESS", "");
			out.println(Request.toJsonString(responseJson));

			return;
		}

		// Usuário inválido
		JsonObject responseJson = Request.createResponse("UPDATE_ACCOUNT_CANDIDATE", "INVALID_EMAIL", "");
		out.println(Request.toJsonString(responseJson));
	}

	private void delete(JsonObject requestJson, PrintWriter out) throws IOException {
		JsonObject data = requestJson.getAsJsonObject("data");
		String email = data.get("email").getAsString();
		String token = data.get("token").getAsString();

		try {
			@SuppressWarnings("unused")
			String userId = JWTValidator.getIdClaim(token);
			List<User> users = readUserDatabase();

			if (!users.stream().anyMatch(user -> user.getEmail().equals(email))) {

				JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
				out.println(Request.toJsonString(responseJson));
				return;
			}

			for (User user : users) {
				if (user.getEmail().equals(email)) {

					users.remove(user);
					writeUserDatabase(users);

					// Exclusão efetuada
					JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "SUCCESS", "");
					out.println(Request.toJsonString(responseJson));
					return;
				}
			}
		} catch (JWTVerificationException e) {

			JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
			out.println(Request.toJsonString(responseJson));
			return;
		}

		// Usuário inexistente
		JsonObject responseJson = Request.createResponse("DELETE_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
		out.println(Request.toJsonString(responseJson));
	}

	private List<User> readUserDatabase() throws IOException {
		List<User> users = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(DATABASE_FILE))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				User user = new User(parts[0], parts[1], parts[2]);
				users.add(user);
			}
		} catch (FileNotFoundException e) {

		}
		return users;
	}

	private void writeUserDatabase(List<User> users) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
			for (User user : users) {
				String userString = user.getEmail() + "," + user.getPassword() + "," + user.getName();
				bw.write(userString);
				bw.newLine();
			}
		}
	}

	public static void main(String[] args) {
		int serverPort = 21234;

		try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("server_log.txt", true));
				ServerSocket serverSocket = new ServerSocket(serverPort)) {

			System.out.println("Servidor iniciado na porta " + serverPort);

			while (true) {
				System.out.println("Aguardando conexão na porta... " + serverPort);
				Socket cSocket = serverSocket.accept();
				System.out.println("Cliente conectado: " + cSocket);

				new Server(cSocket, fileWriter);
			}
		} catch (IOException e) {
			System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
		}
	}

}
