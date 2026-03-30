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
public class InventorySearch {

    public static void searchInventories(JTable table, String keyword) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        if (keyword == null || keyword.trim().isEmpty()) {
            loadInventoriesToTable(table);
            return;
        }

        String sql = "SELECT transaction_id, product_id, transaction_type, quantity, reference_number, transaction_date, reason "
                + "FROM inventory_transactions "
                + "WHERE transaction_id LIKE ? "
                + "OR product_id LIKE ?"
                + "OR transaction_type LIKE ?"
                + "OR quantity LIKE ?"
                + "OR reference_number LIKE ?"
                + "OR transaction_date LIKE ?"
                + "OR reason LIKE ?"
                + "ORDER BY category_id ASC";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            for (int i = 1; i <= 7; i++) {
                pst.setString(i, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("transaction_id"),
                    rs.getInt("product_id"),
                    rs.getString("transaction_type"),
                    rs.getInt("quantity"),
                    rs.getString("reference_number"),
                    rs.getDate("transaction_date"),
                    rs.getString("reason")
                };
                dtm.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void loadInventoriesToTable(JTable table) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        String sql = "SELECT transaction_id, product_id, transaction_type, quantity, reference_number,transaction_date,reason  FROM categories ORDER BY category_id ASC";

        try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object obj[] = {
                    rs.getInt("transaction_id"),
                    rs.getInt("product_id"),
                    rs.getString("transaction_type"),
                    rs.getInt("quantity"),
                    rs.getString("reference_number"),
                    rs.getDate("transaction_date"),
                    rs.getString("reason")
                };
                dtm.addRow(obj);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Load error: " + ex.getMessage());
        }
    }
}
