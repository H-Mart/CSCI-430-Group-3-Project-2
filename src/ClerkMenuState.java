import GUI.ActionPanel;
import GUI.ButtonPanel;
import GUI.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class ClerkMenuState implements WarehouseState {
    private static ClerkMenuState instance;

    JFrame frame;
    AbstractButton addClientButton, showProductsButton,
            switchToClientQueryButton, acceptPaymentButton, becomeClientButton, logoutButton;
    MainPanel mainPanel;
    ButtonPanel buttonPanel;
    ActionPanel actionPanel;

    private ClerkMenuState() {
        mainPanel = new MainPanel();
        buttonPanel = new ButtonPanel();
        actionPanel = new ActionPanel();

        setDefaultLayout();
    }

    private void setDefaultLayout() {
        mainPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.setLayout(new GridLayout(6, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static ClerkMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClerkMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Clerk Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        addClientButton = new JButton("Add Client");
        showProductsButton = new JButton("Show Products");
        acceptPaymentButton = new JButton("Accept Payment");
        switchToClientQueryButton = new JButton("Client Queries");
        becomeClientButton = new JButton("Login as Client");
        logoutButton = new JButton("Logout");

        addClientButton.addActionListener(e -> addClient());
        showProductsButton.addActionListener(e -> displayProducts());
        acceptPaymentButton.addActionListener(e -> acceptPayment());
        switchToClientQueryButton.addActionListener(e -> switchToClientQueryState());
        becomeClientButton.addActionListener(e -> becomeClient());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(addClientButton);
        buttonPanel.add(showProductsButton);
        buttonPanel.add(switchToClientQueryButton);
        buttonPanel.add(acceptPaymentButton);
        buttonPanel.add(becomeClientButton);
        buttonPanel.add(logoutButton);

        frame.add(mainPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(actionPanel);
        frame.setVisible(true);
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
        mainPanel.clear();
        buttonPanel.clear();
        actionPanel.clear();
        buildGUI();
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

    public void switchToClientQueryState() {
        WarehouseContext.instance().changeState(WarehouseContext.CLIENTQUERY);
    }

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    public void addClient() {
        var name = JOptionPane.showInputDialog(frame, "Enter client name: ");
        var address = JOptionPane.showInputDialog(frame, "Enter client address: ");

        var addedId = Warehouse.instance().addClient(name, address);

        if (Warehouse.instance().getClientById(addedId).isPresent()) {
            JOptionPane.showMessageDialog(frame, "Client added - " + Warehouse.instance().getClientById(addedId).get());
        } else {
            JOptionPane.showMessageDialog(frame, "Client not added", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProducts() {
        var productTextArea = new JTextArea();

        var productIterator = Warehouse.instance().getProductIterator();
        while (productIterator.hasNext()) {
            var product = productIterator.next();
            productTextArea.append("Product Id: " + product.getId() + "\nProduct Name: "
                    + product.getName() + "\nProduct Price: " + product.getPrice()
                    + "\nProduct Quantity: " + product.getQuantity() + "\n\n");
        }

        actionPanel.removeAll();

        var productScrollPane = new JScrollPane(productTextArea);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        actionPanel.add(productScrollPane);

        frame.revalidate();
        frame.repaint();
    }

    private void displayProductWaitlist() {
        var productTextArea = new JTextArea();

        var productIterator = Warehouse.instance().getProductIterator();
        while (productIterator.hasNext()) {
            var product = productIterator.next();
            productTextArea.append("Product Id: " + product.getId() + "\nProuct Name: "
                    + product.getName() + "\nProduct Price: " + product.getPrice()
                    + "\nProduct Quantity: " + product.getQuantity() + "\n\n");
        }

        actionPanel.removeAll();

        var productScrollPane = new JScrollPane(productTextArea);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        actionPanel.add(productScrollPane);

        frame.revalidate();
        frame.repaint();
    }

    private void acceptPayment() {
        var clientId = JOptionPane.showInputDialog(frame, "Enter client id: ");

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Client not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var paymentAmount = JOptionPane.showInputDialog(frame, "Enter payment amount: ");

        if (Double.parseDouble(paymentAmount) < 0) {
            JOptionPane.showMessageDialog(frame, "Payment amount must be non-negative", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        client.get().addToBalance(Double.parseDouble(paymentAmount));
        JOptionPane.showMessageDialog(frame, "Client " + client.get().getName() + " new balance: " + client.get().getBalance());
    }
}
