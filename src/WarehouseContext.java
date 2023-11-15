import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;

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
    public static final int WISHLIST = 4;
    public static final int CLIENTQUERY = 5;
    public static final int ILLEGAL = -2;

    public static String currentClientId = null;

    private final Deque<Integer> loginStack = new ArrayDeque<>();

    private final JFrame frame = new JFrame();

    private WarehouseContext() {
        setLookAndFeel();

        askToLoad();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                askToSave();
            }
        });

        states = new WarehouseState[6];
        states[0] = LoginState.instance();
        states[1] = ClientMenuState.instance();
        states[2] = ClerkMenuState.instance();
        states[3] = ManagerMenuState.instance();
        states[4] = WishlistOperationsState.instance();
        states[5] = ClientInfoState.instance();
        // @formatter:off
        nextStates = new int[][]{
                {EXIT,    CLIENT,  CLERK,   MANAGER, ILLEGAL,  ILLEGAL},
                {LOGIN,   ILLEGAL, ILLEGAL, ILLEGAL, WISHLIST, ILLEGAL},
                {LOGIN,   CLIENT,  ILLEGAL, ILLEGAL, ILLEGAL,  CLIENTQUERY},
                {LOGIN,   CLIENT,  CLERK,   ILLEGAL, ILLEGAL,  ILLEGAL},
                {ILLEGAL, CLIENT,  ILLEGAL, ILLEGAL, ILLEGAL,  ILLEGAL},
                {ILLEGAL, ILLEGAL, CLERK,   ILLEGAL, ILLEGAL,  ILLEGAL}
        };
        // @formatter:on
        currentState = LOGIN;

    }

    public static WarehouseContext instance() {
        return Objects.requireNonNullElseGet(context, () -> context = new WarehouseContext());
    }

    private void askToLoad() {
        var f = new File("warehouse.ser");
        if (f.isFile()) {
            var load = JOptionPane.showConfirmDialog(frame, "Load saved data?", "Load", JOptionPane.YES_NO_OPTION);
            if (load == JOptionPane.YES_OPTION) {
                Warehouse.deserializeWarehouse();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No saved data found", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void askToSave() {
        var save = JOptionPane.showConfirmDialog(frame, "Save data?", "Save", JOptionPane.YES_NO_OPTION);
        if (save == JOptionPane.YES_OPTION) {
            Warehouse.serializeWarehouse();
        }
    }

    private void setLookAndFeel() {
        // set look and feel
        // attempt to set look and feel to Windows, GTK+, Nimbus, then system, in that order

        try {
            var looksAvailable = new HashMap<String, UIManager.LookAndFeelInfo>();
            Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .peek(info -> looksAvailable.put(info.getName(), info));

            if (looksAvailable.containsKey("Windows")) {
                UIManager.setLookAndFeel(looksAvailable.get("Windows").getClassName());
            } else if (looksAvailable.containsKey("GTK+")) {
                UIManager.setLookAndFeel(looksAvailable.get("GTK+").getClassName());
            } else if (looksAvailable.containsKey("Nimbus")) {
                UIManager.setLookAndFeel(looksAvailable.get("Nimbus").getClassName());
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
            System.out.println("Error setting look and feel");
        }
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
        frame.getContentPane().removeAll();
        states[currentState].run();
    }

    private void terminate() {
        askToSave();
        System.exit(0);
    }

    public JFrame getFrame() {
        return frame;
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

    public static void main(String[] args) {
        WarehouseContext.instance().process();
    }
}
