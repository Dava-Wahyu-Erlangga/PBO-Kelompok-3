package rental;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFormPanel extends JPanel {
    public BaseFormPanel(String title) {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 65));
        setBorder(BorderFactory.createEmptyBorder(30, 120, 30, 120));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 255, 136));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);
    }
}