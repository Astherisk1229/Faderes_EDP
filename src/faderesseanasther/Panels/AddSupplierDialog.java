/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.DatabaseConnection;
import DbConnection.SupplierSearch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author Admin
 */
public class AddSupplierDialog extends javax.swing.JDialog {

    private JTable supplierTbl;
    private int loggedInUserId = -1;
  

    // optional original values for reset/edit mode
    private String originalSupplierName = "";
    private String originalContactPerson = "";
    private String originalContactNumber = "";
    private String originalEmail = "";
    private String originalAddress = "";

    /**
     * Creates new form AddSupplierDialog
     */
    public AddSupplierDialog(java.awt.Frame parent, boolean modal, JTable supplierTbl, int loggedInUserId) {
        super(parent, modal);
        initComponents();
        this.supplierTbl = supplierTbl;
        this.loggedInUserId = loggedInUserId;

    }

    public AddSupplierDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

    }

    private boolean validateSupplierInputs() {
        String supplierName = supplierNameField.getText().trim();
        String contactPerson = contactPersonField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String email = emailAddressField.getText().trim();
        String address = addressField.getText().trim();

        if (supplierName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier name is required.");
            supplierNameField.requestFocus();
            return false;
        }

        if (contactPerson.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contact person is required.");
            contactPersonField.requestFocus();
            return false;
        }

        if (contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contact number is required.");
            contactNumberField.requestFocus();
            return false;
        }

        // digits, spaces, +, -
        if (!contactNumber.matches("[0-9+\\- ]{7,15}")) {
            JOptionPane.showMessageDialog(this, "Invalid contact number format.");
            contactNumberField.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email address is required.");
            emailAddressField.requestFocus();
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email address format.");
            emailAddressField.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address is required.");
            addressField.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateDuplicateSupplier() {
        String supplierName = supplierNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String email = emailAddressField.getText().trim();

        String sql = "SELECT supplier_id FROM suppliers "
                + "WHERE LOWER(name) = LOWER(?) OR contact_number = ? OR LOWER(email) = LOWER(?)";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, supplierName);
            pst.setString(2, contactNumber);
            pst.setString(3, email);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                            "Duplicate supplier found.\nSupplier name, contact number, or email already exists.");
                    return false;
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate validation error: " + ex.getMessage());
            return false;
        }

        return true;
    }

    private void clearFields() {
        supplierNameField.setText("");
        contactPersonField.setText("");
        contactNumberField.setText("");
        emailAddressField.setText("");
        addressField.setText("");

        supplierNameField.requestFocus();
    }

    private void loadSelectedRowToFields() {
        supplierNameField.setText(originalSupplierName);
        contactPersonField.setText(originalContactPerson);
        contactNumberField.setText(originalContactNumber);
        emailAddressField.setText(originalEmail);
        addressField.setText(originalAddress);
    }

    private void logAuditTrail(int userId, String action, String description) {
        if (userId <= 0) {
            System.out.println("Audit trail skipped: invalid user ID.");
            return;
        }

        String sql = "INSERT INTO transactions_log (user_id, action, description, timestamp) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setString(2, action);
            pst.setString(3, description);
            pst.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pst.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Audit trail logging failed: " + ex.getMessage());
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

        jPanel4 = new javax.swing.JPanel();
        supplierDetails2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        supplierNameField = new javax.swing.JTextField();
        contactNumberField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        contactPersonField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        emailAddressField = new javax.swing.JTextField();
        addSupplierBtn = new javax.swing.JButton();
        clearFieldsBtn = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(820, 295));
        setMinimumSize(new java.awt.Dimension(820, 295));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(210, 102, 90));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierDetails2.setBackground(new java.awt.Color(246, 222, 216));
        supplierDetails2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 0, 0));
        jLabel15.setText("Contact Number");
        supplierDetails2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 70, 120, 30));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 0, 0));
        jLabel16.setText("Supplier Name");
        supplierDetails2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 120, 30));

        supplierNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supplierNameFieldActionPerformed(evt);
            }
        });
        supplierDetails2.add(supplierNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 330, 30));

        contactNumberField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        contactNumberField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactNumberFieldActionPerformed(evt);
            }
        });
        supplierDetails2.add(contactNumberField, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 100, 330, 30));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(102, 0, 0));
        jLabel17.setText("Contact Person");
        supplierDetails2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, 120, 30));

        contactPersonField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        contactPersonField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactPersonFieldActionPerformed(evt);
            }
        });
        supplierDetails2.add(contactPersonField, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, 330, 30));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 0, 0));
        jLabel18.setText("Address");
        supplierDetails2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 120, 30));

        addressField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressFieldActionPerformed(evt);
            }
        });
        supplierDetails2.add(addressField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, 330, 30));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(102, 0, 0));
        jLabel19.setText("Email Address");
        supplierDetails2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 120, 30));

        emailAddressField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        emailAddressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailAddressFieldActionPerformed(evt);
            }
        });
        supplierDetails2.add(emailAddressField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 330, 30));

        addSupplierBtn.setBackground(new java.awt.Color(41, 75, 107));
        addSupplierBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addSupplierBtn.setForeground(new java.awt.Color(255, 255, 255));
        addSupplierBtn.setText("Add");
        addSupplierBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addSupplierBtnMouseClicked(evt);
            }
        });
        addSupplierBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSupplierBtnActionPerformed(evt);
            }
        });
        supplierDetails2.add(addSupplierBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 150, 160, 40));

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
        supplierDetails2.add(clearFieldsBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 150, 150, 40));

        jPanel4.add(supplierDetails2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 800, 210));

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setBackground(new java.awt.Color(76, 157, 176));
        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("ADD SUPPLIER");
        jPanel9.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 2, 190, 50));

        jPanel4.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 50));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 270));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void supplierNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supplierNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_supplierNameFieldActionPerformed

    private void contactNumberFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactNumberFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contactNumberFieldActionPerformed

    private void contactPersonFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactPersonFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contactPersonFieldActionPerformed

    private void addressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addressFieldActionPerformed

    private void emailAddressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailAddressFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailAddressFieldActionPerformed

    private void addSupplierBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSupplierBtnMouseClicked

    }//GEN-LAST:event_addSupplierBtnMouseClicked

    private void addSupplierBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSupplierBtnActionPerformed
        String supplierName = supplierNameField.getText().trim();
        String contactPerson = contactPersonField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String email = emailAddressField.getText().trim();
        String address = addressField.getText().trim();

        if (!validateSupplierInputs()) {
            return;
        }

        if (!validateDuplicateSupplier()) {
            return;
        }

        String sql = "INSERT INTO suppliers "
                + "(name, contact_person, contact_number, email, address, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            Timestamp now = new Timestamp(System.currentTimeMillis());

            pst.setString(1, supplierName);
            pst.setString(2, contactPerson);
            pst.setString(3, contactNumber);
            pst.setString(4, email);
            pst.setString(5, address);
            pst.setTimestamp(6, now);
            pst.setTimestamp(7, now);

            int rowsInserted = pst.executeUpdate();

            if (rowsInserted > 0) {
                logAuditTrail(
                        loggedInUserId,
                        "ADD_SUPPLIER",
                        "Added supplier: " + supplierName
                        + " | Contact Person: " + contactPerson
                        + " | Contact Number: " + contactNumber
                        + " | Email: " + email
                );

                JOptionPane.showMessageDialog(this, "Supplier added successfully.");
                clearFields();

                if (supplierTbl != null) {
                    SupplierSearch.loadSuppliersToTable(supplierTbl);
                }

            } else {
                JOptionPane.showMessageDialog(this, "No supplier was added.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Add supplier error: " + ex.getMessage());
        }
    }//GEN-LAST:event_addSupplierBtnActionPerformed

    private void clearFieldsBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearFieldsBtnMouseClicked

    }//GEN-LAST:event_clearFieldsBtnMouseClicked

    private void clearFieldsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFieldsBtnActionPerformed
        clearFields();
       
    }//GEN-LAST:event_clearFieldsBtnActionPerformed

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
            java.util.logging.Logger.getLogger(AddSupplierDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddSupplierDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddSupplierDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddSupplierDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddSupplierDialog dialog = new AddSupplierDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSupplierBtn;
    private javax.swing.JTextField addressField;
    private javax.swing.JButton clearFieldsBtn;
    private javax.swing.JTextField contactNumberField;
    private javax.swing.JTextField contactPersonField;
    private javax.swing.JTextField emailAddressField;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel supplierDetails2;
    private javax.swing.JTextField supplierNameField;
    // End of variables declaration//GEN-END:variables
}
