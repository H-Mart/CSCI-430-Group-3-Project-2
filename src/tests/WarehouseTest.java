
import java.util.Iterator;

public class WarehouseTest {

    private static void printProductList(Iterator<Product> plIterator) {
        while (plIterator.hasNext()) {
            System.out.println(plIterator.next());
        }
    }

    private static void printClientList(Iterator<Client> clIterator) {
        while (clIterator.hasNext()) {
            System.out.println(clIterator.next());
        }
    }

    private static void printWishlist(Wishlist wl) {
        var wlIterator = wl.getIterator();
        while (wlIterator.hasNext()) {
            System.out.println(wlIterator.next());
        }
    }

    public static void runTest() {
        System.out.println("---------- Begin Warehouse Test ----------");

        System.out.println("Testing addClient() and addProduct(): ");

        System.out.println("Printing Empty Client List: ");
        System.out.println("Expected (should be blank): ");

        System.out.println();

        System.out.println("Printing Empty Product List: ");
        System.out.println("Expected (should be blank): ");

        System.out.println();

        System.out.println("Adding Test Client and Test Product");
        Warehouse.instance().addClient("Test Client", "Test Address");
        Warehouse.instance().addProduct("Test Product", 10.99, 10);

        System.out.println("Printing Client List with one item: ");
        printClientList(Warehouse.instance().getClientIterator());
        System.out.println("Expected:");
        System.out.println("ID: 1, Name: Test Client, Address: Test Address");

        System.out.println();

        System.out.println("Printing Product List with one item: ");
        printProductList(Warehouse.instance().getProductIterator());
        System.out.println("Expected:");
        System.out.println("ID: 1, Price: 10.99, Quantity: 10, Name: Test Product");

        System.out.println();

        System.out.println("Adding Test Client 2 and Test Product 2");
        Warehouse.instance().addClient("Test Client 2", "Test Address 2");
        Warehouse.instance().addProduct("Test Product 2", 2.99, 20);

        System.out.println("Printing Client List with two items: ");
        printClientList(Warehouse.instance().getClientIterator());
        System.out.println("Expected:");
        System.out.println("ID: 1, Name: Test Client, Address: Test Address");
        System.out.println("ID: 2, Name: Test Client 2, Address: Test Address 2");

        System.out.println();

        System.out.println("Printing Product List with two items: ");
        printProductList(Warehouse.instance().getProductIterator());
        System.out.println("Expected:");
        System.out.println("ID: 1, Price: 10.99, Quantity: 10, Name: Test Product");
        System.out.println("ID: 2, Price: 2.99, Quantity: 20, Name: Test Product 2");

        System.out.println();

        System.out.println("Testing getClientById(): ");

        System.out.println("Printing Client with id 1: ");
        assert Warehouse.instance().getClientById("1").isPresent();
        System.out.println(Warehouse.instance().getClientById("1").get());
        System.out.println("Expected:");
        System.out.println("ID: 1, Name: Test Client, Address: Test Address");

        System.out.println();

        System.out.println("Testing getProductById(): ");

        System.out.println("Printing Product with id 1: ");
        assert Warehouse.instance().getProductById("1").isPresent();
        System.out.println(Warehouse.instance().getProductById("1").get());
        System.out.println("Expected:");
        System.out.println("ID: 1, Price: 10.99, Quantity: 10, Name: Test Product");

        System.out.println();

        System.out.println("Testing addProductToClientWishlist(): ");
        System.out.println("Adding Product with id 1 to Client with id 1's wishlist");
        Warehouse.instance().addProductToClientWishlist("1", "1", 2);
        System.out.println("Printing Wishlist of Client with id 1: ");
        printWishlist(Warehouse.instance().getClientById("1").get().getWishlist());
        System.out.println("Expected:");
        System.out.println("productId: 1 quantity: 2");

        System.out.println();
        System.out.println("---------- End Warehouse Test ----------");
    }
}
