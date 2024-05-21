import java.io.*;
import java.net.Socket;

public class Company {
    private static String token = null;

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Digite o endereço IP do servidor:");
            String serverIP = reader.readLine();

            Socket socket = new Socket(serverIP, 21234);
            System.out.println("Conectado ao servidor: " + serverIP);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Loop principal do cliente
            while (true) {
                System.out.println("Escolha uma opção:");
                System.out.println("1. Login");
                System.out.println("2. Cadastro");
                System.out.println("3. Atualização de dados");
                System.out.println("4. Exclusão de conta");
                System.out.println("5. Sair");
                System.out.println("6. Logout");
                System.out.println("7. Consultar informações da conta");
                int option;
                try {
                    option = Integer.parseInt(reader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida. Por favor, digite novamente.");
                    continue;
                }

                switch (option) {
                    case 1:
                        token = LoginC.login(reader, out, in, token);
                        break;
                    case 2:
                        SignupC.signup(reader, out, in);
                        break;
                    case 3:
                        UpdateC.update(reader, out, in, token);
                        break;
                    case 4:
                        DeleteC.delete(reader, out, in, token);
                        break;
                    case 5:
                        System.out.println("Encerrando empresa...");
                        socket.close();
                        return;
                    case 6:
                        token = LogoutC.logout(reader, out, in, token);
                        break;
                    case 7:
                        ReadC.read(reader, out, in, token);
                        break;
                    default:
                        System.out.println("Opção inválida. Por favor, digite novamente.");
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}
