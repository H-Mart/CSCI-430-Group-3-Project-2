//import tests.*;

public class FullTester {
    public static void main(String[] args) {
        IdServerTest.runTest();
        System.out.println();

        ProductTest.runTest();
        System.out.println();

        ProductListTest.runTest();
        System.out.println();

        WishlistItemTest.runTest();
        System.out.println();

        WishlistTest.runTest();
        System.out.println();

        ClientTest.runTest();
        System.out.println();

        ClientListTest.runTest();
        System.out.println();

        WarehouseTest.runTest();
        System.out.println();
    }
}
