import java.io.Serializable;
import java.util.Date;

public class WaitlistItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String clientId;
    private int quantity;
    private final Date date;
    private final int waitlistItemId;

    public WaitlistItem(String clientId, int quantity, IdServer idServer) {
        this.clientId = clientId;
        this.quantity = quantity;
        this.date = new Date();
        this.waitlistItemId = idServer.getNewId();
    }

    public WaitlistItem(WaitlistItem other) {
        this.clientId = other.clientId;
        this.quantity = other.quantity;
        this.date = other.date;
        this.waitlistItemId = other.waitlistItemId;
    }

    public String getClientId() {
        return clientId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getWaitlistItemId() {
        return waitlistItemId;
    }

    public Date getDate() {
        return date;
    }

    public String toString() {
        return "clientId: " + clientId + ", quantity: " + quantity;
    }
}
