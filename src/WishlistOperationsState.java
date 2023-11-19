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

    private JFrame frame;
    private final MainPanel mainPanel;
    private final ButtonPanel buttonPanel;
    private final ActionPanel actionPanel;

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

        var viewWishlistButton = new JButton("View Wishlist");
        var addProductsToWishlistButton = new JButton("Add Products to Wishlist");
        var removeProductsFromWishlistButton = new JButton("Remove Products from Wishlist");
        var changeQuantityOfProductsInWishlistButton = new JButton("Change Quantity of Products in Wishlist");
        var orderProductsInWishlistButton = new JButton("Order Products in Wishlist");
        var exitButton = new JButton("Go Back");

        viewWishlistButton.addActionListener(e -> displayClientWishlist());
        addProductsToWishlistButton.addActionListener(e -> addProductsToClientWishlist());
        removeProductsFromWishlistButton.addActionListener(e -> removeProductsFromClientWishlist());
        changeQuantityOfProductsInWishlistButton.addActionListener(e -> changeQuantityOfProductsInWishlist());
        orderProductsInWishlistButton.addActionListener(e -> startOrder());
        exitButton.addActionListener(e -> exit());

        //noinspection DuplicatedCode
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

        var itemPanel = new JPanel();

        setOrderedItemPanel(itemPanel, wishlistSize, client);

        var scrollPane = new JScrollPane(itemPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> makeOrderFromOrderedItemPanels(itemPanel, client));

        var resetButton = new JButton("Reset");

        resetButton.addActionListener(e -> {
            itemPanel.removeAll();
            setOrderedItemPanel(itemPanel, wishlistSize, client);
            itemPanel.revalidate();
            itemPanel.repaint();
        });

        //noinspection DuplicatedCode
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
        String clientId = WarehouseContext.currentClientId;
        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Client not found", "Error", JOptionPane.ERROR_MESSAGE);
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
            private void updateOutput() {
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

            client.get().getWishlist().getWishlistItem(productId).ifPresentOrElse(
                    wishlistItem -> client.get().updateWishlistItemQuantity(productId,
                            wishlistItem.getQuantity() + quantityInt),
                    () -> client.get().addToWishlist(productId, quantityInt)
            );

            JOptionPane.showMessageDialog(frame, "Product added to wishlist");
            productIdField.setText("");
            quantityField.setText("");
        });

        clearButton.addActionListener(e -> {
            productIdField.setText("");
            quantityField.setText("");
        });

        actionPanel.setLayout(new GridLayout(6, 2, 5, 5));

        actionPanel.clear();

        //noinspection DuplicatedCode
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

        var resetButton = new JButton("Uncheck All");
        resetButton.addActionListener(e -> {
            for (var component : itemPanel.getComponents()) {
                if (component instanceof RemoveWishlistItemPanel) {
                    ((RemoveWishlistItemPanel) component).reset();
                }
            }
        });

        //noinspection DuplicatedCode
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

        //noinspection DuplicatedCode
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


    private void setOrderedItemPanel(JPanel itemPanel, int wishlistSize, Client client) {
        setOrderedItemPanel(itemPanel, wishlistSize, client, new ArrayList<>());
    }

    private void setOrderedItemPanel(JPanel itemPanel, int wishlistSize, Client client,
                                     List<OrderItemInfo> additionalOrders) {
        itemPanel.setLayout(new GridLayout(wishlistSize, 1, 5, 5));

        var clientWishlistIterator = client.getWishlist().getIterator();
        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            Product product = Warehouse.instance().getProductById(wishlistItem.getProductId()).orElseThrow();

            var wishlistQuantityPanel = new OrderQuantityPanel(
                    new OrderItemInfo(wishlistItem.getProductId(), wishlistItem.getQuantity(), product.getPrice())
            );

            wishlistQuantityPanel.setRemoveButtonAction(e -> {
                itemPanel.remove(wishlistQuantityPanel);
                itemPanel.revalidate();
                itemPanel.repaint();
            });

            itemPanel.add(wishlistQuantityPanel);
        }

        for (var orderItem : additionalOrders) {
            var orderQuantityPanel = new OrderQuantityPanel(orderItem);

            orderQuantityPanel.setRemoveButtonAction(e -> {
                itemPanel.remove(orderQuantityPanel);

                itemPanel.revalidate();
                itemPanel.repaint();
            });

            itemPanel.add(orderQuantityPanel);
        }
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
    }

    private void makeOrderFromOrderedItemPanels(JPanel itemPanel, Client client) {
        var orderItemInfoList = new ArrayList<OrderItemInfo>();

        if (itemPanel.getComponents().length == 0) {
            JOptionPane.showMessageDialog(frame, "No items in order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (var component : itemPanel.getComponents()) {
            if (component instanceof OrderQuantityPanel) {
                orderItemInfoList.add(((OrderQuantityPanel) component).getCurrentItem());
            }
        }

        var prelimOrder = new PrelimOrder(client.getId());
        var orderString = new StringBuilder();
        orderString.append("<html>The order is displayed below: <br><br>");
        int itemNumber = 1;

        for (var orderItemInfo : orderItemInfoList) {
            var product = Warehouse.instance().getProductById(orderItemInfo.getProductId()).orElseThrow();

            if (orderItemInfo.getQuantity() > product.getQuantity()) {
                var waitlistQuantity = orderItemInfo.getQuantity() - product.getQuantity();
                orderString.append(String.format("%d - Product Name: %s, Quantity: %d, Price: %.2f<br> &nbsp; &nbsp;" +
                                "Waitlisted: " +
                                "%d <br><br>", itemNumber++,
                        product.getName(), product.getQuantity(),
                        product.getPrice(), waitlistQuantity));
            } else {
                orderString.append(String.format("%d - Product Name: %s, Quantity: %d, Price: %.2f<br><br>",
                        itemNumber++,
                        product.getName(), orderItemInfo.getQuantity(),
                        product.getPrice()));
            }

            prelimOrder.addOrderAction(orderItemInfo.getProductId(), orderItemInfo.getQuantity());

            // check if we need to remove/update the wishlist item (if the item is in the wishlist)
            var wishlistItem = client.getWishlist().getWishlistItem(orderItemInfo.getProductId());
            if (wishlistItem.isPresent()) {
                if (orderItemInfo.getQuantity() >= wishlistItem.get().getQuantity()) {
                    prelimOrder.addRemoveWishlistAction(orderItemInfo.getProductId());
                } else {
                    prelimOrder.addUpdateWishlistAction(orderItemInfo.getProductId(),
                            wishlistItem.get().getQuantity() - orderItemInfo.getQuantity());
                }
            }
        }

        double totalPrice = 0;
        for (var orderItem : orderItemInfoList) {
            totalPrice += orderItem.getPrice() * orderItem.getQuantity();
        }

        orderString.append(String.format("Total Price: %.2f<br><br>", totalPrice));

        var confirm = JOptionPane.showConfirmDialog(frame, orderString.toString(), "Confirm Order",
                JOptionPane.OK_CANCEL_OPTION);

        if (confirm == JOptionPane.OK_CANCEL_OPTION) {
            return;
        }

        String description = Utilities.getValidInput(frame, "Please enter a description for the order: ",
                "Description cannot be empty",
                s -> !s.isEmpty());
        if (description == null) {
            return;
        }

        prelimOrder.finalizeOrder(description);
        JOptionPane.showMessageDialog(frame, "Order submitted");
    }

}