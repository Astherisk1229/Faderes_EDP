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
public class CardPaymentDialog extends javax.swing.JDialog {

    private boolean paymentApproved = false;
    private double amountDue = 0.0;

    private String selectedCardType = "";
    private String approvalNo = "";
    private String last4Digits = "";
    private String cardholderName = "";

    /**
     * Creates new form GCashPaymentScreen
     */
    public CardPaymentDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initializeCardScreen();
    }

    public CardPaymentDialog(java.awt.Frame parent, boolean modal, double amountDue) {
        super(parent, modal);
        initComponents();
        this.amountDue = amountDue;
        initializeCardScreen();
        setAmountDue(amountDue);
    }

    private void initializeCardScreen() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setResizable(false);

        jTextField1.setEditable(false);
        jTextField1.setFocusable(false);

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Select Card Type", "Visa", "Mastercard", "JCB", "American Express", "BancNet", "Debit Card", "Other"
        }));
        jComboBox1.setSelectedIndex(0);

        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) || jTextField5.getText().length() >= 4) {
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
        confirmPaymentBtn.addActionListener(e -> confirmCardPayment());
    }

    private void confirmCancel() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the card payment?",
                "Cancel Card Payment",
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

        String formatted = String.format("₱ %,.2f", amountDue);
        amountDueTxt.setText(formatted);
        jTextField1.setText(formatted);
    }

    private void confirmCardPayment() {
        selectedCardType = jComboBox1.getSelectedItem() == null
                ? ""
                : jComboBox1.getSelectedItem().toString().trim();

        approvalNo = jTextField3.getText().trim();
        last4Digits = jTextField5.getText().trim();
        cardholderName = jTextField4.getText().trim();

        if (amountDue <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid amount due.");
            return;
        }

        if (selectedCardType.isEmpty() || selectedCardType.equalsIgnoreCase("Select Card Type")) {
            JOptionPane.showMessageDialog(this, "Please select a card type.");
            jComboBox1.requestFocusInWindow();
            return;
        }

        if (approvalNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Approval number is required.");
            jTextField3.requestFocusInWindow();
            return;
        }

        if (!approvalNo.matches("[A-Za-z0-9\\-]{6,30}")) {
            JOptionPane.showMessageDialog(this, "Approval number format is invalid.");
            jTextField3.requestFocusInWindow();
            jTextField3.selectAll();
            return;
        }

        if (!last4Digits.isEmpty() && !last4Digits.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Last 4 digits must contain exactly 4 numbers.");
            jTextField5.requestFocusInWindow();
            jTextField5.selectAll();
            return;
        }

        if (cardholderName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cardholder name is required.");
            jTextField4.requestFocusInWindow();
            return;
        }

        if (!cardholderName.matches("[A-Za-zÀ-ÿ .'-]{2,100}")) {
            JOptionPane.showMessageDialog(this, "Cardholder name contains invalid characters.");
            jTextField4.requestFocusInWindow();
            jTextField4.selectAll();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Has the card payment been approved?",
                "Confirm Card Payment",
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

    public String getCardType() {
        return selectedCardType;
    }

    public String getApprovalNo() {
        return approvalNo;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public String getCardholderName() {
        return cardholderName;
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cancelBtn = new javax.swing.JButton();
        confirmPaymentBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        amountDueTxt = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(620, 450));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(248, 248, 249));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("CARD PAYMENT");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 160, 50));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 70));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("Amount:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, -1, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setText("Card Type:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel5.setText("Cardholder Name:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, -1, 30));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 330, 380, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 210, 380, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 290, 380, -1));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, 550, 10));

        cancelBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cancelBtn.setText("CANCEL");
        jPanel1.add(cancelBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 390, 250, 40));

        confirmPaymentBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        confirmPaymentBtn.setText("CONFIRM PAYMENT");
        jPanel1.add(confirmPaymentBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 390, 250, 40));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        amountDueTxt.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        jPanel3.add(amountDueTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 110, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel6.setText("Amount Due:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 80, 150, 80));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Have you received the card approval?");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 350, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Approval No:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel8.setText("Last 4 Digits: ");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, -1, 30));

        jTextField5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 250, 380, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Card Type", "Visa", "Mastercard", "JCB", "American Express", "BancNet", "Debit Card", "Other" }));
        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 380, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 450));

        pack();
        setLocationRelativeTo(null);
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
            java.util.logging.Logger.getLogger(CardPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CardPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CardPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CardPaymentDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CardPaymentDialog dialog = new CardPaymentDialog(new javax.swing.JFrame(), true, 150.00);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amountDueTxt;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton confirmPaymentBtn;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
