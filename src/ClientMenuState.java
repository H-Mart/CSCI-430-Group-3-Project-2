import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClientMenuState implements WarehouseState {
    private static ClientMenuState instance;

    JFrame frame;
    AbstractButton printDetailsButton, printProductsButton, printClientOrderHistoryButton,
            addProductsToClientWishlistButton, printClientWishlistButton, startOrderButton, logoutButton;
    JPanel mainPanel;
    JPanel buttonPanel;
    JPanel actionPanel;

    private ClientMenuState() {
        mainPanel = new JPanel();
        buttonPanel = new JPanel();
        actionPanel = new JPanel();

        setDefaultLayout();
    }

    private void setDefaultLayout() {
        mainPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.setLayout(new GridLayout(7, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static ClientMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClientMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Client Menu");
        printDetailsButton = new JButton("Print Details");
        printProductsButton = new JButton("Print Products");
        printClientOrderHistoryButton = new JButton("Print Order History");
        addProductsToClientWishlistButton = new JButton("Add Products to Wishlist");
        printClientWishlistButton = new JButton("Print Wishlist");
        startOrderButton = new JButton("Start Order");
        logoutButton = new JButton("Logout");

        printDetailsButton.addActionListener(e -> displayDetails());
        printProductsButton.addActionListener(e -> displayProducts());
        printClientOrderHistoryButton.addActionListener(e -> displayClientOrderHistory());
        addProductsToClientWishlistButton.addActionListener(e -> addProductsToClientWishlist());
        printClientWishlistButton.addActionListener(e -> displayClientWishlist());
        startOrderButton.addActionListener(e -> startOrder());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(printDetailsButton);
        buttonPanel.add(printProductsButton);
        buttonPanel.add(printClientOrderHistoryButton);
        buttonPanel.add(addProductsToClientWishlistButton);
        buttonPanel.add(printClientWishlistButton);
        buttonPanel.add(startOrderButton);
        buttonPanel.add(logoutButton);

        frame.add(mainPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(actionPanel);
        frame.setVisible(true);
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        buildGUI();
    }

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
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
        clientDetails.setLineWrap(true);
        clientDetails.setWrapStyleWord(true);

        var scrollPane = new JScrollPane(clientDetails);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.removeAll();
        actionPanel.add(scrollPane);
        actionPanel.revalidate();
        actionPanel.repaint();
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
                    + "\nProduct Price: " + product.getPrice() + "\nProduct Quantity: " + product.getQuantity() + "\n\n");
        }
        productDetails.setLineWrap(true);
        productDetails.setWrapStyleWord(true);

        var scrollPane = new JScrollPane(productDetails);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.removeAll();
        actionPanel.add(scrollPane);
        actionPanel.revalidate();
        actionPanel.repaint();
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
        setDefaultLayout();
        String clientId = WarehouseContext.currentClientId;
        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var clientWishlistIterator = client.getWishlist().getIterator();

        if (!clientWishlistIterator.hasNext()) {
            System.out.println("\nWishlist is empty");
            return;
        }

        var clientWishlist = new JTextArea();
        clientWishlist.setEditable(false);
        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            Optional<Product> product = Warehouse.instance().getProductById(wishlistItem.getProductId());

            if (product.isEmpty()) {
                System.err.println("The product with id " + wishlistItem.getProductId() + " was not found");
                return;
            }

            clientWishlist.append("Product ID: " + wishlistItem.getProductId() + "\nProduct Name: "
                    + product.get().getName() + "\nWishlist Quantity: " + wishlistItem.getQuantity() + "\n\n");
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

    /**
     * Changes the action panel to be an input form that allows the user to input a product and quantity to add to the client's wishlist
     * when the submit button is pressed, the product is added to the client's wishlist and the form is cleared
     * when the clear button is pressed, the form is cleared
     * uses a formatted text field for the input
     *
     * @precondition none
     * @postcondition if the client and product(s) exist,
     * the product(s) is/are added to the client's wishlist as a WishlistItem
     */
    public void addProductsToClientWishlist() {
        String clientId = WarehouseContext.currentClientId;
        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }


        var productIdLabel = new JLabel("Product ID: ");
        var productIdField = new JTextField();
        var productInfoLabel = new JLabel("Product Info: ");
        var productInfoField = new JTextArea();
        var quantityLabel = new JLabel("Quantity: ");
        var quantityField = new JTextField();
        var submitButton = new JButton("Submit");
        var clearButton = new JButton("Clear");

        // add document listener to productIdField to update productInfoField when productIdField is changed
        productIdField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String productId = productIdField.getText();
                Optional<Product> product = Warehouse.instance().getProductById(productId);

                if (product.isEmpty()) {
                    productInfoField.setText("Product not found");
                    return;
                }

                productInfoField.setText("Product Name: " + product.get().getName() + "\nProduct Price: " + product.get().getPrice()
                        + "\nProduct Quantity: " + product.get().getQuantity());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String productId = productIdField.getText();
                Optional<Product> product = Warehouse.instance().getProductById(productId);

                if (product.isEmpty()) {
                    productInfoField.setText("Product not found");
                    return;
                }

                productInfoField.setText("Product Name: " + product.get().getName() + "\nProduct Price: " + product.get().getPrice()
                        + "\nProduct Quantity: " + product.get().getQuantity());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String productId = productIdField.getText();
                Optional<Product> product = Warehouse.instance().getProductById(productId);

                if (product.isEmpty()) {
                    productInfoField.setText("Product not found");
                    return;
                }

                productInfoField.setText("Product Name: " + product.get().getName() + "\nProduct Price: " + product.get().getPrice());
            }
        });

        submitButton.addActionListener(e -> {
            String productId = productIdField.getText();
            String quantity = quantityField.getText();

            if (productId.isEmpty() || quantity.isEmpty()) {
                System.out.println("Please fill out all fields");
                return;
            }

            Optional<Product> product = Warehouse.instance().getProductById(productId);
            if (product.isEmpty()) {
                System.out.println("Product not found");
                return;
            }

            int quantityInt = Integer.parseInt(quantity);
            if (quantityInt <= 0) {
                System.out.println("Quantity must be positive");
                return;
            }

            client.get().addToWishlist(productId, quantityInt);
            System.out.println("Product added to wishlist");
            productIdField.setText("");
            quantityField.setText("");
        });

        clearButton.addActionListener(e -> {
            productIdField.setText("");
            quantityField.setText("");
        });

        actionPanel.setLayout(new GridLayout(6, 2, 5, 5));

        actionPanel.removeAll();

        actionPanel.add(productIdLabel);
        actionPanel.add(productIdField);
        actionPanel.add(productInfoLabel); // Placeholder for grid alignment
        actionPanel.add(productInfoField);
        actionPanel.add(quantityLabel);
        actionPanel.add(quantityField);

        // Add buttons in their own row, spanning two columns
        actionPanel.add(submitButton);
        actionPanel.add(clearButton);

        // Update the actionPanel to show the new layout and components
        actionPanel.revalidate();
        actionPanel.repaint();
    }


    private static void startOrder() {
        String clientId = WarehouseContext.currentClientId;

        if (Warehouse.instance().getClientById(clientId).isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        var client = Warehouse.instance().getClientById(clientId).get();
        var prelimOrder = new PrelimOrder(clientId);
        List<OrderItemInfo> orderInfo = orderWishlist(client, prelimOrder);

        if (orderInfo.isEmpty()) {
            System.out.println("Wishlist is empty, exiting order");
            return;
        }

        System.out.println("The order is displayed below: \n");
        double totalPrice = 0;
        int itemNumber = 1;
        for (var orderItem : orderInfo) {
            var productOrdered = Warehouse.instance().getProductById(orderItem.getProductId()).orElseThrow();
            System.out.printf("%d - Product Name: %s, Quantity: %d, Price: %.2f\n", itemNumber++, productOrdered.getName(), orderItem.getQuantity(),
                    orderItem.getPrice());
            totalPrice += orderItem.getPrice() * orderItem.getQuantity();
        }
        System.out.printf("Total Price: %.2f\n", totalPrice);

        System.out.print("Would you like to complete the order? (y/n): ");
        String input = Utilities.getUserInput();
        if (input.equalsIgnoreCase("n")) {
            return;
        }

        System.out.println("Please enter a description for the order: ");
        String description = Utilities.getUserInput();
        prelimOrder.finalizeOrder(description);
    }

    private static ArrayList<OrderItemInfo> orderWishlist(Client client, PrelimOrder currentPrelimOrder) {
        System.out.println("Ordering Products from Wishlist: ");
        var clientWishlistCopy = new Wishlist(client.getWishlist());
        var clientWishlistIterator = clientWishlistCopy.getIterator();

        if (!clientWishlistIterator.hasNext()) {
            System.out.println("\nWishlist is empty");
            return new ArrayList<>();
        }

        ArrayList<OrderItemInfo> orderItemInfoList = new ArrayList<>();
        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            Optional<Product> product = Warehouse.instance().getProductById(wishlistItem.getProductId());
            if (product.isEmpty()) {
                System.err.println("The product with id " + wishlistItem.getProductId() + " was not found");
                return orderItemInfoList;
            }
            System.out.printf("\n\tItem: %s\n\tQuantity Wishlisted: %d\n\tQuantity Available: %d\n\tPrice: %.2f\n",
                    product.get().getName(),
                    wishlistItem.getQuantity(),
                    product.get().getQuantity(),
                    product.get().getPrice());

            System.out.println("\nOptions: ");
            System.out.println("    1. Remove from wishlist");
            System.out.println("    2. Add amount in wishlist to order");
            System.out.println("    3. Add different amount to order");
            System.out.println("    4. Skip");
            System.out.print("> ");

            String input = Utilities.getUserInput();
            switch (input) {
                case "1": // remove from wishlist
                    currentPrelimOrder.addRemoveWishlistAction(wishlistItem.getProductId());
                    break;
                case "2": // add amount in wishlist to order
                    // storing information about the order item so that it can be printed later
                    orderItemInfoList.add(getOrderItemInfo(product.get(), wishlistItem.getQuantity()));
                    currentPrelimOrder.addOrderAction(wishlistItem.getProductId(), wishlistItem.getQuantity());
                    currentPrelimOrder.addRemoveWishlistAction(wishlistItem.getProductId());

                    if (product.get().getQuantity() < wishlistItem.getQuantity()) {
                        System.out.println("Order quantity exceeds product quantity. " +
                                (wishlistItem.getQuantity() - product.get().getQuantity()) + " will be added to product waitlist.");
                    }
                    break;
                case "3": // add different amount to order
                    System.out.print("\nPlease enter the amount to add to the order: ");
                    int quantity = Integer.parseInt(Utilities.getUserInput());

                    while (quantity <= 0) {
                        System.out.println("Quantity must be positive");
                        System.out.print("\nPlease enter the amount to add to the order: ");
                        quantity = Integer.parseInt(Utilities.getUserInput());
                    }

                    currentPrelimOrder.addOrderAction(wishlistItem.getProductId(), quantity);

                    if (product.get().getQuantity() < quantity) {
                        System.out.println("Order quantity exceeds product quantity. " +
                                (quantity - product.get().getQuantity()) + " will be added to product waitlist.");
                    }

                    // storing information about the order item so that it can be printed later
                    orderItemInfoList.add(getOrderItemInfo(product.get(), quantity));
                    if (quantity >= wishlistItem.getQuantity()) {
                        currentPrelimOrder.addRemoveWishlistAction(wishlistItem.getProductId());
                    } else {
                        currentPrelimOrder.addUpdateWishlistAction(wishlistItem.getProductId(),
                                wishlistItem.getQuantity() - quantity);
                    }
                    break;
                case "4": // skip
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
        return orderItemInfoList;
    }

    private static OrderItemInfo getOrderItemInfo(Product product, int orderQuantity) {
        OrderItemInfo orderItemInfo;
        if (orderQuantity <= product.getQuantity()) {
            orderItemInfo = new OrderItemInfo(product.getId(), orderQuantity, product.getPrice());
        } else {
            orderItemInfo = new OrderItemInfo(product.getId(), product.getQuantity(), product.getPrice());
        }
        return orderItemInfo;
    }
}
