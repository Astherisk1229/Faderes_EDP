/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.DatabaseConnection;
import DbConnection.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class EditUserDialog extends javax.swing.JDialog {

    private Integer editingUserId = null;

    private String originalFirstName = "";
    private String originalMiddleName = "";
    private String originalLastName = "";
    private String originalUsername = "";
    private String originalRole = "";
    private String originalStatus = "";

    private boolean rowLoaded = false;

    /**
     * Creates new form AddProductScreen
     */
    public EditUserDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setupCloseConfirmation();
        setLocationRelativeTo(parent);
        setModeDisplay("EDIT USER DETAILS", "Leave password blank if you do not want to change it.");
    }

    public EditUserDialog(java.awt.Frame parent, boolean modal, int userId,
            String firstName, String middleName, String lastName,
            String username, String role, String status) {
        this(parent, modal);
        loadUserData(userId, firstName, middleName, lastName, username, role, status);
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
                "Are you sure you want to cancel editing user?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    public void loadUserData(
            int userId,
            String firstName,
            String middleName,
            String lastName,
            String username,
            String role,
            String status
    ) {
        editingUserId = userId;

        originalFirstName = firstName != null ? firstName : "";
        originalMiddleName = middleName != null ? middleName : "";
        originalLastName = lastName != null ? lastName : "";
        originalUsername = username != null ? username : "";
        originalRole = role != null ? role : "";
        originalStatus = status != null ? status : "";

        rowLoaded = true;

        firstNameField.setText(originalFirstName);
        middleNameField.setText(originalMiddleName);
        lastNameField.setText(originalLastName);
        usernameField.setText(originalUsername);
        roleCmb.setSelectedItem(originalRole);
        if (originalStatus != null && !originalStatus.trim().isEmpty()) {
            String normalizedStatus = originalStatus.trim();
            normalizedStatus = normalizedStatus.substring(0, 1).toUpperCase()
                    + normalizedStatus.substring(1).toLowerCase();
            statusCmb.setSelectedItem(normalizedStatus);
        } else {
            statusCmb.setSelectedIndex(0);
        }

        passwordField.setText("");
        passwordStatusField.setText("Leave password blank if you do not want to change it.");
    }

    private void resetLoadedUserFields() {
        firstNameField.setText(originalFirstName);
        middleNameField.setText(originalMiddleName);
        lastNameField.setText(originalLastName);
        usernameField.setText(originalUsername);
        roleCmb.setSelectedItem(originalRole);
        if (originalStatus != null && !originalStatus.trim().isEmpty()) {
            String normalizedStatus = originalStatus.trim();
            normalizedStatus = normalizedStatus.substring(0, 1).toUpperCase()
                    + normalizedStatus.substring(1).toLowerCase();
            statusCmb.setSelectedItem(normalizedStatus);
        } else {
            statusCmb.setSelectedIndex(0);
        }

        passwordField.setText("");
        passwordStatusField.setText("Leave password blank if you do not want to change it.");
    }

    private void setModeDisplay(String title, String message) {
        jLabel18.setText(title);
        passwordStatusField.setText(message);
    }

    private boolean anotherSuperAdminExists(Integer currentEditingUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND user_id <> ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "Super Admin");
            pst.setInt(2, currentEditingUserId == null ? -1 : currentEditingUserId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to validate Super Admin role: " + ex.getMessage());
        }

        return false;
    }

    private boolean validateUserFields(boolean requirePassword) {
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleCmb.getSelectedItem() != null ? roleCmb.getSelectedItem().toString().trim() : "";
        String status = statusCmb.getSelectedItem() != null ? statusCmb.getSelectedItem().toString().trim() : "";

        if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required.");
            firstNameField.requestFocus();
            return false;
        }

        if (!firstName.matches("^[A-Za-z' -]+$")) {
            JOptionPane.showMessageDialog(this, "First name must contain letters only.");
            firstNameField.requestFocus();
            return false;
        }

        if (!middleName.isEmpty() && !middleName.matches("^[A-Za-z' -]+$")) {
            JOptionPane.showMessageDialog(this, "Middle name must contain letters only.");
            middleNameField.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required.");
            lastNameField.requestFocus();
            return false;
        }

        if (!lastName.matches("^[A-Za-z' -]+$")) {
            JOptionPane.showMessageDialog(this, "Last name must contain letters only.");
            lastNameField.requestFocus();
            return false;
        }

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            usernameField.requestFocus();
            return false;
        }

        if (username.length() < 4) {
            JOptionPane.showMessageDialog(this, "Username must be at least 4 characters.");
            usernameField.requestFocus();
            return false;
        }

        if (!username.matches("^[A-Za-z0-9._]+$")) {
            JOptionPane.showMessageDialog(this, "Username can only contain letters, numbers, dot, and underscore.");
            usernameField.requestFocus();
            return false;
        }

        if (usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
            usernameField.requestFocus();
            return false;
        }

        if (requirePassword) {
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password is required.");
                passwordField.requestFocus();
                return false;
            }

            if (password.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.");
                passwordField.requestFocus();
                return false;
            }
        } else {
            if (!password.isEmpty() && password.length() < 8) {
                JOptionPane.showMessageDialog(this, "New password must be at least 8 characters.");
                passwordField.requestFocus();
                return false;
            }
        }

        if (role.isEmpty() || role.equalsIgnoreCase("Select Role")) {
            JOptionPane.showMessageDialog(this, "Please select a role.");
            roleCmb.requestFocus();
            return false;
        }

        if (status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a status.");
            statusCmb.requestFocus();
            return false;
        }

        if (role.equalsIgnoreCase("Super Admin") && anotherSuperAdminExists(editingUserId)) {
            JOptionPane.showMessageDialog(this, "Creating another Super Admin is not allowed.");
            roleCmb.requestFocus();
            return false;
        }

        return true;
    }

    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id <> ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setInt(2, editingUserId == null ? -1 : editingUserId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Username validation error: " + ex.getMessage());
        }

        return false;
    }

    private void clearFields() {
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        usernameField.setText("");
        passwordField.setText("");

        if (roleCmb.getItemCount() > 0) {
            roleCmb.setSelectedIndex(0);
        }

        if (statusCmb.getItemCount() > 0) {
            statusCmb.setSelectedIndex(0);
        }

        passwordStatusField.setText("");
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
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        firstNameField = new javax.swing.JTextField();
        middleNameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        roleCmb = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        updateUserBtn = new javax.swing.JButton();
        resetBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        passwordStatusField = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        statusCmb = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1034, 515));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("EDIT USER DETAILS");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, -1, -1));

        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 60));

        jPanel7.setBackground(new java.awt.Color(210, 102, 90));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Middle Name");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 120, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("First Name");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 120, -1));

        firstNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        firstNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameFieldActionPerformed(evt);
            }
        });
        firstNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                firstNameFieldKeyTyped(evt);
            }
        });
        jPanel6.add(firstNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 380, 40));

        middleNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        middleNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                middleNameFieldActionPerformed(evt);
            }
        });
        middleNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                middleNameFieldKeyTyped(evt);
            }
        });
        jPanel6.add(middleNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 380, 40));

        jLabel2.setBackground(new java.awt.Color(76, 157, 176));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(184, 33, 50));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("User Details");
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Last Name");
        jPanel6.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 120, -1));

        lastNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lastNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastNameFieldActionPerformed(evt);
            }
        });
        jPanel6.add(lastNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, 380, 40));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Username");
        jPanel6.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 60, 120, -1));

        usernameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel6.add(usernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 80, 380, 40));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Role");
        jPanel6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 130, 120, -1));

        roleCmb.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        roleCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Role", "Super Admin", "Manager", "Cashier", "Inventory Clerk" }));
        jPanel6.add(roleCmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, 380, 40));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Password");
        jPanel6.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 200, 120, -1));

        passwordField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel6.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 220, 380, 40));

        updateUserBtn.setBackground(new java.awt.Color(242, 178, 140));
        updateUserBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        updateUserBtn.setForeground(new java.awt.Color(102, 0, 0));
        updateUserBtn.setText("Update");
        updateUserBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateUserBtnMouseClicked(evt);
            }
        });
        updateUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateUserBtnActionPerformed(evt);
            }
        });
        jPanel6.add(updateUserBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 310, 120, 40));

        resetBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        resetBtn.setText("Reset");
        resetBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetBtnMouseClicked(evt);
            }
        });
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });
        jPanel6.add(resetBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 310, 120, 40));

        jSeparator1.setForeground(new java.awt.Color(102, 0, 0));
        jPanel6.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 910, 10));
        jPanel6.add(passwordStatusField, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 270, 320, 30));

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Status");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 50, -1));

        statusCmb.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        statusCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));
        jPanel6.add(statusCmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 310, 380, 40));

        jPanel7.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 980, 380));

        getContentPane().add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1050, 460));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void firstNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameFieldActionPerformed

    }//GEN-LAST:event_firstNameFieldActionPerformed

    private void firstNameFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameFieldKeyTyped

    }//GEN-LAST:event_firstNameFieldKeyTyped

    private void middleNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_middleNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_middleNameFieldActionPerformed

    private void middleNameFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_middleNameFieldKeyTyped

    }//GEN-LAST:event_middleNameFieldKeyTyped

    private void lastNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lastNameFieldActionPerformed

    private void updateUserBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateUserBtnMouseClicked

    }//GEN-LAST:event_updateUserBtnMouseClicked

    private void updateUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateUserBtnActionPerformed
        if (editingUserId == null) {
            JOptionPane.showMessageDialog(this, "No user is selected for editing.");
            return;
        }

        if (!validateUserFields(false)) {
            return;
        }

        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleCmb.getSelectedItem() != null
                ? roleCmb.getSelectedItem().toString().trim()
                : "";
        String status = statusCmb.getSelectedItem() != null
                ? statusCmb.getSelectedItem().toString().trim()
                : "";

        status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();

        boolean changePassword = !password.isEmpty();

        String sql;
        if (changePassword) {
            sql = "UPDATE users SET first_name = ?, middle_name = ?, last_name = ?, username = ?, password = ?, role = ?, status = ? WHERE user_id = ?";
        } else {
            sql = "UPDATE users SET first_name = ?, middle_name = ?, last_name = ?, username = ?, role = ?, status = ? WHERE user_id = ?";
        }

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            if (changePassword) {
                String hashedPassword = PasswordHasher.hashPassword(password);

                pst.setString(1, firstName);
                pst.setString(2, middleName);
                pst.setString(3, lastName);
                pst.setString(4, username);
                pst.setString(5, hashedPassword);
                pst.setString(6, role);
                pst.setString(7, status);
                pst.setInt(8, editingUserId);
            } else {
                pst.setString(1, firstName);
                pst.setString(2, middleName);
                pst.setString(3, lastName);
                pst.setString(4, username);
                pst.setString(5, role);
                pst.setString(6, status);
                pst.setInt(7, editingUserId);
            }

            int updatedRows = pst.executeUpdate();

            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "User updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "User update failed.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update user error: " + ex.getMessage());
        }

    }//GEN-LAST:event_updateUserBtnActionPerformed

    private void resetBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_resetBtnMouseClicked

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        if (rowLoaded) {
            resetLoadedUserFields();
            setModeDisplay("EDIT USER DETAILS", "Fields were reset to the selected user's original data.");
            return;
        }

        clearFields();
        setModeDisplay("EDIT USER DETAILS", "Leave password blank if you do not want to change it.");
    }//GEN-LAST:event_resetBtnActionPerformed

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
            java.util.logging.Logger.getLogger(EditUserDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditUserDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditUserDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditUserDialog.class
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditUserDialog dialog = new EditUserDialog(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField firstNameField;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JTextField middleNameField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordStatusField;
    private javax.swing.JButton resetBtn;
    private javax.swing.JComboBox<String> roleCmb;
    private javax.swing.JComboBox<String> statusCmb;
    private javax.swing.JButton updateUserBtn;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
