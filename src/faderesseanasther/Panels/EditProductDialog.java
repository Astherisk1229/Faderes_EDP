/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.ComboItem;
import DbConnection.DatabaseConnection;
import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.sql.Timestamp;

/**
 *
 * @author Admin
 */
public class EditProductDialog extends javax.swing.JDialog {

    private String productImageBase64 = null;
    private JLabel imagePreviewLabel;
    private final ProductManagementPanel productManagementPanel;

    private Integer currentProductId = null;
    private String originalBarcode = null;
    private String originalProductName = "";
    private Integer originalCategoryId = null;
    private Integer originalSupplierId = null;
    private String originalCostPrice = "";
    private String originalSellingPrice = "";
    private String originalStockQty = "";
    private String originalReorderLevel = "";
    private String originalProductImageBase64 = "";

    private final int loggedInUserId;

    /**
     * Creates new form AddProductScreen
     */
    public EditProductDialog(java.awt.Frame parent, boolean modal, ProductManagementPanel productManagementPanel, int loggedInUserId) {
        super(parent, modal);
        initComponents();

        this.productManagementPanel = productManagementPanel;
        this.loggedInUserId = loggedInUserId;

        setupCloseConfirmation();
        setupDialog();
        setupImagePreview();
        loadCategories();
        loadSuppliers();
        applyValidationRules();
        attachRealtimeEvents();
        barcodeField.setEditable(false);
        barcodeField.setFocusable(false);
        barcodeField.setToolTipText("Barcode cannot be edited. It is loaded from the selected product.");

        productIDField.setEditable(false);
        productImageField.setEditable(false);

        cmbProdStatus.setEnabled(false);

        quantityStockField.setEditable(false);
        quantityStockField.setFocusable(false);
        quantityStockField.setToolTipText("Stock quantity is managed through Stock In and Stock Out transactions.");
        jLabel18.setText("EDIT PRODUCT DETAILS");
        updateProductBtn.setText("Update");
    }

    public EditProductDialog(java.awt.Frame parent, boolean modal, int loggedInUserId) {
        this(parent, modal, null, loggedInUserId);
    }

