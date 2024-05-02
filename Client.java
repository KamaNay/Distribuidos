import java.io.*;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String serverIP = putIP(reader);

			Socket socket = new Socket(serverIP, 21234);
			System.out.println("Conectado ao servidor: " + serverIP);

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			String token = null;

			// Loop principal
			while (true) {
				System.out.println("Escolha uma operação:");
				System.out.println("1. Login");
				System.out.println("2. Cadastro");
				System.out.println("3. Atualizar");
				System.out.println("4. Dados");
				System.out.println("5. Excluir");
				System.out.println("6. Sair");

				int option;
				try {
					option = Integer.parseInt(reader.readLine());
				} catch (NumberFormatException e) {
					System.out.println("Opção inválida.");
					continue;
				}

				switch (option) {
					case 1:
						token = Login.login(reader, out, in);
						break;
					case 2:
						Singup.signup(reader, out, in);
						break;
					case 3:
						Update.update(reader, out, in, token);
						break;
					case 4:
						if (token != null) {
							Read.read(token, out, in);
						} else {
							System.out.println("Faça login primeiro para acessar os dados da conta.");
						}
						break;
					case 5:
						Delete.delete(reader, out, in, token);
						break;
					case 6:
						Logout.logout(reader, out, in, token);
						socket.close();
						return;
					default:
						System.out.println("Opção inválida.");
				}
			}

		} catch (IOException e) {
			System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
		}
	}
	// }

	public static String putIP(BufferedReader reader) throws IOException {
		System.out.println("Digite o endereço IP do servidor:");
		return reader.readLine();
	}
}
