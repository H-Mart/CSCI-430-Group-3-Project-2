import java.io.Serializable;

public class IdServer implements Serializable {
    private static final long serialVersionUID = 1L;
    private int nextId = 1;

    public IdServer() {
    }

    /**
     * Get the current next id and increment
     * @precondition none
     * @postcondition nextId is incremented and returned
     * @return the next id
     */
    public int getNewId() {
        return nextId++;
    }

    /**
     * Get the current next id without incrementing
     * @precondition none
     * @postcondition nextId is returned
     * @return the next id
     */
    public int getNextId() {
        return nextId;
    }

    /**
     * String form of the collection
     */
    @Override
    public String toString() {
        return ("IdServer" + nextId);
    }
}
