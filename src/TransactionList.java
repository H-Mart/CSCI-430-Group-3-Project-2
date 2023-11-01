import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionList implements Serializable {
    private final List<TransactionRecord> transactionList;

    public TransactionList() {
        transactionList = new ArrayList<>();
    }

    public void insertTransaction(TransactionRecord transaction) {
        transactionList.add(transaction);
    }

    public Iterator<TransactionRecord> getIterator() {
        return transactionList.iterator();
    }
}
