import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientGUI extends JFrame {
    private JTextField serverIPField;
    private JComboBox<String> userTypeCombo;
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton connectButton;
    private JButton sendButton;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String token = null;
    private int type = 0;

    public ClientGUI() {
        setTitle("Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup GUI components
        serverIPField = new JTextField(15);
        userTypeCombo = new JComboBox<>(new String[] { "Cliente", "Empresa" });
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        inputField = new JTextField(20);
        connectButton = new JButton("Conectar");
        sendButton = new JButton("Enviar");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("IP do Servidor:"));
        topPanel.add(serverIPField);
        topPanel.add(new JLabel("Tipo de Usuário:"));
        topPanel.add(userTypeCombo);
        topPanel.add(connectButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(inputField);
        bottomPanel.add(sendButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Setup action listeners
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest();
            }
        });
    }

    private void connectToServer() {
        String serverIP = serverIPField.getText();
        type = userTypeCombo.getSelectedIndex() + 1; // 1 for Cliente, 2 for Empresa
        try {
            socket = new Socket(serverIP, 21234);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            outputArea.append("Conectado ao servidor: " + serverIP + "\n");
            if (type == 1) {
                outputArea.append("Escolha uma opção:\n");
                outputArea.append("1. Login\n");
                outputArea.append("2. Cadastro\n");
                outputArea.append("3. Atualização de dados\n");
                outputArea.append("4. Exclusão de conta\n");
                outputArea.append("5. Sair\n");
                outputArea.append("6. Logout\n");
                outputArea.append("7. Consultar informações da conta\n");
                outputArea.append("8. Inserir habilidade\n");
                outputArea.append("9. Consultar uma habilidade\n");
                outputArea.append("10. Consultar habilidades\n");
                outputArea.append("11. Excluir uma habilidade\n");
                outputArea.append("12. Atualizar uma habilidade\n");
                outputArea.append("13. Ver vagas\n");
            } else {
                outputArea.append("Escolha uma opção:\n");
                outputArea.append("1. Login\n");
                outputArea.append("2. Cadastro\n");
                outputArea.append("3. Atualização de dados\n");
                outputArea.append("4. Exclusão de conta\n");
                outputArea.append("5. Sair\n");
                outputArea.append("6. Logout\n");
                outputArea.append("7. Consultar informações da conta\n");
                outputArea.append("8. Incluir vaga\n");
                outputArea.append("9. Consultar uma vaga\n");
                outputArea.append("10. Consultar vagas\n");
                outputArea.append("11. Excluir uma vaga\n");
                outputArea.append("12. Atualizar uma vaga\n");
                outputArea.append("13. Definir disponibilidade de busca\n");
                outputArea.append("14. Definir disponibilidade\n");
                outputArea.append("15. Buscar candidatos\n");
                outputArea.append("16. Escolher candidatos\n");
            }
        } catch (IOException e) {
            outputArea.append("Erro ao conectar ao servidor: " + e.getMessage() + "\n");
        }
    }

    private void sendRequest() {
        String request = inputField.getText();
        inputField.setText("");
        switch (request) {
            case "1": // Login
                if (type == 1) {
                    token = Login.login(this, out, in, token);
                } else {
                    token = LoginC.login(this, out, in, token);
                }
                break;
            case "2": // Cadastro
                if (type == 1) {
                    Signup.signup(this, out, in);
                } else {
                    SignupC.signup(this, out, in);
                }
                break;
            case "3": // Atualização de dados
                if (type == 1) {
                    Update.update(this, out, in, token);
                } else {
                    UpdateC.update(this, out, in, token);
                }
                break;
            case "4": // Exclusão de conta
                if (type == 1) {
                    Delete.delete(this, out, in, token);
                } else {
                    DeleteC.delete(this, out, in, token);
                }
                break;
            case "5": // Sair
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
                break;
            case "6": // Logout
                if (type == 1) {
                    token = Logout.logout(this, out, in, token);
                } else {
                    token = LogoutC.logout(this, out, in, token);
                }
                break;
            case "7": // Consultar informações da conta
                if (type == 1) {
                    Read.read(this, out, in, token);
                } else {
                    ReadC.read(this, out, in, token);
                }
                break;
            case "8": // Inserir habilidade/vaga
                if (type == 1) {
                    IncludeSkill.read(this, out, in, token);
                } else {
                    IncludeJob.read(this, out, in, token);
                }
                break;
            case "9": // Consultar uma habilidade/vaga
                if (type == 1) {
                    ReadSkill.read(this, out, in, token);
                } else {
                    ReadJob.read(this, out, in, token);
                }
                break;
            case "10": // Consultar habilidades/vagas
                if (type == 1) {
                    ReadAllSkill.read(this, out, in, token);
                } else {
                    ReadAllJob.read(this, out, in, token);
                }
                break;
            case "11": // Excluir uma habilidade/vaga
                if (type == 1) {
                    DeleteSkill.delete(this, out, in, token);
                } else {
                    DeleteJob.delete(this, out, in, token);
                }
                break;
            case "12": // Atualizar uma habilidade/vaga
                if (type == 1) {
                    UpdateSkill.update(this, out, in, token);
                } else {
                    UpdateJob.update(this, out, in, token);
                }
                break;
            case "13": // Ver vagas
                if (type == 1) {
                    SearchJob.searchJob(this, out, in, token);
                } else {
                    SetJobSearchable.set(this, out, in, token);
                }
                break;
            case "14":
                SetJobAvailable.set(this, out, in, token);
                break;
            case "15":
                SearchCandidate.search(this, out, in, token);
                break;
            case "16":
                ChooseCandidate.read(this, out, in, token);
                break;
            default:
                outputArea.append("Opção inválida. Por favor, digite novamente.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI().setVisible(true);
            }
        });
    }
}