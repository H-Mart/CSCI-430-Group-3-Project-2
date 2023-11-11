import GUI.ActionPanel;
import GUI.ButtonPanel;
import GUI.MainPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WishlistOperationsState implements WarehouseState {
    private static WishlistOperationsState instance;

    JFrame frame;
    AbstractButton viewWishlistButton, addProductsToWishlistButton, removeProductsFromWishlistButton,
            changeQuantityOfProductsInWishlistButton, orderProductsInWishlistButton, exitButton;
    MainPanel mainPanel;
    ButtonPanel buttonPanel;
    ActionPanel actionPanel;

    private WishlistOperationsState() {
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

    public static WishlistOperationsState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new WishlistOperationsState());
    }

    private void buildGUI() {
        frame.setTitle("Client Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        viewWishlistButton = new JButton("View Wishlist");
        addProductsToWishlistButton = new JButton("Add Products to Wishlist");
        removeProductsFromWishlistButton = new JButton("Remove Products from Wishlist");
        changeQuantityOfProductsInWishlistButton = new JButton("Change Quantity of Products in Wishlist");
        orderProductsInWishlistButton = new JButton("Order Products in Wishlist");
        exitButton = new JButton("Exit");

        viewWishlistButton.addActionListener(e -> displayClientWishlist());
        addProductsToWishlistButton.addActionListener(e -> addProductsToClientWishlist());
        removeProductsFromWishlistButton.addActionListener(e -> removeProductsFromClientWishlist());
        orderProductsInWishlistButton.addActionListener(e -> startOrder());
        exitButton.addActionListener(e -> exit());

        buttonPanel.add(viewWishlistButton);
        buttonPanel.add(addProductsToWishlistButton);
        buttonPanel.add(removeProductsFromWishlistButton);
        buttonPanel.add(changeQuantityOfProductsInWishlistButton);
        buttonPanel.add(orderProductsInWishlistButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel);
        mainPanel.add(actionPanel);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        mainPanel.removeAll();
        buttonPanel.removeAll();
        actionPanel.removeAll();
        buildGUI();
    }

    public void exit() {
        WarehouseContext.instance().changeState(WarehouseContext.CLIENT);
    }

    private void startOrder() {
        new OrderDialog(frame);
        System.out.println("hi");
    }

    private void displayClientWishlist() {
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
//        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionPanel.clear();
        actionPanel.add(scrollPane);
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
        setDefaultLayout();
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
            private void updateOutput(DocumentEvent e) {
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
            public void insertUpdate(DocumentEvent e) {
                updateOutput(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOutput(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOutput(e);
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

        actionPanel.clear();

        actionPanel.add(productIdLabel);
        actionPanel.add(productIdField);
        actionPanel.add(productInfoLabel); // Placeholder for grid alignment
        actionPanel.add(productInfoField);
        actionPanel.add(quantityLabel);
        actionPanel.add(quantityField);

        // Add buttons in their own row, spanning two columns
        actionPanel.add(submitButton);
        actionPanel.add(clearButton);
    }

    private void removeProductsFromClientWishlist() {
        setDefaultLayout();
        String clientId = WarehouseContext.currentClientId;
        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var wishlistSize = client.getWishlist().size();

        var itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(wishlistSize, 2, 5, 5));

        var clientWishlistIterator = client.getWishlist().getIterator();
        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            Product product = Warehouse.instance().getProductById(wishlistItem.getProductId()).orElseThrow();

            var productInfoArea = new JTextArea("Product ID: " + wishlistItem.getProductId() + "\nProduct Name: "
                    + product.getName()
                    + "\nWishlist Quantity: " + wishlistItem.getQuantity());
            productInfoArea.setEditable(false);
            var productCheckBox = new JCheckBox("Remove");
            itemPanel.add(productInfoArea);
            itemPanel.add(productCheckBox);
        }

        var scrollPane = new JScrollPane(itemPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // todo add removal action
            System.out.println("Remove button pressed");
        });

        actionPanel.clear();

        actionPanel.add(scrollPane);
        actionPanel.add(submitButton);
    }

    private static void startOrder2() {
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
