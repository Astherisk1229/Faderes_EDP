/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.CategorySearch;
import DbConnection.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author Admin
 */
public class CategoriesManagementPanel extends javax.swing.JPanel {

    private static final int COL_CATEGORY_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_DESCRIPTION = 2;
    private static final int COL_ACTION = 3;

    private ImageIcon editIcon;
    private ImageIcon deleteIcon;
    private int loggedInUserId;

    /**
     * Creates new form CategoriesManagementPanel
     */
    public CategoriesManagementPanel(ProductManagementPanel productManagement, int loggedInUserId) {
        initComponents();
        this.loggedInUserId = loggedInUserId;

        makeActionColumnEditable();
        loadActionIcons();
        setupActionColumn();
        loadCategoriesToTable();
    }

    public CategoriesManagementPanel(ProductManagementPanel productManagement) {
        this(productManagement, -1);
    }

    private Frame getParentFrame() {
        Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            return (Frame) window;
        }
        return null;
    }

    private void makeActionColumnEditable() {
        DefaultTableModel oldModel = (DefaultTableModel) categoryListTbl.getModel();

        DefaultTableModel newModel = new DefaultTableModel(
                new String[]{"CATEGORY_ID", "NAME", "DESCRIPTION", "ACTIONS"},
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
                "Actions"
            });
        }

        categoryListTbl.setModel(newModel);
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
        categoryListTbl.setRowHeight(32);

        TableColumn actionColumn = categoryListTbl.getColumnModel().getColumn(COL_ACTION);
        actionColumn.setPreferredWidth(110);
        actionColumn.setMinWidth(110);
        actionColumn.setMaxWidth(110);

        actionColumn.setCellRenderer(new CategoryActionCellRenderer());
        actionColumn.setCellEditor(new CategoryActionCellEditor());
    }

    private void loadCategoriesToTable() {
        DefaultTableModel model = (DefaultTableModel) categoryListTbl.getModel();
        model.setRowCount(0);

        String sql = "SELECT category_id, name, description FROM categories ORDER BY category_id DESC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); java.sql.ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("category_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    "Actions"
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + ex.getMessage());
        }
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

    private void handleEdit(int categoryId, int modelRow) {
        DefaultTableModel model = (DefaultTableModel) categoryListTbl.getModel();

        String categoryName = getCellValue(model, modelRow, COL_NAME);
        String description = getCellValue(model, modelRow, COL_DESCRIPTION);

        Frame parentFrame = getParentFrame();

        EditCategoryDialog dialog = new EditCategoryDialog(parentFrame, true, loggedInUserId);
        dialog.loadCategoryData(categoryId, categoryName, description);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadCategoriesToTable();
            }
        });

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private void handleDelete(int categoryId, int modelRow) {
        DefaultTableModel model = (DefaultTableModel) categoryListTbl.getModel();
        String categoryName = getCellValue(model, modelRow, COL_NAME);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this category?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, categoryId);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                logAuditTrail(
                        loggedInUserId,
                        "DELETE CATEGORY",
                        "Deleted category: " + categoryName + " (Category ID: " + categoryId + ")"
                );

                model.removeRow(modelRow);
                JOptionPane.showMessageDialog(this, "Category deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Category not found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }

    private String getCellValue(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
    }

    private class CategoryActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        public CategoryActionCellRenderer() {
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

    private class CategoryActionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        private int currentRow = -1;

        public CategoryActionCellEditor() {
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

            int modelRow = categoryListTbl.convertRowIndexToModel(currentRow);
            int categoryId = Integer.parseInt(categoryListTbl.getModel().getValueAt(modelRow, COL_CATEGORY_ID).toString());

            if (e.getSource() == editButton) {
                handleEdit(categoryId, modelRow);
            } else if (e.getSource() == deleteButton) {
                handleDelete(categoryId, modelRow);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        addCategoryBtn = new javax.swing.JButton();
        refreshButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        categoryListTbl = new javax.swing.JTable();

        setMaximumSize(new java.awt.Dimension(1230, 750));
        setMinimumSize(new java.awt.Dimension(1230, 750));
        setPreferredSize(new java.awt.Dimension(1230, 750));

        jPanel1.setBackground(new java.awt.Color(246, 222, 216));
        jPanel1.setMaximumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setMinimumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setPreferredSize(new java.awt.Dimension(1570, 820));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(184, 33, 50));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setBackground(new java.awt.Color(76, 157, 176));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("CATEGORIES MANAGEMENT");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 60));

        jPanel2.setBackground(new java.awt.Color(210, 102, 90));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 0, 0), 3, true));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Search");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 120, 30));

        searchField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel2.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 570, 30));

        searchButton.setBackground(new java.awt.Color(239, 243, 254));
        searchButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
        jPanel2.add(searchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 20, -1, 30));

        addCategoryBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addCategoryBtn.setText("Add Category");
        addCategoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCategoryBtnActionPerformed(evt);
            }
        });
        jPanel2.add(addCategoryBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 20, 130, 30));

        refreshButton1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        refreshButton1.setText("Refresh");
        refreshButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(refreshButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 20, 90, 30));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, 1140, 70));

        categoryListTbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        categoryListTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CATEGORY_ID", "NAME", "DESCRIPTION", "ACTIONS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        categoryListTbl.getTableHeader().setReorderingAllowed(false);
        categoryListTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoryListTblMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(categoryListTbl);
        if (categoryListTbl.getColumnModel().getColumnCount() > 0) {
            categoryListTbl.getColumnModel().getColumn(0).setResizable(false);
            categoryListTbl.getColumnModel().getColumn(1).setResizable(false);
            categoryListTbl.getColumnModel().getColumn(2).setResizable(false);
            categoryListTbl.getColumnModel().getColumn(3).setResizable(false);
        }

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 170, 1140, 550));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1230, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 820, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_searchButtonMouseClicked

    private void searchButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_searchButtonMouseEntered

    private void searchButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_searchButtonMouseExited

    private void searchButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchButtonMousePressed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        CategorySearch.searchCategories(categoryListTbl, searchField.getText().trim());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void categoryListTblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoryListTblMouseClicked

    }//GEN-LAST:event_categoryListTblMouseClicked

    private void addCategoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryBtnActionPerformed
        Frame parentFrame = getParentFrame();

        AddCategoryDialog dialog = new AddCategoryDialog(parentFrame, true, loggedInUserId);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadCategoriesToTable();
            }
        });

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }//GEN-LAST:event_addCategoryBtnActionPerformed

    private void refreshButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButton1ActionPerformed
        searchField.setText("");
        loadCategoriesToTable();
    }//GEN-LAST:event_refreshButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCategoryBtn;
    private javax.swing.JTable categoryListTbl;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton refreshButton1;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
