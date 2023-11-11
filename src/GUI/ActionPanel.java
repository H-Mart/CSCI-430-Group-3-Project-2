package GUI;

import javax.swing.*;

public class ActionPanel extends JPanel {
    public ActionPanel() {
        super();
    }

    public void clear() {
        removeAll();
        revalidate();
        repaint();
    }
}
