import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class ClerkMenuState implements WarehouseState {
    private static ClerkMenuState instance;

    JFrame frame;
    AbstractButton addClientButton, printProductsButton, printClientsButton,
            printClientsWithOutstandingBalanceButton, acceptPaymentButton, becomeClientButton, logoutButton;
    JPanel mainPanel;
    JPanel buttonPanel;
    JPanel actionPanel;

    private ClerkMenuState() {
        mainPanel = new JPanel();
        buttonPanel = new JPanel();
        actionPanel = new JPanel();

        mainPanel.setLayout(new GridLayout(1, 2, 10, 0));
        buttonPanel.setLayout(new GridLayout(7, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static ClerkMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClerkMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Clerk Menu");
        addClientButton = new JButton("Add Client");
        printProductsButton = new JButton("Print Products");
        printClientsButton = new JButton("Print Clients");
        printClientsWithOutstandingBalanceButton = new JButton("Print Clients with Outstanding Balance");
        acceptPaymentButton = new JButton("Accept Payment");
        becomeClientButton = new JButton("Become Client");
        logoutButton = new JButton("Logout");

        addClientButton.addActionListener(e -> addClient());
        printProductsButton.addActionListener(e -> printProducts());
        printClientsButton.addActionListener(e -> printClients());
        printClientsWithOutstandingBalanceButton.addActionListener(e -> printClientsWithOutstandingBalance());
        acceptPaymentButton.addActionListener(e -> acceptPayment());
        becomeClientButton.addActionListener(e -> becomeClient());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(addClientButton);
        buttonPanel.add(printProductsButton);
        buttonPanel.add(printClientsButton);
        buttonPanel.add(printClientsWithOutstandingBalanceButton);
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
        mainPanel.removeAll();
        buttonPanel.removeAll();
        actionPanel.removeAll();
        buildGUI();
    }

    private void becomeClient() {
        var clientId = JOptionPane.showInputDialog(frame, "Enter client id: ");

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Client not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        WarehouseContext.instance().setCurrentClientId(clientId);
        WarehouseContext.instance().setLogin(WarehouseContext.CLIENT);
        WarehouseContext.instance().changeState(WarehouseContext.CLIENT);
    }

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    public static void addClient() {
        System.out.print("Enter client name: ");
        String name = Utilities.getUserInput();

        System.out.print("\nEnter client address: ");
        String address = Utilities.getUserInput();

        String addedId = Warehouse.instance().addClient(name, address);

        if (Warehouse.instance().getClientById(addedId).isPresent()) {
            System.out.println("\nClient added - " + Warehouse.instance().getClientById(addedId).get());
        } else {
            System.out.println("\nClient not added");
        }
    }

    public static void printClients() {
        var idHeader = "ID";
        var nameHeader = "Name";
        var addressHeader = "Address";
        var balanceHeader = "Balance";

        var maxIdLength = idHeader.length();
        var maxNameLength = nameHeader.length();
        var maxAddressLength = addressHeader.length();
        var maxBalanceLength = balanceHeader.length();

        var clientIterator = Warehouse.instance().getClientIterator();
        while (clientIterator.hasNext()) {
            var client = clientIterator.next();
            if (client.getId().length() > maxIdLength) {
                maxIdLength = client.getId().length();
            }
            if (client.getName().length() > maxNameLength) {
                maxNameLength = client.getName().length();
            }
            if (client.getAddress().length() > maxAddressLength) {
                maxAddressLength = client.getAddress().length();
            }
            if (Double.toString(client.getBalance()).length() > maxBalanceLength) {
                maxBalanceLength = Double.toString(client.getBalance()).length();
            }
        }

        // default padding for each column, to be added to the left and right side of the headers
        var idPadding = 3;
        var namePadding = 3;
        var addressPadding = 3;
        var balancePadding = 3;

        // adjust column width to fit the longest string in the column plus the padding
        var idColWidth = idPadding * 2 + maxIdLength;
        var nameColWidth = namePadding * 2 + maxNameLength;
        var addressColWidth = addressPadding * 2 + maxAddressLength;
        var balanceColWidth = balancePadding * 2 + maxBalanceLength;

        // using the whole column width, calculate the amount of padding needed for each header
        var idHeaderPadding = (idColWidth - idHeader.length()) / 2;
        var nameHeaderPadding = (nameColWidth - nameHeader.length()) / 2;
        var addressHeaderPadding = (addressColWidth - addressHeader.length()) / 2;
        var balanceHeaderPadding = (balanceColWidth - balanceHeader.length()) / 2;

        // recalculate the column width to account for the header length and padding
        idColWidth = idHeaderPadding * 2 + idHeader.length();
        nameColWidth = nameHeaderPadding * 2 + nameHeader.length();
        addressColWidth = addressHeaderPadding * 2 + addressHeader.length();
        balanceColWidth = balanceHeaderPadding * 2 + balanceHeader.length();

        String horizontalLine = "-".repeat(idColWidth + nameColWidth + addressColWidth + balanceColWidth + 5);
        System.out.println("Client List: ");
        System.out.println(horizontalLine);

// @formatter:off
        System.out.printf("|%" + idHeaderPadding       + "s" + "%s"  + "%-" +  idHeaderPadding      + "s" +
                          "|%" + nameHeaderPadding     + "s" + "%s"  + "%-" +  nameHeaderPadding    + "s" +
                          "|%" + addressHeaderPadding  + "s" + "%s"  + "%-" +  addressHeaderPadding + "s" +
                          "|%" + balanceHeaderPadding  + "s" + "%s"  + "%-" +  balanceHeaderPadding + "s" + "|\n",
                " ", idHeader, " ",
                " ", nameHeader, " ",
                " ", addressHeader, " ",
                " ", balanceHeader, " ");
// @formatter:on

        System.out.println(horizontalLine);

        clientIterator = Warehouse.instance().getClientIterator();
        while (clientIterator.hasNext()) {
            var client = clientIterator.next();
            // subtract 1 from the column width to account for a space between the column and the border
            System.out.printf("|%" + (idColWidth - 1) + "s | %-" +
                            (nameColWidth - 1) + "s| %-" + (addressColWidth - 1) + "s| %" + (balanceColWidth - 1) + ".2f|\n",
                    client.getId(), client.getName(), client.getAddress(), client.getBalance());
        }
        System.out.println(horizontalLine);
    }

    /*
     * Prints all products in the ordering system in a nicely organized table
     */
    public static void printProducts() {
        var idHeader = "ID";
        var nameHeader = "Name";
        var priceHeader = "Price";
        var quantityHeader = "Quantity";

        var maxIdLength = idHeader.length();
        var maxNameLength = nameHeader.length();
        var maxPriceLength = priceHeader.length();
        var maxQuantityLength = quantityHeader.length();

        var productIterator = Warehouse.instance().getProductIterator();
        while (productIterator.hasNext()) {
            var product = productIterator.next();
            if (product.getId().length() > maxIdLength) {
                maxIdLength = product.getId().length();
            }
            if (product.getName().length() > maxNameLength) {
                maxNameLength = product.getName().length();
            }
            if (Double.toString(product.getPrice()).length() > maxPriceLength) {
                maxPriceLength = Double.toString(product.getPrice()).length();
            }
            if (Integer.toString(product.getQuantity()).length() > maxQuantityLength) {
                maxQuantityLength = Integer.toString(product.getQuantity()).length();
            }
        }

        // default padding for each column, to be added to the left and right side of the headers
        var idPadding = 3;
        var namePadding = 3;
        var pricePadding = 3;
        var quantityPadding = 3;

        // adjust column width to fit the longest string in the column plus the padding
        var idColWidth = idPadding * 2 + maxIdLength;
        var nameColWidth = namePadding * 2 + maxNameLength;
        var priceColWidth = pricePadding * 2 + maxPriceLength;
        var quantityColWidth = quantityPadding * 2 + maxQuantityLength;

        // using the whole column width, calculate the amount of padding needed for each header
        var idHeaderPadding = (idColWidth - idHeader.length()) / 2;
        var nameHeaderPadding = (nameColWidth - nameHeader.length()) / 2;
        var priceHeaderPadding = (priceColWidth - priceHeader.length()) / 2;
        var quantityHeaderPadding = (quantityColWidth - quantityHeader.length()) / 2;

        // recalculate the column width to account for the header length and padding
        idColWidth = idHeaderPadding * 2 + idHeader.length();
        nameColWidth = nameHeaderPadding * 2 + nameHeader.length();
        priceColWidth = priceHeaderPadding * 2 + priceHeader.length();
        quantityColWidth = quantityHeaderPadding * 2 + quantityHeader.length();

        String horizontalLine = "-".repeat(idColWidth + nameColWidth + priceColWidth + quantityColWidth + 5);
        System.out.println("Product List: ");
        System.out.println(horizontalLine);

// @formatter:off
        System.out.printf("|%" + idHeaderPadding       + "s" + "%s"  + "%-" + idHeaderPadding       + "s" +
                          "|%" + nameHeaderPadding     + "s" + "%s"  + "%-" + nameHeaderPadding     + "s" +
                          "|%" + priceHeaderPadding    + "s" + "%s"  + "%-" + priceHeaderPadding    + "s" +
                          "|%" + quantityHeaderPadding + "s" + "%s"  + "%-" + quantityHeaderPadding + "s" + "|\n",
                " ", idHeader, " ",
                " ", nameHeader, " ",
                " ", priceHeader, " ",
                " ", quantityHeader, " ");
// @formatter:on

        System.out.println(horizontalLine);

        productIterator = Warehouse.instance().getProductIterator();
        while (productIterator.hasNext()) {
            var product = productIterator.next();
            System.out.printf("|%" + (idColWidth - 1) + "s | %-" + (nameColWidth - 1) + "s|%" + (priceColWidth - 1) +
                            ".2f |%" + (quantityColWidth - 1) + "s |\n",
                    product.getId(), product.getName(), product.getPrice(), product.getQuantity());
        }
        System.out.println(horizontalLine);
    }

    public void printClientsWithOutstandingBalance() {
        Iterator<Client> clientIterator = Warehouse.instance().getClientIterator();
        while (clientIterator.hasNext()) {
            Client client = clientIterator.next();
            if (client.getBalance() < 0) {
                System.out.printf("Client ID: %s\nClient Name: %s\nClient Balance: %.2f\n\n",
                        client.getId(), client.getName(), client.getBalance());
            }
        }
    }

    public void acceptPayment() {
        System.out.print("Enter client id: ");
        String clientId = Utilities.getUserInput();

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        System.out.printf("Client %s current balance: %.2f\n", client.get().getName(), client.get().getBalance());

        System.out.print("Enter payment amount: ");
        double paymentAmount = Double.parseDouble(Utilities.getUserInput());

        if (paymentAmount < 0) {
            System.out.println("Payment amount must be non-negative");
            return;
        }

        client.get().addToBalance(paymentAmount);
        System.out.printf("Client %s new balance: %.2f\n", client.get().getName(), client.get().getBalance());
    }
}
