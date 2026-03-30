/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

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

/**
 *
 * @author Admin
 */
public class AddStockOutFrame extends javax.swing.JDialog {

    private final JPopupMenu productPopup = new JPopupMenu();
    private final DefaultListModel<String> productSuggestionModel = new DefaultListModel<>();
    private final JList<String> productSuggestionList = new JList<>(productSuggestionModel);

    private final JPopupMenu batchPopup = new JPopupMenu();
    private final DefaultListModel<String> batchSuggestionModel = new DefaultListModel<>();
    private final JList<String> batchSuggestionList = new JList<>(batchSuggestionModel);

    private final List<String> productSuggestions = new ArrayList<>();
    private final List<String> batchSuggestions = new ArrayList<>();

    /**
     * Creates new form AddStockInFrame
     */
    public AddStockOutFrame() {
        initComponents();

        loadSuggestionData();

        setupSuggestionField(txtProductName,
                productPopup,
                productSuggestionList,
                productSuggestionModel,
                productSuggestions
        );

        setupSuggestionField(txtStockInQuantity,
                batchPopup,
                batchSuggestionList,
                batchSuggestionModel,
                batchSuggestions
        );
            
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
        batchSuggestions.clear();

        // SAMPLE DATA
        // Replace these later with database values
        productSuggestions.add("Coca Cola 1.5L");
        productSuggestions.add("Pepsi 1.5L");
        productSuggestions.add("Lucky Me Pancit Canton");
        productSuggestions.add("Nescafe Classic");
        productSuggestions.add("Bear Brand Milk");
        productSuggestions.add("Milo 300g");

        batchSuggestions.add("BATCH-20260320-001");
        batchSuggestions.add("BATCH-20260320-002");
        batchSuggestions.add("BATCH-20260319-001");
        batchSuggestions.add("JUAN-RESTOCK-001");
        batchSuggestions.add("PEDRO-DELIVERY-002");
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
        jLabel7 = new javax.swing.JLabel();
        stockInQuantityField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        dateReceivedChooser = new com.toedter.calendar.JDateChooser();
        cmbReason = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(600, 715));
        setMinimumSize(new java.awt.Dimension(600, 715));
        setPreferredSize(new java.awt.Dimension(600, 715));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(600, 715));
        jPanel1.setMinimumSize(new java.awt.Dimension(600, 715));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 715));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setText("Add Stock Out");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 180, 50));

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setText("Fill in the details to record outgoing stock.");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, -1, -1));

        jSeparator1.setForeground(new java.awt.Color(184, 33, 50));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 600, 590, 10));

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

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Date Released");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 140, 20));

        stockInQuantityField3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(stockInQuantityField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 280, 240, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Reference Number");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 250, 140, 20));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText(" (Optional)");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 520, 140, 20));
        jPanel1.add(dateReceivedChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, 240, 30));

        jPanel1.add(cmbReason, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 370, 240, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Reason");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 340, 140, 20));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Remarks");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 440, 140, 20));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, 530, 110));

        jSeparator2.setForeground(new java.awt.Color(184, 33, 50));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 590, 10));

        jButton1.setBackground(new java.awt.Color(184, 33, 50));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/diskette.png"))); // NOI18N
        jButton1.setText(" Save Stock Out");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 620, 210, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton2.setText("Cancel");
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 620, 110, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 590, 760));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(AddStockOutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddStockOutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddStockOutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddStockOutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddStockOutFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbReason;
    private com.toedter.calendar.JDateChooser dateReceivedChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField stockInQuantityField3;
    private javax.swing.JTextField txtBatchNumber;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtStockInQuantity;
    // End of variables declaration//GEN-END:variables
}
