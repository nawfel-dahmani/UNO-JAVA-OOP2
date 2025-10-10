package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.Player;

public class NimoWinner extends JPanel {
    
    private NimoWindow window;
    private Player[] players;
    private int shadowSpace = 25;
    private WinnerDisplay winnerDisplay;

    public NimoWinner(NimoWindow window, Player[] players) {
        setOpaque(false);
        this.window = window;
        this.players = players;

        int topBottom = 32, leftRight = 64;
        this.setBorder(new EmptyBorder(topBottom, leftRight,
                                     topBottom + shadowSpace, leftRight + shadowSpace));
        this.setLayout(new BorderLayout());
        
        // Find the winner (player with rank 1)
        Player winner = null;
        for (Player p : players) {
            if (p.getRank() == 1) {
                winner = p;
                break;
            }
        }
        
        // If no winner found (shouldn't happen), use the first player
        if (winner == null && players.length > 0) {
            winner = players[0];
        }
        
        // Create and add the winner display
        if (winner != null) {
            winnerDisplay = new WinnerDisplay(winner.getName());
            this.add(winnerDisplay, BorderLayout.CENTER);
        }
        
        // Add navigation buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        
        NimoButton quitButton = new NimoButton("Quit", Color.ORANGE, 15, e -> window.quit());
        quitButton.setPreferredSize(new Dimension(150, 40));
        
        NimoButton menuButton = new NimoButton("Menu", Color.ORANGE, 15, e -> {
            Player.resetPlayerNumbers();
            window.setHomeView();
        });
        menuButton.setPreferredSize(new Dimension(150, 40));
        
        buttonPanel.add(quitButton);
        buttonPanel.add(menuButton);
        
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        
        int radius = 50;
        g2d.setColor(Color.BLACK);
        int space = 9;
        g2d.fillRoundRect(space, space, getWidth()-space, getHeight()-space, radius, radius);
        g2d.setColor(new Color(97, 19, 19));
        g2d.fillRoundRect(0, 0, getWidth()-shadowSpace, getHeight()-shadowSpace, radius, radius);
    }

    // Winner display component
    private class WinnerDisplay extends JPanel {
        private JLabel titleLabel;
        private JLabel nameLabel;
        
        public WinnerDisplay(String winnerName) {
            setOpaque(false);
            setLayout(new BorderLayout(0, 30));
            setBorder(new EmptyBorder(40, 30, 40, 30));
            
            titleLabel = new JLabel("The Winner is "+winnerName);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
            titleLabel.setForeground(Color.YELLOW);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
           /* nameLabel = new JLabel(winnerName);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 60));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);*/
            
            add(titleLabel, BorderLayout.CENTER);
           // add(nameLabel, BorderLayout.SOUTH);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            
            g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            
            int radius = 50;
            g2d.setColor(new Color(50, 50, 70, 200));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}