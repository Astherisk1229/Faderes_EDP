/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package faderesseanasther.Panels;

import DbConnection.DatabaseConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;



import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Admin
 */
public class ReportsManagementPanel extends javax.swing.JPanel {

    private final Color TAB_DEFAULT_COLOR = new Color(250, 250, 250); // Very light gray
    private final Color TAB_ACTIVE_COLOR = new Color(70, 130, 180); // Muted blue accent
    private final Color TAB_HOVER_COLOR = new Color(220, 230, 240); // Light blue-gray
    private final Color BACKGROUND_COLOR = new Color(248, 248, 250); // Off-white background
    private final Color CARD_BACKGROUND = new Color(255, 255, 255); // White for cards
    private final Color BORDER_COLOR = new Color(230, 230, 235); // Light gray borders
    private final Color TEXT_COLOR = new Color(50, 50, 60); // Dark gray text
    private final Color HEADER_COLOR = new Color(245, 245, 248); // Soft header

    private JTable dailyTable;
    private JTable monthlyTable;
    private JTable inventoryTable;
    private JTable lowStockTable;
    private JTable profitTable;
    private JTable auditTable;

    private JLabel dailyTotalTransactionsLabel;
    private JLabel dailyTotalSalesLabel;
    private JLabel monthlyTotalTransactionsLabel;
    private JLabel monthlyTotalSalesLabel;
    private JLabel inventoryTotalProductsLabel;
    private JLabel inventoryValueLabel;
    private JLabel lowStockTotalItemsLabel;
    private JLabel profitGrossRevenueLabel;
    private JLabel profitTotalCostLabel;
    private JLabel profitTotalProfitLabel;

    private JTextField dailyDateField;
    private JTextField monthlyMonthField;
    private JTextField monthlyYearField;
    private JTextField inventorySearchField;
    private JTextField profitStartDateField;
    private JTextField profitEndDateField;
    private JTextField auditSearchField;
    private JTextField auditFromDateField;
    private JTextField auditToDateField;

    private String activeTab = "Daily";

    public ReportsManagementPanel() {
        initComponents();
        postInit();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(240, 240, 245));
        
        // Initialize tab panels
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        
        // Initialize content panel
        jPanel4 = new javax.swing.JPanel();
        
