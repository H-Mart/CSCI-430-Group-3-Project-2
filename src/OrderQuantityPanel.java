import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

class OrderQuantityPanel extends JPanel {
    private final OrderItemInfo item;
    private final JLabel priceLabel;
    private final JLabel qtySpinnerLabel;
    private final JSpinner qtySpinner;

    public OrderQuantityPanel(OrderItemInfo item) {
        super();
        this.item = item;

        priceLabel = new JLabel();
        qtySpinnerLabel = new JLabel();
        qtySpinner = new JSpinner();

        buildGUI();
    }

    private void buildGUI() {
        priceLabel.setText(getProductInfoString());

        qtySpinnerLabel.setText("New Quantity: ");
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
                                .addGap(45, 45, 45)
                                .addComponent(qtySpinnerLabel)
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
                                        .addComponent(qtySpinnerLabel)
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

    public OrderItemInfo getOrderItemInfo() {
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