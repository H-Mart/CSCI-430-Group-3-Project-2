
public class ProductListTest {

    private static void printList(ProductList pl) {
        var plIterator = pl.getIterator();
        while (plIterator.hasNext()) {
            System.out.println(plIterator.next());
        }
    }
    public static void runTest() {
        System.out.println("---------- Begin ProductList Test ----------");
        ProductList productList = new ProductList();
        var idServer = new IdServer();
        var product = new Product("Test Product", 1.99, 10, idServer);
        var product2 = new Product("Test Product 2", 2.99, 20, idServer);
        System.out.println("Printing Empty ProductList: ");
        printList(productList);
        System.out.println("Expected (should be blank): ");
        System.out.println();

        System.out.println("Printing ProductList with one product: ");
        productList.insertProduct(product);
        printList(productList);
        System.out.println("Expected:\nID: 1, Price: 1.99, Quantity: 10, Name: Test Product");
        System.out.println();

        System.out.println("Printing ProductList with two products: ");
        productList.insertProduct(product2);
        printList(productList);
        System.out.println("Expected:");
        System.out.println("ID: 1, Price: 1.99, Quantity: 10, Name: Test Product");
        System.out.println("ID: 2, Price: 2.99, Quantity: 20, Name: Test Product 2");
        System.out.println("---------- End ProductList Test ----------");
    }
}
