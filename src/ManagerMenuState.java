import GUI.ActionPanel;
import GUI.ButtonPanel;
import GUI.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ManagerMenuState implements WarehouseState {
    private static ManagerMenuState instance;

    JFrame frame;
    AbstractButton addProductButton, acceptShipmentButton, clerkButton, logoutButton;

    MainPanel mainPanel;
    ButtonPanel buttonPanel;
    ActionPanel actionPanel;

    private ManagerMenuState() {
        mainPanel = new MainPanel();
        buttonPanel = new ButtonPanel();
        actionPanel = new ActionPanel();

        setDefaultLayout();
    }

    private void setDefaultLayout() {
        buttonPanel.setLayout(new GridLayout(4, 1, 5, 5));
        actionPanel.setLayout(new GridLayout(1, 1));
    }

    public static ManagerMenuState instance() {
        return Objects.requireNonNullElseGet(instance, () -> instance = new ManagerMenuState());
    }

    private void buildGUI() {
        frame.setTitle("Manager Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);

        addProductButton = new JButton("Add Product");
        acceptShipmentButton = new JButton("Accept Shipment");
        clerkButton = new JButton("Clerk");
        logoutButton = new JButton("Logout");

        addProductButton.addActionListener(e -> addProduct());
        acceptShipmentButton.addActionListener(e -> acceptShipment());
        clerkButton.addActionListener(e -> becomeClerk());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(addProductButton);
        buttonPanel.add(acceptShipmentButton);
        buttonPanel.add(clerkButton);
        buttonPanel.add(logoutButton);

        frame.add(mainPanel);
        mainPanel.addButtonPanel(buttonPanel);
        mainPanel.addActionPanel(actionPanel);
        frame.setVisible(true);
    }

    public void run() {
        frame = WarehouseContext.instance().getFrame();
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        mainPanel.clear();
        buttonPanel.clear();
        actionPanel.clear();
        buildGUI();
    }

    public void logout() {
        WarehouseContext.instance().changeState(WarehouseContext.LOGIN);
    }

    public void becomeClerk() {
        WarehouseContext.instance().setLogin(WarehouseContext.CLERK);
        WarehouseContext.instance().changeState(WarehouseContext.CLERK);
    }

    public void addProduct() {
        var name = Utilities.getValidInput(frame, "Enter product name: ", "Product name cannot be empty",
                s -> !s.isEmpty());
        if (name == null) {
            return;
        }

        var price = Utilities.getValidInput(frame, "Enter product price: ", "Invalid Price",
                (input) -> {
                    try {
                        var p = Double.parseDouble(input);
                        return (p >= 0);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
        if (price == null) {
            return;
        }

        var quantity = Utilities.getValidInput(frame, "Enter product quantity: ", "Invalid Quantity",
                (input) -> {
                    try {
                        var q = Integer.parseInt(input);
                        return (q >= 0);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
        if (quantity == null) {
            return;
        }

        var addedId = Warehouse.instance().addProduct(name, Double.parseDouble(price), Integer.parseInt(quantity));

        if (Warehouse.instance().getProductById(addedId).isPresent()) {
            JOptionPane.showMessageDialog(frame,
                    "Product added - " + Warehouse.instance().getProductById(addedId).get());
        } else {
            JOptionPane.showMessageDialog(frame, "Product not added", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acceptShipment() {
        var productId = Utilities.getValidInput(frame, "Enter the product id for the shipment: ",
                "Invalid Product ID",
                s -> {
                    if (s.isEmpty()) {
                        return false;
                    }
                    return Warehouse.instance().getProductById(s).isPresent();
                });
        if (productId == null) {
            return;
        }


        var quantity = Utilities.getValidInput(frame, "Enter the quantity: ", "Invalid Quantity", (input) -> {
            try {
                var q = Integer.parseInt(input);
                return (q >= 0);
            } catch (NumberFormatException e) {
                return false;
            }
        });
        if (quantity == null) {
            return;
        }

        var product = Warehouse.instance().getProductById(productId).orElseThrow();
        product.setQuantity(product.getQuantity() + Integer.parseInt(quantity));

        JOptionPane.showMessageDialog(frame,
                "Updated product quantity from " + (product.getQuantity() - Integer.parseInt(quantity)) + " to " + product.getQuantity());

        if (product.getWaitlist().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No items in the waitlist for this product");
            return;
        }

        var doProcessWaitlist = JOptionPane.showConfirmDialog(frame,
                product.getWaitlist().size() + " items in the waitlist for this product. Process them?",
                "Process Waitlist",
                JOptionPane.YES_NO_OPTION);
        if (doProcessWaitlist == JOptionPane.YES_OPTION) {
            processWaitlist(productId);
        }
    }

    private void processWaitlist(String productId) {
        var waitlistInfoLabel = new JLabel();
        var qtySpinnerLabel = new JLabel();
        var qtySpinner = new JSpinner();
        var remainingQuantityLabel = new JLabel();

        var fillOrderButton = new JButton("Fill Order");
        var skipButton = new JButton("Skip");

        var product = Warehouse.instance().getProductById(productId).orElseThrow();

        var waitlistCopyIterator = new Waitlist(product.getWaitlist()).getIterator();

        // the AtomicReference is used to allow the lambda to modify the value of the waitlistItem
        // this is necessary because the lambda is not allowed to modify local variables
        AtomicReference<WaitlistItem> waitlistItem = new AtomicReference<>(waitlistCopyIterator.next());
        var client = Warehouse.instance().getClientById(waitlistItem.get().getClientId()).orElseThrow();
        waitlistInfoLabel.setText("<html>Client Name " + client.getName() + "<br>Waitlist Quantity: " + waitlistItem.get().getQuantity() +
                "<br><br></html>");
        qtySpinnerLabel.setText("Quantity: ");
        qtySpinner.setValue(waitlistItem.get().getQuantity());

        remainingQuantityLabel.setText("Remaining Quantity: " + product.getQuantity());

        fillOrderButton.addActionListener(e -> {
            Warehouse.instance().fillWaitlistOrder(waitlistItem.get().getWaitlistItemId(), productId,
                    (int) qtySpinner.getValue());
            if (waitlistCopyIterator.hasNext()) {
                waitlistItem.set(waitlistCopyIterator.next());
                waitlistInfoLabel.setText("<html>Client Name " + client.getName() + "<br>Waitlist Quantity: " + waitlistItem.get().getQuantity() +
                        "<br><br></html>");
                qtySpinner.setValue(waitlistItem.get().getQuantity());
                remainingQuantityLabel.setText("Remaining Quantity: " + product.getQuantity());
                waitlistItem.set(waitlistCopyIterator.next());
            } else {
                actionPanel.clear();
                JOptionPane.showMessageDialog(frame, "Waitlist processed");
            }
        });

        skipButton.addActionListener(e -> {
            if (waitlistCopyIterator.hasNext()) {
                var nextWaitlistItem = waitlistCopyIterator.next();
                waitlistInfoLabel.setText("<html>Client Name " + client.getName() + "<br>Waitlist Quantity: " + waitlistItem.get().getQuantity() +
                "<br><br></html>");
                qtySpinner.setValue(nextWaitlistItem.getQuantity());
            } else {
                actionPanel.clear();
                JOptionPane.showMessageDialog(frame, "Waitlist processed");
            }
        });

        GroupLayout layout = new GroupLayout(actionPanel);
        actionPanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(waitlistInfoLabel, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                        .addComponent(remainingQuantityLabel, GroupLayout.DEFAULT_SIZE, 194,
                                                Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(fillOrderButton, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(qtySpinnerLabel, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(skipButton, GroupLayout.PREFERRED_SIZE, 92,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE, 86,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGap(449, 449, 449))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(qtySpinnerLabel)
                                        .addComponent(qtySpinner, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(waitlistInfoLabel, GroupLayout.PREFERRED_SIZE, 60,
                                                GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(fillOrderButton)
                                        .addComponent(skipButton)
                                        .addComponent(remainingQuantityLabel))
                                .addContainerGap(515, Short.MAX_VALUE))
        );
    }
}
