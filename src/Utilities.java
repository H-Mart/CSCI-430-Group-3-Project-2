import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utilities {
    private static final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    public static String getUserInput() {
        String inputLine = "";
        try {
            inputLine = Utilities.inputReader.readLine().trim();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return inputLine;
    }

    // prints a message saying the option is not implemented, for use in stubs
    @SuppressWarnings("unused")
    public static void optionNotImplemented() {
        System.out.println("Option not implemented");
    }
}
