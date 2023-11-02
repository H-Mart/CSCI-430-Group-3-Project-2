import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class ClerkMenuState implements WarehouseState {
    private static ClerkMenuState instance;

    private ClerkMenuState() {

    }

    public static ClerkMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ClerkMenuState());

    }

    private void executeOption(int option) {
        switch (option) {
            case 1:
                addClient();
                break;
            case 2:
                printProducts();
                break;
            case 3:
                printClients();
                break;
            case 4:
                printClientsWithOutstandingBalance();
                break;
            case 5:
                acceptPayment();
                break;
            case 6:
                becomeClient();
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
            System.out.println("    1. Add a Client");
            System.out.println("    2. List Products");
            System.out.println("    3. List Clients");
            System.out.println("    4. List Clients with outstanding balance");
            System.out.println("    5. Accept payment from a client");
            System.out.println("    6. Login as Client");
            System.out.println("    0. Exit");
            System.out.print("> ");
            String input = Utilities.getUserInput();

            System.out.println();
            executeOption(Integer.parseInt(input));
        }
    }

    private void becomeClient() {
        System.out.print("Enter client id: ");
        String clientId = Utilities.getUserInput();

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        WarehouseContext.instance().setCurrentClientId(clientId);
        WarehouseContext.instance().setLogin(WarehouseContext.CLIENT);
        WarehouseContext.instance().changeState(WarehouseContext.CLIENT);
    }

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    public static void addClient() {
        System.out.print("Enter client name: ");
        String name = Utilities.getUserInput();

        System.out.print("\nEnter client address: ");
        String address = Utilities.getUserInput();

        String addedId = Warehouse.instance().addClient(name, address);

        if (Warehouse.instance().getClientById(addedId).isPresent()) {
            System.out.println("\nClient added - " + Warehouse.instance().getClientById(addedId).get());
        } else {
            System.out.println("\nClient not added");
        }
    }

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

    public void printClientsWithOutstandingBalance() {
        Iterator<Client> clientIterator = Warehouse.instance().getClientIterator();
        while (clientIterator.hasNext()) {
            Client client = clientIterator.next();
            if (client.getBalance() < 0) {
                System.out.printf("Client ID: %s\nClient Name: %s\nClient Balance: %.2f\n\n",
                        client.getId(), client.getName(), client.getBalance());
            }
        }
    }

    public void acceptPayment() {
        System.out.print("Enter client id: ");
        String clientId = Utilities.getUserInput();

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        System.out.printf("Client %s current balance: %.2f\n", client.get().getName(), client.get().getBalance());

        System.out.print("Enter payment amount: ");
        double paymentAmount = Double.parseDouble(Utilities.getUserInput());

        if (paymentAmount < 0) {
            System.out.println("Payment amount must be non-negative");
            return;
        }

        client.get().addToBalance(paymentAmount);
        System.out.printf("Client %s new balance: %.2f\n", client.get().getName(), client.get().getBalance());
    }
}