    public EditProductDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, null, -1);
    }

    private void setupDialog() {
        setTitle("Update Product");
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    private void attachRealtimeEvents() {
        javax.swing.event.DocumentListener stockListener = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateStockStatus();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateStockStatus();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateStockStatus();
            }
        };

        quantityStockField.getDocument().addDocumentListener(stockListener);
        reorderLevelField.getDocument().addDocumentListener(stockListener);
    }

    private void setupImagePreview() {
        imagePanel.removeAll();
        imagePanel.setLayout(new BorderLayout());

        imagePreviewLabel = new JLabel("No Image", JLabel.CENTER);
        imagePanel.add(imagePreviewLabel, BorderLayout.CENTER);

        imagePanel.revalidate();
        imagePanel.repaint();
    }

    public void loadProductById(int productId) {
        String sql = "SELECT product_id, barcode, name, category_id, supplier_id, "
                + "cost_price, selling_price, stock_quantity, reorder_level, product_image "
                + "FROM products WHERE product_id = ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, productId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    originalBarcode = rs.getString("barcode");
                    originalProductName = rs.getString("name");
                    originalCategoryId = rs.getInt("category_id");
                    originalSupplierId = rs.getInt("supplier_id");
                    originalCostPrice = String.valueOf(rs.getDouble("cost_price"));
                    originalSellingPrice = String.valueOf(rs.getDouble("selling_price"));
                    originalStockQty = String.valueOf(rs.getInt("stock_quantity"));
                    originalReorderLevel = String.valueOf(rs.getInt("reorder_level"));
                    originalProductImageBase64 = rs.getString("product_image") != null ? rs.getString("product_image") : "";

                    currentProductId = rs.getInt("product_id");
                    productImageBase64 = rs.getString("product_image");

                    productIDField.setText(String.valueOf(rs.getInt("product_id")));
                    barcodeField.setText(rs.getString("barcode"));
                    productNameField.setText(rs.getString("name"));
                    costPriceField.setText(String.valueOf(rs.getDouble("cost_price")));
                    sellingPriceField.setText(String.valueOf(rs.getDouble("selling_price")));
                    quantityStockField.setText(String.valueOf(rs.getInt("stock_quantity")));
                    reorderLevelField.setText(String.valueOf(rs.getInt("reorder_level")));

                    selectComboItemById(cmbProdCategory, rs.getInt("category_id"));
                    selectComboItemById(cmbSupplierName, rs.getInt("supplier_id"));

                    updateStockStatus();

                    if (productImageBase64 != null && !productImageBase64.trim().isEmpty()) {
                        productImageField.setText("image_loaded");
                        showImagePreviewFromBase64(productImageBase64);
                    } else {
                        productImageField.setText("");
                        clearImagePreview();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                    dispose();
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load product error: " + ex.getMessage());
            dispose();
        }
    }

    private void resetFieldsToOriginalValues() {
        if (currentProductId == null) {
            JOptionPane.showMessageDialog(this, "No product selected for editing.");
            return;
        }

        productIDField.setText(String.valueOf(currentProductId));
        barcodeField.setText(originalBarcode != null ? originalBarcode : "");
        productNameField.setText(originalProductName != null ? originalProductName : "");
        costPriceField.setText(originalCostPrice != null ? originalCostPrice : "");
        sellingPriceField.setText(originalSellingPrice != null ? originalSellingPrice : "");
        quantityStockField.setText(originalStockQty != null ? originalStockQty : "");
        reorderLevelField.setText(originalReorderLevel != null ? originalReorderLevel : "");

        if (originalCategoryId != null) {
            selectComboItemById(cmbProdCategory, originalCategoryId);
        }

        if (originalSupplierId != null) {
            selectComboItemById(cmbSupplierName, originalSupplierId);
        }

        productImageBase64 = originalProductImageBase64;

        if (originalProductImageBase64 != null && !originalProductImageBase64.trim().isEmpty()) {
            productImageField.setText("image_loaded");
            showImagePreviewFromBase64(originalProductImageBase64);
        } else {
            productImageField.setText("");
            clearImagePreview();
        }

        updateStockStatus();
    }

    public void loadProductForEdit(int productId, String barcode, String productName,
            int categoryId, int supplierId, double costPrice, double sellingPrice,
            int stockQty, int reorderLevel, String productImageBase64) {

        this.currentProductId = productId;

        this.originalBarcode = barcode != null ? barcode : "";
        this.originalProductName = productName != null ? productName : "";
        this.originalCategoryId = categoryId;
        this.originalSupplierId = supplierId;
        this.originalCostPrice = String.valueOf(costPrice);
        this.originalSellingPrice = String.valueOf(sellingPrice);
        this.originalStockQty = String.valueOf(stockQty);
        this.originalReorderLevel = String.valueOf(reorderLevel);
        this.originalProductImageBase64 = productImageBase64 != null ? productImageBase64 : "";

        productIDField.setText(String.valueOf(productId));
        barcodeField.setText(this.originalBarcode);
        productNameField.setText(this.originalProductName);
        costPriceField.setText(this.originalCostPrice);
        sellingPriceField.setText(this.originalSellingPrice);
        quantityStockField.setText(this.originalStockQty);
        reorderLevelField.setText(this.originalReorderLevel);

        selectComboItemById(cmbProdCategory, categoryId);
        selectComboItemById(cmbSupplierName, supplierId);

        this.productImageBase64 = this.originalProductImageBase64;

        if (!this.originalProductImageBase64.isEmpty()) {
            productImageField.setText("image_loaded");
            showImagePreviewFromBase64(this.originalProductImageBase64);
        } else {
            productImageField.setText("");
            clearImagePreview();
        }

        updateStockStatus();
    }

    private boolean barcodeExistsForOtherProduct(Connection con, String barcode, int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE barcode = ? AND product_id <> ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, barcode);
            pst.setInt(2, productId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean productDetailsExistForOtherProduct(
            Connection con,
            String productName,
            int categoryId,
            int supplierId,
            int productId
    ) throws SQLException {

        String sql = "SELECT COUNT(*) FROM products "
                + "WHERE name = ? AND category_id = ? AND supplier_id = ? AND product_id <> ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, productName);
            pst.setInt(2, categoryId);
            pst.setInt(3, supplierId);
            pst.setInt(4, productId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void loadCategories() {
        cmbProdCategory.removeAllItems();

        String sql = "SELECT category_id, name FROM categories ORDER BY name ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                cmbProdCategory.addItem(new ComboItem(
                        rs.getInt("category_id"),
                        rs.getString("name")
                ));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + ex.getMessage());
        }
    }

    private void selectComboItemById(javax.swing.JComboBox<ComboItem> comboBox, int id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem item = comboBox.getItemAt(i);
            if (item != null && item.getId() == id) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadSuppliers() {
        cmbSupplierName.removeAllItems();

        String sql = "SELECT supplier_id, name FROM suppliers ORDER BY name ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                cmbSupplierName.addItem(new ComboItem(
                        rs.getInt("supplier_id"),
                        rs.getString("name")
                ));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load suppliers: " + ex.getMessage());
        }
    }

    private void applyValidationRules() {
        applyDigitsOnly(productIDField);
        applyDecimalOnly(costPriceField);
        applyDecimalOnly(sellingPriceField);
        applyDigitsOnly(reorderLevelField);
        applyProductNameValidation(productNameField);
    }

    private void applyDigitsOnly(javax.swing.JTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume();
                }
            }
        });
    }

    private void applyDecimalOnly(javax.swing.JTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                String text = field.getText();

                if (Character.isDigit(c) || c == '\b') {
                    return;
                }

                if (c == '.' && !text.contains(".")) {
                    return;
                }

                evt.consume();
            }
        });
    }

    private void applyProductNameValidation(javax.swing.JTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (Character.isISOControl(c)) {
                    return;
                }

                boolean valid = Character.isLetterOrDigit(c)
                        || Character.isSpaceChar(c)
                        || c == '-'
                        || c == '('
                        || c == ')'
                        || c == '.';

                if (!valid) {
                    evt.consume();
                }
            }
        });
    }

    private void browseAndEncodeImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();

        try {
            byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
            productImageBase64 = Base64.getEncoder().encodeToString(fileBytes);

            productImageField.setText(selectedFile.getName());
            showImagePreviewFromBase64(productImageBase64);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage());
        }
    }

    private void showImagePreviewFromBase64(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ImageIcon icon = new ImageIcon(imageBytes);

            Image scaledImage = icon.getImage().getScaledInstance(
                    imagePanel.getWidth() > 0 ? imagePanel.getWidth() - 10 : 170,
                    imagePanel.getHeight() > 0 ? imagePanel.getHeight() - 10 : 160,
                    Image.SCALE_SMOOTH
            );

            imagePreviewLabel.setText("");
            imagePreviewLabel.setIcon(new ImageIcon(scaledImage));

            imagePanel.revalidate();
            imagePanel.repaint();

        } catch (Exception ex) {
            imagePreviewLabel.setText("Invalid Image");
            imagePreviewLabel.setIcon(null);
        }
    }

    private void clearImagePreview() {
        imagePreviewLabel.setText("No Image");
        imagePreviewLabel.setIcon(null);
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void updateStockStatus() {
        int quantity = parseIntOrDefault(quantityStockField.getText().trim(), 0);
        int reorderLevel = parseIntOrDefault(reorderLevelField.getText().trim(), 0);

        cmbProdStatus.setSelectedItem(getStockStatus(quantity, reorderLevel));
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private boolean validateProductFields() {
        String barcode = barcodeField.getText().trim();
        String productName = productNameField.getText().trim();
        String costPrice = costPriceField.getText().trim();
        String sellingPrice = sellingPriceField.getText().trim();
        String stockQty = quantityStockField.getText().trim();
        String reorderLevel = reorderLevelField.getText().trim();

        if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcode is required.");
            barcodeField.requestFocus();
            return false;
        }

        if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required.");
            productNameField.requestFocus();
            return false;
        }

        if (cmbProdCategory.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Category is required.");
            cmbProdCategory.requestFocus();
            return false;
        }

        if (cmbSupplierName.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Supplier is required.");
            cmbSupplierName.requestFocus();
            return false;
        }

        if (costPrice.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cost price is required.");
            costPriceField.requestFocus();
            return false;
        }

        if (sellingPrice.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selling price is required.");
            sellingPriceField.requestFocus();
            return false;
        }

        if (stockQty.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock quantity is required.");
            quantityStockField.requestFocus();
            return false;
        }

        if (reorderLevel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reorder level is required.");
            reorderLevelField.requestFocus();
            return false;
        }

        try {
            double cp = Double.parseDouble(costPrice);
            double sp = Double.parseDouble(sellingPrice);
            int qty = Integer.parseInt(stockQty);
            int reorder = Integer.parseInt(reorderLevel);

            if (cp < 0 || sp < 0 || qty < 0 || reorder < 0) {
                JOptionPane.showMessageDialog(this, "Numeric values cannot be negative.");
                return false;
            }

            if (sp < cp) {
                JOptionPane.showMessageDialog(this, "Selling price cannot be lower than cost price.");
                sellingPriceField.requestFocus();
                return false;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
            return false;
        }

        return true;
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

    private static class ProductFormData {

        int productId;
        String barcode;
        String productName;
        int categoryId;
        int supplierId;
        double costPrice;
        double sellingPrice;
        int stockQty;
        int reorderLevel;
        String productImageBase64;
    }

    private ProductFormData validateAndBuildFormData() {
        String productIdText = productIDField.getText().trim();
        String barcode = barcodeField.getText().trim();
        String productName = productNameField.getText().trim();
        String costText = costPriceField.getText().trim();
        String sellingText = sellingPriceField.getText().trim();
        String stockText = quantityStockField.getText().trim();
        String reorderText = reorderLevelField.getText().trim();

        if (productIdText.isEmpty() || barcode.isEmpty() || productName.isEmpty()
                || costText.isEmpty() || sellingText.isEmpty()
                || stockText.isEmpty() || reorderText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return null;
        }

        ComboItem selectedCategory = (ComboItem) cmbProdCategory.getSelectedItem();
        ComboItem selectedSupplier = (ComboItem) cmbSupplierName.getSelectedItem();

        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Please select a category.");
            return null;
        }

        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "Please select a supplier.");
            return null;
        }

        try {

            ProductFormData data = new ProductFormData();
            if (productIdText.length() > 9) {
                JOptionPane.showMessageDialog(this, "Product ID is too long.");
                return null;
            }
            data.productId = Integer.parseInt(productIdText);
            data.barcode = barcode;
            data.productName = productName;
            data.categoryId = selectedCategory.getId();
            data.supplierId = selectedSupplier.getId();
            data.costPrice = Double.parseDouble(costText);
            data.sellingPrice = Double.parseDouble(sellingText);
            data.stockQty = Integer.parseInt(stockText);
            data.reorderLevel = Integer.parseInt(reorderText);
            data.productImageBase64 = productImageBase64;

            if (data.productId <= 0) {
                JOptionPane.showMessageDialog(this, "Product ID must be greater than 0.");
                return null;
            }

            if (data.barcode.length() < 8) {
                JOptionPane.showMessageDialog(this, "Barcode is too short.");
                return null;
            }

            if (data.barcode.length() > 50) {
                JOptionPane.showMessageDialog(this, "Barcode is too long.");
                return null;
            }

            if (data.productName.length() < 2) {
                JOptionPane.showMessageDialog(this, "Product name is too short.");
                return null;
            }

            if (data.costPrice < 0 || data.sellingPrice < 0) {
                JOptionPane.showMessageDialog(this, "Prices cannot be negative.");
                return null;
            }

            if (data.stockQty < 0 || data.reorderLevel < 0) {
                JOptionPane.showMessageDialog(this, "Stock and reorder level cannot be negative.");
                return null;
            }

            if (data.sellingPrice < data.costPrice) {
                JOptionPane.showMessageDialog(this, "Selling price must not be lower than cost price.");
                return null;
            }

            return data;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
            return null;
        }
    }

    private void clearFields() {
        barcodeField.setText("");
        productNameField.setText("");
        costPriceField.setText("");
        sellingPriceField.setText("");
        quantityStockField.setText("");
        reorderLevelField.setText("");
        productImageField.setText("");
        productImageBase64 = null;

        if (cmbProdCategory.getItemCount() > 0) {
            cmbProdCategory.setSelectedIndex(0);
        }

        if (cmbSupplierName.getItemCount() > 0) {
            cmbSupplierName.setSelectedIndex(0);
        }

        clearImagePreview();
        updateStockStatus();

    }

    private void setupCloseConfirmation() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmCloseFrame();
            }
        });
    }

    private void confirmCloseFrame() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel editing product?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private void logAuditTrail(Connection con, String action, String description) throws SQLException {
        if (loggedInUserId <= 0) {
            throw new SQLException("Invalid logged-in user. Cannot save audit trail.");
        }

        String sql = "INSERT INTO transactions_log (user_id, action, description, timestamp) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
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

        jPanel9 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        productNameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cmbProdCategory = new javax.swing.JComboBox<>();
        barcodeField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        productIDField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        costPriceField = new javax.swing.JTextField();
        quantityStockField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        reorderLevelField = new javax.swing.JTextField();
        sellingPriceField = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        productImageField = new javax.swing.JTextField();
        cmbSupplierName = new javax.swing.JComboBox<>();
        browseBtn = new javax.swing.JButton();
        updateProductBtn = new javax.swing.JButton();
        resetFieldsBtn = new javax.swing.JButton();
        cmbProdStatus = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        imagePanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();

        setMinimumSize(new java.awt.Dimension(1034, 500));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("EDIT PRODUCT DETAILS");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, -1, -1));

        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 60));

        jPanel6.setBackground(new java.awt.Color(210, 102, 90));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(246, 222, 216));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        productNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productNameFieldActionPerformed(evt);
            }
        });
        jPanel2.add(productNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 240, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Product Name");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 280, -1));

        cmbProdCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProdCategoryActionPerformed(evt);
            }
        });
        jPanel2.add(cmbProdCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 240, -1));
        jPanel2.add(barcodeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 240, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Product ID");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 50, -1, 20));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Product Category");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 180, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Product Information");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Barcode");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, -1, -1));
        jPanel2.add(productIDField, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 240, -1));

        jPanel6.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 390, 180));

        jPanel7.setBackground(new java.awt.Color(246, 222, 216));
        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Pricing & Inventory");
        jPanel7.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Cost Price");
        jPanel7.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, -1, 20));
        jPanel7.add(costPriceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 220, -1));

        quantityStockField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityStockFieldActionPerformed(evt);
            }
        });
        jPanel7.add(quantityStockField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 110, 220, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Stock Quantity");
        jPanel7.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, 20));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Selling Price");
        jPanel7.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 70, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Reorder Level");
        jPanel7.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 80, -1));

        reorderLevelField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reorderLevelFieldActionPerformed(evt);
            }
        });
        jPanel7.add(reorderLevelField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 140, 220, -1));

        sellingPriceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sellingPriceFieldActionPerformed(evt);
            }
        });
        jPanel7.add(sellingPriceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 220, -1));

        jPanel6.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 380, 180));

        jPanel8.setBackground(new java.awt.Color(246, 222, 216));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Additional Details");
        jPanel8.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Supplier Name");
        jPanel8.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 100, 20));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("Product Image");
        jPanel8.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, 100, 20));
        jPanel8.add(productImageField, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 160, -1));

        jPanel8.add(cmbSupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 250, -1));

        browseBtn.setText("Browse...");
        browseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseBtnActionPerformed(evt);
            }
        });
        jPanel8.add(browseBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 110, 80, -1));

        updateProductBtn.setBackground(new java.awt.Color(41, 75, 107));
        updateProductBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        updateProductBtn.setForeground(new java.awt.Color(255, 255, 255));
        updateProductBtn.setText("Update");
        updateProductBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateProductBtnMouseClicked(evt);
            }
        });
        updateProductBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateProductBtnActionPerformed(evt);
            }
        });
        jPanel8.add(updateProductBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, 100, 30));

        resetFieldsBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        resetFieldsBtn.setText("Reset");
        resetFieldsBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetFieldsBtnMouseClicked(evt);
            }
        });
        resetFieldsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetFieldsBtnActionPerformed(evt);
            }
        });
        jPanel8.add(resetFieldsBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 100, 80, 30));

        cmbProdStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "In Stock", "Low Stock", "Out of Stock" }));
        cmbProdStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProdStatusActionPerformed(evt);
            }
        });
        jPanel8.add(cmbProdStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 250, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Stock Status");
        jPanel8.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 80, -1));

        jPanel6.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 780, 150));

        imagePanel.setBackground(new java.awt.Color(246, 222, 216));
        imagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        imagePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel6.add(imagePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 10, 190, 180));

        jSeparator1.setForeground(new java.awt.Color(210, 102, 90));
        jPanel6.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 790, 30));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1030, 440));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void productNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productNameFieldActionPerformed

    private void cmbProdCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProdCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbProdCategoryActionPerformed

    private void quantityStockFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityStockFieldActionPerformed
        updateStockStatus();
    }//GEN-LAST:event_quantityStockFieldActionPerformed

    private void reorderLevelFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reorderLevelFieldActionPerformed
        updateStockStatus();
    }//GEN-LAST:event_reorderLevelFieldActionPerformed

    private void sellingPriceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sellingPriceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sellingPriceFieldActionPerformed

    private void browseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseBtnActionPerformed
        browseAndEncodeImage();
    }//GEN-LAST:event_browseBtnActionPerformed

    private void updateProductBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateProductBtnMouseClicked

    }//GEN-LAST:event_updateProductBtnMouseClicked

    private void updateProductBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProductBtnActionPerformed
        if (currentProductId == null) {
            JOptionPane.showMessageDialog(this, "No product selected for editing.");
            return;
        }

        ProductFormData data = validateAndBuildFormData();
        if (data == null) {
            return;
        }

        if (loggedInUserId <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid logged-in user. Cannot save transaction.");
            return;
        }

        try (Connection con = DatabaseConnection.dbConnection()) {
            con.setAutoCommit(false);

            try {
                if (productDetailsExistForOtherProduct(con, data.productName, data.categoryId, data.supplierId, currentProductId)) {
                    JOptionPane.showMessageDialog(this, "Another product with the same details already exists.");
                    productNameField.requestFocus();
                    con.rollback();
                    return;
                }

                String sql = "UPDATE products SET name = ?, category_id = ?, supplier_id = ?, "
                        + "cost_price = ?, selling_price = ?, reorder_level = ?, product_image = ?, updated_at = ? "
                        + "WHERE product_id = ?";

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    pst.setString(1, data.productName);
                    pst.setInt(2, data.categoryId);
                    pst.setInt(3, data.supplierId);
                    pst.setDouble(4, data.costPrice);
                    pst.setDouble(5, data.sellingPrice);
                    pst.setInt(6, data.reorderLevel);

                    if (data.productImageBase64 == null || data.productImageBase64.isBlank()) {
                        pst.setNull(7, java.sql.Types.LONGVARCHAR);
                    } else {
                        pst.setString(7, data.productImageBase64);
                    }

                    pst.setTimestamp(8, now);
                    pst.setInt(9, currentProductId);

                    int updatedRows = pst.executeUpdate();

                    if (updatedRows <= 0) {
                        con.rollback();
                        JOptionPane.showMessageDialog(this, "Product update failed.");
                        return;
                    }
                }

                logAuditTrail(
                        con,
                        "UPDATE PRODUCT",
                        "Updated product | Product ID: " + currentProductId
                        + " | Product Name: " + data.productName
                        + " | Barcode: " + data.barcode
                );

                con.commit();

                if (productManagementPanel != null) {
                    productManagementPanel.loadProductsToTable();
                }

                JOptionPane.showMessageDialog(this, "Product updated successfully.");
                dispose();

            } catch (SQLException ex) {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Update error: " + ex.getMessage());
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }//GEN-LAST:event_updateProductBtnActionPerformed

    private void resetFieldsBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetFieldsBtnMouseClicked

    }//GEN-LAST:event_resetFieldsBtnMouseClicked

    private void resetFieldsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetFieldsBtnActionPerformed
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the fields to the original product data?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            resetFieldsToOriginalValues();
        }
    }//GEN-LAST:event_resetFieldsBtnActionPerformed

    private void cmbProdStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProdStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbProdStatusActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditProductDialog dialog = new EditProductDialog(new javax.swing.JFrame(), true, null, 1);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField barcodeField;
    private javax.swing.JButton browseBtn;
    private javax.swing.JComboBox<ComboItem> cmbProdCategory;
    private javax.swing.JComboBox<String> cmbProdStatus;
    private javax.swing.JComboBox<ComboItem> cmbSupplierName;
    private javax.swing.JTextField costPriceField;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField productIDField;
    private javax.swing.JTextField productImageField;
    private javax.swing.JTextField productNameField;
    private javax.swing.JTextField quantityStockField;
    private javax.swing.JTextField reorderLevelField;
    private javax.swing.JButton resetFieldsBtn;
    private javax.swing.JTextField sellingPriceField;
    private javax.swing.JButton updateProductBtn;
    // End of variables declaration//GEN-END:variables
}
