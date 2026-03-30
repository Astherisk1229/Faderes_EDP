/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package faderesseanasther.Panels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import DbConnection.ComboItem;
import DbConnection.DatabaseConnection;
import faderesseanasther.Panels.EditProductDialog;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Admin
 */
public class ProductManagementPanel extends javax.swing.JPanel {

    private int loggedInUserId;
    private static final int COL_PRODUCT_ID = 0;
    private static final int COL_ACTION = 8;
    private javax.swing.ImageIcon editIcon;
    private javax.swing.ImageIcon deleteIcon;

    /**
     * Creates new form ProductManagementPanel
     */
    public ProductManagementPanel() {
        this(0);
    }

    public ProductManagementPanel(int loggedInUserId) {
        initComponents();
        this.loggedInUserId = loggedInUserId;

        setupTable();
        loadActionIcons();
        setupActionColumn();
        loadFilterCategories();
        loadProductsToTable();
    }

    private void setupTable() {
        productTbl.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Product ID", "BARCODE", "PRODUCT NAME", "CATEGORY",
                    "COST PRICE", "SELLING PRICE", "STOCK", "STATUS", "Actions"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_ACTION;
            }
        });

        productTbl.setRowHeight(32);
        productTbl.getTableHeader().setReorderingAllowed(false);
    }

    private void loadActionIcons() {
        editIcon = loadScaledIcon("/faderesseanasther/Icons/edit.png", 18, 18);
        deleteIcon = loadScaledIcon("/faderesseanasther/Icons/delete.png", 18, 18);
    }

    private ImageIcon loadScaledIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);

        if (url == null) {
            System.out.println("Icon not found: " + path);
            return null;
        }

        ImageIcon original = new ImageIcon(url);
        Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void setupActionColumn() {
        TableColumn actionColumn = productTbl.getColumnModel().getColumn(COL_ACTION);
        actionColumn.setPreferredWidth(110);
        actionColumn.setMinWidth(110);
        actionColumn.setMaxWidth(110);

        actionColumn.setCellRenderer(new ProductActionCellRenderer());
        actionColumn.setCellEditor(new ProductActionCellEditor());
    }

    private Frame getParentFrame() {
        Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            return (Frame) window;
        }
        return null;
    }

    private void openEditProductFrame(int productId) {
        Frame parentFrame = getParentFrame();

        EditProductDialog dialog = new EditProductDialog(parentFrame, true, this, loggedInUserId);
        dialog.loadProductById(productId);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadProductsToTable();
            }
        });

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private void logAuditTrail(String action, String description) {
        if (loggedInUserId <= 0) {
            System.out.println("Audit trail skipped: invalid logged-in user id.");
            return;
        }

        String sql = "INSERT INTO transactions_log (user_id, action, description, timestamp) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            String finalAction = (action == null || action.trim().isEmpty())
                    ? "UNKNOWN"
                    : action.trim().toUpperCase().replace("_", " ");

            if (finalAction.length() > 100) {
                finalAction = finalAction.substring(0, 100);
            }

            pst.setInt(1, loggedInUserId);
            pst.setString(2, finalAction);
            pst.setString(3, description == null ? "" : description);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save transaction log: " + ex.getMessage());
        }
    }

    public void refreshComboSources() {
        Object selected = cmbFilterCategory.getSelectedItem();
        int selectedId = -1;

        if (selected instanceof ComboItem) {
            selectedId = ((ComboItem) selected).getId();
        }

        cmbFilterCategory.removeAllItems();
        cmbFilterCategory.addItem(new ComboItem(-1, "All Categories"));

        String sql = "SELECT category_id, name FROM categories ORDER BY name ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                cmbFilterCategory.addItem(new ComboItem(
                        rs.getInt("category_id"),
                        rs.getString("name")
                ));
            }

            boolean restored = false;
            for (int i = 0; i < cmbFilterCategory.getItemCount(); i++) {
                ComboItem item = cmbFilterCategory.getItemAt(i);
                if (item != null && item.getId() == selectedId) {
                    cmbFilterCategory.setSelectedIndex(i);
                    restored = true;
                    break;
                }
            }

            if (!restored) {
                cmbFilterCategory.setSelectedIndex(0);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Refresh filter categories error: " + ex.getMessage());
        }

        searchProducts(); // or loadProductsToTable();
    }

    public void loadProductsToTable() {
        DefaultTableModel model = (DefaultTableModel) productTbl.getModel();
        model.setRowCount(0);

        String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
                + "c.name AS category_name, p.cost_price, p.selling_price, "
                + "p.stock_quantity, p.reorder_level "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "ORDER BY p.product_id DESC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int stockQty = rs.getInt("stock_quantity");
                int reorderLevel = rs.getInt("reorder_level");

                model.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("barcode"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    String.format("₱%,.2f", rs.getDouble("cost_price")),
                    String.format("₱%,.2f", rs.getDouble("selling_price")),
                    stockQty,
                    getStockStatus(stockQty, reorderLevel),
                    "Actions"
                });
            }

            hideProductIdColumn();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load products error: " + ex.getMessage());
        }
    }

    private void searchProducts() {
        DefaultTableModel model = (DefaultTableModel) productTbl.getModel();
        model.setRowCount(0);

        String keyword = searchField.getText().trim();

        int categoryId = -1;
        Object selected = cmbFilterCategory.getSelectedItem();
        if (selected instanceof ComboItem item) {
            categoryId = item.getId();
        }

        String sql = "SELECT p.product_id, p.barcode, p.name AS product_name, "
                + "c.name AS category_name, p.cost_price, p.selling_price, "
                + "p.stock_quantity, p.reorder_level "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "WHERE (p.name LIKE ? OR p.barcode LIKE ?) "
                + "AND (? = -1 OR p.category_id = ?) "
                + "ORDER BY p.product_id DESC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            pst.setInt(3, categoryId);
            pst.setInt(4, categoryId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int stockQty = rs.getInt("stock_quantity");
                    int reorderLevel = rs.getInt("reorder_level");

                    model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("barcode"),
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        String.format("₱%,.2f", rs.getDouble("cost_price")),
                        String.format("₱%,.2f", rs.getDouble("selling_price")),
                        stockQty,
                        getStockStatus(stockQty, reorderLevel),
                        "Actions"
                    });
                }
            }

            hideProductIdColumn();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }

    private void handleEdit(int productId) {
        openEditProductFrame(productId);
    }

    private void handleDelete(int productId) {
        deleteProduct(productId);
    }

    private class ProductActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        public ProductActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 2));
            setOpaque(true);

            styleActionButton(editButton, editIcon, "Edit");
            styleActionButton(deleteButton, deleteIcon, "Delete");

            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    private class ProductActionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        private int currentRow = -1;

        public ProductActionCellEditor() {
            panel.setOpaque(true);

            styleActionButton(editButton, editIcon, "Edit");
            styleActionButton(deleteButton, deleteIcon, "Delete");

            editButton.addActionListener(this);
            deleteButton.addActionListener(this);

            panel.add(editButton);
            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();

            int modelRow = productTbl.convertRowIndexToModel(currentRow);
            int productId = Integer.parseInt(productTbl.getModel().getValueAt(modelRow, COL_PRODUCT_ID).toString());

            if (e.getSource() == editButton) {
                handleEdit(productId);
            } else if (e.getSource() == deleteButton) {
                handleDelete(productId);
            }
        }
    }

    private void styleActionButton(JButton button, ImageIcon icon, String fallbackText) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        button.setToolTipText(fallbackText);

        if (icon != null) {
            button.setIcon(icon);
            button.setText("");
        } else {
            button.setText(fallbackText);
        }
    }

    private void loadFilterCategories() {
        cmbFilterCategory.removeAllItems();
        cmbFilterCategory.addItem(new ComboItem(-1, "All Categories"));

        String sql = "SELECT category_id, name FROM categories ORDER BY name ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                cmbFilterCategory.addItem(new ComboItem(
                        rs.getInt("category_id"),
                        rs.getString("name")
                ));
            }

            cmbFilterCategory.setSelectedIndex(0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load filter categories error: " + ex.getMessage());
        }
    }

    private String getStockStatus(int quantity, int reorderLevel) {
        if (quantity <= 0) {
            return "Out of Stock";
        }
        if (quantity <= reorderLevel) {
            return "Low Stock";
        }
        return "In Stock";
    }

    private void hideProductIdColumn() {
        productTbl.getColumnModel().getColumn(COL_PRODUCT_ID).setMinWidth(0);
        productTbl.getColumnModel().getColumn(COL_PRODUCT_ID).setMaxWidth(0);
        productTbl.getColumnModel().getColumn(COL_PRODUCT_ID).setWidth(0);
    }

    private void deleteProduct(int productId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String getProductSql = "SELECT name, barcode FROM products WHERE product_id = ?";
        String deleteSql = "DELETE FROM products WHERE product_id = ?";

        try (Connection con = DatabaseConnection.dbConnection()) {

            String productName = "";
            String barcode = "";

            try (PreparedStatement pstGet = con.prepareStatement(getProductSql)) {
                pstGet.setInt(1, productId);

                try (ResultSet rs = pstGet.executeQuery()) {
                    if (rs.next()) {
                        productName = rs.getString("name");
                        barcode = rs.getString("barcode");
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found.");
                        return;
                    }
                }
            }

            try (PreparedStatement pstDelete = con.prepareStatement(deleteSql)) {
                pstDelete.setInt(1, productId);

                int rows = pstDelete.executeUpdate();

                if (rows > 0) {
                    logAuditTrail(
                            "DELETE PRODUCT",
                            "Deleted product | Product ID: " + productId
                            + " | Product Name: " + productName
                            + " | Barcode: " + barcode
                    );

                    JOptionPane.showMessageDialog(this, "Product deleted successfully.");

                    if (!searchField.getText().trim().isEmpty()
                            || (cmbFilterCategory.getSelectedItem() instanceof ComboItem item && item.getId() != -1)) {
                        searchProducts();
                    } else {
                        loadProductsToTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        searchButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbFilterCategory = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        addProductButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        productTbl = new javax.swing.JTable();

        setMaximumSize(new java.awt.Dimension(1230, 750));
        setMinimumSize(new java.awt.Dimension(1230, 750));
        setPreferredSize(new java.awt.Dimension(1230, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(248, 232, 222));
        jPanel3.setMaximumSize(new java.awt.Dimension(1059, 780));
        jPanel3.setMinimumSize(new java.awt.Dimension(1059, 780));
        jPanel3.setPreferredSize(new java.awt.Dimension(1059, 780));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(210, 102, 90));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 0, 0), 3, true));
        jPanel5.setAlignmentY(5.0F);
        jPanel5.setPreferredSize(new java.awt.Dimension(354, 9));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        searchButton.setBackground(new java.awt.Color(41, 75, 107));
        searchButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchButton.setForeground(new java.awt.Color(255, 255, 255));
        searchButton.setText("Search");
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButtonMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchButtonMousePressed(evt);
            }
        });
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        jPanel5.add(searchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 20, -1, -1));

        searchField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel5.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 630, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Search");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 120, -1));

        cmbFilterCategory.setForeground(new java.awt.Color(44, 62, 80));
        cmbFilterCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterCategoryActionPerformed(evt);
            }
        });
        jPanel5.add(cmbFilterCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 20, 130, 30));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Filter");
        jPanel5.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 20, 40, 30));

        addProductButton.setText("Add Product");
        addProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductButtonActionPerformed(evt);
            }
        });
        jPanel5.add(addProductButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 20, 120, 30));

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 1170, 70));

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("PRODUCT MANAGEMENT");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, -1, -1));

        jPanel3.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 60));

        productTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "product_id", "barcode", "name", "category_id", "supplier_id", "cost_price", "selling_price", "stock_quantity", "reorder_level", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productTbl.getTableHeader().setReorderingAllowed(false);
        productTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productTblMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(productTbl);
        if (productTbl.getColumnModel().getColumnCount() > 0) {
            productTbl.getColumnModel().getColumn(0).setResizable(false);
            productTbl.getColumnModel().getColumn(1).setResizable(false);
            productTbl.getColumnModel().getColumn(2).setResizable(false);
            productTbl.getColumnModel().getColumn(3).setResizable(false);
            productTbl.getColumnModel().getColumn(4).setResizable(false);
            productTbl.getColumnModel().getColumn(5).setResizable(false);
            productTbl.getColumnModel().getColumn(6).setResizable(false);
            productTbl.getColumnModel().getColumn(7).setResizable(false);
            productTbl.getColumnModel().getColumn(8).setResizable(false);
            productTbl.getColumnModel().getColumn(9).setResizable(false);
        }

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 1170, 520));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 850));
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseClicked

    }//GEN-LAST:event_searchButtonMouseClicked

    private void searchButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseEntered

    }//GEN-LAST:event_searchButtonMouseEntered

    private void searchButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseExited

    }//GEN-LAST:event_searchButtonMouseExited

    private void searchButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMousePressed

    }//GEN-LAST:event_searchButtonMousePressed

    private void cmbFilterCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterCategoryActionPerformed
        if (cmbFilterCategory.getSelectedItem() != null) {
            searchProducts();
        }
    }//GEN-LAST:event_cmbFilterCategoryActionPerformed

    private void productTblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productTblMouseClicked

    }//GEN-LAST:event_productTblMouseClicked

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        searchProducts();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void addProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductButtonActionPerformed
        Frame parentFrame = getParentFrame();

        AddProductDialog dialog = new AddProductDialog(parentFrame, true, this, loggedInUserId);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadProductsToTable();
            }
        });

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }//GEN-LAST:event_addProductButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProductButton;
    private javax.swing.JComboBox<ComboItem> cmbFilterCategory;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable productTbl;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
