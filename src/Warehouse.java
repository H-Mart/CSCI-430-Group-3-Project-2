import java.io.*;
import java.util.Iterator;
import java.util.Optional;

public class Warehouse implements Serializable {
    // singleton class for coupling the user interface to the back end
    private static final long serialVersionUID = 1L;

    private static Warehouse warehouse;

    private final ProductList productList;

    private final ClientList clientList;

    private final IdServer clientIdServer;

    private final IdServer productIdServer;

    private Warehouse() {
        productList = new ProductList();
        clientList = new ClientList();
        clientIdServer = new IdServer();
        productIdServer = new IdServer();
    }

    /**
     * method to save the state of the warehouse
     *
     * @precondition none
     * @postcondition the warehouse is serialized to the file warehouse.ser
     */
    public static void serializeWarehouse() {
        try (var fileOut = new FileOutputStream("warehouse.ser");
             var objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(warehouse);
            fileOut.flush();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    /**
     * method to restore a previous state of the warehouse
     *
     * @precondition a file named warehouse.ser exists and contains a serialized warehouse
     * @postcondition id servers and lists are restored to the state they were in when the warehouse was serialized
     */
    public static void deserializeWarehouse() {
        try (var fileIn = new FileInputStream("warehouse.ser");
             var objectIn = new ObjectInputStream(fileIn)) {
            warehouse = (Warehouse) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    /**
     * @return a Warehouse instance
     * @precondition none
     * @postcondition if the instance did not exist, it is created, stored in the static variable, and returned.
     * otherwise the existing instance is returned
     */
    public static Warehouse instance() {
        if (warehouse == null) {
            warehouse = new Warehouse();
        }
        return warehouse;
    }

    /**
     * Adds a new client to the client list
     *
     * @param name    the name of the client
     * @param address the address of the client
     * @return a String containing the id of the Client
     * @precondition name and address are not null
     * @postcondition the client is added to the client list, the id is returned, and the id server is updated to the next id
     */
    public String addClient(String name, String address) {
        Client client = new Client(name, address, clientIdServer);
        clientList.insertClient(client);
        return client.getId();
    }

    /**
     * Searches the client list for a client with the given id
     *
     * @param clientId the id of the client to fetch
     * @return an Optional containing the client if it exists, otherwise an empty Optional
     * @precondition clientId is not null
     * @postcondition an empty Optional is returned if the client does not exist, otherwise an Optional containing the client is returned
     */
    public Optional<Client> getClientById(String clientId) {
        return clientList.getClientById(clientId);
    }

    /**
     * Searches the product list for a product with the given id
     *
     * @param productId the id of the product to fetch
     * @return an Optional containing the product if it exists, otherwise an empty Optional
     * @precondition productId is not null
     * @postcondition an empty optional is returned if the product does not exist, otherwise an optional containing the product is returned
     */
    public Optional<Product> getProductById(String productId) {
        return productList.getProductById(productId);
    }

    /**
     * Adds a new product to the product list
     *
     * @param name     the name of the product
     * @param price    the price of the product
     * @param quantity the quantity of the product
     * @return a String containing the id of the Product
     * @precondition name is not null, price is non-negative, and quantity is non-negative
     * @postcondition the product is added to the product list, the id is returned, and the id server is updated to the next id
     */
    public String addProduct(String name, double price, int quantity) {
        if (price < 0) {
            System.out.println("Price must be non-negative");
            throw new IllegalArgumentException();
        } else if (quantity < 0) {
            System.out.println("Quantity must be non-negative");
            throw new IllegalArgumentException();
        }
        Product product = new Product(name, price, quantity, productIdServer);
        productList.insertProduct(product);
        return product.getId();
    }

    /**
     * @param clientId  the id of the client to add the product to
     * @param productId the id of the product to add to the client's wishlist
     * @param quantity  the quantity of the product to add
     * @precondition productId and clientId are not null and exist in their respective lists, and quantity is non-negative
     * @postcondition the product is added to the client's wishlist, or the quantity of the product in the wishlist is updated
     * if any of the preconditions are not met, an error message is printed and the method returns without modifying any state
     */
    public void addProductToClientWishlist(String clientId, String productId, int quantity) {
        var client = clientList.getClientById(clientId);
        var product = productList.getProductById(productId);

        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        } else if (product.isEmpty()) {
            System.out.println("Product not found");
            return;
        } else if (quantity < 0) {
            System.out.println("Quantity must be non-negative");
            return;
        }

        var clientWishlist = client.get().getWishlist();
        Optional<WishlistItem> wishlistItem = clientWishlist.getWishlistItem(productId);
        if (wishlistItem.isPresent()) {
            // if product is already in wishlist, update quantity
            wishlistItem.get().setQuantity(quantity);
        } else {
            // otherwise add product to wishlist
            client.get().addToWishlist(productId, quantity);
        }
    }

    public OrderItemInfo orderItem(String clientId, String productId, int orderQuantity) {
        var client = clientList.getClientById(clientId).orElseThrow();
        var product = productList.getProductById(productId).orElseThrow();

        OrderItemInfo orderItemInfo;
        if (orderQuantity <= product.getQuantity()) {
            orderItemInfo = new OrderItemInfo(product.getId(), orderQuantity, product.getPrice());
            product.setQuantity(product.getQuantity() - orderQuantity);
        } else {
            orderItemInfo = new OrderItemInfo(product.getId(), product.getQuantity(), product.getPrice());
            product.addToWaitlist(clientId, orderQuantity - product.getQuantity());
            product.setQuantity(0);
        }

        client.subtractFromBalance(orderItemInfo.getTotalPrice());
        return orderItemInfo;
    }

    public void fillWaitlistOrder(int waitlistItemId, String productId, int orderQuantity) {
        OrderItemInfo orderItemInfo;
        Product product = Warehouse.instance().getProductById(productId).orElseThrow();
        WaitlistItem waitlistItem = product.getWaitlistItem(waitlistItemId).orElseThrow();

        // todo add prints to show what's happening
        int waitlistQuantityDeficit = 0;
        if (orderQuantity <= product.getQuantity()) {
            orderItemInfo = new OrderItemInfo(product.getId(), orderQuantity, product.getPrice());
            product.setQuantity(product.getQuantity() - orderQuantity);
        } else {
            orderItemInfo = new OrderItemInfo(product.getId(), product.getQuantity(), product.getPrice());
            waitlistQuantityDeficit = waitlistItem.getQuantity() - product.getQuantity(); // if we want to order more product than is available, record that amount
            product.setQuantity(0);
        }

        Client client = Warehouse.instance().getClientById(waitlistItem.getClientId()).orElseThrow();
        client.subtractFromBalance(orderItemInfo.getTotalPrice());

        if (waitlistQuantityDeficit > 0) {
            waitlistItem.setQuantity(waitlistQuantityDeficit);
        } else if (orderQuantity >= waitlistItem.getQuantity()) {
            product.getWaitlist().removeWaitlistItem(waitlistItem.getWaitlistItemId());
        } else {
            waitlistItem.setQuantity(waitlistItem.getQuantity() - orderQuantity);
        }

        var orderInvoice = new Invoice();
        orderInvoice.insertInvoiceItem(orderItemInfo);

        client.addToTransactionList(new TransactionRecord("Autogenerated invoice for waitlist order - waitlisted on " + waitlistItem.getDate(), orderInvoice));
//        return orderInvoice;
    }

    public Iterator<Client> getClientIterator() {
        return clientList.getIterator();
    }

    public Iterator<Product> getProductIterator() {
        return productList.getIterator();
    }
}
