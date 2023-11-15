package GUI;

import javax.swing.*;
import java.awt.*;

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
