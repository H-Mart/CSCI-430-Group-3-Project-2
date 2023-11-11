import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

public class LoginState implements WarehouseState {
    private static LoginState instance;

    JFrame frame;
    AbstractButton clientButton, clerkButton, managerButton, exitButton;

    private LoginState() {
        frame = new JFrame();
    }

    public static LoginState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new LoginState());
    }

    private void becomeClient() {
        var clientId = JOptionPane.showInputDialog(frame, "Enter client id: ");

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (clientId == null) {
            return;
        } else if (client.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Client not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        WarehouseContext.instance().setCurrentClientId(clientId);
        WarehouseContext.instance().setLogin(WarehouseContext.CLIENT);
        WarehouseContext.instance().changeState(WarehouseContext.CLIENT);
    }

    private void becomeClerk() {
        WarehouseContext.instance().setLogin(WarehouseContext.CLERK);
        WarehouseContext.instance().changeState(WarehouseContext.CLERK);
    }

    private void becomeManager() {
        WarehouseContext.instance().setLogin(WarehouseContext.MANAGER);
        WarehouseContext.instance().changeState(WarehouseContext.MANAGER);
    }

    private void exit() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    private void buildGUI() {
        frame.setTitle("Login Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 400);
        frame.setLocationRelativeTo(null);

        clientButton = new JButton("Client");
        clerkButton = new JButton("Clerk");
        managerButton = new JButton("Manager");
        exitButton = new JButton("Exit");

        clientButton.addActionListener(e -> becomeClient());
        clerkButton.addActionListener(e -> becomeClerk());
        managerButton.addActionListener(e -> becomeManager());
        exitButton.addActionListener(e -> exit());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clientButton);
        buttonPanel.add(clerkButton);
        buttonPanel.add(managerButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel);
        frame.setVisible(true);
        frame.pack();
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        buildGUI();
    }
}