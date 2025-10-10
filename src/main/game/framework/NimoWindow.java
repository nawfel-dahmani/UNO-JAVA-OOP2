package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;

import model.Player;
import framework.NimoButton;
import framework.NimoGame;
import framework.NimoSettings;
import framework.NimoWinner;
import framework.HomeView;

public class NimoWindow extends JFrame
{
    private static final long serialVersionUID = 1L;
    
    private MainPanel mainPan;
    private JLabel logoLabel;
    
    public NimoWindow(int w, int h)
    {
        super();
        this.setMinimumSize(new Dimension(w, h));
        this.setTitle("UNO");
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(w, h));
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            ImageIcon icon = new ImageIcon(getImage("UNO_Logo.svg.png"));
            this.setIconImage(icon.getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        try {
            Image logoImg = getImage("UNO_Logo.svg.png");
            if (logoImg != null) {              
                Image scaledLogo = logoImg.getScaledInstance(200, 120, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(scaledLogo));
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        mainPan = new MainPanel();
        mainPan.setLayout(new BorderLayout());
        setContentPane(mainPan);
        
        setHomeView();
        this.setVisible(true);
    }
    
    public void setHomeView() {
        this.getContentPane().removeAll();
        mainPan.changeImage("uno_menu_background.jpg");       
        
        // Add logo at top
        if (logoLabel != null) {
            JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            logoPanel.setOpaque(false);
            logoPanel.add(logoLabel);
            this.getContentPane().add(logoPanel, BorderLayout.NORTH);
        }
        
        // Create main panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(4, 1, 0, 20)); // 4 rows, 1 column, 20px gap
        
        // Add empty space
        JPanel spacer1 = new JPanel();
        spacer1.setOpaque(false);
        buttonPanel.add(spacer1);
        
        
        JPanel playButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playButtonPanel.setOpaque(false);
        NimoButton playButton = createNimoButton("PLAY", Color.ORANGE, 15, e -> setSettingsView());
        playButton.setFont(playButton.getFont().deriveFont(18.0f));
        playButton.setPreferredSize(new Dimension(150, 50));
        playButtonPanel.add(playButton);
        buttonPanel.add(playButtonPanel);
        
        // Create orange quit button
        JPanel quitButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quitButtonPanel.setOpaque(false);
        NimoButton quitButton = createNimoButton("QUIT", Color.ORANGE, 15, e -> quit());
        quitButton.setFont(quitButton.getFont().deriveFont(18.0f));
        quitButton.setPreferredSize(new Dimension(150, 50));
        quitButtonPanel.add(quitButton);
        buttonPanel.add(quitButtonPanel);
        
        // Add empty space
        JPanel spacer2 = new JPanel();
        spacer2.setOpaque(false);
        buttonPanel.add(spacer2);
        
        this.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    public void setSettingsView() {
        this.getContentPane().removeAll();
        mainPan.changeImage("uno_menu_background.jpg");        
        if (logoLabel != null) {
            JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            logoPanel.setOpaque(false);
            logoPanel.add(logoLabel);
            this.getContentPane().add(logoPanel, BorderLayout.NORTH);
        }
        
        NimoSettings settings = new NimoSettings(this);
        this.getContentPane().add(settings, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    public void setBoardGame() {
        setBoardGame(new NimoGame(this));
    }
    
    public void setBoardGame(NimoGame boardGame) {
        this.getContentPane().removeAll();
        mainPan.changeImage("uno_background.jpg");
        
        if (logoLabel != null) {
            JPanel centerLogoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            centerLogoPanel.setOpaque(false);
            centerLogoPanel.add(logoLabel);
            JPanel gamePanel = new JPanel(new BorderLayout());
            gamePanel.setOpaque(false);
            gamePanel.add(centerLogoPanel, BorderLayout.CENTER);
            JPanel boardPanel = new JPanel(new GridBagLayout());
            boardPanel.setOpaque(false);
            boardPanel.add(boardGame);
            gamePanel.add(boardPanel, BorderLayout.CENTER);
            
            this.getContentPane().add(gamePanel);
        } else {
            JPanel pan = new JPanel();
            pan.setOpaque(false);
            pan.setLayout(new GridBagLayout());
            pan.add(boardGame);
            this.getContentPane().add(pan);
        }
        
        revalidate();
        repaint();
    }
    
    public void setRankingView(Player[] players) {
        this.getContentPane().removeAll();
        mainPan.changeImage("uno_menu_background.jpg");
        if (logoLabel != null) {
            JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            logoPanel.setOpaque(false);
            logoPanel.add(logoLabel);
            this.getContentPane().add(logoPanel, BorderLayout.NORTH);
        }
        
        JPanel pan = new JPanel();
        pan.setOpaque(false);
        pan.add(new NimoWinner(this, players));
        setupPan(pan);
        this.getContentPane().add(pan, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    public void setupPan(JPanel pan) {
        pan.setLayout(new GridLayout());
        Dimension screen = this.getSize();
        int w = 1920, h = 1010;
        int top = ((int)screen.getHeight() * 170) / h;
        int left = ((int)screen.getWidth() * 500) / w;
        pan.setBorder(new EmptyBorder(top, left, top, left));
    }
    
    public void quit() {
        this.dispose();
        System.exit(0);
    }
    
    public static NimoButton createNimoButton(String text, Color color, int radius, ActionListener listener) {
        return new NimoButton(text, color, radius, listener);
    }
    
    private class MainPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Image backImg;
        
        public MainPanel() {
            backImg = getImage("uno_menu_background.jpg");
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(backImg != null)
                g.drawImage(backImg, 0, 0, getWidth(), getHeight(), null);
        }
        
        public void changeImage(String name) {
            backImg = getImage(name);
        } 
    }
    
    public static Image getImage(final String pathAndFileName) {
        Image img = null;
        try {
            img = ImageIO.read(new File("resources/"+pathAndFileName));
        } catch (IOException e) {
            try {
                img = ImageIO.read(new File("src/main/resources/"+pathAndFileName));
            } catch (IOException e1) {}
        }
        
        return img;
    }
}