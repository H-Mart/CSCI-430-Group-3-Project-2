package GUI;

import javax.swing.*;

public class MainPanel extends JPanel {
    public MainPanel() {
        super();
    }

    public void clear() {
        removeAll();
        revalidate();
        repaint();
    }
}
