import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<OrderItemInfo> orderItemInfoList;

    public Invoice() {
        orderItemInfoList = new ArrayList<>();
    }

    public Iterator<OrderItemInfo> getIterator() {
        return orderItemInfoList.iterator();
    }

    public void insertInvoiceItem(OrderItemInfo orderItemInfo) {
        orderItemInfoList.add(orderItemInfo);
    }

    public double getTotalCost() {
        double totalCost = 0;
        for (OrderItemInfo orderItemInfo : orderItemInfoList) {
            totalCost += orderItemInfo.getTotalPrice();
        }
        return totalCost;
    }
}

