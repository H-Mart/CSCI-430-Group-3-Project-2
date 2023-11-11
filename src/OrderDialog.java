import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class OrderDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public OrderDialog(JFrame parent) {
        super(parent, "Order", true);
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createUI();
//        add(new JLabel("Order Dialog"));
//        setVisible(true);
    }

    public void clear() {
        removeAll();
        revalidate();
        repaint();
    }

    public void createUI() {
        JPanel panel = new JPanel();
        var clientId = WarehouseContext.currentClientId;
        var client = Warehouse.instance().getClientById(clientId).orElseThrow();


        var itemList = new ArrayList<OrderItemPanel>();
        var clientWishlistIterator = client.getWishlist().getIterator();
        while (clientWishlistIterator.hasNext()) {
            var item = clientWishlistIterator.next();
            var itemPanel = new OrderItemPanel(item);
            itemList.add(itemPanel);
        }
        panel.setLayout(new GridLayout(itemList.size() + 1, 2));
        for (var itemPanel : itemList) {
            panel.add(itemPanel);
            var removeFromOrderButton = new JButton("Remove From Order");
            removeFromOrderButton.addActionListener(e -> {
                panel.remove(itemPanel);
                panel.remove(removeFromOrderButton);
                panel.revalidate();
                panel.repaint();
            });
            panel.add(removeFromOrderButton);
        }
        add(new JLabel("Order Dialog"));
        var scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane);
        pack();
        setVisible(true);
    }
}
