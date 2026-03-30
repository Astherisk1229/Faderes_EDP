/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package faderesseanasther.Panels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.Color;
import javax.swing.JOptionPane;
import DbConnection.DatabaseConnection;
import java.awt.Component;
import java.sql.ResultSet;
import javax.swing.DefaultListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author Admin
 */
public class DashboardManagementPanel extends javax.swing.JPanel {

    private final DefaultListModel<String> lowStockModel = new DefaultListModel<>();
    private final DefaultListModel<String> topSellingProductsModel = new DefaultListModel<>();

    /**
     * Creates new form UserManagementPanel
     */
    public DashboardManagementPanel() {
        initComponents();

        dailySalesLineChartPanel.setLayout(new java.awt.BorderLayout());
        jPanel6.setLayout(new java.awt.BorderLayout());
        topSellingProductGraph.setLayout(new java.awt.BorderLayout());
        styleDashboardLists();
        initDashboardComponents();
        styleDashboardValues();
        loadDashboardData();
        applyListRenderers();
        loadDashboardCharts();
    }

    private void styleDashboardLists() {
        Color accent = new Color(184, 33, 50);
        Color listBg = Color.WHITE;
        Color border = new Color(230, 210, 205);

        topSellingProductsList.setBackground(listBg);

        topSellingProductsList.setSelectionBackground(new Color(245, 220, 224));

        topSellingProductsList.setSelectionForeground(accent);

        topSellingProductsList.setFixedCellHeight(26);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(border, 1, true));
    }

    private void loadDashboardCharts() {
        loadDailySalesChart();
        loadMonthlySalesChart();
        loadTopSellingProductsChart();
        loadLowStockCount();
    }

    private void loadLowStockCount() {
        String sql = "SELECT COUNT(*) AS total FROM products WHERE stock_quantity <= 10";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                numLowStockItemsField.setText(String.valueOf(rs.getInt("total")));
                numLowStockItemsField.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
                numLowStockItemsField.setForeground(new Color(102, 0, 0));
            }

        } catch (SQLException ex) {
            numLowStockItemsField.setText("0");
            JOptionPane.showMessageDialog(this, "Error loading low stock count: " + ex.getMessage());
        }
    }

    private void loadDailySalesChart() {
        TimeSeries series = new TimeSeries("Daily Sales");

        String sql = "SELECT DATE(sale_date) AS sale_day, COALESCE(SUM(total_amount), 0) AS total_sales "
                + "FROM sales "
                + "WHERE sale_date >= CURDATE() - INTERVAL 6 DAY "
                + "GROUP BY DATE(sale_date) "
                + "ORDER BY sale_day ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("sale_day");
                double totalSales = rs.getDouble("total_sales");

                if (sqlDate != null) {
                    java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
                    series.addOrUpdate(new Day(utilDate), totalSales);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading daily sales chart: " + ex.getMessage());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                null,
                "Date",
                "Sales (₱)",
                dataset,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlineVisible(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, new Color(184, 33, 50));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setOpaque(true);
        chartPanel.setBackground(Color.WHITE);

        dailySalesLineChartPanel.removeAll();
        dailySalesLineChartPanel.setLayout(new BorderLayout());
        dailySalesLineChartPanel.add(chartPanel, BorderLayout.CENTER);
        dailySalesLineChartPanel.revalidate();
        dailySalesLineChartPanel.repaint();
    }

    private void loadMonthlySalesChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = "SELECT DATE_FORMAT(sale_date, '%b') AS sale_month, "
                + "MONTH(sale_date) AS month_num, "
                + "COALESCE(SUM(total_amount), 0) AS total_sales "
                + "FROM sales "
                + "WHERE YEAR(sale_date) = YEAR(CURDATE()) "
                + "GROUP BY MONTH(sale_date), DATE_FORMAT(sale_date, '%b') "
                + "ORDER BY month_num ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("sale_month");
                double totalSales = rs.getDouble("total_sales");
                dataset.addValue(totalSales, "Sales", month);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading monthly sales chart: " + ex.getMessage());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Month",
                "Sales (₱)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(184, 33, 50));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setOpaque(true);
        chartPanel.setBackground(Color.WHITE);

        jPanel6.removeAll();
        jPanel6.setLayout(new BorderLayout());
        jPanel6.add(chartPanel, BorderLayout.CENTER);
        jPanel6.revalidate();
        jPanel6.repaint();
    }

    private void loadTopSellingProductsChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        topSellingProductsModel.clear();

        String sql = "SELECT p.name, COALESCE(SUM(si.quantity), 0) AS total_qty "
                + "FROM sale_items si "
                + "JOIN products p ON si.product_id = p.product_id "
                + "GROUP BY p.product_id, p.name "
                + "ORDER BY total_qty DESC "
                + "LIMIT 10";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            int rank = 1;
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                String productName = rs.getString("name");
                int totalQty = rs.getInt("total_qty");

                dataset.addValue(totalQty, "Sold", productName);
                topSellingProductsModel.addElement(rank + ". " + productName + " - " + totalQty + " sold");
                rank++;
            }

            if (!hasData) {
                topSellingProductsModel.addElement("No sales recorded yet.");
            }

        } catch (SQLException ex) {
            topSellingProductsModel.clear();
            topSellingProductsModel.addElement("Could not load top selling products.");
            JOptionPane.showMessageDialog(this, "Error loading top selling products chart: " + ex.getMessage());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                null,
                "Products",
                "Quantity Sold",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlineVisible(false);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(184, 33, 50));
        renderer.setSeriesStroke(0, new BasicStroke(3f));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        plot.getDomainAxis().setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 8.0)
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(true);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setMouseWheelEnabled(true);

        topSellingProductGraph.removeAll();
        topSellingProductGraph.add(chartPanel, java.awt.BorderLayout.CENTER);
        topSellingProductGraph.revalidate();
        topSellingProductGraph.repaint();
    }

    private void applyListRenderers() {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

                if (!isSelected) {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(new Color(102, 0, 0));
                }

                return lbl;
            }
        };

        topSellingProductsList.setCellRenderer(renderer);
    }

    private void initDashboardComponents() {

        topSellingProductsList.setModel(topSellingProductsModel);

        topSellingProductsList.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));

        topSellingProductsList.setSelectionBackground(new java.awt.Color(184, 33, 50));

        topSellingProductsList.setSelectionForeground(java.awt.Color.WHITE);
    }

    private void styleDashboardValues() {
        java.awt.Font valueFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28);
        java.awt.Color valueColor = new java.awt.Color(102, 0, 0);

        ttlProductsField.setFont(valueFont);
        ttlProductsField.setForeground(valueColor);

        ttlCategoriesField.setFont(valueFont);
        ttlCategoriesField.setForeground(valueColor);

        ttlUsersField.setFont(valueFont);
        ttlUsersField.setForeground(valueColor);

        ttlSalesTodayField.setFont(valueFont);
        ttlSalesTodayField.setForeground(valueColor);

        topSellingProductsList.setForeground(valueColor);

        if (ttlSuppliersField != null) {
            ttlSuppliersField.setFont(valueFont);
            ttlSuppliersField.setForeground(valueColor);
        }
    }

    private void loadDashboardData() {
        loadTotalProducts();
        loadTotalCategories();
        loadTotalSuppliers();
        loadTotalUsers();
        loadTotalSalesToday();
        loadLowStockItemsList();

    }

    private void loadTotalProducts() {
        String sql = "SELECT COUNT(*) AS total FROM products";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); java.sql.ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                ttlProductsField.setText(String.valueOf(rs.getInt("total")));
            }

        } catch (SQLException ex) {
            ttlProductsField.setText("0");
            JOptionPane.showMessageDialog(this, "Error loading total products: " + ex.getMessage());
        }
    }

    private void loadTotalCategories() {
        String sql = "SELECT COUNT(*) AS total FROM categories";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); java.sql.ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                ttlCategoriesField.setText(String.valueOf(rs.getInt("total")));
            }

        } catch (SQLException ex) {
            ttlCategoriesField.setText("0");
            JOptionPane.showMessageDialog(this, "Error loading total categories: " + ex.getMessage());
        }
    }

    private void loadTotalSuppliers() {
        String sql = "SELECT COUNT(*) AS total FROM suppliers";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); java.sql.ResultSet rs = pst.executeQuery()) {

            if (rs.next() && ttlSuppliersField != null) {
                ttlSuppliersField.setText(String.valueOf(rs.getInt("total")));
            }

        } catch (SQLException ex) {
            if (ttlSuppliersField != null) {
                ttlSuppliersField.setText("0");
            }
            JOptionPane.showMessageDialog(this, "Error loading total suppliers: " + ex.getMessage());
        }
    }

    private void loadTotalUsers() {
        String sql = "SELECT COUNT(*) AS total FROM users";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); java.sql.ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                ttlUsersField.setText(String.valueOf(rs.getInt("total")));
            }

        } catch (SQLException ex) {
            ttlUsersField.setText("0");
            JOptionPane.showMessageDialog(this, "Error loading total users: " + ex.getMessage());
        }
    }

    private void loadLowStockItemsList() {
        lowStockModel.clear();

        String sql = "SELECT name, stock_quantity FROM products "
                + "WHERE stock_quantity <= 10 "
                + "ORDER BY stock_quantity ASC, name ASC";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String name = rs.getString("name");
                int stock = rs.getInt("stock_quantity");

                lowStockModel.addElement(name + " - Stock: " + stock);
            }

            if (!hasData) {
                lowStockModel.addElement("All products are well stocked.");
            }

        } catch (SQLException ex) {
            lowStockModel.clear();
            lowStockModel.addElement("Could not load low stock items.");
            JOptionPane.showMessageDialog(this, "Error loading low stock items: " + ex.getMessage());
        }
    }

    private void loadTopSellingProductsList() {
        topSellingProductsModel.clear();

        String sql = "SELECT p.name, SUM(si.quantity) AS total_qty "
                + "FROM sale_items si "
                + "JOIN products p ON si.product_id = p.product_id "
                + "GROUP BY p.product_id, p.name "
                + "ORDER BY total_qty DESC "
                + "LIMIT 10";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String name = rs.getString("name");
                int qty = rs.getInt("total_qty");

                topSellingProductsModel.addElement(name + " - Sold: " + qty);
            }

            if (!hasData) {
                topSellingProductsModel.addElement("Top products will appear here.");
            }

        } catch (SQLException ex) {
            topSellingProductsModel.clear();
            topSellingProductsModel.addElement("Could not load top selling products.");
            JOptionPane.showMessageDialog(this, "Error loading top selling products: " + ex.getMessage());
        }
    }

    private void loadTotalSalesToday() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total_sales "
                + "FROM sales WHERE DATE(sale_date) = CURDATE()";

        try (Connection con = DatabaseConnection.dbConnection(); PreparedStatement pst = con.prepareStatement(sql); java.sql.ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                double totalSales = rs.getDouble("total_sales");
                ttlSalesTodayField.setText("₱" + String.format("%,.2f", totalSales));
            }

        } catch (SQLException ex) {
            ttlSalesTodayField.setText("₱0.00");
            JOptionPane.showMessageDialog(this, "Error loading total sales today: " + ex.getMessage());
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
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        ttlStockItemsPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        numLowStockItemsField = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        ttlSalesTodayField = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        ttlUsersField = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ttlProductsPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        ttlProductsField = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ttlCategoriesPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        ttlCategoriesField = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ttlSuppliersPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ttlSuppliersField = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        dailySalesGraphPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dailySalesLineChartPanel = new javax.swing.JPanel();
        monthlySalesChartPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        topSellingProductsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        topSellingProductsList = new javax.swing.JList<>();
        topSellingProductGraph = new javax.swing.JPanel();

        setMaximumSize(new java.awt.Dimension(1230, 750));
        setMinimumSize(new java.awt.Dimension(1230, 750));
        setPreferredSize(new java.awt.Dimension(1230, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(248, 232, 222));
        jPanel1.setMaximumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setMinimumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setPreferredSize(new java.awt.Dimension(1570, 820));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, -1));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 1020, -1));

        jPanel3.setBackground(new java.awt.Color(184, 33, 50));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setBackground(new java.awt.Color(76, 157, 176));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("DASHBOARD");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 1230, 60));

        jPanel15.setBackground(new java.awt.Color(248, 232, 222));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ttlStockItemsPanel.setBackground(new java.awt.Color(255, 255, 255));
        ttlStockItemsPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        ttlStockItemsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Low Stock.png"))); // NOI18N
        ttlStockItemsPanel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 40, 40));
        ttlStockItemsPanel.add(numLowStockItemsField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 130, 40));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel13.setText("Low Stock Items");
        ttlStockItemsPanel.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, 30));

        jPanel15.add(ttlStockItemsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 180, 100));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Sales.png"))); // NOI18N
        jPanel8.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));
        jPanel8.add(ttlSalesTodayField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 130, 40));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setText("Total Sales Today");
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        jPanel15.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 10, 180, 100));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 33, 50), 3));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Users.png"))); // NOI18N
        jPanel7.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 50));
        jPanel7.add(ttlUsersField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 130, 40));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel16.setText("Total Users");
        jPanel7.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, 30));

        jPanel15.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 180, 100));

        ttlProductsPanel.setBackground(new java.awt.Color(255, 255, 255));
        ttlProductsPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        ttlProductsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Products.png"))); // NOI18N
        ttlProductsPanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 50));
        ttlProductsPanel.add(ttlProductsField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 130, 40));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel12.setText("Total Products");
        ttlProductsPanel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, 30));

        jPanel15.add(ttlProductsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 180, 100));

        ttlCategoriesPanel.setBackground(new java.awt.Color(255, 255, 255));
        ttlCategoriesPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        ttlCategoriesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Categories.png"))); // NOI18N
        ttlCategoriesPanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 50));
        ttlCategoriesPanel.add(ttlCategoriesField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 130, 40));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel14.setText("Total Categories");
        ttlCategoriesPanel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, 30));

        jPanel15.add(ttlCategoriesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 180, 100));

        ttlSuppliersPanel.setBackground(new java.awt.Color(255, 255, 255));
        ttlSuppliersPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        ttlSuppliersPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Supplier.png"))); // NOI18N
        ttlSuppliersPanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 50));
        ttlSuppliersPanel.add(ttlSuppliersField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 130, 40));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel15.setText("Total Suppliers");
        ttlSuppliersPanel.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, 30));

        jPanel15.add(ttlSuppliersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 180, 100));

        jPanel1.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, 1210, 120));

        dailySalesGraphPanel.setBackground(new java.awt.Color(255, 255, 255));
        dailySalesGraphPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        dailySalesGraphPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Daily Sales Graph");
        dailySalesGraphPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 160, -1));

        dailySalesLineChartPanel.setMaximumSize(new java.awt.Dimension(10, 10));
        dailySalesGraphPanel.add(dailySalesLineChartPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 1010, 350));

        jTabbedPane1.addTab("Daily Sales Graph", dailySalesGraphPanel);

        monthlySalesChartPanel.setBackground(new java.awt.Color(255, 255, 255));
        monthlySalesChartPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        monthlySalesChartPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Monthly Sales Chart");
        monthlySalesChartPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));
        monthlySalesChartPanel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 1110, 390));

        jTabbedPane1.addTab("Monthly Sales Chart", monthlySalesChartPanel);

        topSellingProductsPanel.setBackground(new java.awt.Color(255, 255, 255));
        topSellingProductsPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(184, 33, 50), 3, true));
        topSellingProductsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Top Selling Products");
        topSellingProductsPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        topSellingProductsList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        topSellingProductsList.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jScrollPane1.setViewportView(topSellingProductsList);

        topSellingProductsPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 210, 380));
        topSellingProductsPanel.add(topSellingProductGraph, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 900, 410));

        jTabbedPane1.addTab("Top Selling Products", topSellingProductsPanel);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 220, 1100, 510));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-270, 0, 1500, 820));
    }// </editor-fold>//GEN-END:initComponents

    /* private void clearFields() {
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        usernameField.setText("");
        passwordField.setText("");

        roleCmb.setSelectedIndex(0);
        statusCmb.setSelectedIndex(0);

        userListTbl.clearSelection();

        rowLoaded = false;
        selectedUserId = -1;
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dailySalesGraphPanel;
    private javax.swing.JPanel dailySalesLineChartPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel monthlySalesChartPanel;
    private javax.swing.JLabel numLowStockItemsField;
    private javax.swing.JPanel topSellingProductGraph;
    private javax.swing.JList<String> topSellingProductsList;
    private javax.swing.JPanel topSellingProductsPanel;
    private javax.swing.JLabel ttlCategoriesField;
    private javax.swing.JPanel ttlCategoriesPanel;
    private javax.swing.JLabel ttlProductsField;
    private javax.swing.JPanel ttlProductsPanel;
    private javax.swing.JLabel ttlSalesTodayField;
    private javax.swing.JPanel ttlStockItemsPanel;
    private javax.swing.JLabel ttlSuppliersField;
    private javax.swing.JPanel ttlSuppliersPanel;
    private javax.swing.JLabel ttlUsersField;
    // End of variables declaration//GEN-END:variables
}
