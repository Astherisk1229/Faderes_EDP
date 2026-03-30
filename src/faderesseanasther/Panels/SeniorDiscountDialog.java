/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class SeniorDiscountDialog extends javax.swing.JDialog {

    private boolean discountApproved = false;
    private int discountCustomerId = -1;
    private String validatedCustomerName = "";
    private String validatedSeniorId = "";
    private String validatedBookletNo = "";

    /**
     * Creates new form SeniorDiscountScreen
     */
    public SeniorDiscountDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        initializeDialog(parent);
    }

    public SeniorDiscountDialog(java.awt.Frame parent, boolean modal, String presetCustomerName) {
        this(parent, modal);

        if (presetCustomerName != null && !presetCustomerName.trim().isEmpty()) {
            nameField.setText(presetCustomerName.trim());
        }
    }

    private void initializeDialog(java.awt.Frame parent) {
        setLocationRelativeTo(parent);
        setResizable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        applyDiscBtn.addActionListener(e -> applySeniorDiscount());
        cancelBtn.addActionListener(e -> confirmCancel());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmCancel();
            }
        });

        bookletNoField.setToolTipText("Optional booklet number");
        seniorCitizenIdField.setToolTipText("Enter the Senior Citizen ID number");
        nameField.setToolTipText("Enter the customer's full name");

        bookletNoField.addActionListener(e -> applySeniorDiscount());
        seniorCitizenIdField.addActionListener(e -> applySeniorDiscount());
        nameField.addActionListener(e -> applySeniorDiscount());
    }

    private void confirmCancel() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the Senior Citizen discount validation?",
                "Cancel Senior Citizen Discount",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            discountApproved = false;
            discountCustomerId = -1;
            validatedCustomerName = "";
            validatedSeniorId = "";
            validatedBookletNo = "";
            dispose();
        }
    }

    private void applySeniorDiscount() {
        String seniorId = seniorCitizenIdField.getText().trim();
        String fullName = nameField.getText().trim();
        String bookletNo = bookletNoField.getText().trim();

        if (seniorId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Senior Citizen ID is required.");
            seniorCitizenIdField.requestFocusInWindow();
            return;
        }

        if (seniorId.length() < 4) {
            JOptionPane.showMessageDialog(this, "Senior Citizen ID appears too short.");
            seniorCitizenIdField.requestFocusInWindow();
            seniorCitizenIdField.selectAll();
            return;
        }

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer name is required.");
            nameField.requestFocusInWindow();
            return;
        }

        if (!fullName.matches("[A-Za-zÀ-ÿ .'-]{2,100}")) {
            JOptionPane.showMessageDialog(this, "Customer name contains invalid characters.");
            nameField.requestFocusInWindow();
            nameField.selectAll();
            return;
        }

        DiscountCustomer customer = validateSeniorCustomer(seniorId, fullName);

        if (customer == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Senior Citizen record not found or inactive.",
                    "Validation Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!bookletNo.isEmpty()
                && customer.bookletNo != null
                && !customer.bookletNo.trim().isEmpty()
                && !bookletNo.equalsIgnoreCase(customer.bookletNo.trim())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booklet number does not match the registered record.",
                    "Validation Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            bookletNoField.requestFocusInWindow();
            bookletNoField.selectAll();
            return;
        }

        discountApproved = true;
        discountCustomerId = customer.discountCustomerId;
        validatedCustomerName = customer.fullName;
        validatedSeniorId = customer.idNumber;
        validatedBookletNo = customer.bookletNo == null ? "" : customer.bookletNo;

        JOptionPane.showMessageDialog(
                this,
                "Senior Citizen validated successfully. Discount can now be applied.",
                "Validation Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    private DiscountCustomer validateSeniorCustomer(String seniorId, String fullName) {
        String sql = "SELECT discount_customer_id, id_number, full_name, booklet_no "
                + "FROM discount_customers "
                + "WHERE discount_type = 'SENIOR' "
                + "AND id_number = ? "
                + "AND TRIM(LOWER(full_name)) = TRIM(LOWER(?)) "
                + "AND status = 'ACTIVE'";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, seniorId);
            pst.setString(2, fullName);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    DiscountCustomer customer = new DiscountCustomer();
                    customer.discountCustomerId = rs.getInt("discount_customer_id");
                    customer.idNumber = rs.getString("id_number");
                    customer.fullName = rs.getString("full_name");
                    customer.bookletNo = rs.getString("booklet_no");
                    return customer;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while validating Senior Citizen: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return null;
    }

    private static class DiscountCustomer {

        int discountCustomerId;
        String idNumber;
        String fullName;
        String bookletNo;
    }

    public boolean isDiscountApproved() {
        return discountApproved;
    }

    public int getDiscountCustomerId() {
        return discountCustomerId;
    }

    public String getValidatedCustomerName() {
        return validatedCustomerName;
    }

    public String getValidatedSeniorId() {
        return validatedSeniorId;
    }

    public String getValidatedBookletNo() {
        return validatedBookletNo;
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
        jPanel9 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        bookletNoField = new javax.swing.JTextField();
        seniorCitizenIdField = new javax.swing.JTextField();
        nameField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        applyDiscBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(248, 232, 222));
        jPanel1.setMaximumSize(new java.awt.Dimension(620, 430));
        jPanel1.setMinimumSize(new java.awt.Dimension(620, 430));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("APPLY SENIOR CITIZEN DISCOUNT");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 2, -1, 60));

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("(optional)");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, 100, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Please enter Senior Citizen details.");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 300, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Senior Citizen ID:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 150, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Name:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 160, 150, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Booklet No.");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 100, 30));

        bookletNoField.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanel1.add(bookletNoField, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 200, 310, 30));

        seniorCitizenIdField.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanel1.add(seniorCitizenIdField, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 120, 350, 30));

        nameField.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanel1.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 160, 350, 30));

        jSeparator1.setBackground(new java.awt.Color(184, 33, 50));
        jSeparator1.setForeground(new java.awt.Color(248, 232, 222));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 540, 30));

        applyDiscBtn.setBackground(new java.awt.Color(210, 102, 90));
        applyDiscBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        applyDiscBtn.setForeground(new java.awt.Color(255, 255, 255));
        applyDiscBtn.setText("Apply Discount");
        jPanel1.add(applyDiscBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 270, 210, 40));

        cancelBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cancelBtn.setText("Cancel");
        jPanel1.add(cancelBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 270, 130, 40));

        jSeparator2.setBackground(new java.awt.Color(184, 33, 50));
        jSeparator2.setForeground(new java.awt.Color(184, 33, 50));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 540, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 671, 347));

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(SeniorDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SeniorDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SeniorDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeniorDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SeniorDiscountDialog dialog = new SeniorDiscountDialog(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyDiscBtn;
    private javax.swing.JTextField bookletNoField;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField seniorCitizenIdField;
    // End of variables declaration//GEN-END:variables
}
