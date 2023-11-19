import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;

class OrderQuantityPanel extends JPanel {
    private final OrderItemInfo initialItem;
    private final JLabel priceLabel;
    private final JLabel qtySpinnerLabel;
    private final JSpinner qtySpinner;
    private final JButton removeButton;

    public OrderQuantityPanel(OrderItemInfo initialItem) {
        super();
        this.initialItem = initialItem;

        priceLabel = new JLabel();
        qtySpinnerLabel = new JLabel();
        qtySpinner = new JSpinner();
        removeButton = new JButton();

        buildGUI();
    }

    private void buildGUI() {
        priceLabel.setText(getProductInfoString());

        qtySpinnerLabel.setText("Order Quantity: ");
        qtySpinner.setValue(initialItem.getQuantity());

        qtySpinner.addChangeListener(e -> {
            var qty = (int) qtySpinner.getValue();
            if (qty < 0) {
                qtySpinner.setValue(0);
            }
        });

        removeButton.setText("Remove From Order");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(qtySpinnerLabel, GroupLayout.PREFERRED_SIZE, 91,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(removeButton)
                                .addContainerGap(155, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(10, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 41,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(qtySpinnerLabel)
                                        .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(removeButton))
                                .addContainerGap(10, Short.MAX_VALUE)
                        ));

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private String getProductInfoString() {
        var product = Warehouse.instance().getProductById(initialItem.getProductId()).orElseThrow();
        return "<html>Product Name: " + product.getName()
                + "<br>Product Price: " + product.getPrice() + "<br><br></html>";
    }

    public OrderItemInfo getCurrentItem() {
        return new OrderItemInfo(initialItem.getProductId(), getQuantity(), initialItem.getPrice());
    }

    public void setRemoveButtonAction(ActionListener removeButtonAction) {
        removeButton.addActionListener(removeButtonAction);
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