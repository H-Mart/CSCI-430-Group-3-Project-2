
public class WishlistTest {

    private static void printList(Wishlist wl) {
        var wlIterator = wl.getIterator();
        while (wlIterator.hasNext()) {
            System.out.println(wlIterator.next());
        }
    }
    public static void runTest() {
        System.out.println("---------- Begin Wishlist Test ----------");
        var wishlist = new Wishlist();
        var wishlistItem = new WishlistItem("1", 2);
        var wishlistItem2 = new WishlistItem("2", 3);
        System.out.println("Printing Empty Wishlist: ");
        printList(wishlist);
        System.out.println("Expected (should be blank): ");
        System.out.println();

        System.out.println("Printing Wishlist with one item: ");
        wishlist.addWishlistItem(wishlistItem);
        printList(wishlist);
        System.out.println("Expected:");
        System.out.println("productId: 1 quantity: 2");
        System.out.println();

        System.out.println("Printing Wishlist with two items: ");
        wishlist.addWishlistItem(wishlistItem2);
        printList(wishlist);
        System.out.println("Expected:");
        System.out.println("productId: 1 quantity: 2");
        System.out.println("productId: 2 quantity: 3");
        System.out.println("---------- End Wishlist Test ----------");
    }
}