        // Initialize labels
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        
        // Initialize other panels
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        
        // Create tab panel with better styling
        JPanel tabPanel = new javax.swing.JPanel();
        tabPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setBackground(TAB_DEFAULT_COLOR);
        tabPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 210)));
        
        // Create tab labels for better visibility
        String[] tabNames = {"Daily Sales", "Monthly Sales", "Inventory", "Low Stock", "Profit", "Audit Logs"};
        JPanel[] tabPanels = {jPanel5, jPanel7, jPanel14, jPanel15, jPanel11, jPanel6};
        
        for (int i = 0; i < tabPanels.length; i++) {
            JPanel tabBtn = tabPanels[i];
            tabBtn.setLayout(new BorderLayout());
            tabBtn.setPreferredSize(new java.awt.Dimension(140, 50));
            tabBtn.setBackground(TAB_DEFAULT_COLOR);
            tabBtn.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
            
            JLabel tabLabel = new JLabel(tabNames[i]);
            tabLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            tabLabel.setForeground(TEXT_COLOR);
            tabLabel.setHorizontalAlignment(JLabel.CENTER);
            tabLabel.setVerticalAlignment(JLabel.CENTER);
            tabBtn.add(tabLabel, BorderLayout.CENTER);
            
            tabPanel.add(tabBtn);
        }
        
        // Add tab panel to top with padding
        JPanel tabContainer = new javax.swing.JPanel(new BorderLayout());
        tabContainer.setBackground(BACKGROUND_COLOR);
        tabContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabContainer.add(tabPanel, BorderLayout.WEST);
        add(tabContainer, BorderLayout.NORTH);
        
        // Style content panel
        jPanel4.setBackground(BACKGROUND_COLOR);
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add content panel to center
        add(jPanel4, BorderLayout.CENTER);
    }

    private void postInit() {
        jPanel4.setLayout(new BorderLayout());
        setupTabListeners();
        switchTab("Daily");
    }

    private void setupTabListeners() {
        bindTabMouse(jPanel5, "Daily");
        bindTabMouse(jPanel7, "Monthly");
        bindTabMouse(jPanel14, "Inventory");
        bindTabMouse(jPanel15, "LowStock");
        bindTabMouse(jPanel11, "Profit");
        bindTabMouse(jPanel6, "Audit");
        resetAllTabStyles();
    }

    private void bindTabMouse(JPanel tab, String tabKey) {
        tab.setBackground(TAB_DEFAULT_COLOR);
        tab.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!tabKey.equals(activeTab)) {
                    tab.setBackground(TAB_HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!tabKey.equals(activeTab)) {
                    tab.setBackground(TAB_DEFAULT_COLOR);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switchTab(tabKey);
            }
        });
    }

    private void switchTab(String tabKey) {
        activeTab = tabKey;
        resetAllTabStyles();

        if ("Daily".equals(tabKey)) {
            jPanel5.setBackground(TAB_ACTIVE_COLOR);
            jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_ACTIVE_COLOR));
            loadDailySalesTab();
        } else if ("Monthly".equals(tabKey)) {
            jPanel7.setBackground(TAB_ACTIVE_COLOR);
            jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_ACTIVE_COLOR));
            loadMonthlySalesTab();
        } else if ("Inventory".equals(tabKey)) {
            jPanel14.setBackground(TAB_ACTIVE_COLOR);
            jPanel14.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_ACTIVE_COLOR));
            loadInventoryTab();
        } else if ("LowStock".equals(tabKey)) {
            jPanel15.setBackground(TAB_ACTIVE_COLOR);
            jPanel15.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_ACTIVE_COLOR));
            loadLowStockTab();
        } else if ("Profit".equals(tabKey)) {
            jPanel11.setBackground(TAB_ACTIVE_COLOR);
            jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_ACTIVE_COLOR));
            loadProfitTab();
        } else if ("Audit".equals(tabKey)) {
            jPanel6.setBackground(TAB_ACTIVE_COLOR);
            jPanel6.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_ACTIVE_COLOR));
            loadAuditLogsTab();
        }
    }

    private void resetAllTabStyles() {
        jPanel5.setBackground(TAB_DEFAULT_COLOR);
        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
        jPanel7.setBackground(TAB_DEFAULT_COLOR);
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
        jPanel14.setBackground(TAB_DEFAULT_COLOR);
        jPanel14.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
        jPanel15.setBackground(TAB_DEFAULT_COLOR);
        jPanel15.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
        jPanel11.setBackground(TAB_DEFAULT_COLOR);
        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
        jPanel6.setBackground(TAB_DEFAULT_COLOR);
        jPanel6.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_DEFAULT_COLOR));
    }

    private void loadDailySalesTab() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);
        detailPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("Daily Sales Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(0.0f);
        detailPanel.add(javax.swing.Box.createVerticalStrut(10));
        detailPanel.add(titleLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        filterPanel.setOpaque(true);
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        filterPanel.setBackground(CARD_BACKGROUND);
        filterPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 60));
        filterPanel.setAlignmentX(0.0f);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_COLOR);
        filterPanel.add(dateLabel);
        
        dailyDateField = new JTextField(15);
        dailyDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dailyDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dailyDateField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        filterPanel.add(dailyDateField);

        JButton refreshButton = createStyledButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> loadDailySalesReport());
        JButton pdfButton = createStyledButton("PDF", new Color(184, 33, 50));
        pdfButton.addActionListener(e -> exportTableToPDF(dailyTable, "Daily Sales Report"));
        JButton excelButton = createStyledButton("Excel", new Color(34, 139, 34));
        excelButton.addActionListener(e -> exportTableToExcel(dailyTable, "Daily Sales Report"));

        detailPanel.add(javax.swing.Box.createVerticalStrut(15));
        detailPanel.add(filterPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 20));
        summaryPanel.setOpaque(true);
        summaryPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        summaryPanel.setBackground(CARD_BACKGROUND);
        summaryPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 70));
        summaryPanel.setAlignmentX(0.0f);

        dailyTotalTransactionsLabel = createSummaryLabel("Total Transactions: 0", new Color(70, 130, 180));
        dailyTotalSalesLabel = createSummaryLabel("Total Sales: 0.00", new Color(34, 139, 34));
        summaryPanel.add(dailyTotalTransactionsLabel);
        summaryPanel.add(dailyTotalSalesLabel);
        detailPanel.add(javax.swing.Box.createVerticalStrut(15));
        detailPanel.add(summaryPanel);

        dailyTable = createDataTable(new String[] {"Sale ID", "Invoice No", "Date", "User ID", "Payment Method", "Total Amount"});
        JScrollPane scrollPane = new JScrollPane(dailyTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        scrollPane.setAlignmentX(0.0f);
        detailPanel.add(javax.swing.Box.createVerticalStrut(15));
        detailPanel.add(scrollPane);

        showReportPanel(detailPanel);
        loadDailySalesReport();
    }

    private void loadMonthlySalesTab() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Monthly Sales Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 60, 80));
        titleLabel.setAlignmentX(0.0f);
        detailPanel.add(javax.swing.Box.createVerticalStrut(5));
        detailPanel.add(titleLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(true);
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.setBackground(new Color(250, 250, 252));
        filterPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 50));
        filterPanel.setAlignmentX(0.0f);

        monthlyMonthField = new JTextField(3);
        monthlyMonthField.setText(new SimpleDateFormat("MM").format(new Date()));
        monthlyMonthField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        monthlyYearField = new JTextField(5);
        monthlyYearField.setText(new SimpleDateFormat("yyyy").format(new Date()));
        monthlyYearField.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        filterPanel.add(new JLabel("Month (MM):"));
        filterPanel.add(monthlyMonthField);
        filterPanel.add(new JLabel("Year (YYYY):"));
        filterPanel.add(monthlyYearField);

        JButton refreshButton = createStyledButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> loadMonthlySalesReport());
        JButton pdfButton = createStyledButton("PDF", new Color(184, 33, 50));
        pdfButton.addActionListener(e -> exportTableToPDF(monthlyTable, "Monthly Sales Report"));
        JButton excelButton = createStyledButton("Excel", new Color(34, 139, 34));
        excelButton.addActionListener(e -> exportTableToExcel(monthlyTable, "Monthly Sales Report"));

        filterPanel.add(refreshButton);
        filterPanel.add(pdfButton);
        filterPanel.add(excelButton);
        detailPanel.add(filterPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 15));
        summaryPanel.setOpaque(true);
        summaryPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        summaryPanel.setBackground(new Color(248, 248, 252));
        summaryPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 60));
        summaryPanel.setAlignmentX(0.0f);
        monthlyTotalTransactionsLabel = createSummaryLabel("Total Transactions: 0", new Color(70, 130, 180));
        monthlyTotalSalesLabel = createSummaryLabel("Total Monthly Sales: 0.00", new Color(184, 33, 50));
        summaryPanel.add(monthlyTotalTransactionsLabel);
        summaryPanel.add(monthlyTotalSalesLabel);
        detailPanel.add(summaryPanel);

        monthlyTable = createDataTable(new String[] {"Sale ID", "Invoice No", "Date", "User ID", "Payment Method", "Total Amount"});
        JScrollPane scrollPane = new JScrollPane(monthlyTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        scrollPane.setAlignmentX(0.0f);
        detailPanel.add(scrollPane);

        showReportPanel(detailPanel);
        loadMonthlySalesReport();
    }

    private void loadInventoryTab() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Inventory Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 60, 80));
        titleLabel.setAlignmentX(0.0f);
        detailPanel.add(javax.swing.Box.createVerticalStrut(5));
        detailPanel.add(titleLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        filterPanel.setOpaque(true);
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        filterPanel.setBackground(CARD_BACKGROUND);
        filterPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 60));
        filterPanel.setAlignmentX(0.0f);

        inventorySearchField = new JTextField(25);
        inventorySearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inventorySearchField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        filterPanel.add(new JLabel("Search Product:"));
        filterPanel.add(inventorySearchField);

        JButton refreshButton = createStyledButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> loadInventoryReport());
        JButton pdfButton = createStyledButton("PDF", new Color(184, 33, 50));
        pdfButton.addActionListener(e -> exportTableToPDF(inventoryTable, "Inventory Report"));
        JButton excelButton = createStyledButton("Excel", new Color(34, 139, 34));
        excelButton.addActionListener(e -> exportTableToExcel(inventoryTable, "Inventory Report"));

        filterPanel.add(refreshButton);
        filterPanel.add(pdfButton);
        filterPanel.add(excelButton);
        detailPanel.add(filterPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 20));
        summaryPanel.setOpaque(true);
        summaryPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        summaryPanel.setBackground(CARD_BACKGROUND);
        summaryPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 70));
        summaryPanel.setAlignmentX(0.0f);
        inventoryTotalProductsLabel = createSummaryLabel("Total Products: 0", new Color(70, 130, 180));
        inventoryValueLabel = createSummaryLabel("Total Inventory Value: 0.00", new Color(34, 139, 34));
        summaryPanel.add(inventoryTotalProductsLabel);
        summaryPanel.add(inventoryValueLabel);
        detailPanel.add(summaryPanel);

        inventoryTable = createDataTable(new String[] {"Product ID", "Barcode", "Product Name", "Category", "Stock Qty", "Expiry Date"});
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        scrollPane.setAlignmentX(0.0f);
        detailPanel.add(scrollPane);

        showReportPanel(detailPanel);
        loadInventoryReport();
    }

    private void loadLowStockTab() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Low Stock Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 60, 80));
        titleLabel.setAlignmentX(0.0f);
        detailPanel.add(javax.swing.Box.createVerticalStrut(5));
        detailPanel.add(titleLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(true);
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.setBackground(new Color(250, 250, 252));
        filterPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 50));
        filterPanel.setAlignmentX(0.0f);
        filterPanel.add(new JLabel("Showing products with stock ≤ 10 units"));

        JButton refreshButton = createStyledButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> loadLowStockReport());
        JButton pdfButton = createStyledButton("PDF", new Color(184, 33, 50));
        pdfButton.addActionListener(e -> exportTableToPDF(lowStockTable, "Low Stock Report"));
        JButton excelButton = createStyledButton("Excel", new Color(34, 139, 34));
        excelButton.addActionListener(e -> exportTableToExcel(lowStockTable, "Low Stock Report"));

        filterPanel.add(refreshButton);
        filterPanel.add(pdfButton);
        filterPanel.add(excelButton);
        detailPanel.add(filterPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 15));
        summaryPanel.setOpaque(true);
        summaryPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        summaryPanel.setBackground(new Color(248, 248, 252));
        summaryPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 60));
        summaryPanel.setAlignmentX(0.0f);
        lowStockTotalItemsLabel = createSummaryLabel("Total Low Stock Items: 0", new Color(184, 33, 50));
        summaryPanel.add(lowStockTotalItemsLabel);
        detailPanel.add(summaryPanel);

        lowStockTable = createDataTable(new String[] {"Product ID", "Barcode", "Product Name", "Category", "Stock Qty"});
        JScrollPane scrollPane = new JScrollPane(lowStockTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        scrollPane.setAlignmentX(0.0f);
        detailPanel.add(scrollPane);

        showReportPanel(detailPanel);
        loadLowStockReport();
    }

    private void loadProfitTab() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Profit Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 60, 80));
        titleLabel.setAlignmentX(0.0f);
        detailPanel.add(javax.swing.Box.createVerticalStrut(5));
        detailPanel.add(titleLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(true);
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.setBackground(new Color(250, 250, 252));
        filterPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 50));
        filterPanel.setAlignmentX(0.0f);

        profitStartDateField = new JTextField(12);
        profitEndDateField = new JTextField(12);
        profitStartDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        profitEndDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        profitStartDateField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        profitEndDateField.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        filterPanel.add(new JLabel("Start Date:"));
        filterPanel.add(profitStartDateField);
        filterPanel.add(new JLabel("End Date:"));
        filterPanel.add(profitEndDateField);

        JButton refreshButton = createStyledButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> loadProfitReport());
        JButton pdfButton = createStyledButton("PDF", new Color(184, 33, 50));
        pdfButton.addActionListener(e -> exportTableToPDF(profitTable, "Profit Report"));
        JButton excelButton = createStyledButton("Excel", new Color(34, 139, 34));
        excelButton.addActionListener(e -> exportTableToExcel(profitTable, "Profit Report"));

        filterPanel.add(refreshButton);
        filterPanel.add(pdfButton);
        filterPanel.add(excelButton);
        detailPanel.add(filterPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 15));
        summaryPanel.setOpaque(true);
        summaryPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        summaryPanel.setBackground(new Color(248, 248, 252));
        summaryPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 60));
        summaryPanel.setAlignmentX(0.0f);
        profitGrossRevenueLabel = createSummaryLabel("Gross Revenue: 0.00", new Color(34, 139, 34));
        profitTotalCostLabel = createSummaryLabel("Total Cost: 0.00", new Color(184, 33, 50));
        profitTotalProfitLabel = createSummaryLabel("Total Profit: 0.00", new Color(0, 100, 0));
        summaryPanel.add(profitGrossRevenueLabel);
        summaryPanel.add(profitTotalCostLabel);
        summaryPanel.add(profitTotalProfitLabel);
        detailPanel.add(summaryPanel);

        profitTable = createDataTable(new String[] {"Sale ID", "Invoice No", "Sale Date", "Product ID", "Barcode", "Product Name", "Quantity Sold", "Unit Cost", "Unit Price", "Discount", "Net Sales", "Total Cost", "Profit"});
        JScrollPane scrollPane = new JScrollPane(profitTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        scrollPane.setAlignmentX(0.0f);
        detailPanel.add(scrollPane);

        showReportPanel(detailPanel);
        loadProfitReport();
    }

    private void loadAuditLogsTab() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Audit Logs Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 60, 80));
        titleLabel.setAlignmentX(0.0f);
        detailPanel.add(javax.swing.Box.createVerticalStrut(5));
        detailPanel.add(titleLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterPanel.setOpaque(true);
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.setBackground(new Color(250, 250, 252));
        filterPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 50));
        filterPanel.setAlignmentX(0.0f);

        auditSearchField = new JTextField(15);
        auditFromDateField = new JTextField(12);
        auditToDateField = new JTextField(12);
        auditFromDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        auditToDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        auditSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        auditFromDateField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        auditToDateField.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(auditSearchField);
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(auditFromDateField);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(auditToDateField);

        JButton refreshButton = createStyledButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> loadAuditLogsReport());
        JButton pdfButton = createStyledButton("PDF", new Color(184, 33, 50));
        pdfButton.addActionListener(e -> exportTableToPDF(auditTable, "Audit Logs Report"));
        JButton excelButton = createStyledButton("Excel", new Color(34, 139, 34));
        excelButton.addActionListener(e -> exportTableToExcel(auditTable, "Audit Logs Report"));

        filterPanel.add(refreshButton);
        filterPanel.add(pdfButton);
        filterPanel.add(excelButton);
        detailPanel.add(filterPanel);

        auditTable = createDataTable(new String[] {"Log ID", "User ID", "Action", "Description", "Timestamp"});
        JScrollPane scrollPane = new JScrollPane(auditTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        scrollPane.setAlignmentX(0.0f);
        detailPanel.add(scrollPane);

        showReportPanel(detailPanel);
        loadAuditLogsReport();
    }

    private void showReportPanel(JPanel detailPanel) {
        jPanel4.removeAll();
        jPanel4.add(detailPanel, BorderLayout.CENTER);
        jPanel4.revalidate();
        jPanel4.repaint();
    }

    private JTable createDataTable(String[] headers) {
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_COLOR);
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 40));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(new Color(200, 220, 240));
                } else {
                    setBackground(row % 2 == 0 ? CARD_BACKGROUND : new Color(250, 250, 252));
                }
                setForeground(TEXT_COLOR);
                setHorizontalAlignment(column == 5 ? RIGHT : CENTER); // Right-align Total Amount
                return this;
            }
        });

        return table;
    }

    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JLabel createSummaryLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(color);
        return label;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(color.darker(), 1),
            javax.swing.BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorderPainted(true);
        button.setPreferredSize(new java.awt.Dimension(80, 35));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void loadDailySalesReport() {
        if (dailyTable == null) { return; }
        DefaultTableModel model = (DefaultTableModel) dailyTable.getModel();
        model.setRowCount(0);

        String date = dailyDateField.getText().trim();
        try (Connection conn = DatabaseConnection.dbConnection()) {
            String query = "SELECT sale_id, invoice_no, sale_date, user_id, payment_method, total_amount FROM sales WHERE CAST(sale_date AS DATE) = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    int totalTransactions = 0;
                    double totalSales = 0;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("sale_id"),
                            rs.getString("invoice_no"),
                            rs.getString("sale_date"),
                            rs.getInt("user_id"),
                            rs.getString("payment_method"),
                            String.format("%.2f", rs.getDouble("total_amount"))
                        });
                        totalTransactions++;
                        totalSales += rs.getDouble("total_amount");
                    }
                    if (totalTransactions == 0) {
                        model.addRow(new Object[] {"No data found", "", "", "", "", ""});
                    }
                    dailyTotalTransactionsLabel.setText("Total Transactions: " + totalTransactions);
                    dailyTotalSalesLabel.setText("Total Sales: " + String.format("%.2f", totalSales));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Daily Sales report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMonthlySalesReport() {
        if (monthlyTable == null) { return; }
        DefaultTableModel model = (DefaultTableModel) monthlyTable.getModel();
        model.setRowCount(0);

        String month = monthlyMonthField.getText().trim();
        String year = monthlyYearField.getText().trim();
        try (Connection conn = DatabaseConnection.dbConnection()) {
            String query = "SELECT sale_id, invoice_no, sale_date, user_id, payment_method, total_amount FROM sales WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, Integer.parseInt(month));
                ps.setInt(2, Integer.parseInt(year));
                try (ResultSet rs = ps.executeQuery()) {
                    int totalTransactions = 0;
                    double totalSales = 0;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("sale_id"),
                            rs.getString("invoice_no"),
                            rs.getString("sale_date"),
                            rs.getInt("user_id"),
                            rs.getString("payment_method"),
                            String.format("%.2f", rs.getDouble("total_amount"))
                        });
                        totalTransactions++;
                        totalSales += rs.getDouble("total_amount");
                    }
                    if (totalTransactions == 0) {
                        model.addRow(new Object[] {"No data found", "", "", "", "", ""});
                    }
                    monthlyTotalTransactionsLabel.setText("Total Transactions: " + totalTransactions);
                    monthlyTotalSalesLabel.setText("Total Monthly Sales: " + String.format("%.2f", totalSales));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Monthly Sales report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInventoryReport() {
        if (inventoryTable == null) { return; }
        DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
        model.setRowCount(0);

        String search = inventorySearchField.getText().trim();
        try (Connection conn = DatabaseConnection.dbConnection()) {
            String query = "SELECT p.product_id, p.barcode, p.name, c.name as category, p.stock_quantity, p.expiration_date, p.cost_price, p.selling_price FROM products p LEFT JOIN categories c ON p.category_id = c.category_id WHERE p.name LIKE ? OR c.name LIKE ? ORDER BY p.product_id";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                String wildcard = "%" + search + "%";
                ps.setString(1, wildcard);
                ps.setString(2, wildcard);
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    double totalValue = 0;
                    while (rs.next()) {
                        double stockValue = rs.getInt("stock_quantity") * rs.getDouble("cost_price");
                        model.addRow(new Object[]{
                            rs.getInt("product_id"),
                            rs.getString("barcode"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("stock_quantity"),
                            rs.getString("expiration_date")
                        });
                        count++;
                        totalValue += stockValue;
                    }
                    if (count == 0) {
                        model.addRow(new Object[] {"No data found", "", "", "", "", ""});
                    }
                    inventoryTotalProductsLabel.setText("Total Products: " + count);
                    inventoryValueLabel.setText("Total Inventory Value: " + String.format("%.2f", totalValue));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Inventory report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLowStockReport() {
        if (lowStockTable == null) { return; }
        DefaultTableModel model = (DefaultTableModel) lowStockTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.dbConnection()) {
            String query = "SELECT p.product_id, p.barcode, p.name, c.name as category, p.stock_quantity, p.reorder_level FROM products p LEFT JOIN categories c ON p.category_id = c.category_id WHERE p.stock_quantity <= p.reorder_level ORDER BY p.stock_quantity ASC";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("product_id"),
                            rs.getString("barcode"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("stock_quantity")
                        });
                        count++;
                    }
                    if (count == 0) {
                        model.addRow(new Object[] {"No data found", "", "", "", ""});
                    }
                    lowStockTotalItemsLabel.setText("Total Low Stock Items: " + count);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Low Stock report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProfitReport() {
        if (profitTable == null) { return; }
        DefaultTableModel model = (DefaultTableModel) profitTable.getModel();
        model.setRowCount(0);

        String start = profitStartDateField.getText().trim();
        String end = profitEndDateField.getText().trim();
        try (Connection conn = DatabaseConnection.dbConnection()) {
            String query = "SELECT s.sale_id, s.invoice_no, s.sale_date, si.product_id, p.barcode, p.name, si.quantity, p.cost_price AS unit_cost, si.unit_price, si.discount, si.total_price AS net_sales, (si.quantity * p.cost_price) AS total_cost, (si.total_price - (si.quantity * p.cost_price)) AS profit FROM sales s JOIN sale_items si ON s.sale_id = si.sale_id JOIN products p ON si.product_id = p.product_id WHERE CAST(s.sale_date AS DATE) BETWEEN ? AND ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, start);
                ps.setString(2, end);
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    double gross = 0; double cost = 0; double profit = 0;
                    while (rs.next()) {
                        double netSales = rs.getDouble("net_sales");
                        double totalCost = rs.getDouble("total_cost");
                        double lineProfit = rs.getDouble("profit");
                        model.addRow(new Object[]{
                            rs.getInt("sale_id"),
                            rs.getString("invoice_no"),
                            rs.getString("sale_date"),
                            rs.getInt("product_id"),
                            rs.getString("barcode"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            String.format("%.2f", rs.getDouble("unit_cost")),
                            String.format("%.2f", rs.getDouble("unit_price")),
                            String.format("%.2f", rs.getDouble("discount")),
                            String.format("%.2f", netSales),
                            String.format("%.2f", totalCost),
                            String.format("%.2f", lineProfit)
                        });
                        count++;
                        gross += netSales;
                        cost += totalCost;
                        profit += lineProfit;
                    }
                    if (count == 0) {
                        model.addRow(new Object[] {"No data found", "", "", "", "", "", "", "", "", "", "", "", ""});
                    }
                    profitGrossRevenueLabel.setText("Gross Revenue: " + String.format("%.2f", gross));
                    profitTotalCostLabel.setText("Total Cost: " + String.format("%.2f", cost));
                    profitTotalProfitLabel.setText("Total Profit: " + String.format("%.2f", profit));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Profit report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAuditLogsReport() {
        if (auditTable == null) { return; }
        DefaultTableModel model = (DefaultTableModel) auditTable.getModel();
        model.setRowCount(0);

        String keyword = auditSearchField.getText().trim();
        String from = auditFromDateField.getText().trim();
        String to = auditToDateField.getText().trim();
        try (Connection conn = DatabaseConnection.dbConnection()) {
            String query = "SELECT log_id, user_id, action, description, timestamp FROM transactions_log WHERE (action LIKE ? OR description LIKE ?) AND CAST(timestamp AS DATE) BETWEEN ? AND ? ORDER BY timestamp DESC";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                String wildcard = "%" + keyword + "%";
                ps.setString(1, wildcard);
                ps.setString(2, wildcard);
                ps.setString(3, from);
                ps.setString(4, to);
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("log_id"),
                            rs.getInt("user_id"),
                            rs.getString("action"),
                            rs.getString("description"),
                            rs.getString("timestamp")
                        });
                        count++;
                    }
                    if (count == 0) {
                        model.addRow(new Object[] {"No data found", "", "", "", ""});
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Audit Logs report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTableToPDF(JTable table, String title) {
        if (table == null) { return; }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF");
        chooser.setSelectedFile(new File(title.replaceAll("\\s+", "_") + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf"));
        int userSelection = chooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) { return; }

        File file = chooser.getSelectedFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();
            Paragraph heading = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            heading.setAlignment(Element.ALIGN_CENTER);
            document.add(heading);
            document.add(new Paragraph("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            document.add(new Paragraph(" "));

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
            pdfTable.setWidthPercentage(100);

            for (int col = 0; col < model.getColumnCount(); col++) {
                PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(col)));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            for (int row = 0; row < model.getRowCount(); row++) {
                boolean noData = model.getValueAt(row, 0) != null && "No data found".equals(model.getValueAt(row, 0).toString());
                for (int col = 0; col < model.getColumnCount(); col++) {
                    String value = model.getValueAt(row, col) == null ? "" : model.getValueAt(row, col).toString();
                    String output = (noData && col > 0) ? "" : value;
                    PdfPCell cell = new PdfPCell(new Phrase(output));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }
                if (noData) break;
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "PDF exported successfully to " + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to export PDF: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTableToExcel(JTable table, String title) {
        if (table == null) { return; }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Excel");
        chooser.setSelectedFile(new File(title.replaceAll("\\s+", "_") + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx"));
        int userSelection = chooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) { return; }

        File file = chooser.getSelectedFile();
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet(title);
            Row header = sheet.createRow(0);
            for (int col = 0; col < table.getColumnCount(); col++) {
                Cell cell = header.createCell(col);
                cell.setCellValue(table.getColumnName(col));
                CellStyle style = workbook.createCellStyle();
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(style);
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                Row excelRow = sheet.createRow(row + 1);
                boolean noData = model.getValueAt(row, 0) != null && "No data found".equals(model.getValueAt(row, 0).toString());
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = excelRow.createCell(col);
                    String value = model.getValueAt(row, col) == null ? "" : model.getValueAt(row, col).toString();
                    if (noData && col > 0) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(value);
                    }
                }
                if (noData) break;
            }

            for (int col = 0; col < table.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(fos);
            JOptionPane.showMessageDialog(this, "Excel exported successfully to " + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to export Excel: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    // End of variables declaration//GEN-END:variables
}
