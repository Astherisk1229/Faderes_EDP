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
public class ProdSearch {
    public static void searchProducts (JTable table, String keyword){
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        
        if (keyword == null || keyword.trim().isEmpty()){
            loadProductsToTable(table);
            return;
        }
        
        String sql = "SELECT product_id, barcode, name, category_id, supplier_id, cost_price, selling_price, stock_quantity, reorder_level, product_image, created_at, updated_at "
                   + "FROM products "
                   + "WHERE product_id LIKE ? "
                   + "OR barcode LIKE ? "
                   + "OR name LIKE ? "
                   + "OR category_id LIKE ? "
                   + "OR supplier_id LIKE ?"
                   + "OR cost_price LIKE ? "
                   + "OR selling_price LIKE ?"
                   + "OR stock_quantity LIKE ?"
                   + "ORDER BY user_id ASC";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                pst.setString(i, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("product_id"),
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getString("category_id"),
                    rs.getString("supplier_id"),
                    rs.getString("cost_price"),
                    rs.getString("selling_price"),
                    rs.getString("stock_quantity"),
                    rs.getTimestamp("reorder_level"),
                    rs.getTimestamp("product_image"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Product not found.");
        }
        
    }
    
    //"SELECT product_id, barcode, name, category_id, supplier_id, cost_price, selling_price, "
        //        + "stock_quantity, reorder_level, product_image, created_at, updated_at "
        //        + "FROM products ORDER BY name ASC"
public static void loadProductsToTable(JTable table) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        String sql = "SELECT product_id, barcode, middle_name, category_id, "
                + "supplier_id, cost_price, selling_price, stock_quantity, reorder_level, "
                + "product_image, created_at, updated_at FROM users ORDER BY user_id ASC";
        

        try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object obj[] = {
                    rs.getString("user_id"),
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
