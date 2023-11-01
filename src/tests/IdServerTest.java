
public class IdServerTest {
    public static void runTest() {
        System.out.println("---------- Begin IdServer Test ----------");
        IdServer idServer = new IdServer();
        System.out.println("IdServer idCounter starting value: " + idServer.getNextId() + ", expected: 1");
        System.out.println("IdServer generating new id: " + idServer.getNewId() + ", expected: 1");
        System.out.println("IdServer generating new id: " + idServer.getNewId() + ", expected: 2");
        System.out.println("IdServer generating new id: " + idServer.getNewId() + ", expected: 3");
        System.out.println("IdServer idCounter value now: " + idServer.getNextId() + ", expected: 4");
        System.out.println("---------- End IdServer Test ----------");
    }
}
