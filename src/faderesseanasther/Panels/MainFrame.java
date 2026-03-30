/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package faderesseanasther.Panels;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;

/**
 *
 * @author Admin
 */
public class MainFrame extends javax.swing.JFrame {

   
    private int loggedInUserId;
    private String loggedInFullName;
    private String loggedInUserRole;

    private ProductManagementPanel productManagement;
    private CategoriesManagementPanel categoryManagement;
    private SupplierManagementPanel supplierManagement;

    private final Color defaultColor = new Color(255, 245, 240);
    private final Color activeColor = new Color(230, 126, 34);
    private final Color hoverColor = new Color(241, 196, 15);
    private final Color headerColor = new Color(44, 62, 80);
    private final Color accentColor = new Color(230, 126, 34);

    private Timer dateTimeTimer;
    private final DateTimeFormatter dateTimeFormat
            = DateTimeFormatter.ofPattern("MMMM dd, yyyy | hh:mm:ss a");

    private void resetSidebarColors() {
        dashboardBtn.setBackground(defaultColor);
        userManagementBtn.setBackground(defaultColor);
        categoriesManagementBtn.setBackground(defaultColor);
        supplierManagementBtn.setBackground(defaultColor);
        productManagementBtn.setBackground(defaultColor);
        inventoryManagementBtn.setBackground(defaultColor);
        posBtn.setBackground(defaultColor);
        salesHistoryManagementBtn.setBackground(defaultColor);
        reportsManagementBtn.setBackground(defaultColor);
    }

    private String safeText(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    private void updateHeaderInfo() {
        fullNameTxt.setText(safeText(loggedInFullName, "Unknown User"));
        roleTxt.setText(safeText(loggedInUserRole, "Unknown Role"));
    }

    private String getDisplayRole() {
        return safeText(loggedInUserRole, "Unknown Role");
    }

    private JPanel[] getSidebarButtons() {
        return new JPanel[]{
            dashboardBtn,
            userManagementBtn,
            categoriesManagementBtn,
            supplierManagementBtn,
            productManagementBtn,
            inventoryManagementBtn,
            posBtn,
            salesHistoryManagementBtn,
            reportsManagementBtn
        };
    }

    private void hideAllSidebarButtons() {
        for (JPanel panel : getSidebarButtons()) {
            panel.setVisible(false);
        }
    }

    private void showSidebarButtons(JPanel... panels) {
        for (JPanel panel : panels) {
            if (panel != null) {
                panel.setVisible(true);
            }
        }
    }

    private void startDateTime() {
        updateDateTime();

        if (dateTimeTimer != null && dateTimeTimer.isRunning()) {
            dateTimeTimer.stop();
        }

        dateTimeTimer = new Timer(1000, e -> updateDateTime());
        dateTimeTimer.start();
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        dateTimeTxt.setText(now.format(dateTimeFormat));
    }

    // highlight selected panel
    private void setActiveMenu(javax.swing.JPanel selectedPanel) {
        resetSidebarColors();
        selectedPanel.setBackground(activeColor);
    }

    public MainFrame(int loggedInUserId, String loggedInFullName, String loggedInUserRole) {
        this.loggedInUserId = (loggedInUserId <= 0) ? 0 : loggedInUserId;

        this.loggedInFullName = (loggedInFullName == null || loggedInFullName.trim().isEmpty())
                ? "Unknown User"
                : loggedInFullName.trim();

        this.loggedInUserRole = (loggedInUserRole == null || loggedInUserRole.trim().isEmpty())
                ? "Unknown Role"
                : loggedInUserRole.trim();

        initComponents();

        updateHeaderInfo();
        applyRoleAccess();
        loadPanels();
        startDateTime();

        showPanel("dashboard");
        setActiveMenu(dashboardBtn);
    }

    public MainFrame() {
        this(0, "Unknown User", "Unknown Role");
    }

    private void applyRoleAccess() {
        String role = getDisplayRole();

        hideAllSidebarButtons();

        // Dashboard is visible to all valid users
        showSidebarButtons(dashboardBtn);

        switch (role.toLowerCase()) {
            case "super admin":
                showSidebarButtons(userManagementBtn,
                        categoriesManagementBtn,
                        supplierManagementBtn,
                        productManagementBtn,
                        inventoryManagementBtn,
                        posBtn,
                        salesHistoryManagementBtn,
                        reportsManagementBtn
                );
                break;

            case "manager":
                showSidebarButtons(userManagementBtn,
                        categoriesManagementBtn,
                        supplierManagementBtn,
                        productManagementBtn,
                        inventoryManagementBtn,
                        posBtn,
                        salesHistoryManagementBtn,
                        reportsManagementBtn
                );
                break;

            case "cashier":
                showSidebarButtons(
                        posBtn,
                        salesHistoryManagementBtn
                );
                break;

            case "inventory clerk":
                showSidebarButtons(
                        categoriesManagementBtn,
                        supplierManagementBtn,
                        productManagementBtn,
                        inventoryManagementBtn
                );
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown role: " + role);
                break;
        }

        rearrangeSidebarButtons();
    }

    private void rearrangeSidebarButtons() {
        sidebarPanel.removeAll();
        sidebarPanel.setLayout(new GridBagLayout());

        Dimension menuSize = new Dimension(250, 55);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 12, 6, 12);

        for (JPanel panel : getSidebarButtons()) {
            if (panel.isVisible()) {
                panel.setPreferredSize(menuSize);
                panel.setMinimumSize(menuSize);
                panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
                
                // Add hover effects
                panel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (!panel.getBackground().equals(activeColor)) {
                            panel.setBackground(hoverColor);
                        }
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (!panel.getBackground().equals(activeColor)) {
                            panel.setBackground(defaultColor);
                        }
                    }
                });
                
