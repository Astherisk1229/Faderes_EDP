package DbConnection;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(4, 10, 4, 10));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        setText(value == null ? "" : value.toString());

        if ("+".equals(value)) {
            setBackground(new Color(41, 75, 107)); // blue
        } else {
            setBackground(new Color(210, 102, 90)); // pink-red
        }

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

        super.paintComponent(g2);
        g2.dispose();
    }
}
