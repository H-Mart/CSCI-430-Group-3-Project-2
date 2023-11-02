import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClientMenuState implements WarehouseState {
    private static ClientMenuState instance;

    private ClientMenuState() {
    }

    public static ClientMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClientMenuState());
    }

    private void executeOption(int option) {
        switch (option) {
            case 1:
                printDetails();
                break;
            case 2:
                printProducts();
                break;
            case 3:
                printClientOrderHistory();
                break;
            case 4:
                addProductsToClientWishlist();
                break;
            case 5:
                printClientWishlist();
                break;
            case 6:
                startOrder();
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
            System.out.println("Clerk Menu:");
            System.out.println("    1. Print Client Details");
            System.out.println("    2. Print Products");
            System.out.println("    3. Print Transactions");
            System.out.println("    4. Add item to Wishlist");
            System.out.println("    5. Print Wishlist");
            System.out.println("    6. Place an Order");
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

    private static void printDetails() {
        String clientId = WarehouseContext.currentClientId;
        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        System.out.println("Client ID: " + client.get().getId());
        System.out.println("Client Name: " + client.get().getName());
        System.out.println("Client Address: " + client.get().getAddress());
        System.out.println("Client Balance: " + client.get().getBalance());
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

    private static void printClientWishlist() {
        String clientId = WarehouseContext.currentClientId;
        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        var clientWishlistIterator = client.get().getWishlist().getIterator();

        if (!clientWishlistIterator.hasNext()) {
            System.out.println("\nWishlist is empty");
            return;
        }

        System.out.println("\nCurrent Wishlist: ");

        while (clientWishlistIterator.hasNext()) {
            var wishlistItem = clientWishlistIterator.next();
            Optional<Product> product = Warehouse.instance().getProductById(wishlistItem.getProductId());

            if (product.isEmpty()) {
                System.err.println("The product with id " + wishlistItem.getProductId() + " was not found");
                return;
            }

            System.out.println("\tProduct ID: " + wishlistItem.getProductId() + "\n\tProduct Name: "
                    + product.get().getName() + "\n\tWishlist Quantity: " + wishlistItem.getQuantity());
            System.out.println();
        }
    }

    private static void printClientOrderHistory() {
        String clientId = WarehouseContext.currentClientId;

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        var clientTransactionIterator = client.get().getTransactionList().getIterator();
        if (!clientTransactionIterator.hasNext()) {
            System.out.println("\nOrder history is empty");
            return;
        }

        System.out.println("\nOrder History: ");

        while (clientTransactionIterator.hasNext()) {
            var transactionRecord = clientTransactionIterator.next();
            System.out.println("\tDate: " + transactionRecord.getDate());
            System.out.println("\tDescription: " + transactionRecord.getDescription());
            System.out.println("\tTotal Price: " + transactionRecord.getTotalCost());
            System.out.println();
            var invoiceIterator = transactionRecord.getInvoice().getIterator();
            System.out.println("\tInvoice: ");
            while (invoiceIterator.hasNext()) {
                var invoiceItem = invoiceIterator.next();
                System.out.println("\t\tProduct ID: " + invoiceItem.getProductId());
                System.out.println("\t\tQuantity: " + invoiceItem.getQuantity());
                System.out.println("\t\tPrice: " + invoiceItem.getPrice());
                System.out.println();
            }
        }
    }

    /**
     * Allow the user to add products to a client's wishlist
     *
     * @precondition none
     * @postcondition if the client and product(s) exist,
     * the product(s) is/are added to the client's wishlist as a WishlistItem
     */
    public static void addProductsToClientWishlist() {
        String clientId = WarehouseContext.currentClientId;

        if (Warehouse.instance().getClientById(clientId).isEmpty()) {
            System.out.println("\nClient not found");
            return;
        }

        while (true) {
            System.out.print("\nPlease enter the product id: ");
            String productId = Utilities.getUserInput();
            if (Warehouse.instance().getProductById(productId).isEmpty()) {
                System.out.println("Product not found");
                return;
            }

            System.out.print("\nPlease enter the quantity: ");
            int quantity = Integer.parseInt(Utilities.getUserInput());

            Warehouse.instance().addProductToClientWishlist(clientId, productId, quantity);

            printClientWishlist();

            System.out.print("\nAdd another product? (y/n): ");
            String input = Utilities.getUserInput();
            if (input.equalsIgnoreCase("n")) {
                break;
            }
        }
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
