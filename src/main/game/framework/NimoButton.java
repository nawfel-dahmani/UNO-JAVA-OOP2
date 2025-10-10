package framework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom button for the Nimo game interface
 */
public class NimoButton extends JButton {
    private static final long serialVersionUID = 1L;
    
    private Color buttonColor;
    private int borderRadius;
    private boolean isHovered = false;
    private int fontSize;

    /**
     * Creates a new NimoButton with the specified text, color, border radius, and action.
     * 
     * @param text Button text
     * @param color Button color
     * @param borderRadius Border radius
     * @param action Action to perform when clicked
     */
    public NimoButton(String text, Color color, int borderRadius, ActionListener action) {
        super(text);
        this.buttonColor = color;
        this.borderRadius = borderRadius;
        this.fontSize = 12; // Default font size
        
        // Configure the button appearance
        setContentAreaFilled(false);
        setBorderPainted(true);
        setFocusPainted(false);
        setOpaque(false);
        
        setFont(new Font("Arial", Font.BOLD, fontSize));
        setForeground(Color.BLACK);
        
        // Add the action listener
        if (action != null) {
            addActionListener(action);
        }
        
        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public Color getButtonColor() {
        return buttonColor;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the rounded background
        if (isHovered) {
            g2.setColor(buttonColor.brighter());
        } else {
            g2.setColor(buttonColor);
        }

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                borderRadius, borderRadius));

        // Add a subtle border
        g2.setColor(isHovered ? Color.WHITE : buttonColor.darker());
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1,
                borderRadius, borderRadius));

        g2.dispose();

        // Paint the text and other components
        super.paintComponent(g);
    }

    public void setButtonColor(Color color) {
        this.buttonColor = color;
        repaint();
    }

    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
        repaint();
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        setFont(new Font(getFont().getFamily(), getFont().getStyle(), fontSize));
        repaint();
    }
}
