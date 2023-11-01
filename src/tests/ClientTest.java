
public class ClientTest {

    private static void printWishlist(Wishlist wl) {
        var wlIterator = wl.getIterator();
        while (wlIterator.hasNext()) {
            System.out.println(wlIterator.next());
        }
    }

    public static void runTest() {
        System.out.println("---------- Begin Client Test ----------");
        var idServer = new IdServer();
        var client = new Client("John Doe", "Test Address", idServer);
        System.out.println("Client id: " + client.getId() + ", expected: 1");
        System.out.println("Client name: " + client.getName() + ", expected: John Doe");
        System.out.println("Client address: " + client.getAddress() + ", expected: Test Address");
        System.out.println("Client toString: " + client + "\n\tID: 1, Name: John Doe, Address: Test Address");
        System.out.println();
        System.out.println("Adding product to wishlist with id 1 and quantity 2");
        client.addToWishlist("1", 2);
        System.out.println("Client wishlist: ");
        printWishlist(client.getWishlist());
        System.out.println("Expected:");
        System.out.println("productId: 1 quantity: 2");
        System.out.println();
        System.out.println("Adding product to wishlist with id 2 and quantity 3");
        client.addToWishlist("2", 3);
        System.out.println("Client wishlist: ");
        printWishlist(client.getWishlist());
        System.out.println("Expected:");
        System.out.println("productId: 1 quantity: 2");
        System.out.println("productId: 2 quantity: 3");
    }
}
