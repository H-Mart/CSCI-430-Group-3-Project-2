import GUI.ActionPanel;
import GUI.ButtonPanel;
import GUI.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Objects;
import java.util.function.Predicate;

public class ClientInfoState implements WarehouseState {
    private static ClientInfoState instance;

    JFrame frame;
    AbstractButton showClientsButton, showClientsWithOutstandingBalanceButton, showClientsWithNoTransactionsButton, logoutButton;

    MainPanel mainPanel;
    ButtonPanel buttonPanel;
    ActionPanel actionPanel;

    private ClientInfoState() {
        mainPanel = new MainPanel();
        buttonPanel = new ButtonPanel();
        actionPanel = new ActionPanel();

        setDefaultLayout();
    }

    private void setDefaultLayout() {
        mainPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.setLayout(new GridLayout(4, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static ClientInfoState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClientInfoState());
    }

    private void buildGUI() {
        frame.setTitle("Manager Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        showClientsButton = new JButton("Show Clients");
        showClientsWithOutstandingBalanceButton = new JButton("Show Clients With Outstanding Balance");
        showClientsWithNoTransactionsButton = new JButton("Show Clients With No Transactions In The Last Six Months");
        logoutButton = new JButton("Logout");

        showClientsButton.addActionListener(e -> showClients());
        showClientsWithOutstandingBalanceButton.addActionListener(e -> showClientsWithOutstandingBalance());
        showClientsWithNoTransactionsButton.addActionListener(e -> showClientsWithNoTransactions());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(showClientsButton);
        buttonPanel.add(showClientsWithOutstandingBalanceButton);
        buttonPanel.add(showClientsWithNoTransactionsButton);
        buttonPanel.add(logoutButton);

        frame.add(mainPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(actionPanel);
        frame.setVisible(true);
    }

    public void showClients(Predicate<Client> clientPredicate) {
        setDefaultLayout();
        var clientDetails = new JTextArea();
        clientDetails.setEditable(false);
        var clientIterator = Warehouse.instance().getClientIterator();
        boolean hasClients = false;
        while (clientIterator.hasNext()) {
            hasClients = true;
            var client = clientIterator.next();
            if (!clientPredicate.test(client)) {
                continue;
            }
            clientDetails.append("Client ID: " + client.getId() + "\nClient Name: " + client.getName()
                    + "\nClient Address: " + client.getAddress() + "\nClient Balance: " + client.getBalance() + "\n\n");
        }

        if (!hasClients) {
            clientDetails.append("No clients found");
        }
        clientDetails.setLineWrap(true);
        clientDetails.setWrapStyleWord(true);

        var scrollPane = new JScrollPane(clientDetails);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.clear();
        actionPanel.add(scrollPane);
    }

    public void showClients() {
        showClients(client -> true);
    }

    public void showClientsWithOutstandingBalance() {
        showClients(client -> client.getBalance() < 0);
    }

    public void showClientsWithNoTransactions() {
        showClients(client -> {
            var transactionIterator = client.getTransactionList().getIterator();
            boolean hasTransactions = false;
            while (transactionIterator.hasNext()) {
                var transaction = transactionIterator.next();
                var c = Calendar.getInstance();
                c.add(Calendar.MONTH, -6);
                if (transaction.getDate().after(c.getTime())) {
                    hasTransactions = true;
                    break;
                }
            }
            return !hasTransactions;
        });
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

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.CLERK);
    }

}
