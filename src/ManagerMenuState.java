import GUI.ActionPanel;
import GUI.ButtonPanel;
import GUI.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ManagerMenuState implements WarehouseState {
    private static ManagerMenuState instance;

    JFrame frame;
    AbstractButton addProductButton, acceptShipmentButton, clerkButton, logoutButton;

    MainPanel mainPanel;
    ButtonPanel buttonPanel;
    ActionPanel actionPanel;

    private ManagerMenuState() {
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

    public static ManagerMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ManagerMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Manager Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        addProductButton = new JButton("Add Product");
        acceptShipmentButton = new JButton("Accept Shipment");
        clerkButton = new JButton("Clerk");
        logoutButton = new JButton("Logout");

        addProductButton.addActionListener(e -> addProduct());
        acceptShipmentButton.addActionListener(e -> acceptShipment());
        clerkButton.addActionListener(e -> becomeClerk());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(addProductButton);
        buttonPanel.add(acceptShipmentButton);
        buttonPanel.add(clerkButton);
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

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    public void becomeClerk() {
        WarehouseContext.instance().setLogin(WarehouseContext.CLERK);
        WarehouseContext.instance().changeState(WarehouseContext.CLERK);
    }

    public void addProduct() {
        var name = getValidInput("Enter product name: ", "Product name cannot be empty",
                s -> !s.isEmpty());
        if (name == null) {
            return;
        }

        var price = getValidInput("Enter product price: ", "Invalid Price",
                (input) -> {
                    try {
                        var p = Double.parseDouble(input);
                        return (p >= 0);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
        if (price == null) {
            return;
        }

        var quantity = getValidInput("Enter product quantity: ", "Invalid Quantity",
                (input) -> {
                    try {
                        var q = Integer.parseInt(input);
                        return (q >= 0);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
        if (quantity == null) {
            return;
        }

        var addedId = Warehouse.instance().addProduct(name, Double.parseDouble(price), Integer.parseInt(quantity));

        if (Warehouse.instance().getProductById(addedId).isPresent()) {
            JOptionPane.showMessageDialog(frame, "Product added - " + Warehouse.instance().getProductById(addedId).get());
        } else {
            JOptionPane.showMessageDialog(frame, "Product not added", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getValidInput(String message, String errorMessage, Predicate<String> validator) {
        while (true) {
            String input = JOptionPane.showInputDialog(frame, message);
            if (input == null || !validator.test(input)) {
                if (input == null) return null;
                JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                return input;
            }
        }
    }

    private static void printWaitlist() {
        System.out.print("Enter product id: ");
        String productId = Utilities.getUserInput();

        Optional<Product> product = Warehouse.instance().getProductById(productId);

        if (product.isEmpty()) {
            System.out.println("Product not found");
            return;
        }

        System.out.println("Waitlist for " + product.get().getName() + ": ");

        var waitlistIterator = product.get().getWaitlist().getIterator();

        if (!waitlistIterator.hasNext()) {
            System.out.println("\nWaitlist is empty");
            return;
        }

        while (waitlistIterator.hasNext()) {
            var waitlistItem = waitlistIterator.next();
            System.out.println("\tWaitlisted by: " + waitlistItem.getClientId());
            System.out.println("\tDate: " + waitlistItem.getDate());
            System.out.println("\tQuantity: " + waitlistItem.getQuantity());
            System.out.println();
        }

    }

    private static void acceptShipment() {
        System.out.print("Please enter the product id for the shipment: ");
        String productId = Utilities.getUserInput();
        Optional<Product> shipmentProductOp = Warehouse.instance().getProductById(productId);

        if (shipmentProductOp.isEmpty()) {
            System.out.println("Product not found");
            return;
        }

        Product shipmentProduct = shipmentProductOp.get();

        System.out.print("\nPlease enter the quantity: ");
        int shipmentQuantity = Integer.parseInt(Utilities.getUserInput());
        if (shipmentQuantity < 0) {
            System.out.println("\nQuantity must be non-negative");
            return;
        }

        shipmentProduct.setQuantity(shipmentProduct.getQuantity() + shipmentQuantity);

        System.out.printf("\nUpdated product quantity from %d to %d\n\n",
                shipmentProduct.getQuantity() - shipmentQuantity, shipmentProduct.getQuantity());

        var waitlistCopyIterator = new Waitlist(shipmentProduct.getWaitlist()).getIterator();
        while (waitlistCopyIterator.hasNext()) {
            var waitlistItem = waitlistCopyIterator.next();
            System.out.println("Processing waitlist item: ");
            System.out.printf("\tClient ID: %s\n\tQuantity: %d\n", waitlistItem.getClientId(), waitlistItem.getQuantity());
            System.out.println("\tDate: " + waitlistItem.getDate());


            System.out.println("Options:");
            System.out.println("    1. Order Waitlisted Amount");
            System.out.println("    2. Order Different Amount");
            System.out.println("    3. Skip");
            String input = Utilities.getUserInput();

            switch (input) {
                case "1": // order waitlisted amount
                    Warehouse.instance().fillWaitlistOrder(waitlistItem.getWaitlistItemId(), productId, waitlistItem.getQuantity());
                    break;
                case "2": // order different amount
                    System.out.print("\nPlease enter the amount to order: ");
                    int orderQuantity = Integer.parseInt(Utilities.getUserInput());
                    Warehouse.instance().fillWaitlistOrder(waitlistItem.getWaitlistItemId(), productId, orderQuantity);
                    break;
                case "3": // skip
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }
}
