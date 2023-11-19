import java.io.Serializable;

public final class WishlistItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String productId;

    private int quantity;

    public WishlistItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "productId: " + productId + " quantity: " + quantity;
    }
}
