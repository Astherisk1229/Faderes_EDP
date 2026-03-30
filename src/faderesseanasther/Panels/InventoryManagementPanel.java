/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.ComboItem;
import DbConnection.DatabaseConnection;
import DbConnection.FilterByCategory;
import DbConnection.InventorySearch;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Admin
 */
public class InventoryManagementPanel extends javax.swing.JPanel {

    private static final int TAB_MONITORING = 0;
    private static final int TAB_STOCK_IN = 1;
    private static final int TAB_STOCK_OUT = 2;

    private static final String ACTION_LABEL = "View Details";
    private static final int ACTION_COL_MONITORING = 9;
    private static final int ACTION_COL_STOCK_IN = 9;
    private static final int ACTION_COL_STOCK_OUT = 9;

    private int loggedInUserId = -1;
    private String loggedInUserName = "";
    private String loggedInUserRole = "";

    private JPopupMenu stockMonitoringSuggestionsPopup;
    private JList<String> stockMonitoringSuggestionsList;
    private DefaultListModel<String> stockMonitoringSuggestionsModel;

    private JPopupMenu stockInSuggestionsPopup;
    private JList<String> stockInSuggestionsList;
    private DefaultListModel<String> stockInSuggestionsModel;

    private JPopupMenu stockOutSuggestionsPopup;
    private JList<String> stockOutSuggestionsList;
    private DefaultListModel<String> stockOutSuggestionsModel;

    private boolean suppressInventorySuggestionEvent = false;

// Use classpath resource, not raw C:\ path
    private static final String VIEW_ICON_PATH = "/faderesseanasther/Icons/info.png";

    /**
     * Creates new form InventoryManagementPanel
     */
    public InventoryManagementPanel() {
        this(-1, "", "");
    }

    public InventoryManagementPanel(int loggedInUserId, String loggedInUserName, String loggedInUserRole) {
        initComponents();
        this.loggedInUserId = loggedInUserId;
        this.loggedInUserName = loggedInUserName == null ? "" : loggedInUserName;
        this.loggedInUserRole = loggedInUserRole == null ? "" : loggedInUserRole;

        initInventoryPanel();
    }

    private void initInventoryPanel() {
        setupTabChangeListener();
        setupInventoryAutoComplete();
        setupInventoryLiveFiltering();
        loadFilterComboData();
        loadDashboardPlaceholders();
        refreshTableBySelectedTab();
    }

    private void setupInventoryAutoComplete() {
        setupSuggestionForField(stockMonitoringProductNameField, "MONITORING");
        setupSuggestionForField(productStockInField, "STOCK_IN");
        setupSuggestionForField(productStockOutField, "STOCK_OUT");
    }

    private void setupSuggestionForField(javax.swing.JTextField field, String mode) {
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        list.setFixedCellHeight(28);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        popup.add(scrollPane);

        if ("MONITORING".equals(mode)) {
            stockMonitoringSuggestionsModel = model;
            stockMonitoringSuggestionsList = list;
            stockMonitoringSuggestionsPopup = popup;
        } else if ("STOCK_IN".equals(mode)) {
            stockInSuggestionsModel = model;
            stockInSuggestionsList = list;
            stockInSuggestionsPopup = popup;
        } else if ("STOCK_OUT".equals(mode)) {
            stockOutSuggestionsModel = model;
            stockOutSuggestionsList = list;
            stockOutSuggestionsPopup = popup;
        }

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleSuggestionTyping(field, mode);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleSuggestionTyping(field, mode);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleSuggestionTyping(field, mode);
            }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                JPopupMenu activePopup = getPopupByMode(mode);
                JList<String> activeList = getListByMode(mode);
                DefaultListModel<String> activeModel = getModelByMode(mode);

                if (activePopup == null || !activePopup.isVisible()) {
                    return;
                }

                int size = activeModel.getSize();
                if (size == 0) {
                    return;
                }

