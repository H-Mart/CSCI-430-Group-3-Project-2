import javax.swing.*;
import java.text.ParseException;

class OrderItemPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final WishlistItem item;
    private final JLabel priceLabel;
    private final JLabel qtyLabel;
    private final JSpinner qtySpinner;

    public OrderItemPanel(WishlistItem item) {
        super();
        this.item = item;
        priceLabel = new JLabel();
        qtyLabel = new JLabel();
        qtySpinner = new JSpinner();

        buildGUI();
    }

    private void buildGUI() {
        priceLabel.setText(getProductInfoString());

        qtyLabel.setText("Initial Quantity");

        var qtySpinner = new JSpinner();

        GroupLayout panelLayout = new GroupLayout(this);
        this.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup()
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 167,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(qtyLabel)
                                .addGap(61, 61, 61)
                                .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE, 62,
                                        GroupLayout.PREFERRED_SIZE)
                        ));
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup()
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGap(5, 7, 10)
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 56,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(qtyLabel)
                                        .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        ));
    }

    private String getProductInfoString() {
        var product = Warehouse.instance().getProductById(item.getProductId()).orElseThrow();
        return "<html>Product ID: " + product.getId() + "<br>Product Name: " + product.getName()
                + "<br>Product Price: " + product.getPrice() + "<br>Product Quantity: " + product.getQuantity() +
                "<br><br></html>";
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
}