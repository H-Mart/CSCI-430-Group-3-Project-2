import java.io.Serializable;

public class OrderItemInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String productId;
    private final int quantity;
    private final double price;

    public OrderItemInfo(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
}