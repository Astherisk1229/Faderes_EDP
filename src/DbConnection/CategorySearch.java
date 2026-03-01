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
public class CategorySearch {

    public static void searchCategories(JTable table, String keyword) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        if (keyword == null || keyword.trim().isEmpty()) {
            loadCategoriesToTable(table);
            return;
        }

        String sql = "SELECT category_id, name, description, created_at, updated_at "
                + "FROM categories "
                + "WHERE name LIKE ? "
                + "OR description LIKE ?"
                + "ORDER BY category_id ASC";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            for (int i = 1; i <= 3; i++) {
                pst.setString(i, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("category_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void loadCategoriesToTable(JTable table) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        String sql = "SELECT category_id, name, description, created_at, updated_at FROM categories ORDER BY category_id ASC";

        try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object obj[] = {
                    rs.getString("category_id"),
                    rs.getString("name"),
                    rs.getString("description"),
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
