package DbConnection;

import faderesseanasther.Panels.PointOfSalePanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

public class ButtonEditor extends DefaultCellEditor {

    private final JButton button;
    private String label;
    private boolean clicked;
    private final JTable table;
    private final PointOfSalePanel panel;
    private int row;

    public ButtonEditor(JCheckBox checkBox, JTable table, PointOfSalePanel panel) {
        super(checkBox);
        this.table = table;
        this.panel = panel;

        button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(4, 10, 4, 10));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.row = row;
        label = value == null ? "" : value.toString();
        button.setText(label);

        if ("+".equals(label)) {
            button.setBackground(new Color(41, 75, 107));
        } else {
            button.setBackground(new Color(210, 102, 90));
        }

        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            if ("+".equals(label)) {
                panel.increaseRowQuantity(row);
            } else if ("-".equals(label)) {
                panel.decreaseRowQuantity(row);
            }else if("X".equals(label)){
                panel.removeRowItem(row);
            }
        }
        clicked = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}

