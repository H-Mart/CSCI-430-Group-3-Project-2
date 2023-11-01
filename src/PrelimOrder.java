import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PrelimOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    interface Action extends Serializable {
        long serialVersionUID = 1L;

        void execute();
    }

    private class OrderAction implements Serializable, Action {
        private static final long serialVersionUID = 1L;
        private final String productId;
        private final int quantity;

        public OrderAction(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        @Override
        public void execute() {
            OrderItemInfo orderItemInfo = warehouse.orderItem(PrelimOrder.this.client.getId(), this.productId, this.quantity);
            orderInvoice.insertInvoiceItem(orderItemInfo);
        }
    }

    private class RemoveWishlistAction implements Serializable, Action {
        private static final long serialVersionUID = 1L;
        private final String productId;

        public RemoveWishlistAction(String productId) {
            this.productId = productId;
        }

        @Override
        public void execute() {
            client.removeFromWishlist(this.productId);
        }
    }

    private class UpdateWishlistAction implements Serializable, Action {
        private static final long serialVersionUID = 1L;
        private final String productId;

        private final int quantity;

        public UpdateWishlistAction(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        @Override
        public void execute() {
            client.updateWishlistItemQuantity(this.productId, this.quantity);
        }
    }

    private final Warehouse warehouse;

    private final Client client;

    private final List<Action> actions;

    public final Invoice orderInvoice;

    public PrelimOrder(String clientId) {
        this.warehouse = Warehouse.instance();
        this.client = warehouse.getClientById(clientId).orElseThrow();
        this.actions = new ArrayList<>();
        this.orderInvoice = new Invoice();
    }

    public void addOrderAction(String productId, int quantity) {
        actions.add(new OrderAction(productId, quantity));
    }

    public void addRemoveWishlistAction(String productId) {
        actions.add(new RemoveWishlistAction(productId));
    }

    public void addUpdateWishlistAction(String productId, int quantity) {
        actions.add(new UpdateWishlistAction(productId, quantity));
    }

    public void executeAll() {
        for (Action action : actions) {
            action.execute();
        }
    }

    public void finalizeOrder(String description) {
        executeAll();
        client.addToTransactionList(new TransactionRecord(description, orderInvoice));
    }
}
