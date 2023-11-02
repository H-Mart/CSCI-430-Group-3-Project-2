import java.util.Objects;
import java.util.Optional;

public class ManagerMenuState implements WarehouseState {
    private static ManagerMenuState instance;

    private ManagerMenuState() {
    }

    public static ManagerMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ManagerMenuState());
    }

    private void executeOption(int option) {
        switch (option) {
            case 1:
                addProducts();
                break;
            case 2:
                printWaitlist();
                break;
            case 3:
                acceptShipment();
                break;
            case 4:
                becomeClerk();
                break;
            case 0:
                logout();
                break;
            default:
                System.out.println("Invalid input");
                break;
        }
    }

    public void run() {
        while (WarehouseContext.isSystemRunning()) {
            System.out.println("Manager Menu:");
            System.out.println("    1. Add a product");
            System.out.println("    2. Display the waitlist for a product");
            System.out.println("    3. Receive a shipment");
            System.out.println("    4. Login as Clerk");
            System.out.println("    0. Exit");
            System.out.print("> ");
            String input = Utilities.getUserInput();
            System.out.println();
            executeOption(Integer.parseInt(input));
        }
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
