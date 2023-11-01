import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UserInterface implements Serializable {
    private static final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    private UserInterface() {
    }

    /**
     * Gets user input from the console
     *
     * @return user input as a string
     */
    public static String getUserInput() {
        String inputLine = "";
        try {
            inputLine = UserInterface.inputReader.readLine().trim();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return inputLine;
    }

    public static void main(String[] args) {
        // print welcome message
        System.out.println("Welcome to the ordering system!");
        while (true) {
            System.out.println();
            System.out.println("Main Menu: ");
            System.out.println("    1.  Add client");
            System.out.println("    2.  Add products");
            System.out.println("    3.  Add products to Client wishlist");
            System.out.println("    4.  Start order");
            System.out.println("    5.  Accept Shipment");
            System.out.println("    6.  Print clients");
            System.out.println("    7.  Print products");
            System.out.println("    8.  Print Client wishlist");
            System.out.println("    9.  Print Product waitlist");
            System.out.println("    10. Print Client Order History");
            System.out.println("    11. Print Client Balance");
            System.out.println("    12. Save current state");
            System.out.println("    13. Load stored state");
            System.out.println("    0. Exit");
            System.out.print("> ");
            String input = UserInterface.getUserInput();
            System.out.println();

            switch (input) {
                case "1":
                    addClient();
                    break;
                case "2":
                    addProducts();
                    break;
                case "3":
                    addProductsToClientWishlist();
                    break;
                case "4":
                    startOrder();
                    break;
                case "5":
                    acceptShipment();
                    break;
                case "6":
                    printClients();
                    break;
                case "7":
                    printProducts();
                    break;
                case "8":
                    printClientWishlist();
                    break;
                case "9":
                    printWaitlist();
                    break;
                case "10":
                    printClientOrderHistory();
                    break;
                case "11":
                    printClientBalance();
                    break;
                case "12":
                    saveState();
                    break;
                case "13":
                    loadState();
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    private static void saveState() {
        Warehouse.serializeWarehouse();
    }

    private static void loadState() {
        Warehouse.deserializeWarehouse();
    }

    /**
     * @precondition none
     * @postcondition a new client is added to the ordering system and printed if successful
     */
    public static void addClient() {
        System.out.print("Enter client name: ");
        String name = UserInterface.getUserInput();

        System.out.print("\nEnter client address: ");
        String address = UserInterface.getUserInput();

        String addedId = Warehouse.instance().addClient(name, address);

        if (Warehouse.instance().getClientById(addedId).isPresent()) {
            System.out.println("\nClient added - " + Warehouse.instance().getClientById(addedId).get());
        } else {
            System.out.println("\nClient not added");
        }
    }

    /**
     * @precondition none
     * @postcondition a new product is added to the ordering system and printed if successful
     */
    private static void addProduct() {
        System.out.print("Enter product name: ");
        String name = UserInterface.getUserInput();

        System.out.print("\nEnter product price: ");
        double price = Double.parseDouble(UserInterface.getUserInput());

        System.out.print("\nEnter product quantity: ");
        int quantity = Integer.parseInt(UserInterface.getUserInput());

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
            String input = UserInterface.getUserInput();
            if (input.equalsIgnoreCase("n")) {
                break;
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
        System.out.print("Please enter your client id: ");
        String clientId = UserInterface.getUserInput();
        if (Warehouse.instance().getClientById(clientId).isEmpty()) {
            System.out.println("\nClient not found");
            return;
        }

        while (true) {
            System.out.print("\nPlease enter the product id: ");
            String productId = UserInterface.getUserInput();
            if (Warehouse.instance().getProductById(productId).isEmpty()) {
                System.out.println("Product not found");
                return;
            }

            System.out.print("\nPlease enter the quantity: ");
            int quantity = Integer.parseInt(UserInterface.getUserInput());

            Warehouse.instance().addProductToClientWishlist(clientId, productId, quantity);

            printClientWishlist(clientId);

            System.out.print("\nAdd another product? (y/n): ");
            String input = UserInterface.getUserInput();
            if (input.equalsIgnoreCase("n")) {
                break;
            }
        }
    }

    /*
     * Prints all clients in the ordering system in a nicely organized table
     */
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

    /**
     * @postcondition the client's wishlist is printed
     */
    public static void printClientWishlist() {
        System.out.print("Enter client id: ");
        String clientId = UserInterface.getUserInput();

        printClientWishlist(clientId);
    }

    /*
     * Prints the client's wishlist including the product id, product name, and wishlist-ed quantity
     */
    private static void printClientWishlist(String clientId) {
        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        var clientWishlistIterator = client.get().getWishlist().getIterator();

        if (!clientWishlistIterator.hasNext()) {
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

    private static void printWaitlist() {
        System.out.print("Enter product id: ");
        String productId = UserInterface.getUserInput();

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

    private static void printClientOrderHistory() {
        System.out.print("Enter client id: ");
        String clientId = UserInterface.getUserInput();

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

    private static void printClientBalance() {
        System.out.print("Enter client id: ");
        String clientId = UserInterface.getUserInput();

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        System.out.printf("Balance: %.2f", client.get().getBalance());
    }


    private static void startOrder() {
        System.out.print("Please enter the client id: ");
        String clientId = UserInterface.getUserInput();
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
        String input = UserInterface.getUserInput();
        if (input.equalsIgnoreCase("n")) {
            return;
        }

        System.out.println("Please enter a description for the order: ");
        String description = UserInterface.getUserInput();
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

            String input = UserInterface.getUserInput();
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
                    int quantity = Integer.parseInt(UserInterface.getUserInput());

                    while (quantity <= 0) {
                        System.out.println("Quantity must be positive");
                        System.out.print("\nPlease enter the amount to add to the order: ");
                        quantity = Integer.parseInt(UserInterface.getUserInput());
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

    private static void acceptShipment() {
        System.out.print("Please enter the product id for the shipment: ");
        String productId = UserInterface.getUserInput();
        Product shipmentProduct = Warehouse.instance().getProductById(productId).orElseThrow();

        System.out.print("\nPlease enter the quantity: ");
        int shipmentQuantity = Integer.parseInt(UserInterface.getUserInput());
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
            String input = UserInterface.getUserInput();

            switch (input) {
                case "1": // order waitlisted amount
                    Warehouse.instance().fillWaitlistOrder(waitlistItem.getWaitlistItemId(), productId, waitlistItem.getQuantity());
                    break;
                case "2": // order different amount
                    System.out.print("\nPlease enter the amount to order: ");
                    int orderQuantity = Integer.parseInt(UserInterface.getUserInput());
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

    // prints a message saying the option is not implemented, for use in stubs
    public static void optionNotImplemented() {
        System.out.println("Option not implemented");
    }
}