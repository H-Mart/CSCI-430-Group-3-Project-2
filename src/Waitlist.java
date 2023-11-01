import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class Waitlist implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<WaitlistItem> waitlist;

    public Waitlist() {
        waitlist = new ArrayList<>();
    }

    public Waitlist(Waitlist other) {
        waitlist = new ArrayList<>();
        for (WaitlistItem item : other.waitlist) {
            waitlist.add(new WaitlistItem(item));
        }
    }

    public void addWaitlistItem(WaitlistItem item) {
        waitlist.add(item);
    }

    public void removeWaitlistItem(int waitlistItemId) {
        int index = -1;
        for (var item : waitlist) {
            index++;
            if (item.getWaitlistItemId() == waitlistItemId) {
                waitlist.remove(index);
                return;
            }
        }
    }

    public Optional<WaitlistItem> getWaitlistItem(int waitlistItemId) {
        for (var item : waitlist) {
            if (item.getWaitlistItemId() == waitlistItemId) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public Iterator<WaitlistItem> getIterator() {
        return waitlist.iterator();
    }

}
