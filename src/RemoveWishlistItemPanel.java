import javax.swing.*;
import java.awt.*;

class RemoveWishlistItemPanel extends JPanel {
    private final WishlistItem item;
    private final JLabel infoLabel;
    private final JLabel removeLabel;
    private final JCheckBox removeCheckBox;

    public RemoveWishlistItemPanel(WishlistItem item) {
        super();
        this.item = item;

        infoLabel = new JLabel();
        removeLabel = new JLabel("Remove: ");
        removeCheckBox = new JCheckBox();

        buildGUI();
    }

    private void buildGUI() {
        infoLabel.setText(getProductInfoString());
        infoLabel.setHorizontalTextPosition(JLabel.CENTER);

        GroupLayout panelLayout = new GroupLayout(this);
        this.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(infoLabel, GroupLayout.PREFERRED_SIZE, 167,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeLabel, GroupLayout.PREFERRED_SIZE, 50,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(removeCheckBox, GroupLayout.PREFERRED_SIZE, 167,
                                        GroupLayout.PREFERRED_SIZE)
                        ));

        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(infoLabel, GroupLayout.PREFERRED_SIZE, 56,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(removeLabel)
                                        .addComponent(removeCheckBox)
                                )));

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

    public boolean getRemove() {
        return removeCheckBox.isSelected();
    }

    public void reset() {
        removeCheckBox.setSelected(false);
    }
}