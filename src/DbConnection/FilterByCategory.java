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
public class FilterByCategory {
    public static void filterTableWithCategory (JTable table, int categoryId){
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        
        
         String sql = "SELECT product_id, barcode, name, category_id, supplier_id, cost_price, "
                 + "selling_price, stock_quantity, reorder_level, product_image, created_at, updated_at "
                   + "FROM products "
                   + "WHERE category_id LIKE ? "
                   + "ORDER BY category_id ASC";
     
        
        try (Connection con = DatabaseConnection.dbConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // ✅ Set parameter BEFORE executeQuery
            pst.setInt(1, categoryId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("product_id"),
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getInt("category_id"),
                        rs.getInt("supplier_id"),
                        rs.getDouble("cost_price"),
                        rs.getDouble("selling_price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("reorder_level"),
                        rs.getString("product_image"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    };
                    dtm.addRow(row);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static void loadProductsByCategory(JTable table) {
       DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        String sql = "SELECT product_id, barcode, name, category_id, supplier_id, cost_price, selling_price, " +
                     "stock_quantity, reorder_level, product_image, created_at, updated_at " +
                     "FROM products ORDER BY category_id ASC";

        try (Connection con = DatabaseConnection.dbConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] obj = {
                    rs.getInt("product_id"),
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getInt("category_id"),
                    rs.getInt("supplier_id"),
                    rs.getDouble("cost_price"),
                    rs.getDouble("selling_price"),
                    rs.getInt("stock_quantity"),
                    rs.getInt("reorder_level"),
                    rs.getString("product_image"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(obj);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}