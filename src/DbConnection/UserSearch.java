/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class UserSearch {

    public static void searchUsers(JTable table, String keyword) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        if (keyword == null || keyword.trim().isEmpty()) {
            loadUsersToTable(table);
            return;
        }

        String sql = "SELECT user_id, first_name, middle_name, last_name, username, role, status, created_at, updated_at "
                + "FROM users "
                + "WHERE first_name LIKE ? "
                + "OR last_name LIKE ? "
                + "OR username LIKE ? "
                + "OR role LIKE ? "
                + "OR status LIKE ? "
                + "ORDER BY user_id ASC";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                pst.setString(i, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Search error: " + ex.getMessage());
        }
    }

    public static void filterUsersByStatus(JTable table, String status) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase("All")) {
            loadUsersToTable(table);
            return;
        }

        String sql = "SELECT user_id, first_name, middle_name, last_name, username, role, status, created_at, updated_at "
                + "FROM users "
                + "WHERE status = ? "
                + "ORDER BY user_id ASC";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Filter error: " + ex.getMessage());
        }
    }

    public static void filterUsers(JTable table, String keyword, String status) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("All");

        if (!hasKeyword && !hasStatus) {
            loadUsersToTable(table);
            return;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT user_id, first_name, middle_name, last_name, username, role, status, created_at, updated_at "
                + "FROM users WHERE 1=1 "
        );

        if (hasKeyword) {
            sql.append("AND (first_name LIKE ? "
                    + "OR last_name LIKE ? "
                    + "OR username LIKE ? "
                    + "OR role LIKE ? "
                    + "OR status LIKE ?) ");
        }

        if (hasStatus) {
            sql.append("AND status = ? ");
        }

        sql.append("ORDER BY user_id ASC");

        try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (hasKeyword) {
                for (int i = 0; i < 5; i++) {
                    pst.setString(paramIndex++, "%" + keyword + "%");
                }
            }

            if (hasStatus) {
                pst.setString(paramIndex++, status);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Filter error: " + ex.getMessage());
        }
    }

    public static void loadUsersToTable(JTable table) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        String sql = "SELECT user_id, first_name, middle_name, last_name, username, role, status, created_at, updated_at "
                + "FROM users ORDER BY user_id ASC";

        try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] obj = {
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(obj);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Load error: " + ex.getMessage());
        }
    }

}
