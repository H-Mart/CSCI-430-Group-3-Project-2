package GUI;

import javax.swing.*;

public class ActionPanel extends JPanel {
    public ActionPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Actions"));
    }

    public void clear() {
        removeAll();
        revalidate();
        repaint();
    }
}
