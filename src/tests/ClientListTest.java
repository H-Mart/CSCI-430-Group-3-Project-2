
public class ClientListTest {

    private static void printList(ClientList cll) {
        var cllIterator = cll.getIterator();
        while (cllIterator.hasNext()) {
            System.out.println(cllIterator.next());
        }
    }

    public static void runTest() {
        System.out.println("---------- Begin ClientList Test ----------");
        ClientList clientList = new ClientList();
        var idServer = new IdServer();
        var client = new Client("Test Client", "Test Address", idServer);
        var client2 = new Client("Test Client 2", "Test Address 2", idServer);
        System.out.println("Printing Empty ClientList: ");
        printList(clientList);
        System.out.println("Expected (should be blank): ");
        System.out.println();

        System.out.println("Printing ClientList with one client: ");
        clientList.insertClient(client);
        printList(clientList);
        System.out.println("Expected:\nID: 1, Name: Test Client, Address: Test Address");
        System.out.println();

        System.out.println("Printing ClientList with two clients: ");
        clientList.insertClient(client2);
        printList(clientList);
        System.out.println("Expected:");
        System.out.println("ID: 1, Name: Test Client, Address: Test Address");
        System.out.println("ID: 2, Name: Test Client 2, Address: Test Address 2");
        System.out.println("---------- End ClientList Test ----------");
    }
}
