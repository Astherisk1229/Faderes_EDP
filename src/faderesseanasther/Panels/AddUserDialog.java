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
public class AddUserDialog extends javax.swing.JDialog {

    private Integer editingUserId = null;
    private boolean editMode = false;

    /**
     * Creates new form AddProductScreen
     */
    public AddUserDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        setupCloseConfirmation();
        setAddMode();
        setLocationRelativeTo(parent);

    }

    public AddUserDialog(java.awt.Frame parent, boolean modal, boolean editMode) {
        this(parent, modal);
        this.editMode = editMode;
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
            message = "Are you sure you want to cancel editing user?";
        } else {
            message = "Are you sure you want to cancel adding user?";
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

    private boolean superAdminAlreadyExists() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "Super Admin");

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

    public void loadUserDataFromTableRow(
            int userId,
            String firstName,
            String middleName,
            String lastName,
            String username,
            String role
    ) {
        this.editingUserId = userId;
        this.editMode = true;

        firstNameField.setText(firstName != null ? firstName : "");
        middleNameField.setText(middleName != null ? middleName : "");
        lastNameField.setText(lastName != null ? lastName : "");
        usernameField.setText(username != null ? username : "");
        if (role != null) {
            roleCmb.setSelectedItem(role);
        }

        passwordField.setText("");

        addNewUserBtn.setText("Update");
        jLabel18.setText("EDIT USER");
        passwordStatusField.setText("Leave password blank if you do not want to change it.");
    }

    private void setAddMode() {
        editingUserId = null;
        editMode = false;

        addNewUserBtn.setText("Add");
        jLabel18.setText("ADD USER");
        passwordField.setText("");
        passwordStatusField.setText("");

        if (roleCmb.getItemCount() > 0) {
            roleCmb.setSelectedIndex(0);
        }
    }

    private boolean validateUserFields(boolean requirePassword) {
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        Object roleObj = roleCmb.getSelectedItem();
        String role = roleObj != null ? roleObj.toString().trim() : "";

        if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required.");
            firstNameField.requestFocus();
            return false;
        }

        if (!Pattern.matches("^[A-Za-z' -]+$", firstName)) {
            JOptionPane.showMessageDialog(this, "First name must contain letters only.");
            firstNameField.requestFocus();
            return false;
        }

        if (!middleName.isEmpty() && !Pattern.matches("^[A-Za-z' -]+$", middleName)) {
            JOptionPane.showMessageDialog(this, "Middle name must contain letters only.");
            middleNameField.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required.");
            lastNameField.requestFocus();
            return false;
        }

        if (!Pattern.matches("^[A-Za-z' -]+$", lastName)) {
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

        if (!Pattern.matches("^[A-Za-z0-9._]+$", username)) {
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
            JOptionPane.showMessageDialog(this, "Please select a valid role.");
            roleCmb.requestFocus();
            return false;
        }

        if (!editMode && role.equalsIgnoreCase("Super Admin") && superAdminAlreadyExists()) {
            JOptionPane.showMessageDialog(this, "Creating another Super Admin is not allowed.");
            roleCmb.requestFocus();
            return false;
        }

        return true;
    }

    private boolean usernameExists(String username) {
        String sql;

        if (editMode && editingUserId != null) {
            sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id <> ?";
        } else {
            sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        }

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);

            if (editMode && editingUserId != null) {
                pst.setInt(2, editingUserId);
            }

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
        passwordStatusField.setText("");

        if (roleCmb.getItemCount() > 0) {
            roleCmb.setSelectedIndex(0);
        }

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
        addNewUserBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        passwordStatusField = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1034, 515));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("ADD USER");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, -1, -1));

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
        jPanel6.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 60, 120, -1));

        usernameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel6.add(usernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 80, 380, 40));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Role");
        jPanel6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 130, 120, -1));

        roleCmb.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        roleCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Role", "Super Admin", "Manager", "Cashier", "Inventory Clerk" }));
        jPanel6.add(roleCmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 150, 380, 40));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Password");
        jPanel6.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 200, 120, -1));

        passwordField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel6.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 220, 380, 40));

        addNewUserBtn.setBackground(new java.awt.Color(242, 178, 140));
        addNewUserBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addNewUserBtn.setForeground(new java.awt.Color(102, 0, 0));
        addNewUserBtn.setText("Add");
        addNewUserBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addNewUserBtnMouseClicked(evt);
            }
        });
        addNewUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewUserBtnActionPerformed(evt);
            }
        });
        jPanel6.add(addNewUserBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 310, 120, 40));

        clearBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
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
        jPanel6.add(clearBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 310, 120, 40));

        jSeparator1.setForeground(new java.awt.Color(102, 0, 0));
        jPanel6.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 900, 10));
        jPanel6.add(passwordStatusField, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 270, 320, 30));

        jPanel7.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 970, 380));

        getContentPane().add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1020, 430));

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

    private void addNewUserBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addNewUserBtnMouseClicked

    }//GEN-LAST:event_addNewUserBtnMouseClicked

    private void addNewUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewUserBtnActionPerformed
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleCmb.getSelectedItem().toString().trim();

        if (!validateUserFields(!editMode)) {
            return;
        }

        try (Connection con = DatabaseConnection.dbConnection()) {

            if (editMode && editingUserId != null) {
                boolean changePassword = !password.isEmpty();
                String sql;

                if (changePassword) {
                    sql = "UPDATE users SET first_name=?, middle_name=?, last_name=?, username=?, password=?, role=? WHERE user_id=?";
                } else {
                    sql = "UPDATE users SET first_name=?, middle_name=?, last_name=?, username=?, role=? WHERE user_id=?";
                }

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    if (changePassword) {
                        String hashedPassword = PasswordHasher.hashPassword(password);

                        pst.setString(1, firstName);
                        pst.setString(2, middleName);
                        pst.setString(3, lastName);
                        pst.setString(4, username);
                        pst.setString(5, hashedPassword);
                        pst.setString(6, role);
                        pst.setInt(7, editingUserId);
                    } else {
                        pst.setString(1, firstName);
                        pst.setString(2, middleName);
                        pst.setString(3, lastName);
                        pst.setString(4, username);
                        pst.setString(5, role);
                        pst.setInt(6, editingUserId);
                    }

                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "User updated successfully.");

            } else {
                String hashedPassword = PasswordHasher.hashPassword(password);
                String defaultStatus = "Active";

                String sql = "INSERT INTO users (first_name, middle_name, last_name, username, password, role, status, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, firstName);
                    pst.setString(2, middleName);
                    pst.setString(3, lastName);
                    pst.setString(4, username);
                    pst.setString(5, hashedPassword);
                    pst.setString(6, role);
                    pst.setString(7, defaultStatus);
                    pst.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "User added successfully.");
            }

            clearFields();
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Save user error: " + ex.getMessage());
        }
    }//GEN-LAST:event_addNewUserBtnActionPerformed

    private void clearBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_clearBtnMouseClicked

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all input fields?",
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddUserDialog dialog = new AddUserDialog(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewUserBtn;
    private javax.swing.JButton clearBtn;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JTextField middleNameField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordStatusField;
    private javax.swing.JComboBox<String> roleCmb;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
