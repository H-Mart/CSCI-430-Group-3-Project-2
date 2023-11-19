import GUI.ActionPanel;
import GUI.ButtonPanel;
import GUI.MainPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class ClerkMenuState implements WarehouseState {
    private static ClerkMenuState instance;

    private JFrame frame;
    private final MainPanel mainPanel;
    private final ButtonPanel buttonPanel;
    private final ActionPanel actionPanel;

    private ClerkMenuState() {
        mainPanel = new MainPanel();
        buttonPanel = new ButtonPanel();
        actionPanel = new ActionPanel();

        setDefaultLayout();
    }

    private void setDefaultLayout() {
        buttonPanel.setLayout(new GridLayout(7, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static ClerkMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClerkMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Clerk Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);

        var addClientButton = new JButton("Add Client");
        var showProductsButton = new JButton("Show Products");
        var acceptPaymentButton = new JButton("Accept Payment");
        var showProductWaitlistButton = new JButton("Show Product Waitlist");
        var switchToClientQueryButton = new JButton("Client Queries");
        var becomeClientButton = new JButton("Login as Client");
        var logoutButton = new JButton("Logout");

        addClientButton.addActionListener(e -> addClient());
        showProductsButton.addActionListener(e -> displayProducts());
        acceptPaymentButton.addActionListener(e -> acceptPayment());
        showProductWaitlistButton.addActionListener(e -> displayProductWaitlist());
        switchToClientQueryButton.addActionListener(e -> switchToClientQueryState());
        becomeClientButton.addActionListener(e -> becomeClient());
        //noinspection DuplicatedCode
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(addClientButton);
        buttonPanel.add(showProductsButton);
        buttonPanel.add(switchToClientQueryButton);
        buttonPanel.add(acceptPaymentButton);
        buttonPanel.add(showProductWaitlistButton);
        buttonPanel.add(becomeClientButton);
        buttonPanel.add(logoutButton);

        frame.add(mainPanel);
        mainPanel.addButtonPanel(buttonPanel);
        mainPanel.addActionPanel(actionPanel);
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
        @SuppressWarnings("DuplicatedCode") var clientId = JOptionPane.showInputDialog(frame, "Enter client id: ");

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
        var name = Utilities.getValidInput(frame, "Enter client name: ", "Client name cannot be empty",
                s -> !s.isEmpty());
        if (name == null) {
            return;
        }

        var address = Utilities.getValidInput(frame, "Enter client address: ", "Client address cannot be empty",
                s -> !s.isEmpty());
        if (address == null) {
            return;
        }

        var addedId = Warehouse.instance().addClient(name, address);

        if (Warehouse.instance().getClientById(addedId).isPresent()) {
            JOptionPane.showMessageDialog(frame, "Client added - " + Warehouse.instance().getClientById(addedId).get());
        } else {
            JOptionPane.showMessageDialog(frame, "Client not added", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProducts() {
         setDefaultLayout();
        var productTextArea = new JTextArea();

        var productIterator = Warehouse.instance().getProductIterator();
        while (productIterator.hasNext()) {
            var product = productIterator.next();
            productTextArea.append("Product Id: " + product.getId() + "\nProduct Name: "
                    + product.getName() + "\nProduct Price: " + product.getPrice()
                    + "\nProduct Quantity: " + product.getQuantity() + "\n\n");
        }

        var productScrollPane = new JScrollPane(productTextArea);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        actionPanel.clear();
        actionPanel.add(productScrollPane);
        frame.revalidate();
        frame.repaint();
    }

    private void displayProductWaitlist() {
        var productIdLabel = new JLabel("Product Id: ");
        var productIdField = new JTextField(10);
        var waitlistTextArea = new JTextArea();
        waitlistTextArea.setEditable(false);

        var scrollPane = new JScrollPane(waitlistTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        actionPanel.clear();

        var layout = new GroupLayout(actionPanel);
        actionPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(productIdLabel, GroupLayout.PREFERRED_SIZE,
                                                        90,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(productIdField, GroupLayout.PREFERRED_SIZE, 76,
                                                        GroupLayout.PREFERRED_SIZE))
                                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(productIdLabel, GroupLayout.PREFERRED_SIZE, 22,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(productIdField, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                                .addContainerGap(27, Short.MAX_VALUE))
        );

        productIdField.requestFocus();

        productIdField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateOutput() {
                String productId = productIdField.getText();
                Optional<Product> product = Warehouse.instance().getProductById(productId);

                waitlistTextArea.setText("Waitlist: \n\n");

                if (product.isEmpty()) {
                    waitlistTextArea.setText("Product not found");
                    return;
                }

                var waitlistIterator = product.get().getWaitlist().getIterator();
                if (!waitlistIterator.hasNext()) {
                    waitlistTextArea.setText("Product Waitlist is empty");
                    return;
                }

                while (waitlistIterator.hasNext()) {
                    var waitlistItem = waitlistIterator.next();
                    waitlistTextArea.append("Client Id: " + waitlistItem.getClientId() + "\nQuantity: "
                            + waitlistItem.getQuantity() + "\nDate:" + waitlistItem.getDate() + "\n\n");
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOutput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOutput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOutput();
            }
        });
    }

    private void acceptPayment() {
        var clientId = Utilities.getValidInput(frame, "Enter client id: ", "Client id cannot be empty",
                s -> !s.isEmpty());
        if (clientId == null) {
            return;
        }

        Optional<Client> client = Warehouse.instance().getClientById(clientId);

        if (client.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Client not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var paymentAmount = Utilities.getValidInput(frame, "Enter payment amount: ", "Payment amount must be a " +
                        "positive number",
                s -> {
                    try {
                        var n = Double.parseDouble(s);
                        return n >= 0;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
        if (paymentAmount == null) {
            return;
        }

        client.get().addToBalance(Double.parseDouble(paymentAmount));
        JOptionPane.showMessageDialog(frame,
                "Client " + client.get().getName() + " new balance: " + client.get().getBalance());
    }
}
