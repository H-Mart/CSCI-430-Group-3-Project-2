import GUI.ActionPanel;
import GUI.ButtonPanel;
import GUI.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class ClientMenuState implements WarehouseState {
    private static ClientMenuState instance;

    private JFrame frame;

    private final MainPanel mainPanel;
    private final ButtonPanel buttonPanel;
    private final ActionPanel actionPanel;

    private ClientMenuState() {
        mainPanel = new MainPanel();
        buttonPanel = new ButtonPanel();
        actionPanel = new ActionPanel();

        setDefaultLayout();
    }

    private void setDefaultLayout() {
        buttonPanel.setLayout(new GridLayout(6, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static ClientMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClientMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Client Menu - Logged in with ID: " + WarehouseContext.currentClientId);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);

        var showDetailsButton = new JButton("Show Client Details");
        var showProductsButton = new JButton("Show Product List");
        var showClientOrderHistoryButton = new JButton("Show Client Order History");
        var showClientWishlistButton = new JButton("Show Client Wishlist");
        var switchToWishlistOperationsButton = new JButton("Wishlist Operations");
        var logoutButton = new JButton("Logout");

        showDetailsButton.addActionListener(e -> displayDetails());
        showProductsButton.addActionListener(e -> displayProducts());
        showClientOrderHistoryButton.addActionListener(e -> displayClientOrderHistory());
        showClientWishlistButton.addActionListener(e -> displayClientWishlist());
        switchToWishlistOperationsButton.addActionListener(e -> switchToWishlistOperations());
        //noinspection DuplicatedCode
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(showDetailsButton);
        buttonPanel.add(showProductsButton);
        buttonPanel.add(showClientOrderHistoryButton);
        buttonPanel.add(showClientWishlistButton);
        buttonPanel.add(switchToWishlistOperationsButton);
        buttonPanel.add(logoutButton);

        frame.add(mainPanel);
        mainPanel.addButtonPanel(buttonPanel);
        mainPanel.addActionPanel(actionPanel);
        frame.setVisible(true);
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        frame.getContentPane().removeAll();
        mainPanel.removeAll();
        buttonPanel.removeAll();
        actionPanel.removeAll();

        frame.revalidate();
        frame.repaint();
        buildGUI();
    }

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    public void switchToWishlistOperations() {
        WarehouseContext.instance().changeState(WarehouseContext.WISHLIST);
    }

    private void displayDetails() {
        setDefaultLayout();
        // displays the client details on the action panel
        String clientId = WarehouseContext.currentClientId;
        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var clientDetails = new JTextArea();
        clientDetails.setEditable(false);
        clientDetails.setText("Client ID: " + client.getId() + "\nClient Name: " + client.getName()
                + "\nClient Address: " + client.getAddress() + "\nClient Balance: " + client.getBalance());
        //noinspection DuplicatedCode
        clientDetails.setLineWrap(true);
        clientDetails.setWrapStyleWord(true);

        var scrollPane = new JScrollPane(clientDetails);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.clear();
        actionPanel.add(scrollPane);
    }

    private void displayProducts() {
        setDefaultLayout();
        // displays the products on the action panel
        var productIterator = Warehouse.instance().getProductIterator();
        var productDetails = new JTextArea();
        productDetails.setEditable(false);
        while (productIterator.hasNext()) {
            var product = productIterator.next();
            productDetails.append("Product ID: " + product.getId() + "\nProduct Name: " + product.getName()
                    + "\nProduct Price: " + product.getPrice() + "\nProduct Quantity: " + product.getQuantity() + "\n" +
                    "\n");
        }
        //noinspection DuplicatedCode
        productDetails.setLineWrap(true);
        productDetails.setWrapStyleWord(true);

        var scrollPane = new JScrollPane(productDetails);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.clear();
        actionPanel.add(scrollPane);
    }

    private void displayClientOrderHistory() {
        setDefaultLayout();
        // displays the client order history on the action panel
        String clientId = WarehouseContext.currentClientId;

        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var clientTransactionIterator = client.getTransactionList().getIterator();
        if (!clientTransactionIterator.hasNext()) {
            System.out.println("\nOrder history is empty");
            return;
        }

        var clientOrderHistory = new JTextArea();
        clientOrderHistory.setEditable(false);

        while (clientTransactionIterator.hasNext()) {
            var transactionRecord = clientTransactionIterator.next();
            clientOrderHistory.append("Date: " + transactionRecord.getDate() + "\nDescription: " + transactionRecord.getDescription()
                    + "\nTotal Price: " + transactionRecord.getTotalCost() + "\n\n");
            var invoiceIterator = transactionRecord.getInvoice().getIterator();
            clientOrderHistory.append("Invoice: \n");
            while (invoiceIterator.hasNext()) {
                var invoiceItem = invoiceIterator.next();
                clientOrderHistory.append("\tProduct ID: " + invoiceItem.getProductId() + "\n\tQuantity: " + invoiceItem.getQuantity()
                        + "\n\tPrice: " + invoiceItem.getPrice() + "\n\n");
            }
        }

        //noinspection DuplicatedCode
        clientOrderHistory.setLineWrap(true);
        clientOrderHistory.setWrapStyleWord(true);

        var scrollPane = new JScrollPane(clientOrderHistory);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.removeAll();
        actionPanel.add(scrollPane);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void displayClientWishlist() {
        //noinspection DuplicatedCode
        setDefaultLayout();
        String clientId = WarehouseContext.currentClientId;
        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var clientWishlistIterator = client.getWishlist().getIterator();

        var wishlistIsEmpty = true;
        var clientWishlist = new JTextArea();
        clientWishlist.setEditable(false);
        while (clientWishlistIterator.hasNext()) {
            wishlistIsEmpty = false;
            var wishlistItem = clientWishlistIterator.next();
            Optional<Product> product = Warehouse.instance().getProductById(wishlistItem.getProductId());

            if (product.isEmpty()) {
                System.err.println("The product with id " + wishlistItem.getProductId() + " was not found");
                return;
            }

            clientWishlist.append("Product ID: " + wishlistItem.getProductId() + "\nProduct Name: "
                    + product.get().getName() + "\nWishlist Quantity: " + wishlistItem.getQuantity() + "\n\n");
        }

        if (wishlistIsEmpty) {
            clientWishlist.append("Wishlist is empty");
        }

        clientWishlist.setLineWrap(true);
        clientWishlist.setWrapStyleWord(true);

        var scrollPane = new JScrollPane(clientWishlist);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.removeAll();
        actionPanel.add(scrollPane);
        actionPanel.revalidate();
        actionPanel.repaint();
    }
}
