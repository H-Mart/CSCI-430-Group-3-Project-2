import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

class WishlistQuantityPanel extends JPanel {
    private final WishlistItem item;
    private final JLabel priceLabel;
    private final JLabel initialqtyLabel;
    private final JLabel newQtyLabel;
    private final JSpinner qtySpinner;

    public WishlistQuantityPanel(WishlistItem item) {
        super();
        this.item = item;

        priceLabel = new JLabel();
        initialqtyLabel = new JLabel();
        newQtyLabel = new JLabel();
        qtySpinner = new JSpinner();

        buildGUI();
    }

    private void buildGUI() {
        priceLabel.setText(getProductInfoString());

        initialqtyLabel.setText("Initial Quantity: " + item.getQuantity());

        initialqtyLabel.setPreferredSize(new Dimension(100, 50));
        initialqtyLabel.setMaximumSize(new Dimension(100, 50));

        newQtyLabel.setText("New Quantity: ");
        qtySpinner.setValue(item.getQuantity());

        qtySpinner.addChangeListener(e -> {
            var qty = (int) qtySpinner.getValue();
            if (qty < 0) {
                qtySpinner.setValue(0);
            } else if (qty != item.getQuantity()) {
                setBackground(new Color(194, 151, 215));
            } else {
                setBackground(UIManager.getColor("Panel.background"));
            }
        });

        GroupLayout panelLayout = new GroupLayout(this);
        this.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup()
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 167,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(initialqtyLabel, GroupLayout.PREFERRED_SIZE, 167,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(newQtyLabel)
                                .addGap(10, 10, 10)
                                .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE, 62,
                                        GroupLayout.PREFERRED_SIZE)
                        ));

        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup()
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 56,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(initialqtyLabel)
                                        .addComponent(newQtyLabel)
                                        .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        ));

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private String getProductInfoString() {
        var product = Warehouse.instance().getProductById(item.getProductId()).orElseThrow();
        return "<html>Product Name: " + product.getName()
                + "<br>Product Price: " + product.getPrice() + "<br><br></html>";
    }

    public WishlistItem getWishlistItem() {
        return item;
    }

    public int getQuantity() {
        try {
            qtySpinner.commitEdit();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return (int) qtySpinner.getValue();
    }

    public void reset() {
        initialqtyLabel.setText("Initial Quantity: " + item.getQuantity());
        qtySpinner.setValue(item.getQuantity());
        setBackground(UIManager.getColor("Panel.background"));
    }
}