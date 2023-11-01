import java.io.Serializable;
import java.util.Optional;

public class Product implements Serializable {
    private static final long serialVersionUID = 3L;

    private final String id;
    private final String name;
    private final double price;
    private int quantity;
    private final Waitlist waitlist;
    private final IdServer waitlistIdServer;

    /**
     * @param name     name of product
     * @param price    price of product
     * @param quantity quantity of product
     * @postcondition name, price, and quantity are set, id is set to nextId, nextId is incremented
     */
    public Product(String name, double price, int quantity, IdServer idServer) {
        this.id = Integer.toString(idServer.getNewId());
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.waitlist = new Waitlist();
        this.waitlistIdServer = new IdServer();
    }

    // private attribute getters

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addToWaitlist(String clientId, int quantity) {
        waitlist.addWaitlistItem(new WaitlistItem(clientId, quantity, waitlistIdServer));
    }

    public Optional<WaitlistItem> getWaitlistItem(int waitlistItemId) {
        return waitlist.getWaitlistItem(waitlistItemId);
    }

    public Waitlist getWaitlist() {
        return waitlist;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Price: " + price + ", Quantity: " + quantity + ", Name: " + name;
    }
}