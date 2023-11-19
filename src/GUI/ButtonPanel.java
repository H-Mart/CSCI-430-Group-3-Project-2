package GUI;

import javax.swing.*;

public class ButtonPanel extends JPanel {
    public ButtonPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Options"));
    }

    public void clear() {
        removeAll();
        revalidate();
        repaint();
    }
}
