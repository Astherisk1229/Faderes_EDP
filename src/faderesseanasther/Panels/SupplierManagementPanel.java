/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.CategorySearch;
import DbConnection.DatabaseConnection;
import DbConnection.ClearFields;
import DbConnection.ComboItem;
import java.awt.Color;
import DbConnection.SupplierSearch;
import java.awt.Font;

import javax.swing.JOptionPane;
import java.sql.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Admin
 */
public class SupplierManagementPanel extends javax.swing.JPanel {

    private int loggedInUserId;

    private static final int COL_SUPPLIER_ID = 0;
    private static final int COL_ACTION = 8;

    private ImageIcon editIcon;
    private ImageIcon deleteIcon;

    public SupplierManagementPanel(ProductManagementPanel productManagement) {
        initComponents();

        makeActionColumnEditable();
        loadActionIcons();
        setupActionColumn();
        loadSuppliersToTable();
    }

    private void makeActionColumnEditable() {
        DefaultTableModel oldModel = (DefaultTableModel) supplierTbl.getModel();

        DefaultTableModel newModel = new DefaultTableModel(
                new String[]{
                    "supplier_id", "name", "contact_person", "contact_number",
                    "email", "address", "created_at", "updated_at", "Actions"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_ACTION;
            }
        };

        for (int i = 0; i < oldModel.getRowCount(); i++) {
            newModel.addRow(new Object[]{
                oldModel.getValueAt(i, 0),
                oldModel.getValueAt(i, 1),
                oldModel.getValueAt(i, 2),
                oldModel.getValueAt(i, 3),
                oldModel.getValueAt(i, 4),
                oldModel.getValueAt(i, 5),
                oldModel.getValueAt(i, 6),
                oldModel.getValueAt(i, 7),
                "Actions"
            });
        }

        supplierTbl.setModel(newModel);
    }

    private void loadActionIcons() {
        editIcon = loadScaledIcon("/faderesseanasther/Icons/edit.png", 18, 18);
        deleteIcon = loadScaledIcon("/faderesseanasther/Icons/delete.png", 18, 18);
    }

