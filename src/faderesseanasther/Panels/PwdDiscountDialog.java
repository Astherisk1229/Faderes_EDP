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
public class PwdDiscountDialog extends javax.swing.JDialog {

    private boolean discountApproved = false;
    private int discountCustomerId = -1;
    private String validatedCustomerName = "";
    private String validatedPwdId = "";
    private String validatedBookletNo = "";

    /**
     * Creates new form SeniorDiscountScreen
     */
    public PwdDiscountDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        initializeDialog(parent);
    }

    public PwdDiscountDialog(java.awt.Frame parent, boolean modal, String presetCustomerName) {
        this(parent, modal);

        if (presetCustomerName != null && !presetCustomerName.trim().isEmpty()) {
            jTextField3.setText(presetCustomerName.trim());
        }
    }

    private void initializeDialog(java.awt.Frame parent) {
        setLocationRelativeTo(parent);
        setResizable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jButton1.addActionListener(e -> applyPwdDiscount());
        jButton2.addActionListener(e -> confirmCancel());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmCancel();
            }
        });

        jTextField1.setToolTipText("Optional booklet number");
        jTextField2.setToolTipText("Enter the PWD ID number");
        jTextField3.setToolTipText("Enter the customer's full name");

        jTextField1.addActionListener(e -> applyPwdDiscount());
        jTextField2.addActionListener(e -> applyPwdDiscount());
        jTextField3.addActionListener(e -> applyPwdDiscount());
    }

    private void confirmCancel() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the PWD discount validation?",
                "Cancel PWD Discount",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            discountApproved = false;
            discountCustomerId = -1;
            validatedCustomerName = "";
            validatedPwdId = "";
            validatedBookletNo = "";
            dispose();
        }
    }

    private void applyPwdDiscount() {
        String pwdId = jTextField2.getText().trim();
        String fullName = jTextField3.getText().trim();
        String bookletNo = jTextField1.getText().trim();

        if (pwdId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PWD ID is required.");
            jTextField2.requestFocusInWindow();
            return;
        }

        if (pwdId.length() < 4) {
            JOptionPane.showMessageDialog(this, "PWD ID appears too short.");
            jTextField2.requestFocusInWindow();
            jTextField2.selectAll();
            return;
        }

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer name is required.");
            jTextField3.requestFocusInWindow();
            return;
        }

        if (!fullName.matches("[A-Za-zÀ-ÿ .'-]{2,100}")) {
            JOptionPane.showMessageDialog(this, "Customer name contains invalid characters.");
            jTextField3.requestFocusInWindow();
            jTextField3.selectAll();
            return;
        }

        DiscountCustomer customer = validatePwdCustomer(pwdId, fullName);

        if (customer == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "PWD record not found or inactive.",
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
            jTextField1.requestFocusInWindow();
            jTextField1.selectAll();
            return;
        }

        discountApproved = true;
        discountCustomerId = customer.discountCustomerId;
        validatedCustomerName = customer.fullName;
        validatedPwdId = customer.idNumber;
        validatedBookletNo = customer.bookletNo == null ? "" : customer.bookletNo;

        JOptionPane.showMessageDialog(
                this,
                "PWD customer validated successfully. Discount can now be applied.",
                "Validation Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    private DiscountCustomer validatePwdCustomer(String pwdId, String fullName) {
        String sql = "SELECT discount_customer_id, id_number, full_name, booklet_no "
                + "FROM discount_customers "
                + "WHERE discount_type = 'PWD' "
                + "AND id_number = ? "
                + "AND TRIM(LOWER(full_name)) = TRIM(LOWER(?)) "
                + "AND status = 'ACTIVE'";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pwdId);
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
                    "Database error while validating PWD customer: " + ex.getMessage(),
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

    public String getValidatedPwdId() {
        return validatedPwdId;
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

        jPanel2 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(248, 232, 222));
        jPanel2.setMaximumSize(new java.awt.Dimension(620, 430));
        jPanel2.setMinimumSize(new java.awt.Dimension(620, 430));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(184, 33, 50));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(76, 157, 176));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("APPLY PWD DISCOUNT");
        jPanel9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, -1, 60));

        jPanel2.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("(optional)");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, 100, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Please enter PWD customer details.");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 300, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("PWD ID:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 150, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Customer name:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 160, 150, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Booklet No.");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 100, 30));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanel2.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 200, 310, 30));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanel2.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 120, 350, 30));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanel2.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 160, 350, 30));

        jSeparator1.setBackground(new java.awt.Color(184, 33, 50));
        jSeparator1.setForeground(new java.awt.Color(248, 232, 222));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 540, 30));

        jButton1.setBackground(new java.awt.Color(210, 102, 90));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Apply Discount");
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 270, 210, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton2.setText("Cancel");
        jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 270, 130, 40));

        jSeparator2.setBackground(new java.awt.Color(184, 33, 50));
        jSeparator2.setForeground(new java.awt.Color(184, 33, 50));
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 540, 30));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

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
            java.util.logging.Logger.getLogger(PwdDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PwdDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PwdDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PwdDiscountDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PwdDiscountDialog dialog = new PwdDiscountDialog(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
