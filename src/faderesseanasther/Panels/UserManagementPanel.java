/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package faderesseanasther.Panels;

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
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import DbConnection.DatabaseConnection;
import DbConnection.UserSearch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.DefaultListModel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class UserManagementPanel extends javax.swing.JPanel {

    private boolean editMode = false;
    private boolean rowLoaded = false;
    private int selectedUserId = -1;

    private String origFirstName;
    private String origMiddleName;
    private String origLastName;
    private String origUsername;
    private String origPassword;
    private String origRole;
    private String origStatus;

    private static final int ACTION_COLUMN_INDEX = 7;

    private ImageIcon editIcon;
    private ImageIcon deleteIcon;

    private int loggedInUserId = -1;
    private String loggedInUsername = "";
    private String loggedInRole = "";

    private JPopupMenu searchSuggestionsPopup;
    private JList<String> searchSuggestionsList;
    private DefaultListModel<String> searchSuggestionsModel;
    private boolean suppressSuggestionEvent = false;

    private class ActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        public ActionCellRenderer() {
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

    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        private final JButton editButton = new JButton();
        private final JButton deleteButton = new JButton();

        private int currentRow = -1;

        public ActionCellEditor() {
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

            int modelRow = userListTbl.convertRowIndexToModel(currentRow);
            int userId = Integer.parseInt(userListTbl.getModel().getValueAt(modelRow, 0).toString());

            if (e.getSource() == editButton) {
                handleEdit(userId, modelRow);
            } else if (e.getSource() == deleteButton) {
                handleDelete(userId, modelRow);
            }
        }
    }

    private Frame getParentFrame() {
        Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            return (Frame) window;
        }
        return null;
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

    private String getCellValue(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
    }

    private void handleEdit(int userId, int modelRow) {
        DefaultTableModel model = (DefaultTableModel) userListTbl.getModel();

        String firstName = getCellValue(model, modelRow, 1);
        String middleName = getCellValue(model, modelRow, 2);
        String lastName = getCellValue(model, modelRow, 3);
        String username = getCellValue(model, modelRow, 4);
        String role = getCellValue(model, modelRow, 5);
        String status = getCellValue(model, modelRow, 6);

        Frame parentFrame = getParentFrame();

        EditUserDialog dialog = new EditUserDialog(parentFrame, true);
        dialog.loadUserData(userId, firstName, middleName, lastName, username, role, status);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshUserTableAndCounts();
            }
        });

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    /**
     * Creates new form UserManagementPanel
     */
    private void refreshUserTableAndCounts() {
        loadUsersToTable();
        loadUserCounts();
    }

    public UserManagementPanel() {
        this(-1, "", "");
    }

    public UserManagementPanel(int loggedInUserId, String loggedInUsername, String loggedInRole) {
        initComponents();

        this.loggedInUserId = loggedInUserId;
        this.loggedInUsername = loggedInUsername == null ? "" : loggedInUsername.trim();
        this.loggedInRole = loggedInRole == null ? "" : loggedInRole.trim();

        makeActionColumnEditable();
        loadActionIcons();
        setupActionColumn();

        ttlUsersField.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        ttlManagersField.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        ttlInventoryClerkField.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        ttlCashiersField.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));

        setupSearchEnterKey();
        setupSearchSuggestions();
        refreshUserTableAndCounts();
    }

    private void loadUserCounts() {
        String sql = "SELECT "
                + "COUNT(*) AS total_users, "
                + "SUM(CASE WHEN role = 'Manager' THEN 1 ELSE 0 END) AS total_managers, "
                + "SUM(CASE WHEN role = 'Inventory Clerk' THEN 1 ELSE 0 END) AS total_inventory_clerk, "
                + "SUM(CASE WHEN role = 'Cashier' THEN 1 ELSE 0 END) AS total_cashiers "
                + "FROM users "
                + "WHERE role <> ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, HIDDEN_ROLE);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    ttlUsersField.setText(formatCount(rs.getInt("total_users")));
                    ttlManagersField.setText(formatCount(rs.getInt("total_managers")));
                    ttlInventoryClerkField.setText(formatCount(rs.getInt("total_inventory_clerk")));
                    ttlCashiersField.setText(formatCount(rs.getInt("total_cashiers")));
                }
            }

        } catch (SQLException ex) {
            showDatabaseError("Failed to load user counts", ex);
        }
    }

    private String formatCount(int count) {
        return count == 0 ? "--" : String.valueOf(count);
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
        userListTbl.setRowHeight(34);

        TableColumn actionColumn = userListTbl.getColumnModel().getColumn(ACTION_COLUMN_INDEX);
        actionColumn.setPreferredWidth(120);
        actionColumn.setMinWidth(120);
        actionColumn.setMaxWidth(120);

        actionColumn.setCellRenderer(new ActionCellRenderer());
        actionColumn.setCellEditor(new ActionCellEditor());
    }

    private void makeActionColumnEditable() {
        javax.swing.table.DefaultTableModel oldModel
                = (javax.swing.table.DefaultTableModel) userListTbl.getModel();

        javax.swing.table.DefaultTableModel newModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "user_id", "first_name", "middle_name", "last_name",
                    "username", "role", "status", "Actions"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        userListTbl.setModel(newModel);
    }

    private static final String HIDDEN_ROLE = "Super Admin";

    private void loadUsersToTable() {
        DefaultTableModel model = (DefaultTableModel) userListTbl.getModel();
        model.setRowCount(0);

        String sql = "SELECT user_id, first_name, middle_name, last_name, username, role, status "
                + "FROM users "
                + "WHERE role <> ? "
                + "ORDER BY user_id DESC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, HIDDEN_ROLE);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    addUserRow(model, rs);
                }
            }

        } catch (SQLException ex) {
            showDatabaseError("Failed to load users", ex);
        }
    }

    private void addUserRow(DefaultTableModel model, ResultSet rs) throws SQLException {
        model.addRow(new Object[]{
            rs.getInt("user_id"),
            rs.getString("first_name"),
            rs.getString("middle_name"),
            rs.getString("last_name"),
            rs.getString("username"),
            rs.getString("role"),
            rs.getString("status"),
            "Actions"
        });
    }

    private void showDatabaseError(String title, SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                title + ": " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void setupSearchEnterKey() {
        searchField.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            String status = filterStatusCmb.getSelectedItem().toString();
            searchSuggestionsPopup.setVisible(false);
            UserSearch.filterUsers(userListTbl, keyword, status);
        });
    }

    private void setupSearchSuggestions() {
        searchSuggestionsModel = new DefaultListModel<>();
        searchSuggestionsList = new JList<>(searchSuggestionsModel);
        searchSuggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchSuggestionsList.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        searchSuggestionsList.setFixedCellHeight(28);

        JScrollPane scrollPane = new JScrollPane(searchSuggestionsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(searchField.getWidth(), 140));

        searchSuggestionsPopup = new JPopupMenu();
        searchSuggestionsPopup.setBorder(BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        searchSuggestionsPopup.add(scrollPane);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                showSuggestionsFromText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                showSuggestionsFromText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                showSuggestionsFromText();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!searchSuggestionsPopup.isVisible()) {
                    return;
                }

                int size = searchSuggestionsModel.getSize();
                if (size == 0) {
                    return;
                }

                int selectedIndex = searchSuggestionsList.getSelectedIndex();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if (selectedIndex < size - 1) {
                            searchSuggestionsList.setSelectedIndex(selectedIndex + 1);
                            searchSuggestionsList.ensureIndexIsVisible(selectedIndex + 1);
                        } else if (selectedIndex == -1) {
                            searchSuggestionsList.setSelectedIndex(0);
                            searchSuggestionsList.ensureIndexIsVisible(0);
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_UP:
                        if (selectedIndex > 0) {
                            searchSuggestionsList.setSelectedIndex(selectedIndex - 1);
                            searchSuggestionsList.ensureIndexIsVisible(selectedIndex - 1);
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_ENTER:
                        if (selectedIndex >= 0) {
                            applySelectedSuggestion(searchSuggestionsList.getSelectedValue());
                            e.consume();
                        }
                        break;

                    case KeyEvent.VK_ESCAPE:
                        searchSuggestionsPopup.setVisible(false);
                        break;
                }
            }
        });

        searchSuggestionsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String selectedValue = searchSuggestionsList.getSelectedValue();
                    if (selectedValue != null && !selectedValue.trim().isEmpty()) {
                        applySelectedSuggestion(selectedValue);
                    }
                }
            }
        });

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!searchSuggestionsList.isFocusOwner()) {
                        searchSuggestionsPopup.setVisible(false);
                    }
                });
            }
        });
    }

    private void showSuggestionsFromText() {
        if (suppressSuggestionEvent) {
            return;
        }

        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            searchSuggestionsPopup.setVisible(false);
            UserSearch.filterUsers(userListTbl, "", filterStatusCmb.getSelectedItem().toString());
            return;
        }

        List<String> suggestions = getUserSearchSuggestions(keyword, filterStatusCmb.getSelectedItem().toString());

        searchSuggestionsModel.clear();

        for (String suggestion : suggestions) {
            searchSuggestionsModel.addElement(suggestion);
        }

        if (searchSuggestionsModel.isEmpty()) {
            searchSuggestionsPopup.setVisible(false);
        } else {
            searchSuggestionsList.setSelectedIndex(0);
            showSuggestionPopup();
        }

        UserSearch.filterUsers(userListTbl, keyword, filterStatusCmb.getSelectedItem().toString());
    }

    private void showSuggestionPopup() {
        if (searchSuggestionsPopup == null || searchSuggestionsModel.isEmpty()) {
            return;
        }

        int popupWidth = searchField.getWidth();
        int popupHeight = Math.min(searchSuggestionsModel.getSize() * 28 + 8, 140);

        searchSuggestionsPopup.setPopupSize(popupWidth, popupHeight);

        Point p = searchField.getLocation();
        searchSuggestionsPopup.show(searchField.getParent(), p.x, p.y + searchField.getHeight());
    }

    private void applySelectedSuggestion(String selectedValue) {
        suppressSuggestionEvent = true;
        searchField.setText(selectedValue);
        suppressSuggestionEvent = false;

        searchSuggestionsPopup.setVisible(false);

        String status = filterStatusCmb.getSelectedItem().toString();
        UserSearch.filterUsers(userListTbl, selectedValue, status);
    }

    private List<String> getUserSearchSuggestions(String keyword, String status) {
        List<String> suggestions = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT suggestion FROM ("
                + " SELECT username AS suggestion FROM users WHERE role <> ? "
                + " UNION "
                + " SELECT CONCAT(first_name, ' ', last_name) AS suggestion FROM users WHERE role <> ? "
                + " UNION "
                + " SELECT CONCAT(first_name, ' ', middle_name, ' ', last_name) AS suggestion FROM users "
                + " WHERE role <> ? AND middle_name IS NOT NULL AND TRIM(middle_name) <> '' "
                + ") AS x "
                + "WHERE suggestion IS NOT NULL "
                + "AND TRIM(suggestion) <> '' "
                + "AND suggestion LIKE ? "
        );

        boolean filterByStatus = status != null && !status.equalsIgnoreCase("All");
        if (filterByStatus) {
            sql = new StringBuilder(
                    "SELECT DISTINCT suggestion FROM ("
                    + " SELECT username AS suggestion, status FROM users WHERE role <> ? "
                    + " UNION "
                    + " SELECT CONCAT(first_name, ' ', last_name) AS suggestion, status FROM users WHERE role <> ? "
                    + " UNION "
                    + " SELECT CONCAT(first_name, ' ', middle_name, ' ', last_name) AS suggestion, status FROM users "
                    + " WHERE role <> ? AND middle_name IS NOT NULL AND TRIM(middle_name) <> '' "
                    + ") AS x "
                    + "WHERE suggestion IS NOT NULL "
                    + "AND TRIM(suggestion) <> '' "
                    + "AND suggestion LIKE ? "
                    + "AND status = ? "
                    + "ORDER BY suggestion ASC "
                    + "LIMIT 8"
            );
        } else {
            sql.append("ORDER BY suggestion ASC LIMIT 8");
        }

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql.toString())) {

            pst.setString(1, HIDDEN_ROLE);
            pst.setString(2, HIDDEN_ROLE);
            pst.setString(3, HIDDEN_ROLE);
            pst.setString(4, "%" + keyword + "%");

            if (filterByStatus) {
                pst.setString(5, status);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String value = rs.getString("suggestion");
                    if (value != null && !value.trim().isEmpty() && !suggestions.contains(value.trim())) {
                        suggestions.add(value.trim());
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return suggestions;
    }

    private boolean isProtectedAdminAccount(int userId, String role, String username) {
        if (role != null && role.equalsIgnoreCase("Super Admin")) {
            return true;
        }

        if (username != null && username.equalsIgnoreCase("admin")) {
            return true;
        }

        return isSuperAdminInDatabase(userId);
    }

    private boolean isSuperAdminInDatabase(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ? AND role = ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setString(2, HIDDEN_ROLE);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            showDatabaseError("Failed to validate protected admin account", ex);
        }

        return false;
    }

    private void logAuditTrail(String action, String description) {
        if (loggedInUserId <= 0) {
            System.out.println("Audit trail skipped: invalid logged-in user id.");
            return;
        }

        String sql = "INSERT INTO transactions_log (user_id, action, description, timestamp) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, loggedInUserId);
            pst.setString(2, action == null ? "UNKNOWN" : action.trim().toUpperCase());

            String finalDescription = (description == null ? "" : description.trim())
                    + " | Performed by: " + loggedInUsername
                    + " | Role: " + loggedInRole
                    + " | User ID: " + loggedInUserId;

            pst.setString(3, finalDescription);
            pst.executeUpdate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save audit trail: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleDelete(int userId, int modelRow) {
        DefaultTableModel model = (DefaultTableModel) userListTbl.getModel();

        String username = getCellValue(model, modelRow, 4);
        String role = getCellValue(model, modelRow, 5);

        if (userId == loggedInUserId) {
            JOptionPane.showMessageDialog(
                    this,
                    "You cannot delete the account that is currently logged in.",
                    "Delete Not Allowed",
                    JOptionPane.WARNING_MESSAGE
            );

            logAuditTrail(
                    "DELETE USER BLOCKED",
                    "Attempted to delete own active account | Target User ID: " + userId
                    + " | Username: " + username
            );
            return;
        }

        if (isProtectedAdminAccount(userId, role, username)) {
            JOptionPane.showMessageDialog(
                    this,
                    "The admin account cannot be deleted.",
                    "Delete Not Allowed",
                    JOptionPane.WARNING_MESSAGE
            );

            logAuditTrail(
                    "DELETE USER BLOCKED",
                    "Attempted to delete protected admin account | Target User ID: " + userId
                    + " | Username: " + username
                    + " | Role: " + role
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                logAuditTrail(
                        "DELETE USER",
                        "Deleted user account | Target User ID: " + userId
                        + " | Username: " + username
                        + " | Role: " + role
                );

                refreshUserTableAndCounts();

                JOptionPane.showMessageDialog(
                        this,
                        "User deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "User was not found.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete user: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
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
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        filterStatusCmb = new javax.swing.JComboBox<>();
        addUserBtn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        userListTbl = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        ttlCashiersField = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        ttlUsersField = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        ttlManagersField = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        ttlInventoryClerkField = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1230, 750));
        setMinimumSize(new java.awt.Dimension(1230, 750));
        setPreferredSize(new java.awt.Dimension(1230, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(246, 222, 216));
        jPanel1.setMaximumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setMinimumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setPreferredSize(new java.awt.Dimension(1570, 820));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, -1));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 1020, -1));

        jPanel8.setBackground(new java.awt.Color(210, 102, 90));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 0, 0), 3));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        searchField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel8.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 640, 30));

        searchButton.setBackground(new java.awt.Color(242, 178, 140));
        searchButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        searchButton.setForeground(new java.awt.Color(102, 0, 0));
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
        jPanel8.add(searchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 20, -1, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Search");
        jPanel8.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 50, 30));

        filterStatusCmb.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        filterStatusCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Active", "Inactive" }));
        filterStatusCmb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterStatusCmbActionPerformed(evt);
            }
        });
        jPanel8.add(filterStatusCmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 20, -1, 30));

        addUserBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addUserBtn.setText("Add User");
        addUserBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addUserBtnMouseClicked(evt);
            }
        });
        addUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserBtnActionPerformed(evt);
            }
        });
        jPanel8.add(addUserBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 20, 110, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Status");
        jPanel8.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 20, 50, 30));

        userListTbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        userListTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "user_id", "first_name", "middle_name", "last_name", "username", "role", "status", "Actions"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userListTbl.getTableHeader().setReorderingAllowed(false);
        userListTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userListTblMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(userListTbl);
        if (userListTbl.getColumnModel().getColumnCount() > 0) {
            userListTbl.getColumnModel().getColumn(0).setResizable(false);
            userListTbl.getColumnModel().getColumn(1).setResizable(false);
            userListTbl.getColumnModel().getColumn(2).setResizable(false);
            userListTbl.getColumnModel().getColumn(3).setResizable(false);
            userListTbl.getColumnModel().getColumn(4).setResizable(false);
            userListTbl.getColumnModel().getColumn(5).setResizable(false);
            userListTbl.getColumnModel().getColumn(6).setResizable(false);
            userListTbl.getColumnModel().getColumn(7).setResizable(false);
        }

        jPanel8.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 1160, 370));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, 1180, 450));

        jPanel3.setBackground(new java.awt.Color(184, 33, 50));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setBackground(new java.awt.Color(76, 157, 176));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("USER MANAGEMENT");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, -1, 40));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 1230, 60));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel14.setText("Total Cashiers");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, 30));

        ttlCashiersField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel5.add(ttlCashiersField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 130, 40));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 90, 230, 120));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel13.setText("Total Users");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, 30));

        ttlUsersField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel6.add(ttlUsersField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 130, 40));

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 230, 120));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel15.setText("Total Managers");
        jPanel7.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, 30));

        ttlManagersField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel7.add(ttlManagersField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 130, 40));

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 90, 230, 120));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel12.setText("Total Inventory Clerk");
        jPanel9.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, 30));

        ttlInventoryClerkField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel9.add(ttlInventoryClerkField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 130, 40));

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 90, 230, 120));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-270, 0, 1500, 820));
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
        String keyword = searchField.getText().trim();
        String status = filterStatusCmb.getSelectedItem().toString();
        if (searchSuggestionsPopup != null) {
            searchSuggestionsPopup.setVisible(false);
        }
        UserSearch.filterUsers(userListTbl, keyword, status);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void addUserBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addUserBtnMouseClicked

    }//GEN-LAST:event_addUserBtnMouseClicked

    private void addUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserBtnActionPerformed
        Frame parentFrame = getParentFrame();

        AddUserDialog dialog = new AddUserDialog(parentFrame, true);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshUserTableAndCounts();
            }
        });

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }//GEN-LAST:event_addUserBtnActionPerformed

    private void userListTblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userListTblMouseClicked

    }//GEN-LAST:event_userListTblMouseClicked

    private void filterStatusCmbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterStatusCmbActionPerformed
        String keyword = searchField.getText().trim();
        String status = filterStatusCmb.getSelectedItem().toString();

        UserSearch.filterUsers(userListTbl, keyword, status);

        if (!keyword.isEmpty()) {
            showSuggestionsFromText();
        } else if (searchSuggestionsPopup != null) {
            searchSuggestionsPopup.setVisible(false);
        }
    }//GEN-LAST:event_filterStatusCmbActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addUserBtn;
    private javax.swing.JComboBox<String> filterStatusCmb;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel ttlCashiersField;
    private javax.swing.JLabel ttlInventoryClerkField;
    private javax.swing.JLabel ttlManagersField;
    private javax.swing.JLabel ttlUsersField;
    private javax.swing.JTable userListTbl;
    // End of variables declaration//GEN-END:variables
}