    private ImageIcon loadScaledIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);

        if (url == null) {
            System.out.println("Icon not found: " + path);
            return null;
        }

        ImageIcon original = new ImageIcon(url);
        Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void setupActionColumn() {
        supplierTbl.setRowHeight(32);

        TableColumn actionColumn = supplierTbl.getColumnModel().getColumn(COL_ACTION);
        actionColumn.setPreferredWidth(110);
        actionColumn.setMinWidth(110);
        actionColumn.setMaxWidth(110);

        actionColumn.setCellRenderer(new SupplierActionCellRenderer());
        actionColumn.setCellEditor(new SupplierActionCellEditor());
    }

    private boolean isValidName(String name) {
        return name.matches("^[A-Za-z]+(?:[ -][A-Za-z]+)*$");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidAddress(String address) {
        return address.matches("^[A-Za-z0-9][A-Za-z0-9\\s,./#-]{4,149}$");
    }

    private class SupplierActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        public SupplierActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 2));
            setOpaque(true);

            styleActionButton(editButton, editIcon, "Edit");
            styleActionButton(deleteButton, deleteIcon, "Delete");

            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    private class SupplierActionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        private int currentRow = -1;

        public SupplierActionCellEditor() {
            panel.setOpaque(true);

            styleActionButton(editButton, editIcon, "Edit");
            styleActionButton(deleteButton, deleteIcon, "Delete");

            editButton.addActionListener(this);
            deleteButton.addActionListener(this);

            panel.add(editButton);
            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();

            int modelRow = supplierTbl.convertRowIndexToModel(currentRow);
            int supplierId = Integer.parseInt(supplierTbl.getModel().getValueAt(modelRow, COL_SUPPLIER_ID).toString());

            if (e.getSource() == editButton) {
                handleEdit(supplierId, modelRow);
            } else if (e.getSource() == deleteButton) {
                handleDelete(supplierId, modelRow);
            }
        }
    }

    private void styleActionButton(JButton button, ImageIcon icon, String fallbackText) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        button.setToolTipText(fallbackText);

        if (icon != null) {
            button.setIcon(icon);
            button.setText("");
        } else {
            button.setText(fallbackText);
        }
    }

    private void loadSuppliersToTable() {
        DefaultTableModel model = (DefaultTableModel) supplierTbl.getModel();
        model.setRowCount(0);

        String sql = "SELECT supplier_id, name, contact_person, contact_number, email, address, created_at, updated_at "
                + "FROM suppliers ORDER BY supplier_id DESC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    "Actions"
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load suppliers: " + ex.getMessage());
        }
    }

    private void searchSuppliers() {
        DefaultTableModel model = (DefaultTableModel) supplierTbl.getModel();
        model.setRowCount(0);

        String keyword = searchField.getText().trim();

        String sql = "SELECT supplier_id, name, contact_person, contact_number, email, address, created_at, updated_at "
                + "FROM suppliers "
                + "WHERE name LIKE ? OR contact_person LIKE ? OR contact_number LIKE ? OR email LIKE ? OR address LIKE ? "
                + "ORDER BY supplier_id DESC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            pst.setString(3, like);
            pst.setString(4, like);
            pst.setString(5, like);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_person"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        "Actions"
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }

    private void handleEdit(int supplierId, int modelRow) {
        DefaultTableModel model = (DefaultTableModel) supplierTbl.getModel();

        String supplierName = getCellValue(model, modelRow, 1);
        String contactPerson = getCellValue(model, modelRow, 2);
        String contactNumber = getCellValue(model, modelRow, 3);
        String email = getCellValue(model, modelRow, 4);
        String address = getCellValue(model, modelRow, 5);

        EditSupplierDialog editSupplierDialog = new EditSupplierDialog(null, true);
        editSupplierDialog.setSupplierTable(supplierTbl);
        editSupplierDialog.setLoggedInUserId(loggedInUserId); // use your real logged-in user variable
        editSupplierDialog.loadSupplierData(
                supplierId,
                supplierName,
                contactPerson,
                contactNumber,
                email,
                address
        );
        editSupplierDialog.setLocationRelativeTo(null);
        editSupplierDialog.setVisible(true);
    }

    private void handleDelete(int supplierId, int modelRow) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this supplier?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, supplierId);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                DefaultTableModel model = (DefaultTableModel) supplierTbl.getModel();
                model.removeRow(modelRow);

                JOptionPane.showMessageDialog(this, "Supplier deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Supplier not found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }

    private String getCellValue(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
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
        label = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        searchButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        addSupplierBtn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        supplierTbl = new javax.swing.JTable();
        refreshButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(1230, 750));
        setMinimumSize(new java.awt.Dimension(1230, 750));
        setPreferredSize(new java.awt.Dimension(1230, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 178, 140));
        jPanel1.setMaximumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setMinimumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setName(""); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1570, 820));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(label, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 80, 280, 30));

        jPanel3.setBackground(new java.awt.Color(184, 33, 50));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setBackground(new java.awt.Color(76, 157, 176));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("SUPPLIER MANAGEMENT");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 60));

        jPanel5.setBackground(new java.awt.Color(210, 102, 90));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0), 3));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(210, 102, 90));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0), 3));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        searchButton.setBackground(new java.awt.Color(41, 75, 107));
        searchButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchButton.setForeground(new java.awt.Color(255, 255, 255));
        searchButton.setText("Search");
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButtonMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchButtonMousePressed(evt);
            }
        });
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        jPanel2.add(searchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 80, -1));

        searchField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel2.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 280, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Search");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 100, -1));

        addSupplierBtn.setBackground(new java.awt.Color(239, 243, 254));
        addSupplierBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addSupplierBtn.setText("Add Supplier");
        addSupplierBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addSupplierBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addSupplierBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addSupplierBtnMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                addSupplierBtnMousePressed(evt);
            }
        });
        addSupplierBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSupplierBtnActionPerformed(evt);
            }
        });
        jPanel2.add(addSupplierBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 20, 160, 40));

        supplierTbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SUPPLIER ID", "NAME", "CONTACT PERSON", "CONTACT NUMBER", "EMAIL", "ADDRESS", "ACTIONS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        supplierTbl.getTableHeader().setReorderingAllowed(false);
        supplierTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                supplierTblMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(supplierTbl);

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 990, 530));

        refreshButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        jPanel2.add(refreshButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 20, 90, -1));

        jPanel5.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, 590));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 1090, 630));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 800));
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseClicked

    }//GEN-LAST:event_searchButtonMouseClicked

    private void searchButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseEntered

    }//GEN-LAST:event_searchButtonMouseEntered

    private void searchButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseExited

    }//GEN-LAST:event_searchButtonMouseExited

    private void searchButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMousePressed

    }//GEN-LAST:event_searchButtonMousePressed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        searchSuppliers();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void addSupplierBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSupplierBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_addSupplierBtnMouseClicked

    private void addSupplierBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSupplierBtnMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_addSupplierBtnMouseEntered

    private void addSupplierBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSupplierBtnMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_addSupplierBtnMouseExited

    private void addSupplierBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSupplierBtnMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_addSupplierBtnMousePressed

    private void addSupplierBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSupplierBtnActionPerformed
        AddSupplierDialog addSupplierDialog = new AddSupplierDialog(null, true);
        addSupplierDialog.setLocationRelativeTo(null);
        addSupplierDialog.setVisible(true);

        loadSuppliersToTable();
    }//GEN-LAST:event_addSupplierBtnActionPerformed

    private void supplierTblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierTblMouseClicked

    }//GEN-LAST:event_supplierTblMouseClicked

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        searchField.setText("");
        loadSuppliersToTable();
    }//GEN-LAST:event_refreshButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSupplierBtn;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JTable supplierTbl;
    // End of variables declaration//GEN-END:variables
}
