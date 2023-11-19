import javax.swing.*;
import java.awt.*;
import java.util.function.Predicate;

public class Utilities {
    public static String getValidInput(Component parentComponent, String message, String errorMessage,
                                Predicate<String> validator) {
        while (true) {
            String input = JOptionPane.showInputDialog(parentComponent, message);
            if (input == null || !validator.test(input)) {
                if (input == null) return null;
                JOptionPane.showMessageDialog(parentComponent, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                return input;
            }
        }
    }

    // prints a message saying the option is not implemented, for use in stubs
    @SuppressWarnings("unused")
    public static void optionNotImplemented() {
        System.out.println("Option not implemented");
    }
}
