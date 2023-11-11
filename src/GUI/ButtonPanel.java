package GUI;

import javax.swing.*;

public class ButtonPanel extends JPanel {
    public ButtonPanel() {
        super();
    }

    public void clear() {
        removeAll();
        revalidate();
        repaint();
    }
}
