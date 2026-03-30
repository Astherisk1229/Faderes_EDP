/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.DatabaseConnection;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Admin
 */
public class AddStockInFrame extends javax.swing.JDialog {

    private final JPopupMenu productPopup = new JPopupMenu();
    private final DefaultListModel<String> productSuggestionModel = new DefaultListModel<>();
    private final JList<String> productSuggestionList = new JList<>(productSuggestionModel);

    private final List<String> productSuggestions = new ArrayList<>();

    private boolean suppressBatchGeneration = false;

    /**
     * Creates new form AddStockInFrame
     */
    private void loadReasonCombo() {
        cmbReason.removeAllItems();
        cmbReason.addItem("Select Reason");
        cmbReason.addItem("New Delivery");
        cmbReason.addItem("Restock");
        cmbReason.addItem("Initial Stock");
        cmbReason.addItem("Replacement");
        cmbReason.addItem("Returned Item");
        cmbReason.addItem("Adjustment");
    }

    private void setupBatchAutoGenerationTriggers() {
        txtProductName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                maybeGenerateBatchNumber();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                maybeGenerateBatchNumber();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                maybeGenerateBatchNumber();
            }
        });

        dateReceivedChooser.getDateEditor().addPropertyChangeListener("date", evt -> maybeGenerateBatchNumber());
    }

    private void setupValidationBehavior() {
        txtStockInQuantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        stockInQuantityField3.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String cleaned = stockInQuantityField3.getText()
                        .replaceAll("[^A-Za-z0-9\\-_/]", "")
                        .toUpperCase();
                stockInQuantityField3.setText(cleaned);
            }
        });
    }

    private void maybeGenerateBatchNumber() {
        if (suppressBatchGeneration) {
            return;
        }

        String productName = txtProductName.getText().trim();
        java.util.Date dateReceived = dateReceivedChooser.getDate();

        if (productName.isEmpty() || dateReceived == null) {
            txtBatchNumber.setText("");
            return;
        }

        if (getProductIdByName(productName) == null) {
            txtBatchNumber.setText("");
            return;
        }

        txtBatchNumber.setText(generateBatchNumber(productName, dateReceived));
    }

    private String generateBatchNumber(String productName, java.util.Date dateReceived) {
        String productCode = buildProductCode(productName);
        String datePart = new SimpleDateFormat("yyyyMMdd").format(dateReceived);
        String prefix = productCode + "-" + datePart + "-";

        int nextSequence = 1;

        String sql = "SELECT batch_number "
                + "FROM inventory_transactions "
                + "WHERE batch_number LIKE ? "
                + "ORDER BY batch_number DESC "
                + "LIMIT 1";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, prefix + "%");

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String lastBatch = rs.getString("batch_number");
                    if (lastBatch != null && lastBatch.matches(".*-\\d{3}$")) {
                        String seqPart = lastBatch.substring(lastBatch.lastIndexOf("-") + 1);
                        nextSequence = Integer.parseInt(seqPart) + 1;
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return prefix + String.format("%03d", nextSequence);
    }

    private String buildProductCode(String productName) {
        String cleaned = productName == null ? "" : productName.replaceAll("[^A-Za-z0-9]", "").toUpperCase();

        if (cleaned.isEmpty()) {
            return "ITEM";
        }

        if (cleaned.length() > 8) {
            cleaned = cleaned.substring(0, 8);
        }

        return cleaned;
    }

    private Integer getProductIdByName(String productName) {
        String sql = "SELECT product_id FROM products WHERE LOWER(name) = LOWER(?) LIMIT 1";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, productName.trim());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("product_id");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking product: " + ex.getMessage());
        }

        return null;
    }

    private boolean validateForm() {
        String productName = txtProductName.getText().trim();
        String batchNumber = txtBatchNumber.getText().trim();
        String quantityText = txtStockInQuantity.getText().trim();
        String supplierName = txtSupplierName.getText().trim();
        String referenceNumber = stockInQuantityField3.getText().trim();
        java.util.Date dateReceived = dateReceivedChooser.getDate();
        java.util.Date expiryDate = dateExpiryChooser.getDate();
        String reason = cmbReason.getSelectedItem() == null ? "" : cmbReason.getSelectedItem().toString().trim();

        if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product Name is required.");
            txtProductName.requestFocus();
            return false;
        }

        if (getProductIdByName(productName) == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid existing product.");
            txtProductName.requestFocus();
            return false;
        }

        if (batchNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Batch Number was not generated yet.");
            return false;
        }

        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quantity is required.");
            txtStockInQuantity.requestFocus();
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                txtStockInQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must contain numbers only.");
            txtStockInQuantity.requestFocus();
            return false;
        }

        if (supplierName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier is required.");
            txtSupplierName.requestFocus();
            return false;
        }

        if (!supplierName.matches("[A-Za-z0-9 .,&()\\-']{2,100}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid supplier name.");
            txtSupplierName.requestFocus();
            return false;
        }

        if (referenceNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reference Number is required.");
            stockInQuantityField3.requestFocus();
            return false;
        }

        if (!referenceNumber.matches("[A-Za-z0-9\\-_/]{3,50}")) {
            JOptionPane.showMessageDialog(this, "Reference Number may contain letters, numbers, -, _, / only.");
            stockInQuantityField3.requestFocus();
            return false;
        }

        if (isReferenceNumberAlreadyUsed(referenceNumber)) {
            JOptionPane.showMessageDialog(this, "Reference Number already exists. Please use a unique reference number.");
            stockInQuantityField3.requestFocus();
            return false;
        }

        if (dateReceived == null) {
            JOptionPane.showMessageDialog(this, "Date Received is required.");
            return false;
        }

        if (dateReceived.after(new java.util.Date())) {
            JOptionPane.showMessageDialog(this, "Date Received cannot be in the future.");
            return false;
        }

        if (expiryDate != null && expiryDate.before(dateReceived)) {
            JOptionPane.showMessageDialog(this, "Expiry Date cannot be earlier than Date Received.");
            return false;
        }

        if (reason.equals("Select Reason") || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a reason.");
            cmbReason.requestFocus();
            return false;
        }

        return true;
    }

    private void saveStockIn() {
        if (!validateForm()) {
            return;
        }

        String productName = txtProductName.getText().trim();
        int productId = getProductIdByName(productName);
        int quantity = Integer.parseInt(txtStockInQuantity.getText().trim());
        String batchNumber = generateBatchNumber(productName, dateReceivedChooser.getDate());
        String referenceNumber = stockInQuantityField3.getText().trim();
        String reason = cmbReason.getSelectedItem().toString().trim();
        String supplierName = txtSupplierName.getText().trim();
        String remarks = jTextArea1.getText().trim();
        java.util.Date dateReceived = dateReceivedChooser.getDate();
        java.util.Date expiryDate = dateExpiryChooser.getDate();

        String remarksToStore = "Supplier: " + supplierName;
        if (!remarks.isEmpty()) {
            remarksToStore += " | Remarks: " + remarks;
        }

        String insertSql = "INSERT INTO inventory_transactions "
                + "(product_id, transaction_type, quantity, batch_number, expiry_date, reference_number, reason, remarks, transaction_date) "
                + "VALUES (?, 'Stock In', ?, ?, ?, ?, ?, ?, ?)";

        String updateSql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";

        try (Connection con = DatabaseConnection.dbConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement pstInsert = con.prepareStatement(insertSql); PreparedStatement pstUpdate = con.prepareStatement(updateSql)) {

                pstInsert.setInt(1, productId);
                pstInsert.setInt(2, quantity);
                pstInsert.setString(3, batchNumber);

                if (expiryDate != null) {
                    pstInsert.setDate(4, new Date(expiryDate.getTime()));
                } else {
                    pstInsert.setNull(4, java.sql.Types.DATE);
                }

                pstInsert.setString(5, referenceNumber);
                pstInsert.setString(6, reason);
                pstInsert.setString(7, remarksToStore);
                pstInsert.setTimestamp(8, new java.sql.Timestamp(dateReceived.getTime()));
                pstInsert.executeUpdate();

                pstUpdate.setInt(1, quantity);
                pstUpdate.setInt(2, productId);
                pstUpdate.executeUpdate();

                con.commit();

                JOptionPane.showMessageDialog(this, "Stock In saved successfully.");
                dispose();

            } catch (SQLException ex) {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Error saving stock in: " + ex.getMessage());
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage());
        }
    }

    private boolean isReferenceNumberAlreadyUsed(String referenceNumber) {
        String sql = "SELECT COUNT(*) AS total "
                + "FROM inventory_transactions "
                + "WHERE reference_number = ? "
                + "AND transaction_type = 'Stock In'";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, referenceNumber.trim());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking reference number: " + ex.getMessage());
        }

        return false;
    }

    public AddStockInFrame() {
        initComponents();

        loadSuggestionData();

        setupSuggestionField(
                txtProductName,
                productPopup,
                productSuggestionList,
                productSuggestionModel,
                productSuggestions
        );

        txtBatchNumber.setEditable(false);
        txtBatchNumber.setFocusable(false);
        txtBatchNumber.setBackground(new java.awt.Color(245, 245, 245));

        dateReceivedChooser.setDate(new java.util.Date());

        loadReasonCombo();
        setupBatchAutoGenerationTriggers();
        setupValidationBehavior();

        maybeGenerateBatchNumber();
    }

    private void setupSuggestionField(
            JTextField textField,
            JPopupMenu popup,
            JList<String> suggestionList,
            DefaultListModel<String> suggestionModel,
            List<String> sourceData
    ) {
        suggestionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        popup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        popup.add(panel);

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selected = suggestionList.getSelectedValue();
                if (selected != null) {
                    textField.setText(selected);
                    popup.setVisible(false);
                    maybeGenerateBatchNumber();
                }
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String input = textField.getText().trim().toLowerCase();
                    suggestionModel.clear();

                    if (input.isEmpty()) {
                        popup.setVisible(false);
                        return;
                    }

                    for (String item : sourceData) {
                        if (item.toLowerCase().contains(input)) {
                            suggestionModel.addElement(item);
                        }
                    }

                    if (suggestionModel.isEmpty()) {
                        popup.setVisible(false);
                    } else {
                        suggestionList.setVisibleRowCount(Math.min(6, suggestionModel.size()));
                        popup.setPopupSize(textField.getWidth(), suggestionList.getFixedCellHeight() > 0
                                ? suggestionList.getFixedCellHeight() * Math.min(6, suggestionModel.size()) + 8
                                : 150);
                        popup.show(textField, 0, textField.getHeight());
                    }
                });
            }
        });

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                SwingUtilities.invokeLater(() -> popup.setVisible(false));
            }
        });
    }

    private void loadSuggestionData() {
        productSuggestions.clear();

        String sql = "SELECT name FROM products ORDER BY name ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String productName = rs.getString("name");
                if (productName != null && !productName.trim().isEmpty()) {
                    productSuggestions.add(productName.trim());
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading product names: " + ex.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtStockInQuantity = new javax.swing.JTextField();
        txtProductName = new javax.swing.JTextField();
        txtBatchNumber = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtSupplierName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        stockInQuantityField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        dateExpiryChooser = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        dateReceivedChooser = new com.toedter.calendar.JDateChooser();
        cmbReason = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        saveStockInBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(590, 760));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(590, 740));
        jPanel1.setPreferredSize(new java.awt.Dimension(590, 740));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setText("Add Stock In");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 180, 50));

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setText("Fill in the details to record incoming stock.");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, -1, -1));

        jSeparator1.setForeground(new java.awt.Color(184, 33, 50));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 680, 590, 10));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Batch Number");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, 110, 20));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Quantity");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, 110, 20));

        txtStockInQuantity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(txtStockInQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 240, 30));

        txtProductName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 240, 30));

        txtBatchNumber.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(txtBatchNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 190, 240, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Product Name");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 110, 20));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Supplier");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 250, 110, 20));

        txtSupplierName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(txtSupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 280, 240, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Date Received");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 340, 140, 20));

        stockInQuantityField3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(stockInQuantityField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, 240, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Reference Number");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 140, 20));
        jPanel1.add(dateExpiryChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 460, 240, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Expiry Date");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, 140, 20));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText(" (Optional)");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 520, 140, 20));
        jPanel1.add(dateReceivedChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 370, 240, 30));

        jPanel1.add(cmbReason, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 460, 240, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Reason");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 430, 140, 20));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Remarks");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 520, 140, 20));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 550, 530, 110));

        jSeparator2.setForeground(new java.awt.Color(184, 33, 50));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 590, 10));

        saveStockInBtn.setBackground(new java.awt.Color(184, 33, 50));
        saveStockInBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        saveStockInBtn.setForeground(new java.awt.Color(255, 255, 255));
        saveStockInBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/diskette.png"))); // NOI18N
        saveStockInBtn.setText(" Save Stock In");
        saveStockInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveStockInBtnActionPerformed(evt);
            }
        });
        jPanel1.add(saveStockInBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 700, 170, 40));

        cancelBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        jPanel1.add(cancelBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 700, 110, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 590, 760));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void saveStockInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveStockInBtnActionPerformed
        saveStockIn();
    }//GEN-LAST:event_saveStockInBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

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
            java.util.logging.Logger.getLogger(AddStockInFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddStockInFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddStockInFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddStockInFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddStockInFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelBtn;
    private javax.swing.JComboBox<String> cmbReason;
    private com.toedter.calendar.JDateChooser dateExpiryChooser;
    private com.toedter.calendar.JDateChooser dateReceivedChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton saveStockInBtn;
    private javax.swing.JTextField stockInQuantityField3;
    private javax.swing.JTextField txtBatchNumber;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtStockInQuantity;
    private javax.swing.JTextField txtSupplierName;
    // End of variables declaration//GEN-END:variables
}