                sidebarPanel.add(panel, gbc);
                gbc.gridy++;
            }
        }

        gbc.weighty = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        sidebarPanel.add(spacer, gbc);

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void loadPanels() {
        productManagement = new ProductManagementPanel(loggedInUserId);
        categoryManagement = new CategoriesManagementPanel(productManagement);
        supplierManagement = new SupplierManagementPanel(productManagement);
        rightPanel.setLayout(new CardLayout());
        rightPanel.add(new DashboardManagementPanel(), "dashboard");
        rightPanel.add(new UserManagementPanel(), "users");
        rightPanel.add(categoryManagement, "categories");
        rightPanel.add(supplierManagement, "suppliers");
        rightPanel.add(productManagement, "products");
        rightPanel.add(new ProductManagementPanel(loggedInUserId), "products");
        rightPanel.add(new InventoryManagementPanel(), "inventories");
        rightPanel.add(new PointOfSalePanel(loggedInUserId, loggedInFullName, loggedInUserRole), "pos");
        rightPanel.add(new SalesHistoryManagementPanel(), "sales_history");
        rightPanel.add(new ReportsManagementPanel(), "reports");
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) rightPanel.getLayout();
        cl.show(rightPanel, panelName);

        if ("products".equals(panelName) && productManagement != null) {
            productManagement.refreshComboSources();
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
        jPanel3 = new javax.swing.JPanel();
        roleTxt = new javax.swing.JLabel();
        fullNameTxt = new javax.swing.JLabel();
        dateTimeTxt = new javax.swing.JLabel();
        menuSidebarPanel = new javax.swing.JPanel();
        sidebarPanel = new javax.swing.JPanel();
        userManagementBtn = new javax.swing.JPanel();
        userManagementLbl = new javax.swing.JLabel();
        dashboardBtn = new javax.swing.JPanel();
        dashboardLbl = new javax.swing.JLabel();
        posBtn = new javax.swing.JPanel();
        supplierManagementBtn10 = new javax.swing.JPanel();
        posLbl = new javax.swing.JLabel();
        categoriesManagementBtn = new javax.swing.JPanel();
        supplierManagementBtn7 = new javax.swing.JPanel();
        categoriesManagementLbl = new javax.swing.JLabel();
        supplierManagementBtn = new javax.swing.JPanel();
        supplierManagementBtn3 = new javax.swing.JPanel();
        supplierManagementLbl = new javax.swing.JLabel();
        productManagementBtn = new javax.swing.JPanel();
        supplierManagementBtn4 = new javax.swing.JPanel();
        productManagementLbl = new javax.swing.JLabel();
        inventoryManagementBtn = new javax.swing.JPanel();
        supplierManagementBtn5 = new javax.swing.JPanel();
        inventoryManagementLbl = new javax.swing.JLabel();
        salesHistoryManagementBtn = new javax.swing.JPanel();
        supplierManagementBtn8 = new javax.swing.JPanel();
        salesHistoryManagementLbl = new javax.swing.JLabel();
        reportsManagementBtn = new javax.swing.JPanel();
        supplierManagementBtn1 = new javax.swing.JPanel();
        reportsManagementLbl = new javax.swing.JLabel();
        logoutBtn = new javax.swing.JButton();
        rightPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1567, 819));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(239, 243, 254));
        jPanel1.setMaximumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setMinimumSize(new java.awt.Dimension(1570, 820));
        jPanel1.setPreferredSize(new java.awt.Dimension(1570, 820));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(184, 33, 50));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        roleTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        roleTxt.setForeground(new java.awt.Color(255, 255, 255));
        roleTxt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel3.add(roleTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 30, 270, 30));

        fullNameTxt.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        fullNameTxt.setForeground(new java.awt.Color(255, 255, 255));
        fullNameTxt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel3.add(fullNameTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 10, 270, 30));

        dateTimeTxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        dateTimeTxt.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(dateTimeTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 270, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1570, 70));

        menuSidebarPanel.setBackground(new java.awt.Color(184, 33, 50));
        menuSidebarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sidebarPanel.setBackground(new java.awt.Color(242, 178, 140));
        sidebarPanel.setMaximumSize(new java.awt.Dimension(300, 630));
        sidebarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        userManagementBtn.setBackground(new java.awt.Color(242, 178, 140));
        userManagementBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userManagementBtnMouseClicked(evt);
            }
        });
        userManagementBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        userManagementLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        userManagementLbl.setForeground(new java.awt.Color(102, 0, 0));
        userManagementLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Users.png"))); // NOI18N
        userManagementLbl.setText("User Management");
        userManagementLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userManagementLblMouseClicked(evt);
            }
        });
        userManagementBtn.add(userManagementLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 50));

        sidebarPanel.add(userManagementBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 290, 60));

        dashboardBtn.setBackground(new java.awt.Color(242, 178, 140));
        dashboardBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardBtnMouseClicked(evt);
            }
        });
        dashboardBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        dashboardLbl.setForeground(new java.awt.Color(102, 0, 0));
        dashboardLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Dashboard.png"))); // NOI18N
        dashboardLbl.setText("Dashboard");
        dashboardLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardLblMouseClicked(evt);
            }
        });
        dashboardBtn.add(dashboardLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 50));

        sidebarPanel.add(dashboardBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 290, 60));

        posBtn.setBackground(new java.awt.Color(242, 178, 140));
        posBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                posBtnMouseClicked(evt);
            }
        });
        posBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierManagementBtn10.setBackground(new java.awt.Color(51, 68, 100));
        supplierManagementBtn10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        posBtn.add(supplierManagementBtn10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 310, 60));

        posLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        posLbl.setForeground(new java.awt.Color(102, 0, 0));
        posLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/POS.png"))); // NOI18N
        posLbl.setText("POS Controller");
        posLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                posLblMouseClicked(evt);
            }
        });
        posBtn.add(posLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 50));

        sidebarPanel.add(posBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 290, 60));

        categoriesManagementBtn.setBackground(new java.awt.Color(242, 178, 140));
        categoriesManagementBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoriesManagementBtnMouseClicked(evt);
            }
        });
        categoriesManagementBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierManagementBtn7.setBackground(new java.awt.Color(51, 68, 100));
        supplierManagementBtn7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        categoriesManagementBtn.add(supplierManagementBtn7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 310, 60));

        categoriesManagementLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        categoriesManagementLbl.setForeground(new java.awt.Color(102, 0, 0));
        categoriesManagementLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Categories.png"))); // NOI18N
        categoriesManagementLbl.setText("Categories Managment");
        categoriesManagementLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoriesManagementLblMouseClicked(evt);
            }
        });
        categoriesManagementBtn.add(categoriesManagementLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 50));

        sidebarPanel.add(categoriesManagementBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 290, 60));

        supplierManagementBtn.setBackground(new java.awt.Color(242, 178, 140));
        supplierManagementBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                supplierManagementBtnMouseClicked(evt);
            }
        });
        supplierManagementBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierManagementBtn3.setBackground(new java.awt.Color(51, 68, 100));
        supplierManagementBtn3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        supplierManagementBtn.add(supplierManagementBtn3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 310, 60));

        supplierManagementLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        supplierManagementLbl.setForeground(new java.awt.Color(102, 0, 0));
        supplierManagementLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Supplier.png"))); // NOI18N
        supplierManagementLbl.setText("Supplier Management");
        supplierManagementBtn.add(supplierManagementLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 50));

        sidebarPanel.add(supplierManagementBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 290, 60));

        productManagementBtn.setBackground(new java.awt.Color(242, 178, 140));
        productManagementBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productManagementBtnMouseClicked(evt);
            }
        });
        productManagementBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierManagementBtn4.setBackground(new java.awt.Color(51, 68, 100));
        supplierManagementBtn4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        productManagementBtn.add(supplierManagementBtn4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 310, 60));

        productManagementLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        productManagementLbl.setForeground(new java.awt.Color(102, 0, 0));
        productManagementLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Products.png"))); // NOI18N
        productManagementLbl.setText("Product Management");
        productManagementLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productManagementLblMouseClicked(evt);
            }
        });
        productManagementBtn.add(productManagementLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 50));

        sidebarPanel.add(productManagementBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 290, 60));

        inventoryManagementBtn.setBackground(new java.awt.Color(242, 178, 140));
        inventoryManagementBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventoryManagementBtnMouseClicked(evt);
            }
        });
        inventoryManagementBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierManagementBtn5.setBackground(new java.awt.Color(51, 68, 100));
        supplierManagementBtn5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        inventoryManagementBtn.add(supplierManagementBtn5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 310, 60));

        inventoryManagementLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        inventoryManagementLbl.setForeground(new java.awt.Color(102, 0, 0));
        inventoryManagementLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Inventory.png"))); // NOI18N
        inventoryManagementLbl.setText("Inventory Management");
        inventoryManagementLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventoryManagementLblMouseClicked(evt);
            }
        });
        inventoryManagementBtn.add(inventoryManagementLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 50));

        sidebarPanel.add(inventoryManagementBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 290, 60));

        salesHistoryManagementBtn.setBackground(new java.awt.Color(242, 178, 140));
        salesHistoryManagementBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salesHistoryManagementBtnMouseClicked(evt);
            }
        });
        salesHistoryManagementBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierManagementBtn8.setBackground(new java.awt.Color(51, 68, 100));
        supplierManagementBtn8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        salesHistoryManagementBtn.add(supplierManagementBtn8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 310, 60));

        salesHistoryManagementLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        salesHistoryManagementLbl.setForeground(new java.awt.Color(102, 0, 0));
        salesHistoryManagementLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Sales History.png"))); // NOI18N
        salesHistoryManagementLbl.setText("Sales History");
        salesHistoryManagementBtn.add(salesHistoryManagementLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 50));

        sidebarPanel.add(salesHistoryManagementBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 290, 60));

        reportsManagementBtn.setBackground(new java.awt.Color(242, 178, 140));
        reportsManagementBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportsManagementBtnMouseClicked(evt);
            }
        });
        reportsManagementBtn.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierManagementBtn1.setBackground(new java.awt.Color(51, 68, 100));
        supplierManagementBtn1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        reportsManagementBtn.add(supplierManagementBtn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 310, 60));

        reportsManagementLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportsManagementLbl.setForeground(new java.awt.Color(102, 0, 0));
        reportsManagementLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/faderesseanasther/Icons/Reports.png"))); // NOI18N
        reportsManagementLbl.setText("Reports");
        reportsManagementBtn.add(reportsManagementLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 50));

        sidebarPanel.add(reportsManagementBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 290, 60));

        menuSidebarPanel.add(sidebarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 310, 560));

        logoutBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        logoutBtn.setText("Logout");
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });
        menuSidebarPanel.add(logoutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 710, 310, 50));

        jPanel1.add(menuSidebarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 820));

        rightPanel.setBackground(new java.awt.Color(246, 222, 216));
        rightPanel.setLayout(new java.awt.CardLayout());
        jPanel1.add(rightPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 1220, 750));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1570, 820));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated


    }//GEN-LAST:event_formWindowActivated

    private void dashboardLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLblMouseClicked
        showPanel("dashboard");
        setActiveMenu(dashboardBtn);
    }//GEN-LAST:event_dashboardLblMouseClicked

    private void inventoryManagementLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryManagementLblMouseClicked
        showPanel("inventories");
        setActiveMenu(inventoryManagementBtn);
    }//GEN-LAST:event_inventoryManagementLblMouseClicked

    private void dashboardBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardBtnMouseClicked
        showPanel("dashboard");
        setActiveMenu(dashboardBtn);
    }//GEN-LAST:event_dashboardBtnMouseClicked

    private void userManagementBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userManagementBtnMouseClicked
        showPanel("users");
        setActiveMenu(userManagementBtn);
    }//GEN-LAST:event_userManagementBtnMouseClicked

    private void supplierManagementBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierManagementBtnMouseClicked
        showPanel("suppliers");
        setActiveMenu(supplierManagementBtn);
    }//GEN-LAST:event_supplierManagementBtnMouseClicked

    private void productManagementBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productManagementBtnMouseClicked
        showPanel("products");
        setActiveMenu(productManagementBtn);
    }//GEN-LAST:event_productManagementBtnMouseClicked

    private void inventoryManagementBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryManagementBtnMouseClicked
        showPanel("inventories");
        setActiveMenu(inventoryManagementBtn);
    }//GEN-LAST:event_inventoryManagementBtnMouseClicked

    private void productManagementLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productManagementLblMouseClicked

    }//GEN-LAST:event_productManagementLblMouseClicked

    private void reportsManagementBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportsManagementBtnMouseClicked
        showPanel("reports");
        setActiveMenu(reportsManagementBtn);
    }//GEN-LAST:event_reportsManagementBtnMouseClicked

    private void salesHistoryManagementBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesHistoryManagementBtnMouseClicked
        showPanel("sales_history");
        setActiveMenu(salesHistoryManagementBtn);
    }//GEN-LAST:event_salesHistoryManagementBtnMouseClicked

    private void categoriesManagementBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoriesManagementBtnMouseClicked
        showPanel("categories");
        setActiveMenu(categoriesManagementBtn);
    }//GEN-LAST:event_categoriesManagementBtnMouseClicked

    private void userManagementLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userManagementLblMouseClicked
        showPanel("users");
        setActiveMenu(userManagementBtn);
    }//GEN-LAST:event_userManagementLblMouseClicked

    private void categoriesManagementLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoriesManagementLblMouseClicked

    }//GEN-LAST:event_categoriesManagementLblMouseClicked

    private void posLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_posLblMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_posLblMouseClicked

    private void posBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_posBtnMouseClicked
        showPanel("pos");
        setActiveMenu(posBtn);
    }//GEN-LAST:event_posBtnMouseClicked

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
        LoginScreen ls = new LoginScreen();
        ls.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel categoriesManagementBtn;
    private javax.swing.JLabel categoriesManagementLbl;
    private javax.swing.JPanel dashboardBtn;
    private javax.swing.JLabel dashboardLbl;
    private javax.swing.JLabel dateTimeTxt;
    private javax.swing.JLabel fullNameTxt;
    private javax.swing.JPanel inventoryManagementBtn;
    private javax.swing.JLabel inventoryManagementLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JPanel menuSidebarPanel;
    private javax.swing.JPanel posBtn;
    private javax.swing.JLabel posLbl;
    private javax.swing.JPanel productManagementBtn;
    private javax.swing.JLabel productManagementLbl;
    private javax.swing.JPanel reportsManagementBtn;
    private javax.swing.JLabel reportsManagementLbl;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JLabel roleTxt;
    private javax.swing.JPanel salesHistoryManagementBtn;
    private javax.swing.JLabel salesHistoryManagementLbl;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel supplierManagementBtn;
    private javax.swing.JPanel supplierManagementBtn1;
    private javax.swing.JPanel supplierManagementBtn10;
    private javax.swing.JPanel supplierManagementBtn3;
    private javax.swing.JPanel supplierManagementBtn4;
    private javax.swing.JPanel supplierManagementBtn5;
    private javax.swing.JPanel supplierManagementBtn7;
    private javax.swing.JPanel supplierManagementBtn8;
    private javax.swing.JLabel supplierManagementLbl;
    private javax.swing.JPanel userManagementBtn;
    private javax.swing.JLabel userManagementLbl;
    // End of variables declaration//GEN-END:variables
}
