import javax.swing.*;

class OrderItemPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final WishlistItem item;
    private final JTextArea itemLabel;
    private final JLabel quantityLabel;
    private final JTextField quantityField;

    public OrderItemPanel(WishlistItem item) {
        super();
        this.item = item;
        itemLabel = new JTextArea();
        itemLabel.setText(getProductInfoString());
        itemLabel.setEditable(false);
        quantityLabel = new JLabel("Quantity: ");
        quantityField = new JTextField(Integer.toString(item.getQuantity()), 5);
        add(itemLabel);
        add(quantityLabel);
        add(quantityField);
    }

    private String getProductInfoString() {
        var product = Warehouse.instance().getProductById(item.getProductId()).orElseThrow();
        return "Product ID: " + product.getId() + "\nProduct Name: " + product.getName()
                + "\nProduct Price: " + product.getPrice() + "\nProduct Quantity: " + product.getQuantity() + "\n\n";
    }

    public WishlistItem getWishlistItem() {
        return item;
    }

    public int getQuantity() {
        return Integer.parseInt(quantityField.getText());
    }
}