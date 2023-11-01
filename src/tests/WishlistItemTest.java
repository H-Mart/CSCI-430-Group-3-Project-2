
public class WishlistItemTest {
    public static void runTest() {
        System.out.println("---------- Begin WishlistItem Test ----------");
        var wishlistItem = new WishlistItem("1", 2);
        System.out.println("WishlistItem id: " + wishlistItem.getProductId() + ", expected: 1");
        System.out.println("WishlistItem quantity: " + wishlistItem.getQuantity() + ", expected: 2");
        System.out.println("WishlistItem toString: " + wishlistItem + "\n\texpected: productId: 1 quantity: 2");
        System.out.println("---------- End WishlistItem Test ----------");
    }
}
