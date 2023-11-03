import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class WarehouseContext {
    private static WarehouseContext context;

    private final WarehouseState[] states;
    private final int[][] nextStates;

    private int currentState;
    public static final int EXIT = -1;
    public static final int LOGIN = 0;
    public static final int CLIENT = 1;
    public static final int CLERK = 2;
    public static final int MANAGER = 3;
    public static final int ILLEGAL = -2;

    public static String currentClientId = null;

    private final Deque<Integer> loginStack = new ArrayDeque<>();

    private static Boolean systemRunning = true;

    private WarehouseContext() {
        var f = new File("warehouse.ser");
        if (f.exists()) {
            System.out.print("Load saved data? (y/n): ");
            String input = Utilities.getUserInput();

            if (input.equalsIgnoreCase("y")) {
                if (Warehouse.deserializeWarehouse()) {
                    System.out.println("The warehouse has been successfully loaded from the file warehouse.ser \n");
                } else {
                    System.out.println("There has been an error in loading \n");
                }
            }
        }
        else {
            System.out.println("No saved data found \n");
        }
        states = new WarehouseState[4];
        states[0] = LoginState.instance();
        states[1] = ClientMenuState.instance();
        states[2] = ClerkMenuState.instance();
        states[3] = ManagerMenuState.instance();
        // @formatter:off
        nextStates = new int[][]{
                {EXIT,  CLIENT,  CLERK,   MANAGER},
                {LOGIN, ILLEGAL, ILLEGAL, ILLEGAL},
                {LOGIN, CLIENT,  ILLEGAL, ILLEGAL},
                {LOGIN, CLIENT,  CLERK,   ILLEGAL}
        };
        // @formatter:on
        currentState = LOGIN;
    }

    public static WarehouseContext instance() {
        return Objects.requireNonNullElseGet(context, () -> context = new WarehouseContext());
    }

    public void changeState(int transition) {
        currentState = this.nextStates[currentState][transition];

        if (currentState == LOGIN) {
            var currentUser = loginStack.peek();
            if (currentUser == null) {
                System.out.println("Error: no user logged in, but logout was called");
                terminate();
            }
            loginStack.pop();
            currentUser = loginStack.peek();
            currentState = Objects.requireNonNullElse(currentUser, LOGIN);
        } else if (currentState == ILLEGAL) {
            System.out.println("Error has occurred");
            terminate();
        } else if (currentState == EXIT) {
            terminate();
        }

        states[currentState].run();
    }

    private void terminate() {
        System.out.println("Save data? (y/n): ");
        String input = Utilities.getUserInput();

        if (input.equalsIgnoreCase("y")) {
            if (Warehouse.serializeWarehouse()) {
                System.out.println("The warehouse has been successfully saved in the file warehouse.ser \n");
            } else {
                System.out.println("There has been an error in saving \n");
            }
        }

        System.out.println("Goodbye \n ");
        systemRunning = false;
        System.exit(0);
    }

    public void setLogin(int login) {
        loginStack.push(login);
    }

    public void setCurrentClientId(String clientId) {
        currentClientId = clientId;
    }

    public void process() {
        states[currentState].run();
    }

    public static Boolean isSystemRunning() {
        return systemRunning;
    }

    public static void main(String[] args) {
        WarehouseContext.instance().process();
    }
}
