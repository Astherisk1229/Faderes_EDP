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
public class SupplierSearch {
    public static void searchSuppliers (JTable table, String keyword){
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        
        if (keyword == null || keyword.trim().isEmpty()){
            loadSuppliersToTable(table);
            return;
        }
        
        String sql = "SELECT supplier_id, name, contact_person, contact_number, email, address, created_at, updated_at "
                   + "FROM suppliers "
                   + "WHERE name LIKE ? "
                   + "OR contact_person LIKE ? "
                   + "OR contact_number LIKE ? "
                   + "OR email LIKE ? "
                   + "OR address LIKE ? "
                   + "ORDER BY supplier_id ASC";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                pst.setString(i, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                dtm.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    
public static void loadSuppliersToTable(JTable table) {
        Connection con = DatabaseConnection.dbConnection();
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);

        String sql = "SELECT supplier_id, name, contact_person, contact_number, email, address, created_at, updated_at FROM suppliers ORDER BY supplier_id ASC";
        

        try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object obj[] = {
                    rs.getString("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
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
