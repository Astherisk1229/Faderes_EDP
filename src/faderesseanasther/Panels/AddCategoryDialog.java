/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import javax.swing.table.DefaultTableModel;
import DbConnection.DatabaseConnection;
import DbConnection.PasswordHasher;
import java.sql.Timestamp;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class AddCategoryDialog extends javax.swing.JDialog {

    private Integer editingCategoryId = null;
    private boolean editMode = false;
    private int loggedInUserId = -1;

    /**
     * Creates new form AddProductScreen
     */
    public AddCategoryDialog(java.awt.Frame parent, boolean modal, int loggedInUserId) {
        super(parent, modal);
        initComponents();
        this.loggedInUserId = loggedInUserId;

        setLocationRelativeTo(parent);
        setupCloseConfirmation();
        setAddMode();
    }

    public AddCategoryDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, -1);
    }

    private void logAuditTrail(int userId, String action, String description) {
        if (userId <= 0) {
            System.out.println("Audit trail skipped: invalid user id.");
            return;
        }

        String sql = "INSERT INTO transactions_log (user_id, action, description, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setString(2, action);
            pst.setString(3, description);
            pst.executeUpdate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Audit trail error: " + ex.getMessage());
        }
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
        String message;

        if (editMode) {
            message = "Are you sure you want to cancel editing category?";
        } else {
            message = "Are you sure you want to cancel adding category?";
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    public void loadCategoryData(int categoryId, String categoryName, String description) {
        this.editingCategoryId = categoryId;
        this.editMode = true;

        categoryNameField.setText(categoryName != null ? categoryName : "");
        descriptionField.setText(description != null ? description : "");

        addCategoryBtn.setText("Update");
        jLabel18.setText("EDIT CATEGORY");
    }

    private void setAddMode() {
        editingCategoryId = null;
        editMode = false;

        addCategoryBtn.setText("Add");
        jLabel18.setText("ADD CATEGORY");
    }

    private boolean validateCategoryFields() {
        String categoryName = categoryNameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name is required.");
            categoryNameField.requestFocus();
            return false;
        }

        if (categoryName.length() < 2) {
            JOptionPane.showMessageDialog(this, "Category name must be at least 2 characters.");
            categoryNameField.requestFocus();
            return false;
        }

        if (categoryExists(categoryName)) {
            JOptionPane.showMessageDialog(this, "Category name already exists.");
            categoryNameField.requestFocus();
            return false;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required.");
            descriptionField.requestFocus();
            return false;
        }

        return true;
    }

    private boolean categoryExists(String categoryName) {
        String sql;

        if (editMode && editingCategoryId != null) {
            sql = "SELECT COUNT(*) FROM categories WHERE name = ? AND category_id <> ?";
        } else {
            sql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        }

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, categoryName);

            if (editMode && editingCategoryId != null) {
                pst.setInt(2, editingCategoryId);
            }

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Category validation error: " + ex.getMessage());
        }

        return false;
    }

    private void clearFields() {
        categoryNameField.setText("");
        descriptionField.setText("");
        setAddMode();
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
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        categoryNameField = new javax.swing.JTextField();
        descriptionField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        addCategoryBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(649, 365));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("ADD CATEGORY");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 12, -1, 40));

        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 60));

        jPanel4.setBackground(new java.awt.Color(210, 102, 90));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Description");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 120, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Category Name");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 120, -1));

        categoryNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        categoryNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryNameFieldActionPerformed(evt);
            }
        });
        jPanel5.add(categoryNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 520, 40));

        descriptionField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        descriptionField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptionFieldActionPerformed(evt);
            }
        });
        jPanel5.add(descriptionField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 520, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(184, 33, 50));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Category Details");
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        addCategoryBtn.setBackground(new java.awt.Color(184, 33, 50));
        addCategoryBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addCategoryBtn.setForeground(new java.awt.Color(255, 255, 255));
        addCategoryBtn.setText("Add");
        addCategoryBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addCategoryBtnMouseClicked(evt);
            }
        });
        addCategoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCategoryBtnActionPerformed(evt);
            }
        });
        jPanel5.add(addCategoryBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, 110, 40));

        clearBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        clearBtn.setText("Clear");
        clearBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearBtnMouseClicked(evt);
            }
        });
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });
        jPanel5.add(clearBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 220, 110, 40));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 620, 280));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 650, 310));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void categoryNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryNameFieldActionPerformed

    private void descriptionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptionFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descriptionFieldActionPerformed

    private void addCategoryBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addCategoryBtnMouseClicked

    }//GEN-LAST:event_addCategoryBtnMouseClicked

    private void addCategoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryBtnActionPerformed
        if (!validateCategoryFields()) {
            return;
        }

        String categoryName = categoryNameField.getText().trim();
        String description = descriptionField.getText().trim();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection con = DatabaseConnection.dbConnection()) {

            if (editMode && editingCategoryId != null) {
                String sql = "UPDATE categories SET name = ?, description = ?, updated_at = ? WHERE category_id = ?";

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, categoryName);
                    pst.setString(2, description);
                    pst.setTimestamp(3, now);
                    pst.setInt(4, editingCategoryId);
                    pst.executeUpdate();
                }

                logAuditTrail(
                        loggedInUserId,
                        "UPDATE CATEGORY",
                        "Updated category: " + categoryName + " (Category ID: " + editingCategoryId + ")"
                );

                JOptionPane.showMessageDialog(this, "Category updated successfully.");
            } else {
                String sql = "INSERT INTO categories (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";

                int insertedCategoryId = -1;

                try (PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pst.setString(1, categoryName);
                    pst.setString(2, description);
                    pst.setTimestamp(3, now);
                    pst.setTimestamp(4, now);
                    pst.executeUpdate();

                    try (ResultSet rs = pst.getGeneratedKeys()) {
                        if (rs.next()) {
                            insertedCategoryId = rs.getInt(1);
                        }
                    }
                }

                logAuditTrail(
                        loggedInUserId,
                        "ADD CATEGORY",
                        "Added category: " + categoryName + (insertedCategoryId > 0 ? " (Category ID: " + insertedCategoryId + ")" : "")
                );

                JOptionPane.showMessageDialog(this, "Category added successfully.");
            }

            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Save category error: " + ex.getMessage());
        }
    }//GEN-LAST:event_addCategoryBtnActionPerformed

    private void clearBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearBtnMouseClicked

    }//GEN-LAST:event_clearBtnMouseClicked

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the fields?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            clearFields();
        }
    }//GEN-LAST:event_clearBtnActionPerformed

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
            java.util.logging.Logger.getLogger(AddCategoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddCategoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddCategoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddCategoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                AddCategoryDialog dialog = new AddCategoryDialog(new javax.swing.JFrame(), true, 1);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCategoryBtn;
    private javax.swing.JTextField categoryNameField;
    private javax.swing.JButton clearBtn;
    private javax.swing.JTextField descriptionField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    // End of variables declaration//GEN-END:variables
}
