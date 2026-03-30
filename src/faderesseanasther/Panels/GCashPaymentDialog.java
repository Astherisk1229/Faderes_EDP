/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class GCashPaymentDialog extends javax.swing.JDialog {

    private boolean paymentApproved = false;
    private double amountDue = 0.0;

    private String referenceNo = "";
    private String senderName = "";
    private String senderMobileNo = "";

    /**
     * Creates new form GCashPaymentScreen
     */
    public GCashPaymentDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
         setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        initializeGCashScreen();
    }

    public GCashPaymentDialog(java.awt.Frame parent, boolean modal, double amountDue) {
        super(parent, modal);
        initComponents();
        this.amountDue = amountDue;
        initializeGCashScreen();
        setAmountDue(amountDue);
    }

    private void initializeGCashScreen() {
        cancelBtn.addActionListener(e -> confirmCancel());
        setLocationRelativeTo(getParent());
        setResizable(false);

        amountDueField.setEditable(false);
        amountDueField.setFocusable(false);

        referenceNoField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        senderNameField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        senderMobileNoField.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        senderMobileNoField.setToolTipText("Enter 11-digit Philippine mobile number starting with 09");
        referenceNoField.setToolTipText("Enter the GCash reference number");

        senderMobileNoField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) || senderMobileNoField.getText().length() >= 11) {
                    evt.consume();
                }
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmCancel();
            }
        });

        cancelBtn.addActionListener(e -> confirmCancel());
        confirmPaymentBtn.addActionListener(e -> confirmGCashPayment());
    }

    private void confirmCancel() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the GCash payment?",
                "Cancel GCash Payment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            paymentApproved = false;
            dispose();
        }
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
        amountDueField.setText(String.format("₱ %,.2f", amountDue));
    }

    private void confirmGCashPayment() {
        referenceNo = referenceNoField.getText().trim();
        senderName = senderNameField.getText().trim();
        senderMobileNo = senderMobileNoField.getText().trim();

        if (amountDue <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid amount due.");
            return;
        }

        if (referenceNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reference number is required.");
            referenceNoField.requestFocusInWindow();
            return;
        }

        if (!referenceNo.matches("[A-Za-z0-9\\-]{6,30}")) {
            JOptionPane.showMessageDialog(this, "Reference number format is invalid.");
            referenceNoField.requestFocusInWindow();
            referenceNoField.selectAll();
            return;
        }

        if (senderName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sender name is required.");
            senderNameField.requestFocusInWindow();
            return;
        }

        if (!senderName.matches("[A-Za-zÀ-ÿ .'-]{2,100}")) {
            JOptionPane.showMessageDialog(this, "Sender name contains invalid characters.");
            senderNameField.requestFocusInWindow();
            senderNameField.selectAll();
            return;
        }

        if (senderMobileNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sender mobile number is required.");
            senderMobileNoField.requestFocusInWindow();
            return;
        }

        if (!senderMobileNo.matches("09\\d{9}")) {
            JOptionPane.showMessageDialog(this, "Sender mobile number must be a valid Philippine mobile number.");
            senderMobileNoField.requestFocusInWindow();
            senderMobileNoField.selectAll();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Have you received the GCash payment?",
                "Confirm GCash Payment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        paymentApproved = true;
        dispose();
    }

    public boolean isPaymentApproved() {
        return paymentApproved;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderMobileNo() {
        return senderMobileNo;
    }

    public double getAmountDue() {
        return amountDue;
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        senderMobileNoField = new javax.swing.JTextField();
        amountDueField = new javax.swing.JTextField();
        referenceNoField = new javax.swing.JTextField();
        senderNameField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cancelBtn = new javax.swing.JButton();
        confirmPaymentBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(248, 248, 249));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("GCASH PAYMENT");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 160, 50));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 540, 70));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("Sender Mobile No:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel3.setText("Amount Due:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setText("Reference No:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel5.setText("Sender Name:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, 30));

        senderMobileNoField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        senderMobileNoField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(senderMobileNoField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 210, 310, -1));

        amountDueField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        amountDueField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(amountDueField, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 90, 170, -1));

        referenceNoField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        referenceNoField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(referenceNoField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 310, -1));

        senderNameField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        senderNameField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(senderNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 170, 310, -1));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 520, 10));

        cancelBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cancelBtn.setText("CANCEL");
        jPanel1.add(cancelBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 240, 40));

        confirmPaymentBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        confirmPaymentBtn.setText("CONFIRM PAYMENT");
        jPanel1.add(confirmPaymentBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 270, 240, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 540, 330));

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
            java.util.logging.Logger.getLogger(GCashPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GCashPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GCashPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GCashPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GCashPaymentDialog dialog = new GCashPaymentDialog(new javax.swing.JFrame(), true, 150.00);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField amountDueField;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton confirmPaymentBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField referenceNoField;
    private javax.swing.JTextField senderMobileNoField;
    private javax.swing.JTextField senderNameField;
    // End of variables declaration//GEN-END:variables
}
