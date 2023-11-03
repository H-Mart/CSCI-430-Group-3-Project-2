import java.util.Optional;

public class LoginState implements WarehouseState {
    private static LoginState instance;

    private LoginState() {

    }

    public static LoginState instance() {
        if (instance == null) {
            instance = new LoginState();
        }
        return instance;
    }

    private void becomeClient() {
        System.out.print("Enter client id: ");
        String clientId = Utilities.getUserInput();
        System.out.println();

        Optional<Client> client = Warehouse.instance().getClientById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found");
            return;
        }

        WarehouseContext.instance().setCurrentClientId(clientId);
        WarehouseContext.instance().setLogin(WarehouseContext.CLIENT);
        WarehouseContext.instance().changeState(WarehouseContext.CLIENT);
    }

    private void becomeClerk() {
        WarehouseContext.instance().setLogin(WarehouseContext.CLERK);
        WarehouseContext.instance().changeState(WarehouseContext.CLERK);
    }

    private void becomeManager() {
        WarehouseContext.instance().setLogin(WarehouseContext.MANAGER);
        WarehouseContext.instance().changeState(WarehouseContext.MANAGER);
    }

    private void exit() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    private void executeOption(int option) {
        switch (option) {
            case 1:
                becomeClient();
                break;
            case 2:
                becomeClerk();
                break;
            case 3:
                becomeManager();
                break;
            case 0:
                exit();
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
    }

    public void run() {
        while (WarehouseContext.isSystemRunning()) {
            System.out.println("Login Menu: ");
            System.out.println("    1. Login as Client");
            System.out.println("    2. Login as Clerk");
            System.out.println("    3. Login as Manager");
            System.out.println("    0. Exit");
            System.out.print("> ");

            String input = Utilities.getUserInput();
            System.out.println();
            try {
                executeOption(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            }
        }
    }
}