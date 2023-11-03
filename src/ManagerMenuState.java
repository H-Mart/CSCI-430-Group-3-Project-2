import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

public class ManagerMenuState implements WarehouseState {
    private static ManagerMenuState instance;

    JFrame frame;
    AbstractButton addProductButton, printWaitlistButton, acceptShipmentButton, clerkButton, logoutButton;

    private ManagerMenuState() {
    }

    public static ManagerMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ManagerMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Manager Menu");
        addProductButton = new JButton("Add Product");
        printWaitlistButton = new JButton("Print Waitlist");
        acceptShipmentButton = new JButton("Accept Shipment");
        clerkButton = new JButton("Clerk");
        logoutButton = new JButton("Logout");

        addProductButton.addActionListener(e -> addProducts());
        printWaitlistButton.addActionListener(e -> printWaitlist());
        acceptShipmentButton.addActionListener(e -> acceptShipment());
        clerkButton.addActionListener(e -> becomeClerk());
        logoutButton.addActionListener(e -> logout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addProductButton);
        buttonPanel.add(printWaitlistButton);
        buttonPanel.add(acceptShipmentButton);
        buttonPanel.add(clerkButton);
        buttonPanel.add(logoutButton);

        frame.add(buttonPanel);
        frame.setVisible(true);
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        buildGUI();
    }

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    public void becomeClerk() {
        WarehouseContext.instance().setLogin(WarehouseContext.CLERK);
        WarehouseContext.instance().changeState(WarehouseContext.CLERK);
    }

    /**
     * @precondition none
     * @postcondition a new product is added to the ordering system and printed if successful
     */
    private static void addProduct() {
        System.out.print("Enter product name: ");
        String name = Utilities.getUserInput();

        System.out.print("\nEnter product price: ");
        double price = Double.parseDouble(Utilities.getUserInput());

        System.out.print("\nEnter product quantity: ");
        int quantity = Integer.parseInt(Utilities.getUserInput());

        String addedId = Warehouse.instance().addProduct(name, price, quantity);

        if (Warehouse.instance().getProductById(addedId).isPresent()) {
            System.out.println("\nProduct added - " + Warehouse.instance().getProductById(addedId).get());
        } else {
            System.out.println("\nProduct not added");
        }
    }

    /**
     * @precondition none
     * @postcondition multiple products are added to the ordering system
     */
    public static void addProducts() {
        while (true) {
            addProduct();
            System.out.print("\nAdd another product? (y/n): ");
            String input = Utilities.getUserInput();
            if (input.equalsIgnoreCase("n")) {
                break;
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