                int selectedIndex = activeList.getSelectedIndex();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if (selectedIndex < size - 1) {
                            activeList.setSelectedIndex(selectedIndex + 1);
                            activeList.ensureIndexIsVisible(selectedIndex + 1);
                        } else if (selectedIndex == -1) {
                            activeList.setSelectedIndex(0);
                            activeList.ensureIndexIsVisible(0);
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_UP:
                        if (selectedIndex > 0) {
                            activeList.setSelectedIndex(selectedIndex - 1);
                            activeList.ensureIndexIsVisible(selectedIndex - 1);
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_ENTER:
                        if (selectedIndex >= 0) {
                            applyInventorySuggestion(field, activeList.getSelectedValue(), mode);
                            e.consume();
                        }
                        break;

                    case KeyEvent.VK_ESCAPE:
                        activePopup.setVisible(false);
                        break;
                }
            }
        });

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String selectedValue = list.getSelectedValue();
                    if (selectedValue != null && !selectedValue.trim().isEmpty()) {
                        applyInventorySuggestion(field, selectedValue, mode);
                    }
                }
            }
        });

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!list.isFocusOwner()) {
                        popup.setVisible(false);
                    }
                });
            }
        });
    }

    private JPopupMenu getPopupByMode(String mode) {
        if ("MONITORING".equals(mode)) {
            return stockMonitoringSuggestionsPopup;
        } else if ("STOCK_IN".equals(mode)) {
            return stockInSuggestionsPopup;
        } else {
            return stockOutSuggestionsPopup;
        }
    }

    private JList<String> getListByMode(String mode) {
        if ("MONITORING".equals(mode)) {
            return stockMonitoringSuggestionsList;
        } else if ("STOCK_IN".equals(mode)) {
            return stockInSuggestionsList;
        } else {
            return stockOutSuggestionsList;
        }
    }

    private DefaultListModel<String> getModelByMode(String mode) {
        if ("MONITORING".equals(mode)) {
            return stockMonitoringSuggestionsModel;
        } else if ("STOCK_IN".equals(mode)) {
            return stockInSuggestionsModel;
        } else {
            return stockOutSuggestionsModel;
        }
    }

    private void handleSuggestionTyping(javax.swing.JTextField field, String mode) {
        if (suppressInventorySuggestionEvent) {
            return;
        }

        String keyword = field.getText().trim();
        DefaultListModel<String> model = getModelByMode(mode);
        JList<String> list = getListByMode(mode);
        JPopupMenu popup = getPopupByMode(mode);

        if (keyword.isEmpty()) {
            if (popup != null) {
                popup.setVisible(false);
            }
            refreshTableByMode(mode);
            return;
        }

        List<String> suggestions = getProductNameSuggestions(keyword);

        model.clear();
        for (String suggestion : suggestions) {
            model.addElement(suggestion);
        }

        if (model.isEmpty()) {
            popup.setVisible(false);
        } else {
            list.setSelectedIndex(0);
            showInventorySuggestionPopup(field, popup, model);
        }

        refreshTableByMode(mode);
    }

    private void showInventorySuggestionPopup(javax.swing.JTextField field, JPopupMenu popup, DefaultListModel<String> model) {
        if (popup == null || model == null || model.isEmpty()) {
            return;
        }

        int popupWidth = field.getWidth();
        int popupHeight = Math.min(model.getSize() * 28 + 8, 140);

        popup.setPopupSize(new Dimension(popupWidth, popupHeight));

        Point p = field.getLocation();
        popup.show(field.getParent(), p.x, p.y + field.getHeight());
    }

    private void applyInventorySuggestion(javax.swing.JTextField field, String selectedValue, String mode) {
        suppressInventorySuggestionEvent = true;
        field.setText(selectedValue);
        suppressInventorySuggestionEvent = false;

        JPopupMenu popup = getPopupByMode(mode);
        if (popup != null) {
            popup.setVisible(false);
        }

        refreshTableByMode(mode);
    }

    private List<String> getProductNameSuggestions(String keyword) {
        List<String> suggestions = new ArrayList<>();

        String sql = "SELECT DISTINCT name "
                + "FROM products "
                + "WHERE name LIKE ? "
                + "ORDER BY name ASC "
                + "LIMIT 8";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("name");
                    if (productName != null && !productName.trim().isEmpty()) {
                        suggestions.add(productName.trim());
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return suggestions;
    }

    private void refreshTableByMode(String mode) {
        if ("MONITORING".equals(mode)) {
            if (jTabbedPane1.getSelectedIndex() == TAB_MONITORING) {
                loadStockMonitoring();
            }
        } else if ("STOCK_IN".equals(mode)) {
            if (jTabbedPane1.getSelectedIndex() == TAB_STOCK_IN) {
                loadStockInTransactions();
            }
        } else if ("STOCK_OUT".equals(mode)) {
            if (jTabbedPane1.getSelectedIndex() == TAB_STOCK_OUT) {
                loadStockOutTransactions();
            }
        }
    }

    private void setupInventoryLiveFiltering() {
        stockMonitoringProductNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_MONITORING) {
                    loadStockMonitoring();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_MONITORING) {
                    loadStockMonitoring();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_MONITORING) {
                    loadStockMonitoring();
                }
            }
        });

        productStockInField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_STOCK_IN) {
                    loadStockInTransactions();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_STOCK_IN) {
                    loadStockInTransactions();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_STOCK_IN) {
                    loadStockInTransactions();
                }
            }
        });

        productStockOutField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_STOCK_OUT) {
                    loadStockOutTransactions();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_STOCK_OUT) {
                    loadStockOutTransactions();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!suppressInventorySuggestionEvent && jTabbedPane1.getSelectedIndex() == TAB_STOCK_OUT) {
                    loadStockOutTransactions();
                }
            }
        });
    }

    private void loadFilterComboData() {
        loadCategoryCombo();
        loadStatusCombo();
        loadStockInBatchCombo();
        loadStockOutBatchCombo();
    }

    private void loadCategoryCombo() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem("All");

        String sql = "SELECT name FROM categories ORDER BY name ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                jComboBox1.addItem(rs.getString("name"));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
        }
    }

    private void loadStatusCombo() {
        jComboBox2.removeAllItems();
        jComboBox2.addItem("All");
        jComboBox2.addItem("In Stock");
        jComboBox2.addItem("Low Stock");
        jComboBox2.addItem("Out of Stock");
    }

    private void loadStockInBatchCombo() {
        stockInBatchNumberCmb.removeAllItems();
        stockInBatchNumberCmb.addItem("All");

        String sql = "SELECT DISTINCT COALESCE(batch_number, '-') AS batch_number "
                + "FROM inventory_transactions "
                + "WHERE transaction_type = 'Stock In' "
                + "ORDER BY batch_number ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                stockInBatchNumberCmb.addItem(rs.getString("batch_number"));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading stock in batch numbers: " + ex.getMessage());
        }
    }

    private void loadStockOutBatchCombo() {
        stockOutBatchNumberCmb.removeAllItems();
        stockOutBatchNumberCmb.addItem("All");

        String sql = "SELECT DISTINCT COALESCE(batch_number, '-') AS batch_number "
                + "FROM inventory_transactions "
                + "WHERE transaction_type = 'Stock Out' "
                + "ORDER BY batch_number ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                stockOutBatchNumberCmb.addItem(rs.getString("batch_number"));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading stock out batch numbers: " + ex.getMessage());
        }
    }

    private void logAuditTrail(String action, String description) {
        if (loggedInUserId <= 0) {
            System.out.println("Audit trail skipped: invalid user id.");
            return;
        }

        String sql = "INSERT INTO transactions_log (user_id, action, description, timestamp) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            String finalAction = (action == null || action.trim().isEmpty())
                    ? "UNKNOWN"
                    : action.trim().toUpperCase().replace("_", " ");

            if (finalAction.length() > 100) {
                finalAction = finalAction.substring(0, 100);
            }

            String finalDescription = (description == null ? "" : description.trim())
                    + " | User: " + loggedInUserName
                    + " | Role: " + loggedInUserRole
                    + " | User ID: " + loggedInUserId;

            pst.setInt(1, loggedInUserId);
            pst.setString(2, finalAction);
            pst.setString(3, finalDescription);
            pst.executeUpdate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save audit trail: " + ex.getMessage());
        }
    }

    private void loadDashboardPlaceholders() {
        loadTotalProductsPlaceholder();
        loadTotalStockInPlaceholder();
        loadTotalStockOutPlaceholder();
        loadLowStockPlaceholder();
    }

    private void loadTotalProductsPlaceholder() {
        String sql = "SELECT COUNT(*) AS total_products FROM products";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int totalProducts = rs.getInt("total_products");
                totalProductsPlaceHolder.setText(formatPlaceholderValue(totalProducts));
            } else {
                totalProductsPlaceHolder.setText("--");
            }

        } catch (SQLException ex) {
            totalProductsPlaceHolder.setText("--");
            JOptionPane.showMessageDialog(this, "Error loading total products: " + ex.getMessage());
        }
    }

    private void loadTotalStockInPlaceholder() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS total_stock_in "
                + "FROM inventory_transactions "
                + "WHERE transaction_type = 'Stock In'";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int totalStockIn = rs.getInt("total_stock_in");
                totalStockInPlaceHolder.setText(formatPlaceholderValue(totalStockIn));
            } else {
                totalStockInPlaceHolder.setText("--");
            }

        } catch (SQLException ex) {
            totalStockInPlaceHolder.setText("--");
            JOptionPane.showMessageDialog(this, "Error loading total stock in: " + ex.getMessage());
        }
    }

    private void loadTotalStockOutPlaceholder() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS total_stock_out "
                + "FROM inventory_transactions "
                + "WHERE transaction_type = 'Stock Out'";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int totalStockOut = rs.getInt("total_stock_out");
                totalStockOutPlaceHolder.setText(formatPlaceholderValue(totalStockOut));
            } else {
                totalStockOutPlaceHolder.setText("--");
            }

        } catch (SQLException ex) {
            totalStockOutPlaceHolder.setText("--");
            JOptionPane.showMessageDialog(this, "Error loading total stock out: " + ex.getMessage());
        }
    }

    private void loadLowStockPlaceholder() {
        String sql = "SELECT COUNT(*) AS low_stock_count "
                + "FROM products "
                + "WHERE stock_quantity <= reorder_level";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                int lowStockCount = rs.getInt("low_stock_count");
                numLowStockItemsPlaceHolder.setText(formatPlaceholderValue(lowStockCount));
            } else {
                numLowStockItemsPlaceHolder.setText("--");
            }

        } catch (SQLException ex) {
            numLowStockItemsPlaceHolder.setText("--");
            JOptionPane.showMessageDialog(this, "Error loading low stock items: " + ex.getMessage());
        }
    }

    private String formatPlaceholderValue(int value) {
        return value <= 0 ? "--" : String.valueOf(value);
    }

    private void setupTabChangeListener() {
        jTabbedPane1.addChangeListener(e -> refreshTableBySelectedTab());
    }

    private void refreshTableBySelectedTab() {
        int selectedTab = jTabbedPane1.getSelectedIndex();

        if (selectedTab == TAB_MONITORING) {
            setupStockMonitoringTable();
            loadStockMonitoring();
        } else if (selectedTab == TAB_STOCK_IN) {
            setupStockInTable();
            loadStockInTransactions();
        } else if (selectedTab == TAB_STOCK_OUT) {
            setupStockOutTable();
            loadStockOutTransactions();
        }

        loadDashboardPlaceholders();
    }

    private void setupStockMonitoringTable() {
        stockTbl.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Product ID",
                    "Product Name",
                    "Category",
                    "Batch Number",
                    "Expiry Date",
                    "Stock In",
                    "Stock Out",
                    "Available Stock",
                    "Stock Status",
                    "Action"
                }
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ACTION_COL_MONITORING;
            }
        });

        commonTableStyle();
        setupActionColumn(ACTION_COL_MONITORING);
    }

    private void setupStockInTable() {
        stockTbl.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Transaction ID",
                    "Date",
                    "Product ID",
                    "Product Name",
                    "Batch Number",
                    "Expiry Date",
                    "Quantity In",
                    "Reference No",
                    "Remarks",
                    "Action"
                }
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ACTION_COL_STOCK_IN;
            }
        });

        commonTableStyle();
        setupActionColumn(ACTION_COL_STOCK_IN);
    }

    private void setupStockOutTable() {
        stockTbl.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Transaction ID",
                    "Date",
                    "Product ID",
                    "Product Name",
                    "Batch Number",
                    "Expiry Date",
                    "Quantity Out",
                    "Reference No",
                    "Reason",
                    "Action"
                }
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ACTION_COL_STOCK_OUT;
            }
        });

        commonTableStyle();
        setupActionColumn(ACTION_COL_STOCK_OUT);
    }

    private void commonTableStyle() {
        stockTbl.setRowHeight(30);
        stockTbl.getTableHeader().setReorderingAllowed(false);
    }

    private void setupActionColumn(int actionColumnIndex) {
        stockTbl.getColumnModel().getColumn(actionColumnIndex).setMinWidth(120);
        stockTbl.getColumnModel().getColumn(actionColumnIndex).setMaxWidth(140);
        stockTbl.getColumnModel().getColumn(actionColumnIndex).setPreferredWidth(130);

        stockTbl.getColumnModel().getColumn(actionColumnIndex)
                .setCellRenderer(new ActionButtonRenderer());

        stockTbl.getColumnModel().getColumn(actionColumnIndex)
                .setCellEditor(new ActionButtonEditor());
    }

    private ImageIcon loadActionIcon() {
        java.net.URL url = getClass().getResource(VIEW_ICON_PATH);
        if (url == null) {
            return null;
        }

        ImageIcon original = new ImageIcon(url);
        Image scaled = original.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private class ActionButtonRenderer extends JButton implements TableCellRenderer {

        private final ImageIcon icon = loadActionIcon();

        public ActionButtonRenderer() {
            setText(ACTION_LABEL);
            setFocusPainted(false);
            setBackground(new Color(41, 75, 107));
            setForeground(Color.WHITE);
            if (icon != null) {
                setIcon(icon);
            }
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(ACTION_LABEL);
            return this;
        }
    }

    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton button = new JButton();
        private final ImageIcon icon = loadActionIcon();
        private int currentRow = -1;

        public ActionButtonEditor() {
            button.setText(ACTION_LABEL);
            button.setFocusPainted(false);
            button.setBackground(new Color(41, 75, 107));
            button.setForeground(Color.WHITE);
            if (icon != null) {
                button.setIcon(icon);
            }

            button.addActionListener((ActionEvent e) -> {
                fireEditingStopped();
                openViewDetailsDialog(currentRow);
            });
        }

        @Override
        public Object getCellEditorValue() {
            return ACTION_LABEL;
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            button.setText(ACTION_LABEL);
            return button;
        }
    }

    private void openViewDetailsDialog(int row) {
        if (row < 0) {
            return;
        }

        int selectedTab = jTabbedPane1.getSelectedIndex();
        DefaultTableModel model = (DefaultTableModel) stockTbl.getModel();

        StringBuilder details = new StringBuilder("View Details\n\n");

        if (selectedTab == TAB_MONITORING) {
            details.append("Product ID: ").append(model.getValueAt(row, 0)).append("\n");
            details.append("Product Name: ").append(model.getValueAt(row, 1)).append("\n");
            details.append("Category: ").append(model.getValueAt(row, 2)).append("\n");
            details.append("Batch Number: ").append(model.getValueAt(row, 3)).append("\n");
            details.append("Expiry Date: ").append(model.getValueAt(row, 4)).append("\n");
            details.append("Stock In: ").append(model.getValueAt(row, 5)).append("\n");
            details.append("Stock Out: ").append(model.getValueAt(row, 6)).append("\n");
            details.append("Available Stock: ").append(model.getValueAt(row, 7)).append("\n");
            details.append("Stock Status: ").append(model.getValueAt(row, 8)).append("\n");
        } else if (selectedTab == TAB_STOCK_IN) {
            details.append("Transaction ID: ").append(model.getValueAt(row, 0)).append("\n");
            details.append("Date: ").append(model.getValueAt(row, 1)).append("\n");
            details.append("Product ID: ").append(model.getValueAt(row, 2)).append("\n");
            details.append("Product Name: ").append(model.getValueAt(row, 3)).append("\n");
            details.append("Batch Number: ").append(model.getValueAt(row, 4)).append("\n");
            details.append("Expiry Date: ").append(model.getValueAt(row, 5)).append("\n");
            details.append("Quantity In: ").append(model.getValueAt(row, 6)).append("\n");
            details.append("Reference No: ").append(model.getValueAt(row, 7)).append("\n");
            details.append("Remarks: ").append(model.getValueAt(row, 8)).append("\n");
        } else if (selectedTab == TAB_STOCK_OUT) {
            details.append("Transaction ID: ").append(model.getValueAt(row, 0)).append("\n");
            details.append("Date: ").append(model.getValueAt(row, 1)).append("\n");
            details.append("Product ID: ").append(model.getValueAt(row, 2)).append("\n");
            details.append("Product Name: ").append(model.getValueAt(row, 3)).append("\n");
            details.append("Batch Number: ").append(model.getValueAt(row, 4)).append("\n");
            details.append("Expiry Date: ").append(model.getValueAt(row, 5)).append("\n");
            details.append("Quantity Out: ").append(model.getValueAt(row, 6)).append("\n");
            details.append("Reference No: ").append(model.getValueAt(row, 7)).append("\n");
            details.append("Reason: ").append(model.getValueAt(row, 8)).append("\n");
        }
        logAuditTrail(
                "VIEW INVENTORY DETAILS",
                "Viewed inventory details for row " + row + " on tab index " + selectedTab + "."
        );
        JOptionPane.showMessageDialog(this, details.toString(), "View Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearStockMonitoringFields() {
        stockMonitoringProductNameField.setText("");
        batchNumberField.setText("");
        startDateStockMonitoringField.setDate(null);
        endDateStockMonitoringField.setDate(new java.util.Date());
        singleDateCheckBox.setSelected(false);

        if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(0);
        }
        if (jComboBox2.getItemCount() > 0) {
            jComboBox2.setSelectedIndex(0);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        stockInPanel = new javax.swing.JPanel();
        supplierDetails7 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        endDateStockMonitoringField = new com.toedter.calendar.JDateChooser();
        filterStockMonitoringBtn = new javax.swing.JButton();
        clearFieldsStockOutBtn4 = new javax.swing.JButton();
        batchNumberField = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        stockMonitoringProductNameField = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        startDateStockMonitoringField = new com.toedter.calendar.JDateChooser();
        singleDateCheckBox = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel46 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel49 = new javax.swing.JLabel();
        stockOutPanel = new javax.swing.JPanel();
        supplierDetails6 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        toDateStockInChooser = new com.toedter.calendar.JDateChooser();
        filterStockInTableBtn = new javax.swing.JButton();
        clearFieldsStockInFilterBtn = new javax.swing.JButton();
        productStockInField = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        fromDateStockInChooser = new com.toedter.calendar.JDateChooser();
        jTextField1 = new javax.swing.JTextField();
        addStockInBtn = new javax.swing.JButton();
        stockInBatchNumberCmb = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        supplierDetails8 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        toDateStockOutChooser = new com.toedter.calendar.JDateChooser();
        filterStockOutTableBtn = new javax.swing.JButton();
        clearFieldsStockOutBtn = new javax.swing.JButton();
        productStockOutField = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        fromDateStockOutChooser = new com.toedter.calendar.JDateChooser();
        jTextField2 = new javax.swing.JTextField();
        addStockOutBtn = new javax.swing.JButton();
        stockOutBatchNumberCmb = new javax.swing.JComboBox<>();
        label = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        stockTbl = new javax.swing.JTable();
        lowStockAlertPanel = new javax.swing.JPanel();
        numLowStockItemsPlaceHolder = new javax.swing.JLabel();
        numbLowStockItemsLbl = new javax.swing.JLabel();
        totalProductsPanel = new javax.swing.JPanel();
        totalProductsLbl = new javax.swing.JLabel();
        totalProductsPlaceHolder = new javax.swing.JLabel();
        totalStockInPanel = new javax.swing.JPanel();
        totalStockInPlaceHolder = new javax.swing.JLabel();
        totalStockInLbl = new javax.swing.JLabel();
        totalStockOutPanel = new javax.swing.JPanel();
        totalStockOutLbl = new javax.swing.JLabel();
        totalStockOutPlaceHolder = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1230, 750));
        setMinimumSize(new java.awt.Dimension(1230, 750));
        setPreferredSize(new java.awt.Dimension(1230, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBackground(new java.awt.Color(248, 232, 222));

        contentPanel.setBackground(new java.awt.Color(248, 232, 222));
        contentPanel.setMaximumSize(new java.awt.Dimension(1230, 900));
        contentPanel.setMinimumSize(new java.awt.Dimension(1230, 900));
        contentPanel.setName(""); // NOI18N
        contentPanel.setPreferredSize(new java.awt.Dimension(1230, 900));
        contentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setForeground(new java.awt.Color(184, 33, 50));
        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentMoved(evt);
            }
        });

        stockInPanel.setBackground(new java.awt.Color(255, 255, 255));
        stockInPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierDetails7.setBackground(new java.awt.Color(255, 255, 255));
        supplierDetails7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(210, 102, 90), 3, true));
        supplierDetails7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel44.setText("Category");
        supplierDetails7.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, 100, 20));

        jLabel8.setBackground(new java.awt.Color(184, 33, 50));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(184, 33, 50));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Filter & Search");
        supplierDetails7.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 130, -1));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("To Date");
        supplierDetails7.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 150, 60, 30));
        supplierDetails7.add(endDateStockMonitoringField, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 180, 230, 30));

        filterStockMonitoringBtn.setBackground(new java.awt.Color(41, 75, 107));
        filterStockMonitoringBtn.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        filterStockMonitoringBtn.setForeground(new java.awt.Color(255, 255, 255));
        filterStockMonitoringBtn.setText("Filter");
        filterStockMonitoringBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterStockMonitoringBtnMouseClicked(evt);
            }
        });
        filterStockMonitoringBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterStockMonitoringBtnActionPerformed(evt);
            }
        });
        supplierDetails7.add(filterStockMonitoringBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 180, 110, 30));

        clearFieldsStockOutBtn4.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        clearFieldsStockOutBtn4.setText("Clear");
        clearFieldsStockOutBtn4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearFieldsStockOutBtn4MouseClicked(evt);
            }
        });
        clearFieldsStockOutBtn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFieldsStockOutBtn4ActionPerformed(evt);
            }
        });
        supplierDetails7.add(clearFieldsStockOutBtn4, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 180, 110, 30));

        batchNumberField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails7.add(batchNumberField, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, 230, 30));

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel48.setText("Product Name");
        supplierDetails7.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 100, 20));

        stockMonitoringProductNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails7.add(stockMonitoringProductNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 230, 30));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel47.setText("From Date");
        supplierDetails7.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 130, -1));
        supplierDetails7.add(startDateStockMonitoringField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 230, 30));

        singleDateCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        singleDateCheckBox.setText("Use Single Date");
        supplierDetails7.add(singleDateCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails7.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 100, 230, 30));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel46.setText("Batch Number");
        supplierDetails7.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 70, 100, 20));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails7.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 100, 230, 30));

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel49.setText("Status");
        supplierDetails7.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 70, 100, 20));

        stockInPanel.add(supplierDetails7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1080, 250));

        jTabbedPane1.addTab("Stock Monitoring", stockInPanel);

        stockOutPanel.setBackground(new java.awt.Color(255, 255, 255));
        stockOutPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierDetails6.setBackground(new java.awt.Color(255, 255, 255));
        supplierDetails6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        supplierDetails6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setText("Batch Number");
        supplierDetails6.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 70, 180, -1));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setText("Product Name");
        supplierDetails6.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 120, -1));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(210, 102, 90));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Stock In Filters");
        supplierDetails6.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel40.setText("To Date");
        supplierDetails6.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 70, 120, -1));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setText("Remarks");
        supplierDetails6.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 120, -1));
        supplierDetails6.add(toDateStockInChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 100, 230, 30));

        filterStockInTableBtn.setBackground(new java.awt.Color(41, 75, 107));
        filterStockInTableBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        filterStockInTableBtn.setForeground(new java.awt.Color(255, 255, 255));
        filterStockInTableBtn.setText("Filter");
        filterStockInTableBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterStockInTableBtnMouseClicked(evt);
            }
        });
        filterStockInTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterStockInTableBtnActionPerformed(evt);
            }
        });
        supplierDetails6.add(filterStockInTableBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 170, 360, 50));

        clearFieldsStockInFilterBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clearFieldsStockInFilterBtn.setText("Clear");
        clearFieldsStockInFilterBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearFieldsStockInFilterBtnMouseClicked(evt);
            }
        });
        clearFieldsStockInFilterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFieldsStockInFilterBtnActionPerformed(evt);
            }
        });
        supplierDetails6.add(clearFieldsStockInFilterBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 170, 360, 50));

        productStockInField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails6.add(productStockInField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 230, 30));

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel43.setText("From Date");
        supplierDetails6.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, 120, -1));
        supplierDetails6.add(fromDateStockInChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 100, 230, 30));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails6.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 230, 30));

        addStockInBtn.setBackground(new java.awt.Color(41, 75, 107));
        addStockInBtn.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        addStockInBtn.setForeground(new java.awt.Color(255, 255, 255));
        addStockInBtn.setText("+ Add Stock In");
        addStockInBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addStockInBtnMouseClicked(evt);
            }
        });
        addStockInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStockInBtnActionPerformed(evt);
            }
        });
        supplierDetails6.add(addStockInBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 20, 140, 40));

        stockInBatchNumberCmb.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails6.add(stockInBatchNumberCmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, 230, 30));

        stockOutPanel.add(supplierDetails6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1070, 250));

        jTabbedPane1.addTab("Stock In", stockOutPanel);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierDetails8.setBackground(new java.awt.Color(255, 255, 255));
        supplierDetails8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        supplierDetails8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel42.setText("Batch Number");
        supplierDetails8.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 70, 180, -1));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel50.setText("Product Name");
        supplierDetails8.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 120, -1));

        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(210, 102, 90));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("Stock Out Filters");
        supplierDetails8.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel52.setText("To Date");
        supplierDetails8.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 70, 120, -1));

        jLabel53.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel53.setText("Remarks");
        supplierDetails8.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 120, -1));
        supplierDetails8.add(toDateStockOutChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 100, 230, 30));

        filterStockOutTableBtn.setBackground(new java.awt.Color(41, 75, 107));
        filterStockOutTableBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        filterStockOutTableBtn.setForeground(new java.awt.Color(255, 255, 255));
        filterStockOutTableBtn.setText("Filter");
        filterStockOutTableBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterStockOutTableBtnMouseClicked(evt);
            }
        });
        filterStockOutTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterStockOutTableBtnActionPerformed(evt);
            }
        });
        supplierDetails8.add(filterStockOutTableBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 170, 360, 50));

        clearFieldsStockOutBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clearFieldsStockOutBtn.setText("Clear");
        clearFieldsStockOutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearFieldsStockOutBtnMouseClicked(evt);
            }
        });
        clearFieldsStockOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFieldsStockOutBtnActionPerformed(evt);
            }
        });
        supplierDetails8.add(clearFieldsStockOutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 170, 360, 50));

        productStockOutField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails8.add(productStockOutField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 230, 30));

        jLabel54.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel54.setText("From Date");
        supplierDetails8.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, 120, -1));
        supplierDetails8.add(fromDateStockOutChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 100, 230, 30));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails8.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 230, 30));

        addStockOutBtn.setBackground(new java.awt.Color(41, 75, 107));
        addStockOutBtn.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        addStockOutBtn.setForeground(new java.awt.Color(255, 255, 255));
        addStockOutBtn.setText("- Add Stock Out");
        addStockOutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addStockOutBtnMouseClicked(evt);
            }
        });
        addStockOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStockOutBtnActionPerformed(evt);
            }
        });
        supplierDetails8.add(addStockOutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 20, 140, 40));

        stockOutBatchNumberCmb.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supplierDetails8.add(stockOutBatchNumberCmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, 230, 30));

        jPanel2.add(supplierDetails8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1070, 250));

        jTabbedPane1.addTab("Stock Out", jPanel2);

        contentPanel.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 1120, 320));

        label.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        contentPanel.add(label, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 160, 370, -1));

        jPanel3.setBackground(new java.awt.Color(184, 33, 50));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setBackground(new java.awt.Color(76, 157, 176));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("INVENTORY MANAGEMENT");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, -1, -1));

        contentPanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 60));

        jPanel6.setBackground(new java.awt.Color(210, 102, 90));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0), 3));
        jPanel6.setForeground(new java.awt.Color(1, 12, 74));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        stockTbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        stockTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "transaction_id", "date", "product_name", "batch_number", "transaction_type", "quantity", "reason", "reference_number", "transaction_date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stockTbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(stockTbl);

        jPanel6.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1080, 250));

        contentPanel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 590, 1120, 290));

        lowStockAlertPanel.setBackground(new java.awt.Color(255, 255, 255));
        lowStockAlertPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        lowStockAlertPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        numLowStockItemsPlaceHolder.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lowStockAlertPanel.add(numLowStockItemsPlaceHolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 110, 40));

        numbLowStockItemsLbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        numbLowStockItemsLbl.setText("Low Stock Alert");
        lowStockAlertPanel.add(numbLowStockItemsLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 110, 30));

        contentPanel.add(lowStockAlertPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 90, 250, 150));

        totalProductsPanel.setBackground(new java.awt.Color(255, 255, 255));
        totalProductsPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        totalProductsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        totalProductsLbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        totalProductsLbl.setText("Total Products");
        totalProductsPanel.add(totalProductsLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 110, 30));

        totalProductsPlaceHolder.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalProductsPanel.add(totalProductsPlaceHolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 110, 40));

        contentPanel.add(totalProductsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 250, 150));

        totalStockInPanel.setBackground(new java.awt.Color(255, 255, 255));
        totalStockInPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        totalStockInPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        totalStockInPlaceHolder.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalStockInPanel.add(totalStockInPlaceHolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 110, 40));

        totalStockInLbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        totalStockInLbl.setText("Total Stock In");
        totalStockInPanel.add(totalStockInLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 110, 30));

        contentPanel.add(totalStockInPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, 250, 150));

        totalStockOutPanel.setBackground(new java.awt.Color(255, 255, 255));
        totalStockOutPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        totalStockOutPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        totalStockOutLbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        totalStockOutLbl.setText("Total Stock Out");
        totalStockOutPanel.add(totalStockOutLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 110, 30));

        totalStockOutPlaceHolder.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalStockOutPanel.add(totalStockOutPlaceHolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 110, 40));

        contentPanel.add(totalStockOutPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 250, 150));

        jScrollPane1.setViewportView(contentPanel);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 770));
    }// </editor-fold>//GEN-END:initComponents

    private void clearFieldsStockInFilterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFieldsStockInFilterBtnActionPerformed
        productStockInField.setText("");
        jTextField1.setText("");
        fromDateStockInChooser.setDate(null);
        toDateStockInChooser.setDate(null);

        if (stockInBatchNumberCmb.getItemCount() > 0) {
            stockInBatchNumberCmb.setSelectedIndex(0);
        }

        if (stockInSuggestionsPopup != null) {
            stockInSuggestionsPopup.setVisible(false);
        }

        loadStockInTransactions();

        logAuditTrail(
                "CLEAR STOCK IN FILTERS",
                "Cleared stock in filters."
        );
    }//GEN-LAST:event_clearFieldsStockInFilterBtnActionPerformed

    private void clearFieldsStockInFilterBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearFieldsStockInFilterBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_clearFieldsStockInFilterBtnMouseClicked

    private void filterStockInTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterStockInTableBtnActionPerformed
        loadStockInTransactions();

        logAuditTrail(
                "FILTER STOCK IN",
                "Filtered stock in transactions."
        );
    }//GEN-LAST:event_filterStockInTableBtnActionPerformed

    private void filterStockInTableBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterStockInTableBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_filterStockInTableBtnMouseClicked

    private void clearFieldsStockOutBtn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFieldsStockOutBtn4ActionPerformed
        clearStockMonitoringFields();
        refreshTableBySelectedTab();
    }//GEN-LAST:event_clearFieldsStockOutBtn4ActionPerformed

    private void clearFieldsStockOutBtn4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearFieldsStockOutBtn4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_clearFieldsStockOutBtn4MouseClicked

    private void filterStockMonitoringBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterStockMonitoringBtnActionPerformed
        loadStockMonitoring();

        logAuditTrail(
                "FILTER STOCK MONITORING",
                "Filtered stock monitoring records."
        );
    }//GEN-LAST:event_filterStockMonitoringBtnActionPerformed

    private void filterStockMonitoringBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterStockMonitoringBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_filterStockMonitoringBtnMouseClicked

    private void jTabbedPane1ComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1ComponentMoved

    private void addStockInBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addStockInBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_addStockInBtnMouseClicked

    private void addStockInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStockInBtnActionPerformed
        AddStockInFrame asInf = new AddStockInFrame();
        asInf.setVisible(true);

        loadStockInBatchCombo();
        loadStockOutBatchCombo();

        logAuditTrail(
                "OPEN ADD STOCK IN",
                "Opened Add Stock In form."
        );
    }//GEN-LAST:event_addStockInBtnActionPerformed

    private void addStockOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStockOutBtnActionPerformed
        AddStockOutFrame asOutF = new AddStockOutFrame();
        asOutF.setVisible(true);

        loadStockInBatchCombo();
        loadStockOutBatchCombo();

        logAuditTrail(
                "OPEN ADD STOCK OUT",
                "Opened Add Stock Out form."
        );
    }//GEN-LAST:event_addStockOutBtnActionPerformed

    private void addStockOutBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addStockOutBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_addStockOutBtnMouseClicked

    private void clearFieldsStockOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFieldsStockOutBtnActionPerformed
        productStockOutField.setText("");
        jTextField2.setText("");
        fromDateStockOutChooser.setDate(null);
        toDateStockOutChooser.setDate(null);

        if (stockOutBatchNumberCmb.getItemCount() > 0) {
            stockOutBatchNumberCmb.setSelectedIndex(0);
        }

        if (stockOutSuggestionsPopup != null) {
            stockOutSuggestionsPopup.setVisible(false);
        }

        loadStockOutTransactions();

        logAuditTrail(
                "CLEAR STOCK OUT FILTERS",
                "Cleared stock out filters."
        );
    }//GEN-LAST:event_clearFieldsStockOutBtnActionPerformed

    private void clearFieldsStockOutBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearFieldsStockOutBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_clearFieldsStockOutBtnMouseClicked

    private void filterStockOutTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterStockOutTableBtnActionPerformed
        loadStockOutTransactions();

        logAuditTrail(
                "FILTER STOCK OUT",
                "Filtered stock out transactions."
        );
    }//GEN-LAST:event_filterStockOutTableBtnActionPerformed

    private void filterStockOutTableBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterStockOutTableBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_filterStockOutTableBtnMouseClicked

    private void filterTransactionsByCategory(int categoryId) {
        DefaultTableModel model = (DefaultTableModel) stockTbl.getModel();
        model.setRowCount(0);

        int selectedTab = jTabbedPane1.getSelectedIndex();
        String sql;

        if (selectedTab == TAB_STOCK_IN) {
            sql = "SELECT it.transaction_id, it.transaction_date, it.product_id, p.name AS product_name, "
                    + "COALESCE(it.batch_number, '-') AS batch_number, it.expiry_date, "
                    + "it.quantity, it.reference_number, it.reason "
                    + "FROM inventory_transactions it "
                    + "JOIN products p ON it.product_id = p.product_id "
                    + "WHERE p.category_id = ? AND it.transaction_type = 'Stock In' "
                    + "ORDER BY it.transaction_date DESC, it.transaction_id DESC";
        } else if (selectedTab == TAB_STOCK_OUT) {
            sql = "SELECT it.transaction_id, it.transaction_date, it.product_id, p.name AS product_name, "
                    + "COALESCE(it.batch_number, '-') AS batch_number, it.expiry_date, "
                    + "it.quantity, it.reference_number, it.reason "
                    + "FROM inventory_transactions it "
                    + "JOIN products p ON it.product_id = p.product_id "
                    + "WHERE p.category_id = ? AND it.transaction_type = 'Stock Out' "
                    + "ORDER BY it.transaction_date DESC, it.transaction_id DESC";
        } else {
            sql = "SELECT p.product_id, p.name AS product_name, "
                    + "COALESCE(c.name, '-') AS category_name, "
                    + "COALESCE(it.batch_number, '-') AS batch_number, "
                    + "it.expiry_date, "
                    + "SUM(CASE WHEN it.transaction_type = 'Stock In' THEN it.quantity ELSE 0 END) AS total_stock_in, "
                    + "SUM(CASE WHEN it.transaction_type = 'Stock Out' THEN it.quantity ELSE 0 END) AS total_stock_out "
                    + "FROM products p "
                    + "LEFT JOIN categories c ON p.category_id = c.category_id "
                    + "LEFT JOIN inventory_transactions it ON p.product_id = it.product_id "
                    + "WHERE p.category_id = ? "
                    + "GROUP BY p.product_id, p.name, c.name, it.batch_number, it.expiry_date "
                    + "ORDER BY p.name ASC, it.batch_number ASC";
        }

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, categoryId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    if (selectedTab == TAB_MONITORING) {
                        int stockIn = rs.getInt("total_stock_in");
                        int stockOut = rs.getInt("total_stock_out");
                        int availableStock = stockIn - stockOut;

                        String status;
                        if (availableStock <= 0) {
                            status = "Out of Stock";
                        } else if (availableStock <= 5) {
                            status = "Low Stock";
                        } else {
                            status = "In Stock";
                        }

                        model.addRow(new Object[]{
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("category_name"),
                            rs.getString("batch_number"),
                            rs.getDate("expiry_date"),
                            stockIn,
                            stockOut,
                            availableStock,
                            status,
                            ACTION_LABEL
                        });
                    } else {
                        model.addRow(new Object[]{
                            rs.getInt("transaction_id"),
                            rs.getTimestamp("transaction_date"),
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("batch_number"),
                            rs.getDate("expiry_date"),
                            rs.getInt("quantity"),
                            rs.getString("reference_number"),
                            rs.getString("reason"),
                            ACTION_LABEL
                        });
                    }
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error filtering transactions: " + ex.getMessage());
        }
    }

    private void loadStockInTransactions() {
        DefaultTableModel model = (DefaultTableModel) stockTbl.getModel();
        model.setRowCount(0);

        StringBuilder sql = new StringBuilder(
                "SELECT it.transaction_id, it.transaction_date, it.product_id, p.name AS product_name, "
                + "COALESCE(it.batch_number, '-') AS batch_number, "
                + "it.expiry_date, it.quantity, it.reference_number, it.reason "
                + "FROM inventory_transactions it "
                + "JOIN products p ON it.product_id = p.product_id "
                + "WHERE it.transaction_type = 'Stock In' "
        );

        List<Object> params = new ArrayList<>();

        String productName = productStockInField.getText().trim();
        String batchNumber = stockInBatchNumberCmb.getSelectedItem() != null ? stockInBatchNumberCmb.getSelectedItem().toString() : "All";
        String remarks = jTextField1.getText().trim();
        java.util.Date fromDate = fromDateStockInChooser.getDate();
        java.util.Date toDate = toDateStockInChooser.getDate();

        if (!productName.isEmpty()) {
            sql.append(" AND p.name LIKE ? ");
            params.add("%" + productName + "%");
        }

        if (batchNumber != null && !batchNumber.equalsIgnoreCase("All")) {
            sql.append(" AND COALESCE(it.batch_number, '-') = ? ");
            params.add(batchNumber);
        }

        if (!remarks.isEmpty()) {
            sql.append(" AND COALESCE(it.reason, '') LIKE ? ");
            params.add("%" + remarks + "%");
        }

        if (fromDate != null) {
            sql.append(" AND DATE(it.transaction_date) >= ? ");
            params.add(new java.sql.Date(fromDate.getTime()));
        }

        if (toDate != null) {
            sql.append(" AND DATE(it.transaction_date) <= ? ");
            params.add(new java.sql.Date(toDate.getTime()));
        }

        sql.append(" ORDER BY it.transaction_date DESC, it.transaction_id DESC");

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("transaction_id"),
                        rs.getTimestamp("transaction_date"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("batch_number"),
                        rs.getDate("expiry_date"),
                        rs.getInt("quantity"),
                        rs.getString("reference_number"),
                        rs.getString("reason"),
                        ACTION_LABEL
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading stock in transactions: " + ex.getMessage());
        }
    }

    private void loadStockOutTransactions() {
        DefaultTableModel model = (DefaultTableModel) stockTbl.getModel();
        model.setRowCount(0);

        StringBuilder sql = new StringBuilder(
                "SELECT it.transaction_id, it.transaction_date, it.product_id, p.name AS product_name, "
                + "COALESCE(it.batch_number, '-') AS batch_number, "
                + "it.expiry_date, it.quantity, it.reference_number, it.reason "
                + "FROM inventory_transactions it "
                + "JOIN products p ON it.product_id = p.product_id "
                + "WHERE it.transaction_type = 'Stock Out' "
        );

        List<Object> params = new ArrayList<>();

        String productName = productStockOutField.getText().trim();
        String batchNumber = stockOutBatchNumberCmb.getSelectedItem() != null ? stockOutBatchNumberCmb.getSelectedItem().toString() : "All";
        String reason = jTextField2.getText().trim();
        java.util.Date fromDate = fromDateStockOutChooser.getDate();
        java.util.Date toDate = toDateStockOutChooser.getDate();

        if (!productName.isEmpty()) {
            sql.append(" AND p.name LIKE ? ");
            params.add("%" + productName + "%");
        }

        if (batchNumber != null && !batchNumber.equalsIgnoreCase("All")) {
            sql.append(" AND COALESCE(it.batch_number, '-') = ? ");
            params.add(batchNumber);
        }

        if (!reason.isEmpty()) {
            sql.append(" AND COALESCE(it.reason, '') LIKE ? ");
            params.add("%" + reason + "%");
        }

        if (fromDate != null) {
            sql.append(" AND DATE(it.transaction_date) >= ? ");
            params.add(new java.sql.Date(fromDate.getTime()));
        }

        if (toDate != null) {
            sql.append(" AND DATE(it.transaction_date) <= ? ");
            params.add(new java.sql.Date(toDate.getTime()));
        }

        sql.append(" ORDER BY it.transaction_date DESC, it.transaction_id DESC");

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("transaction_id"),
                        rs.getTimestamp("transaction_date"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("batch_number"),
                        rs.getDate("expiry_date"),
                        rs.getInt("quantity"),
                        rs.getString("reference_number"),
                        rs.getString("reason"),
                        ACTION_LABEL
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading stock out transactions: " + ex.getMessage());
        }
    }

    private void loadStockMonitoring() {
        DefaultTableModel model = (DefaultTableModel) stockTbl.getModel();
        model.setRowCount(0);

        StringBuilder sql = new StringBuilder(
                "SELECT p.product_id, p.name AS product_name, "
                + "COALESCE(c.name, '-') AS category_name, "
                + "COALESCE(it.batch_number, '-') AS batch_number, "
                + "it.expiry_date, "
                + "SUM(CASE WHEN it.transaction_type = 'Stock In' THEN it.quantity ELSE 0 END) AS total_stock_in, "
                + "SUM(CASE WHEN it.transaction_type = 'Stock Out' THEN it.quantity ELSE 0 END) AS total_stock_out "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN inventory_transactions it ON p.product_id = it.product_id "
                + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        String productName = stockMonitoringProductNameField.getText().trim();
        String batchNumber = batchNumberField.getText().trim();
        String category = jComboBox1.getSelectedItem() != null ? jComboBox1.getSelectedItem().toString() : "All";
        String statusFilter = jComboBox2.getSelectedItem() != null ? jComboBox2.getSelectedItem().toString() : "All";
        java.util.Date fromDate = startDateStockMonitoringField.getDate();
        java.util.Date toDate = endDateStockMonitoringField.getDate();

        if (!productName.isEmpty()) {
            sql.append(" AND p.name LIKE ? ");
            params.add("%" + productName + "%");
        }

        if (!batchNumber.isEmpty()) {
            sql.append(" AND COALESCE(it.batch_number, '-') LIKE ? ");
            params.add("%" + batchNumber + "%");
        }

        if (category != null && !category.equalsIgnoreCase("All")) {
            sql.append(" AND c.name = ? ");
            params.add(category);
        }

        if (singleDateCheckBox.isSelected()) {
            if (fromDate != null) {
                sql.append(" AND DATE(it.transaction_date) = ? ");
                params.add(new java.sql.Date(fromDate.getTime()));
            }
        } else {
            if (fromDate != null) {
                sql.append(" AND DATE(it.transaction_date) >= ? ");
                params.add(new java.sql.Date(fromDate.getTime()));
            }

            if (toDate != null) {
                sql.append(" AND DATE(it.transaction_date) <= ? ");
                params.add(new java.sql.Date(toDate.getTime()));
            }
        }

        sql.append(" GROUP BY p.product_id, p.name, c.name, it.batch_number, it.expiry_date ");

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int stockIn = rs.getInt("total_stock_in");
                    int stockOut = rs.getInt("total_stock_out");
                    int availableStock = stockIn - stockOut;

                    String status;
                    if (availableStock <= 0) {
                        status = "Out of Stock";
                    } else if (availableStock <= 5) {
                        status = "Low Stock";
                    } else {
                        status = "In Stock";
                    }

                    if (!"All".equalsIgnoreCase(statusFilter) && !status.equalsIgnoreCase(statusFilter)) {
                        continue;
                    }

                    model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        rs.getString("batch_number"),
                        rs.getDate("expiry_date"),
                        stockIn,
                        stockOut,
                        availableStock,
                        status,
                        ACTION_LABEL
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading stock monitoring: " + ex.getMessage());
        }
    }

    private void loadStockInSummary() {
        DefaultTableModel model = (DefaultTableModel) stockTbl.getModel();
        model.setRowCount(0);

        String sql = "SELECT p.product_id, p.name AS product_name, "
                + "COALESCE(c.name, '-') AS category_name, "
                + "COALESCE(it.batch_number, '-') AS batch_number, "
                + "it.expiry_date, "
                + "SUM(CASE WHEN it.transaction_type = 'Stock In' THEN it.quantity ELSE 0 END) AS total_stock_in, "
                + "SUM(CASE WHEN it.transaction_type = 'Stock Out' THEN it.quantity ELSE 0 END) AS total_stock_out "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN inventory_transactions it ON p.product_id = it.product_id "
                + "GROUP BY p.product_id, p.name, c.name, it.batch_number, it.expiry_date "
                + "HAVING SUM(CASE WHEN it.transaction_type = 'Stock In' THEN it.quantity ELSE 0 END) > 0 "
                + "ORDER BY p.name ASC, it.batch_number ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int stockIn = rs.getInt("total_stock_in");
                int stockOut = rs.getInt("total_stock_out");
                int availableStock = stockIn - stockOut;

                String status;
                if (availableStock <= 0) {
                    status = "Out of Stock";
                } else if (availableStock <= 5) {
                    status = "Low Stock";
                } else {
                    status = "In Stock";
                }

                model.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getString("batch_number"),
                    rs.getDate("expiry_date"),
                    stockIn,
                    stockOut,
                    availableStock,
                    status,
                    ACTION_LABEL
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading stock in summary: " + ex.getMessage());
        }
    }

    private String generateBatchNumber() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new java.util.Date());
        return "BATCH-" + datePart + "-001";
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addStockInBtn;
    private javax.swing.JButton addStockOutBtn;
    private javax.swing.JTextField batchNumberField;
    private javax.swing.JButton clearFieldsStockInFilterBtn;
    private javax.swing.JButton clearFieldsStockOutBtn;
    private javax.swing.JButton clearFieldsStockOutBtn4;
    private javax.swing.JPanel contentPanel;
    private com.toedter.calendar.JDateChooser endDateStockMonitoringField;
    private javax.swing.JButton filterStockInTableBtn;
    private javax.swing.JButton filterStockMonitoringBtn;
    private javax.swing.JButton filterStockOutTableBtn;
    private com.toedter.calendar.JDateChooser fromDateStockInChooser;
    private com.toedter.calendar.JDateChooser fromDateStockOutChooser;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel label;
    private javax.swing.JPanel lowStockAlertPanel;
    private javax.swing.JLabel numLowStockItemsPlaceHolder;
    private javax.swing.JLabel numbLowStockItemsLbl;
    private javax.swing.JTextField productStockInField;
    private javax.swing.JTextField productStockOutField;
    private javax.swing.JCheckBox singleDateCheckBox;
    private com.toedter.calendar.JDateChooser startDateStockMonitoringField;
    private javax.swing.JComboBox<String> stockInBatchNumberCmb;
    private javax.swing.JPanel stockInPanel;
    private javax.swing.JTextField stockMonitoringProductNameField;
    private javax.swing.JComboBox<String> stockOutBatchNumberCmb;
    private javax.swing.JPanel stockOutPanel;
    private javax.swing.JTable stockTbl;
    private javax.swing.JPanel supplierDetails6;
    private javax.swing.JPanel supplierDetails7;
    private javax.swing.JPanel supplierDetails8;
    private com.toedter.calendar.JDateChooser toDateStockInChooser;
    private com.toedter.calendar.JDateChooser toDateStockOutChooser;
    private javax.swing.JLabel totalProductsLbl;
    private javax.swing.JPanel totalProductsPanel;
    private javax.swing.JLabel totalProductsPlaceHolder;
    private javax.swing.JLabel totalStockInLbl;
    private javax.swing.JPanel totalStockInPanel;
    private javax.swing.JLabel totalStockInPlaceHolder;
    private javax.swing.JLabel totalStockOutLbl;
    private javax.swing.JPanel totalStockOutPanel;
    private javax.swing.JLabel totalStockOutPlaceHolder;
    // End of variables declaration//GEN-END:variables
}
