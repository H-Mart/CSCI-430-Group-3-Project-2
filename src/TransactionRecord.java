import java.io.Serializable;
import java.util.Date;

public class TransactionRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Date date;

    private final String description;

    private final double totalCost;

    private final Invoice invoice;

    public TransactionRecord(String description, Invoice invoice) {
        this.invoice = invoice;
        this.date = new Date();
        this.description = description;
        this.totalCost = invoice.getTotalCost();
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public Invoice getInvoice() {
        return invoice;
    }
}
