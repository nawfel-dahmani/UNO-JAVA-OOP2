package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.ComputerPlayer;
import model.Player;

public class NimoSettings extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField[] editNames;
    private JTextField playersField;
    private JTextField botsField;

    private NimoPanel names = new NimoPanel();
    
    public NimoSettings(NimoWindow frame) {
        setOpaque(false);
        FlowLayout layout = new FlowLayout();
        layout.setHgap(999999);
        layout.setVgap(60);
        setLayout(layout);

        
        NimoButton start = new NimoButton("Start Game",Color.ORANGE, 15, 
            event -> {
                startGame(frame);
            });
            
        NimoButton back = new NimoButton("Back", Color.ORANGE, 15, 
            event -> {
                frame.setHomeView();
            });
            
        JPanel buttons = new JPanel();
        buttons.add(back);
        buttons.add(start);
        buttons.setOpaque(false);

        // Create player name text fields
        editNames = new JTextField[4];
        for(int i = 0; i < editNames.length; i++) {
            editNames[i] = new JTextField();
            editNames[i].setColumns(15);
        }

        // Initialize with default values
        int defaultPlayers = 4;
        int defaultBots = 3;
        names.refresh(defaultPlayers, defaultBots);

        // Player count selection panel with text field
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BorderLayout(5, 5));
        playersPanel.setOpaque(false);
        
        JLabel labelPlayers = new JLabel("Number of Players (2-4):", SwingConstants.CENTER);
        playersField = new JTextField(String.valueOf(defaultPlayers), 5);
        
        playersPanel.add(labelPlayers, BorderLayout.NORTH);
        playersPanel.add(playersField, BorderLayout.CENTER);
        
        // Bot count selection panel with text field
        JPanel botsPanel = new JPanel();
        botsPanel.setLayout(new BorderLayout(5, 5));
        botsPanel.setOpaque(false);
        
        JLabel labelBots = new JLabel("Number of Bots (0-4):", SwingConstants.CENTER);
        botsField = new JTextField(String.valueOf(defaultBots), 5);
        
        botsPanel.add(labelBots, BorderLayout.NORTH);
        botsPanel.add(botsField, BorderLayout.CENTER);
        
        // Apply button to update player/bot configuration
        NimoButton applyButton = new NimoButton("Apply", Color.ORANGE, 15,
            event -> {
                updatePlayerSettings();
            });
            
        JPanel applyPanel = new JPanel();
        applyPanel.add(applyButton);
        applyPanel.setOpaque(false);

        // Add components to the main panel
        add(playersPanel);
        add(botsPanel);
        add(applyPanel);
        add(names);
        add(buttons);
    }
    
    private void updatePlayerSettings() {
        try {
            int playerCount = Integer.parseInt(playersField.getText().trim());
            int botCount = Integer.parseInt(botsField.getText().trim());
            
            // Validate inputs
            playerCount = Math.max(2, Math.min(4, playerCount));
            botCount = Math.max(0, Math.min(playerCount, botCount));
            
            // Update the text fields to show valid values
            playersField.setText(String.valueOf(playerCount));
            botsField.setText(String.valueOf(botCount));
            
            // Update the name panel
            names.refresh(playerCount, botCount);
            names.revalidate();
            names.repaint();
        } catch (NumberFormatException e) {
            // Handle invalid input - reset to default values
            playersField.setText("4");
            botsField.setText("3");
            names.refresh(4, 3);
            names.revalidate();
            names.repaint();
        }
    }
    
    private void startGame(NimoWindow frame) {
        try {
            int playerCount = Integer.parseInt(playersField.getText().trim());
            int botCount = Integer.parseInt(botsField.getText().trim());
            
            // Validate inputs
            playerCount = Math.max(2, Math.min(4, playerCount));
            botCount = Math.max(0, Math.min(playerCount, botCount));
            
            // Always set AI speed to 1000
            ComputerPlayer.sleepTime = 1000;
            
            NimoGame board = new NimoGame(frame);
            int humanPlayerCount = playerCount - botCount;
            Player[] players = new Player[playerCount];

            char[] pos = new char[] {'B', 'L', 'U', 'R'};
            for(int i = 0; i < players.length; i++) {
                if(i < humanPlayerCount) {
                    players[i] = new Player(board, pos[i], i == 0);
                    if(!editNames[i].getText().isBlank())
                        players[i].setName(editNames[i].getText());
                }
                else {
                    players[i] = new ComputerPlayer(board, pos[i]);
                }
            }
            board.setupPlayers(players);
            frame.setBoardGame(board);
        } catch (NumberFormatException e) {
            // Handle invalid input
            playersField.setText("4");
            botsField.setText("3");
        }
    }

    private class NimoPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        public NimoPanel() {
            this.setOpaque(false);
        }

        public void refresh(int p, int ai) {
            this.removeAll();
            if (ai > p)
                ai = p;
            this.setLayout(new GridLayout(2, p));
            for (int i = 1; i <= p - ai; i++)
                this.add(new JLabel("Player " + i));
            for (int i = p - ai; i < p; i++)
                this.add(new JLabel("Bot " + (i+1)));
            for(int i = 0; i < p; i++) {
                editNames[i].setEditable(true);
                this.add(editNames[i]);
            }
            for(int i = p - ai; i < p; i++) {
                editNames[i].setEditable(false);
                editNames[i].setText("");
            }
        }
    }
}