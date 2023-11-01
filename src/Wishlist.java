import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Wishlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<WishlistItem> wishlist;

    /**
     * @postcondition creates empty wishlist
     */
    public Wishlist() {
        wishlist = new ArrayList<>();
    }


    // copy constructor
    public Wishlist(Wishlist other) {
        wishlist = new ArrayList<>();
        for (WishlistItem item : other.wishlist) {
            wishlist.add(new WishlistItem(item));
        }
    }
    /**
     * Adds a WishlistItem to the wishlist
     *
     * @param wishlistItem wishlist item to add
     * @postcondition wishlist item is added to wishlist
     */
    public void addWishlistItem(WishlistItem wishlistItem) {
        wishlist.add(wishlistItem);
    }

    public void removeWishlistItem(String productId) {
        int index = -1;
        for (var item : wishlist) {
            index++;
            if (item.getProductId().equals(productId)) {
                wishlist.remove(index);
                return;
            }
        }
    }

    public void updateWishlistItemQuantity(String productId, int quantity) {
        for (var item : wishlist) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public Iterator<WishlistItem> getIterator() {
        return wishlist.iterator();
    }

    /**
     * Searches the wishlist for a WishlistItem with the given id
     *
     * @param productId the product id of the WishlistItem to fetch
     * @return an Optional containing the WishlistItem if it exists, otherwise an empty Optional
     * @precondition productId is not null
     * @postcondition an empty optional is returned if the WishlistItem does not exist, otherwise an optional containing the WishlistItem is returned
     */
    public Optional<WishlistItem> getWishlistItem(String productId) {
        for (WishlistItem wishlistItem : wishlist) {
            if (wishlistItem.getProductId().equals(productId)) {
                return Optional.of(wishlistItem);
            }
        }
        return Optional.empty();
    }
}
