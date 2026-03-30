/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.DatabaseConnection;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class EditCategoryDialog extends javax.swing.JDialog {

    private String originalCategoryName = "";
    private String originalDescription = "";
    private Integer editingCategoryId = null;
    private boolean editMode = false;
    private int loggedInUserId = -1;

    /**
     * Creates new form AddProductScreen
     */
    public EditCategoryDialog(java.awt.Frame parent, boolean modal, int loggedInUserId) {
        super(parent, modal);
        initComponents();
        this.loggedInUserId = loggedInUserId;

        setLocationRelativeTo(parent);
        setupCloseConfirmation();
        setEditModeDisplay();
    }

    public EditCategoryDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, -1);
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
                "Are you sure you want to cancel editing category?",
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

        originalCategoryName = categoryName != null ? categoryName : "";
        originalDescription = description != null ? description : "";

        categoryNameField.setText(originalCategoryName);
        descriptionField.setText(originalDescription);

        setEditModeDisplay();
    }

    private void setEditModeDisplay() {
        updateCategoryBtn.setText("Update");
        jLabel18.setText("EDIT CATEGORY");
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
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ? AND category_id <> ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, categoryName);
            pst.setInt(2, editingCategoryId == null ? -1 : editingCategoryId);

            try (java.sql.ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Category validation error: " + ex.getMessage());
        }

        return false;
    }

    private void resetFieldsToOriginalValues() {
        categoryNameField.setText(originalCategoryName);
        descriptionField.setText(originalDescription);
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
        updateCategoryBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(649, 365));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("EDIT CATEGORY");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 10, 200, 40));

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

        updateCategoryBtn.setBackground(new java.awt.Color(184, 33, 50));
        updateCategoryBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        updateCategoryBtn.setForeground(new java.awt.Color(255, 255, 255));
        updateCategoryBtn.setText("Update");
        updateCategoryBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateCategoryBtnMouseClicked(evt);
            }
        });
        updateCategoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateCategoryBtnActionPerformed(evt);
            }
        });
        jPanel5.add(updateCategoryBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, 110, 40));

        clearBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        clearBtn.setText("Reset");
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

    private void updateCategoryBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateCategoryBtnMouseClicked

    }//GEN-LAST:event_updateCategoryBtnMouseClicked

    private void updateCategoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCategoryBtnActionPerformed
        if (editingCategoryId == null) {
            JOptionPane.showMessageDialog(this, "No category selected for editing.");
            return;
        }

        if (!validateCategoryFields()) {
            return;
        }

        String categoryName = categoryNameField.getText().trim();
        String description = descriptionField.getText().trim();

        String sql = "UPDATE categories SET name = ?, description = ?, updated_at = ? WHERE category_id = ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            Timestamp now = new Timestamp(System.currentTimeMillis());

            pst.setString(1, categoryName);
            pst.setString(2, description);
            pst.setTimestamp(3, now);
            pst.setInt(4, editingCategoryId);

            int updatedRows = pst.executeUpdate();

            if (updatedRows > 0) {
                logAuditTrail(
                        loggedInUserId,
                        "UPDATE CATEGORY",
                        "Updated category: " + categoryName + " (Category ID: " + editingCategoryId + ")"
                );

                JOptionPane.showMessageDialog(this, "Category updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Category update failed.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update error: " + ex.getMessage());
        }
    }//GEN-LAST:event_updateCategoryBtnActionPerformed

    private void clearBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearBtnMouseClicked

    }//GEN-LAST:event_clearBtnMouseClicked

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the fields to the original category data?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            resetFieldsToOriginalValues();
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
            java.util.logging.Logger.getLogger(EditCategoryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditCategoryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditCategoryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditCategoryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                EditCategoryDialog dialog = new EditCategoryDialog(new javax.swing.JFrame(), true, 1);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JButton updateCategoryBtn;
    // End of variables declaration//GEN-END:variables
}
