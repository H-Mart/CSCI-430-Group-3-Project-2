import java.io.Serializable;

public class Client implements Serializable {
    private static final long serialVersionUID = 2L;

    private final String id;
    private final String name;
    private final String address;

    private final Wishlist wishlist;
    private final TransactionList transactionList;

    private double balance;

    /**
     * @param name    the name of the client
     * @param address the address of the client
     * @precondition name and address are not null, idServer is a valid IdServer instance
     * @postcondition name and address are set, id is set to nextId, nextId is incremented, wishlist is initialized
     */
    public Client(String name, String address, IdServer idServer) {
        this.id = Integer.toString(idServer.getNewId());
        this.name = name;
        this.address = address;
        this.wishlist = new Wishlist();
        this.transactionList = new TransactionList();
        this.balance = 0;
    }

    /**
     * Method for adding a WishlistItem to the wishlist from a Product id
     *
     * @param productId the id of the product to add to the wishlist
     * @param quantity  the quantity of the product to add
     * @precondition productId is not null, quantity is greater than 0
     * @postcondition the product and quantity are added to the wishlist as a WishlistItem
     */
    public void addToWishlist(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        wishlist.addWishlistItem(new WishlistItem(productId, quantity));
    }

    public void addToTransactionList(TransactionRecord transactionRecord) {
        transactionList.insertTransaction(transactionRecord);
    }

    public void removeFromWishlist(String productId) {
        wishlist.removeWishlistItem(productId);
    }

    public void updateWishlistItemQuantity(String productId, int quantity) {
        wishlist.updateWishlistItemQuantity(productId, quantity);
    }

    public void subtractFromBalance(double amount) {
        balance -= amount;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public TransactionList getTransactionList() {
        return transactionList;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Address: " + address;
    }
}
