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
        buttonPanel.setLayout(new GridLayout(6, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static WishlistOperationsState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new WishlistOperationsState());
    }

    private void buildGUI() {
        frame.setTitle("Client Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);

        viewWishlistButton = new JButton("View Wishlist");
        addProductsToWishlistButton = new JButton("Add Products to Wishlist");
        removeProductsFromWishlistButton = new JButton("Remove Products from Wishlist");
        changeQuantityOfProductsInWishlistButton = new JButton("Change Quantity of Products in Wishlist");
        orderProductsInWishlistButton = new JButton("Order Products in Wishlist");
        exitButton = new JButton("Go Back");

        viewWishlistButton.addActionListener(e -> displayClientWishlist());
        addProductsToWishlistButton.addActionListener(e -> addProductsToClientWishlist());
        removeProductsFromWishlistButton.addActionListener(e -> removeProductsFromClientWishlist());
        changeQuantityOfProductsInWishlistButton.addActionListener(e -> changeQuantityOfProductsInWishlist());
        orderProductsInWishlistButton.addActionListener(e -> startOrder());
        exitButton.addActionListener(e -> exit());

        buttonPanel.add(viewWishlistButton);
        buttonPanel.add(addProductsToWishlistButton);
        buttonPanel.add(removeProductsFromWishlistButton);
        buttonPanel.add(changeQuantityOfProductsInWishlistButton);
        buttonPanel.add(orderProductsInWishlistButton);
        buttonPanel.add(exitButton);

        mainPanel.addButtonPanel(buttonPanel);
        mainPanel.addActionPanel(actionPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        mainPanel.clear();
        buttonPanel.clear();
        actionPanel.clear();
        buildGUI();
    }

    public void exit() {
        WarehouseContext.instance().changeState(WarehouseContext.CLIENT);
    }

    private void startOrder() {
        String clientId = WarehouseContext.currentClientId;
        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var wishlistSize = client.getWishlist().size();

        var layout = new GroupLayout(actionPanel);
        actionPanel.setLayout(layout);

        var itemPanel = getOrderedItemPanel(wishlistSize, client);

        var scrollPane = new JScrollPane(itemPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var submitButton = new JButton("Submit");
//        submitButton.addActionListener(e -> {
//            for (var component : itemPanel.getComponents()) {
//                if (component instanceof OrderQuantityPanel) {
//                    ((OrderQuantityPanel) component).getWishlistItem().setQuantity(
//                            ((OrderQuantityPanel) component).getQuantity());
//                }
//            }
//        });

        var resetButton = new JButton("Reset");

        actionPanel.clear();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(126, Short.MAX_VALUE)
                                .addComponent(submitButton, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE,
                                        GroupLayout.DEFAULT_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(submitButton)
                                        .addComponent(resetButton))
                                .addContainerGap(12, Short.MAX_VALUE))
        );
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
     * Changes the action panel to be an input form that allows the user to input a product and quantity to add to
     * the client's wishlist
     * when the submit button is pressed, the product is added to the client's wishlist and the form is cleared
     * when the clear button is pressed, the form is cleared
     * uses a formatted text field for the input
     *
     * @precondition none
     * @postcondition if the client and product(s) exist,
     * the product(s) is/are added to the client's wishlist as a WishlistItem
     */
    public void addProductsToClientWishlist() {
//        setDefaultLayout();
        String clientId = WarehouseContext.currentClientId;
        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        // todo make this an error debug message
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

        // disable submit button and quantity field until productIdField is filled with a valid product id
        submitButton.setEnabled(false);
        quantityField.setEnabled(false);
        // add document listener to productIdField to update productInfoField when productIdField is changed
        productIdField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateOutput(DocumentEvent e) {
                String productId = productIdField.getText();
                Optional<Product> product = Warehouse.instance().getProductById(productId);

                if (product.isEmpty()) {
                    productInfoField.setText("Product not found");
                    submitButton.setEnabled(false);
                    quantityField.setEnabled(false);
                    return;
                } else {
                    submitButton.setEnabled(validQuantity(quantityField.getText()));
                    quantityField.setEnabled(true);
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

        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                submitButton.setEnabled(validQuantity(quantityField.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                submitButton.setEnabled(validQuantity(quantityField.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                submitButton.setEnabled(validQuantity(quantityField.getText()));
            }
        });

        submitButton.addActionListener(e -> {
            String productId = productIdField.getText();
            String quantity = quantityField.getText();

            if (productId.isEmpty() || quantity.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill out all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Optional<Product> product = Warehouse.instance().getProductById(productId);
            if (product.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Product not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantityInt = Integer.parseInt(quantity);
            if (quantityInt <= 0) {
                JOptionPane.showMessageDialog(frame, "Quantity must be positive", "Error", JOptionPane.ERROR_MESSAGE);
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
        String clientId = WarehouseContext.currentClientId;
        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var layout = new GroupLayout(actionPanel);
        actionPanel.setLayout(layout);

        var itemPanel = new JPanel();

        setRemoveWishlistItemPanel(itemPanel, client.getWishlist().size(), client);

        var scrollPane = new JScrollPane(itemPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            for (var component : itemPanel.getComponents()) {
                if (component instanceof RemoveWishlistItemPanel) {
                    if (((RemoveWishlistItemPanel) component).getRemove()) {
                        client.removeFromWishlist(((RemoveWishlistItemPanel) component).getWishlistItem().getProductId());
                    }
                    ((RemoveWishlistItemPanel) component).reset();
                }
            }
            itemPanel.removeAll();
            setRemoveWishlistItemPanel(itemPanel, client.getWishlist().size(), client);
            itemPanel.revalidate();
            itemPanel.repaint();
        });

        var resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            for (var component : itemPanel.getComponents()) {
                if (component instanceof RemoveWishlistItemPanel) {
                    ((RemoveWishlistItemPanel) component).reset();
                }
            }
        });

        actionPanel.clear();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(126, Short.MAX_VALUE)
                                .addComponent(submitButton, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE,
                                        GroupLayout.DEFAULT_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(submitButton)
                                        .addComponent(resetButton))
                                .addContainerGap(12, Short.MAX_VALUE))
        );
    }

    private boolean validQuantity(String quantity) {
        if (quantity.isEmpty()) {
            return false;
        }

        int quantityInt;
        try {
            quantityInt = Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            return false;
        }

        return quantityInt > 0;
    }

    private void changeQuantityOfProductsInWishlist() {
        String clientId = WarehouseContext.currentClientId;
        Client client = Warehouse.instance().getClientById(clientId).orElseThrow();

        var wishlistSize = client.getWishlist().size();

        var layout = new GroupLayout(actionPanel);
        actionPanel.setLayout(layout);

        var itemPanel = getChangeQuantityPanel(wishlistSize, client);

        var scrollPane = new JScrollPane(itemPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            for (var component : itemPanel.getComponents()) {
                if (component instanceof WishlistQuantityPanel) {
                    ((WishlistQuantityPanel) component).getWishlistItem().setQuantity(
                            ((WishlistQuantityPanel) component).getQuantity());
                    ((WishlistQuantityPanel) component).reset();
                }
            }
            JOptionPane.showMessageDialog(frame, "Quantities updated");
        });

        var resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            for (var component : itemPanel.getComponents()) {
                if (component instanceof WishlistQuantityPanel) {
                    ((WishlistQuantityPanel) component).reset();
                }
            }
        });

        actionPanel.clear();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(126, Short.MAX_VALUE)
                                .addComponent(submitButton, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE,
                                        GroupLayout.DEFAULT_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(submitButton)
                                        .addComponent(resetButton))
                                .addContainerGap(12, Short.MAX_VALUE))
        );
    }

    private JPanel getOrderedItemPanel(int wishlistSize, Client client) {
        return getOrderedItemPanel(wishlistSize, client, new ArrayList<OrderItemInfo>());
    }


    private JPanel getOrderedItemPanel(int wishlistSize, Client client, List<OrderItemInfo> additionalOrders) {
        var itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(wishlistSize, 1, 5, 5));

        var clientWishlistIterator = client.getWishlist().getIterator();
        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            Product product = Warehouse.instance().getProductById(wishlistItem.getProductId()).orElseThrow();

            var wishlistQuantityPanel = new OrderQuantityPanel(
                    new OrderItemInfo(wishlistItem.getProductId(), wishlistItem.getQuantity(), product.getPrice())
            );

            itemPanel.add(wishlistQuantityPanel);
        }

        for (var orderItem : additionalOrders) {
            var orderQuantityPanel = new OrderQuantityPanel(orderItem);
            itemPanel.add(orderQuantityPanel);
        }

        return itemPanel;
    }

    private static JPanel getChangeQuantityPanel(int wishlistSize, Client client) {
        var itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(wishlistSize, 1, 5, 5));

        var clientWishlistIterator = client.getWishlist().getIterator();
        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            var wishlistQuantityPanel = new WishlistQuantityPanel(wishlistItem);

            itemPanel.add(wishlistQuantityPanel);
        }
        return itemPanel;
    }

    private void setRemoveWishlistItemPanel(JPanel itemPanel, int wishlistSize, Client client) {
//        var itemPanel = new JPanel();
        itemPanel.removeAll();
        itemPanel.setLayout(new GridLayout(wishlistSize, 1, 5, 5));

        var clientWishlistIterator = client.getWishlist().getIterator();
        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            var removeWishlistItemPanel = new RemoveWishlistItemPanel(wishlistItem);

            itemPanel.add(removeWishlistItemPanel);
        }
        itemPanel.revalidate();
        itemPanel.repaint();
//        return itemPanel;
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
            System.out.printf("%d - Product Name: %s, Quantity: %d, Price: %.2f\n", itemNumber++,
                    productOrdered.getName(), orderItem.getQuantity(),
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
                                (wishlistItem.getQuantity() - product.get().getQuantity()) + " will be added to " +
                                "product waitlist.");
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