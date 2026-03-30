/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.BarcodeService;
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
public class AddProductDialog extends javax.swing.JDialog {

    private int loggedInUserId;
    private String productImageBase64 = null;
    private JLabel imagePreviewLabel;
    private ProductManagementPanel productManagementPanel;

    /**
     * Creates new form AddProductScreen
     */
    public AddProductDialog(java.awt.Frame parent, boolean modal, ProductManagementPanel productManagementPanel, int loggedInUserId) {
        super(parent, modal);
        initComponents();

        this.productManagementPanel = productManagementPanel;
        this.loggedInUserId = loggedInUserId;

        setupDialog();
        setupCloseConfirmation();
        setupImagePreview();
        loadCategories();
        loadSuppliers();
        applyValidationRules();
        attachRealtimeEvents();
        generateInitialBarcode();

        productImageField.setEditable(false);
        cmbProdStatus.setEnabled(false);
        productIDField.setEditable(false);
        generateInitialProductId();
    }

    public AddProductDialog(java.awt.Frame parent, boolean modal, int loggedInUserId) {
        this(parent, modal, null, loggedInUserId);
    }

    public AddProductDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, null, -1);
    }

    private void setupDialog() {
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    private void setupCloseConfirmation() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmClose();
            }
        });
    }

    private void confirmClose() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel adding product?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private void attachRealtimeEvents() {
        java.awt.event.KeyAdapter stockStatusUpdater = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateStockStatus();
            }
        };

        quantityStockField.addKeyListener(stockStatusUpdater);
        reorderLevelField.addKeyListener(stockStatusUpdater);
    }

    private void generateInitialProductId() {
        String sql = "SELECT COALESCE(MAX(product_id), 0) + 1 AS next_id FROM products";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                productIDField.setText(String.valueOf(rs.getInt("next_id")));
            }

        } catch (SQLException ex) {
            productIDField.setText("");
            JOptionPane.showMessageDialog(this, "Failed to generate product ID: " + ex.getMessage());
        }
    }

    private void generateInitialBarcode() {
        try (Connection con = DatabaseConnection.dbConnection()) {
            barcodeField.setText(BarcodeService.generateUniqueBarcode(con));
        } catch (Exception e) {
            barcodeField.setText("");
            JOptionPane.showMessageDialog(this, "Failed to generate barcode: " + e.getMessage());
        }
    }

    private void setupImagePreview() {
        imagePanel.removeAll();
        imagePanel.setLayout(new BorderLayout());

        imagePreviewLabel = new JLabel("No Image", JLabel.CENTER);
        imagePanel.add(imagePreviewLabel, BorderLayout.CENTER);

        imagePanel.revalidate();
        imagePanel.repaint();
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
        applyDigitsOnly(barcodeField);
        applyDecimalOnly(costPriceField);
        applyDecimalOnly(sellingPriceField);
        applyDigitsOnly(quantityStockField);
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

    private void browseAndEncodeImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();

        // limit file size to 2 MB
        long maxFileSize = 2L * 1024 * 1024;

        if (selectedFile.length() > maxFileSize) {
            JOptionPane.showMessageDialog(this,
                    "Image is too large. Please select an image smaller than 2 MB.");
            return;
        }

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

            if (data.productImageBase64 != null && data.productImageBase64.length() > 10_000_000) {
                JOptionPane.showMessageDialog(this, "Encoded image is too large to save.");
                return null;
            }

            return data;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
            return null;
        }
    }

    private boolean barcodeExists(Connection con, String barcode) throws SQLException {
        String sql = "SELECT 1 FROM products WHERE barcode = ? LIMIT 1";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, barcode);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean productIdExists(Connection con, int productId) throws SQLException {
        String sql = "SELECT 1 FROM products WHERE product_id = ? LIMIT 1";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, productId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertInitialStockInTransaction(Connection con, ProductFormData data) throws SQLException {
        if (data.stockQty <= 0) {
            return;
        }

        String sql = "INSERT INTO inventory_transactions "
                + "(product_id, transaction_type, quantity, transaction_date, batch_number, expiry_date, "
                + "reference_number, reason, transacted_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, data.productId);
            pst.setString(2, "Stock In");
            pst.setInt(3, data.stockQty);
            pst.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pst.setString(5, "OPENING-" + data.productId);
            pst.setNull(6, java.sql.Types.DATE);
            pst.setString(7, "INITIAL-STOCK-" + data.productId);
            pst.setString(8, "Initial stock during product creation");
            pst.setInt(9, loggedInUserId);
            pst.executeUpdate();
        }
    }

    private void clearFields() {
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
        generateInitialBarcode();
        generateInitialProductId();
        updateStockStatus();
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
        generateBarcodeBtn = new javax.swing.JButton();
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
        addProductBtn = new javax.swing.JButton();
        clearFieldsBtn = new javax.swing.JButton();
        cmbProdStatus = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        imagePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1034, 500));
        setMinimumSize(new java.awt.Dimension(1034, 500));
        setPreferredSize(new java.awt.Dimension(1034, 428));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("ADD PRODUCT");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, -1, -1));

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
        jPanel2.add(barcodeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 150, -1));

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

        generateBarcodeBtn.setText("Generate");
        generateBarcodeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                generateBarcodeBtnMouseClicked(evt);
            }
        });
        generateBarcodeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateBarcodeBtnActionPerformed(evt);
            }
        });
        jPanel2.add(generateBarcodeBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 80, -1, -1));

        jPanel6.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 390, 180));

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

        jPanel6.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 380, 180));

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

        addProductBtn.setBackground(new java.awt.Color(41, 75, 107));
        addProductBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addProductBtn.setForeground(new java.awt.Color(255, 255, 255));
        addProductBtn.setText("Add");
        addProductBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addProductBtnMouseClicked(evt);
            }
        });
        addProductBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductBtnActionPerformed(evt);
            }
        });
        jPanel8.add(addProductBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, 100, 30));

        clearFieldsBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clearFieldsBtn.setText("Clear");
        clearFieldsBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearFieldsBtnMouseClicked(evt);
            }
        });
        clearFieldsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFieldsBtnActionPerformed(evt);
            }
        });
        jPanel8.add(clearFieldsBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 100, 80, 30));

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

        jPanel6.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 780, 150));

        imagePanel.setBackground(new java.awt.Color(246, 222, 216));
        imagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        imagePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel6.add(imagePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, 190, 180));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1040, 450));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void productNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productNameFieldActionPerformed

    private void cmbProdCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProdCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbProdCategoryActionPerformed

    private void generateBarcodeBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_generateBarcodeBtnMouseClicked

    }//GEN-LAST:event_generateBarcodeBtnMouseClicked

    private void generateBarcodeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateBarcodeBtnActionPerformed
        generateInitialBarcode();
    }//GEN-LAST:event_generateBarcodeBtnActionPerformed

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

    private void addProductBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addProductBtnMouseClicked

    }//GEN-LAST:event_addProductBtnMouseClicked

    private void addProductBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductBtnActionPerformed
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
                if (productIdExists(con, data.productId)) {
                    JOptionPane.showMessageDialog(this, "Product ID already exists.");
                    con.rollback();
                    return;
                }

                if (barcodeExists(con, data.barcode)) {
                    JOptionPane.showMessageDialog(this, "Barcode already exists.");
                    con.rollback();
                    return;
                }

                String sql = "INSERT INTO products "
                        + "(product_id, barcode, name, category_id, supplier_id, cost_price, selling_price, "
                        + "stock_quantity, reorder_level, product_image, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    pst.setInt(1, data.productId);
                    pst.setString(2, data.barcode);
                    pst.setString(3, data.productName);
                    pst.setInt(4, data.categoryId);
                    pst.setInt(5, data.supplierId);
                    pst.setDouble(6, data.costPrice);
                    pst.setDouble(7, data.sellingPrice);
                    pst.setInt(8, data.stockQty);
                    pst.setInt(9, data.reorderLevel);

                    if (data.productImageBase64 == null || data.productImageBase64.isBlank()) {
                        pst.setNull(10, java.sql.Types.LONGVARCHAR);
                    } else {
                        pst.setString(10, data.productImageBase64);
                    }

                    pst.setTimestamp(11, now);
                    pst.setTimestamp(12, now);

                    pst.executeUpdate();
                }

                insertInitialStockInTransaction(con, data);

                logAuditTrail(
                        con,
                        "ADD PRODUCT",
                        "Added product: " + data.productName
                        + " | Product ID: " + data.productId
                        + " | Barcode: " + data.barcode
                        + " | Stock Qty: " + data.stockQty
                );

                con.commit();

                if (productManagementPanel != null) {
                    productManagementPanel.loadProductsToTable();
                }

                JOptionPane.showMessageDialog(this, "Product added successfully.");
                dispose();

            } catch (SQLException ex) {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Add product error: " + ex.getMessage());
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }//GEN-LAST:event_addProductBtnActionPerformed

    private void clearFieldsBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearFieldsBtnMouseClicked

    }//GEN-LAST:event_clearFieldsBtnMouseClicked

    private void clearFieldsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFieldsBtnActionPerformed
        generateInitialProductId();
    }//GEN-LAST:event_clearFieldsBtnActionPerformed

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
            java.util.logging.Logger.getLogger(AddProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddProductDialog dialog = new AddProductDialog(new javax.swing.JFrame(), true, null, 1);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProductBtn;
    private javax.swing.JTextField barcodeField;
    private javax.swing.JButton browseBtn;
    private javax.swing.JButton clearFieldsBtn;
    private javax.swing.JComboBox<ComboItem> cmbProdCategory;
    private javax.swing.JComboBox<String> cmbProdStatus;
    private javax.swing.JComboBox<ComboItem> cmbSupplierName;
    private javax.swing.JTextField costPriceField;
    private javax.swing.JButton generateBarcodeBtn;
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
    private javax.swing.JTextField productIDField;
    private javax.swing.JTextField productImageField;
    private javax.swing.JTextField productNameField;
    private javax.swing.JTextField quantityStockField;
    private javax.swing.JTextField reorderLevelField;
    private javax.swing.JTextField sellingPriceField;
    // End of variables declaration//GEN-END:variables
}
