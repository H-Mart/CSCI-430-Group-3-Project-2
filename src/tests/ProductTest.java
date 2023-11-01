
public class ProductTest {
    public static void runTest() {
        System.out.println("---------- Begin Product Test ----------");
        var idServer = new IdServer();
        Product product = new Product("Test Product", 1.99, 10, idServer);
        System.out.println("Product name: " + product.getName() + ", expected: Test Product");
        System.out.println("Product price: " + product.getPrice() + ", expected: 1.99");
        System.out.println("Product quantity: " + product.getQuantity() + ", expected: 10");
        System.out.println("Product id: " + product.getId() + ", expected: 1");
        System.out.println();
        System.out.println("Product toString: " + product + "\nexpected: ID: 1, Price: 1.99, Quantity: 10, Name: Test Product");
        System.out.println("---------- End Product Test ----------");
    }
}
