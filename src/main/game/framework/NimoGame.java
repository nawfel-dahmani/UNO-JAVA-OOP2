package framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.ComputerPlayer;
import model.Player;

public class NimoGame extends JPanel
{
	private static final long serialVersionUID = 1L;

	private NimoWindow window;

	private Player[] players;
	private int nextRank = 1;
	private DeckView gameDeck, topCard;
	private JLabel playTurn = new JLabel("");
	public static Color BACKGROUND_COLOR = new Color(176, 255, 233);
	public static int WIDTH = 1000, HEIGHT = 600, PLAY_TURN = 0;
	private int sens = 1, cardsToAdd = 0, skipPlayers = 0;
	private boolean twoMoreCards, fourMoreCards;
	private NimoButton changeTurnButton; // Add this with other instance variables

	// UNO declaration components
	private NimoButton unoButton;
	private Timer unoTimer;
	private boolean unoDeclared = false;
	private static final int UNO_TIMER_DURATION = 3000; // 3 seconds

	public NimoGame(NimoWindow window, Player[] players)
	{
		this(window);
	}

	public NimoGame(NimoWindow window)
	{
		setOpaque(false);
		this.window = window;
		this.setLayout(null);

		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(BACKGROUND_COLOR);

		gameDeck = new DeckView(this, 0);

		topCard = new DeckView(this, 1);

		topCard.addCard(DeckView.getSomeCards(1, gameDeck, true)[0]);

		// Create UNO button - bold orange color for better visibility across all players
		unoButton = new NimoButton("UNO!", new Color(255, 140, 0), 20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				declareUno();
			}
		});

		// Create a medium-sized button that's visible but not too intrusive
		unoButton.setBounds(WIDTH/2 - 60, HEIGHT - 80, 120, 50);
		unoButton.setFont(new Font("Arial", Font.BOLD, 24));
		unoButton.setForeground(Color.WHITE);
		unoButton.setBorderRadius(10);

		// Initially hidden
		unoButton.setVisible(false);

		playTurn.setBounds(120, 0, 400, 100);
		playTurn.setFont(new Font("Arial", Font.BOLD, 16));

		// Add components in order
		this.add(gameDeck);
		this.add(topCard);
		this.add(playTurn);
		this.add(unoButton);

		// In the constructor, after creating other components:
		changeTurnButton = new NimoButton("Change Turn", new Color(50, 150, 250), 20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleChangeTurn();
			}
		});

		// Set button size and position
		changeTurnButton.setBounds(WIDTH/2 + 100, HEIGHT - 80, 120, 50);
		changeTurnButton.setFont(new Font("Arial", Font.BOLD, 16));
		changeTurnButton.setForeground(Color.WHITE);
		changeTurnButton.setBorderRadius(10);
		changeTurnButton.setVisible(false); // Initially hidden

		// Add the button to the panel (add this after adding other components)
		this.add(changeTurnButton);

		// Remove test button
	}

	public void setupPlayers(Player[] players) {
		if (players == null || players.length == 0) {
			return;
		}

		this.players = players;

		// Reset PLAY_TURN to ensure it's valid with new player array
		PLAY_TURN = 0;

		// Process all players
		for (Player p : players) {
			if (p == null) continue;

			// Skip if player's deck is null
			DeckView playerDeck = p.getDeck();
			if (playerDeck == null) continue;

			// Add player's deck to the game panel
			this.add(playerDeck);

			// Set game reference for all cards in the deck
			CardView[] cards = playerDeck.getCards();
			if (cards != null) {
				for (CardView card : cards) {
					if (card != null) {
						card.setGame(this);
					}
				}
			}

			// Make sure deck is configured properly for this player's position
			char position = playerDeck.getPosition();
			setupDeckForPosition(playerDeck, position);
		}

		Player currentPlayer = getActualPlayer();
		if (currentPlayer != null) {
			playTurn.setText(currentPlayer.getName());
			playTurn.setForeground(Color.WHITE);
		}

		// Initialize card visibility
		updateCardVisibility();
	}

	/**
	 * Sets up a deck for the proper position on the board
	 */
	private void setupDeckForPosition(DeckView deck, char position) {
		if (deck == null) return;

		// Position-specific adjustments for proper card display
		switch (position) {
			case 'B': // Bottom
				// Bottom player's deck is positioned at the bottom of the screen
				// Default position is already set correctly
				break;

			case 'L': // Left
				// Left player's deck is positioned on the left side
				// Default position is already set correctly
				break;

			case 'U': // Top
				// Top player's deck is positioned at the top
				// Default position is already set correctly
				break;

			case 'R': // Right
				// Right player's deck is positioned on the right
				// Default position is already set correctly
				break;

			default:
				// Use bottom position as fallback
				deck.setPosition('B');
				break;
		}

		// Make sure deck display is refreshed
		deck.refreshDeck();
	}

	public void dropCard()
	{
		Player currentPlayer = getActualPlayer();
		if (currentPlayer == null) {
			return;
		}

		if(currentPlayer.canPlay())
			return;

		CardView card = gameDeck.getAndDelLastCard();
		if(card != null)
		{
			DeckView playerDeck = currentPlayer.getDeck();
			if (playerDeck == null) {
				return;
			}

			if(currentPlayer.isPlayer())
			{
				card.setGame(this);
				playerDeck.addCard(card);
				playerDeck.setLastLength(999);
				// Show change turn button after drawing
				changeTurnButton.setVisible(true);
			}
			else
			{
				card.setGame(this);
				CardView firstCard = playerDeck.getFirstCard();
				// Check if first card exists before accessing it
				if (firstCard != null) {
					card.setCardVisible(firstCard.isCardVisible());
				} else {
					card.setCardVisible(false);
				}
				playerDeck.addCard(card);
			}
		}
	}

	public void dropToTop() {
	    DeckView deck = getActualPlayer().getDeck();
	    CardView currentBinCard = topCard.getFirstCard();
	    CardView[] cards = sortCards(deck.getUpCards(), currentBinCard);

	    if (cards == null || cards.length == 0) {
	        return;
	    }

	    if (checkCards(cards, currentBinCard)) {
	        applyChangements(cards);
	        deck.delUpCards();

	        // Clear the top first
	        topCard.clearCards();

	        // Only add the last played card (most recent)
	        CardView lastPlayedCard = cards[cards.length - 1];
	        topCard.addCard(lastPlayedCard);
	    }
	}

	public static CardView[] sortCards(CardView[] cards, CardView top)
	{
		if(cards == null)
			return null;
		List<CardView> list = new ArrayList<>();
		CardView[] tab;
		for(int i=0;i<cards.length;i++)
		{
			if(cards[i].getColor() == top.getColor())
				list.add(0, cards[i]);
			else
				list.add(cards[i]);
		}

		tab = new CardView[list.size()];
		for(int i=0;i<list.size();i++)
			tab[i] = list.get(i);
		return tab;
	}

	public void applyChangements(CardView[] cards)
	{
		String type = cards[0].getType();
		if(type.equals("sens"))
		{
			if(((cards.length+1)%2)== 0)
				sens *= -1;
		}
		else if(type.equals("forbidden"))
		{
			skipPlayers = cards.length;
		}
		else if(type.equals("+2"))
		{
			cardsToAdd += cards.length*2;
			twoMoreCards = true;
		}
		else if(type.equals("+4"))
		{
			cardsToAdd += cards.length*4;
			fourMoreCards = true;
		}
	}

	public boolean checkCards(CardView[] cards, CardView binCard)
	{
		if(cards.length == 1)
			return checkCards(cards[0], binCard);
		else if(cards.length >= 2 && !checkTypes(cards))
			return false;
		CardView temp = cards[0];
		if(twoMoreCards && !temp.getType().equals("+2"))
			return false;
		else if(fourMoreCards && !temp.getType().equals("+4"))
			return false;
		switch(temp.getType())
		{
		case "NUMBER":
				if(compareNumbers(cards) && (temp.getNb() == binCard.getNb()
				|| checkColor(cards, binCard.getColor())))
					return true;
			break;
		case "+4":
			return true;
		case "+2":
		case "forbidden":
		case "sens":
		default:
			return checkColor(cards, binCard.getColor()) || temp.getType() == binCard.getType();
		}

		return false;
	}

public boolean checkCards(CardView card, CardView binCard)
{
    // Only check for +2/+4 restrictions if the flags are still active
    if(twoMoreCards && !card.getType().equals("+2"))
        return false;
    else if(fourMoreCards && !card.getType().equals("+4"))
        return false;

    switch(card.getType())
    {
    case "NUMBER":
        if(card.getNb() == binCard.getNb()
        || card.getColor() == binCard.getColor())
            return true;
        break;
    case "+4":
        return true;
    case "colorChanger":
        return true; // Color changers can always be played
    case "forbidden":
    case "sens":
    default:
        if(card.getType().equals(binCard.getType()) ||
           card.getColor() == binCard.getColor())
            return true;
        break;
    }

    return false;
}

	public static boolean canStackCards(CardView card, CardView card2)
	{
		String type = card.getType();

		if(type.equals("NUMBER"))
		{
			if(card.getNb() == card2.getNb())
				return true;
		}
		else if(!type.equals("COLOR_CHANGER"))
		{
			if(type.equals(card2.getType()))
				return true;
		}

		return false;
	}

	public static boolean checkTypes(CardView[] cards)
	{
		String type = cards[0].getType();
		for(int i=0;i<cards.length;i++)
			if(!cards[i].getType().equals(type))
				return false;
		return true;
	}

	public static boolean checkTypes(CardView[] cards, String type)
	{
		for(int i=0;i<cards.length;i++)
			if(cards[i].getType().equals(type))
				return true;
		return false;
	}

	public static boolean checkColor(CardView[] cards, char color)
	{

		for(int i=0;i<cards.length;i++)
			if(cards[i].getColor() == color)
				return true;
		return false;
	}

	public static boolean compareColors(CardView[] cards)
	{
		char color = cards[0].getColor();
		for(int i=0;i<cards.length;i++)
			if(cards[i].getColor() != color)
				return false;
		return true;
	}

	public static boolean compareNumbers(CardView[] cards)
	{
		int nb = cards[0].getNb();
		for(int i=0;i<cards.length;i++)
			if(cards[i].getNb() != nb)
				return false;
		return true;
	}

	public void changeTurn()
	{
		changeTurn(false);
	}

	public void changeTurn(boolean force)
	{
		// Add this at the beginning of the method
		changeTurnButton.setVisible(false);

		Player player = getActualPlayer();

		// Cancel any UNO timer when changing turns
		cancelUnoTimer();
		// Hide the UNO button when changing turns
		unoButton.setVisible(false);

		if(player.isPlayer()) {
			if(!force && player.getDeck().getLastLength() == player.getDeck().getLength()
			&& !player.hasFinished())
				return;
		}

		PLAY_TURN += sens;

		// Handle negative PLAY_TURN correctly
		if(PLAY_TURN < 0)
			PLAY_TURN += players.length;

		// Make sure PLAY_TURN is within bounds
		PLAY_TURN %= players.length;

		player = getActualPlayer();
		if(player.isPlayer() && !force)
			player.getDeck().refreshLastLength();

		// Skip players who have finished
		if(player.hasFinished()) {
			changeTurn(true);
			return;
		}

		// Process skip players logic
		if(skipPlayers > 0)
		{
			skipPlayers--;
			changeTurn(true);
			return;
		}

		// Update card visibility for all players
		updateCardVisibility();

		playTurn.setText(getActualPlayer().getName()+"'s turn");
	}

	// Update card visibility - show current player's cards, hide others
	private void updateCardVisibility() {
		if (players == null) {
			return;
		}

		Player currentPlayer = getActualPlayer();
		if (currentPlayer == null) {
			return;
		}

		// First reset all cards to non-selected state
		for (Player p : players) {
			if (p == null) continue;

			DeckView deck = p.getDeck();
			if (deck == null) continue;

			// Put all cards down (not selected)
			deck.putCardsDown();

			// Clear any card selections
			deck.deselectAllCards();

			// Set visibility based on whether this is the current player
			boolean isCurrentPlayer = (p == currentPlayer);
			boolean shouldShowCards = (isCurrentPlayer && p.isPlayer());

			CardView[] cards = deck.getCards();
			if (cards != null) {
				for (CardView card : cards) {
					if (card != null) {
						card.setCardVisible(shouldShowCards);
					}
				}
				deck.setupDeck();
			}
		}

		// Refresh the UI to show the changes
		repaint();
	}

	public boolean hasCard(Player player, String type) {
		if (player == null || type == null) {
			return false;
		}

		DeckView deck = player.getDeck();
		if (deck == null) {
			return false;
		}

		CardView[] cards = deck.getCards();
		if (cards == null) {
			return false;
		}

		for(CardView card : cards) {
			if (card != null && type.equals(card.getType())) {
				return true;
			}
		}
		return false;
	}

	public void penalityCards() {
	    Player currentPlayer = getActualPlayer();
	    if(currentPlayer == null || currentPlayer.hasFinished())
	        return;

	    DeckView deck = currentPlayer.getDeck();
	    if (deck == null) {
	        return;
	    }

	    boolean skipTurn = false;

	    if(twoMoreCards) {
	        if(!hasCard(currentPlayer, "+2")) {
	            int cardsAdded = 0;
	            for(int i=0; i<cardsToAdd; i++) {
	                CardView card = gameDeck.getAndDelLastCard();
	                if(card == null)
	                    break;

	                if(currentPlayer instanceof ComputerPlayer)
	                    card.setCardVisible(false);
	                else
	                    card.setCardVisible(true); // Make sure cards are visible for human players

	                card.setGame(this); // Ensure the card is properly connected to the game
	                deck.addCard(card);
	                cardsAdded++;
	            }

	            deck.setLastLength(deck.getLength());
	            skipTurn = true;
	        }
	        // Reset these flags after drawing cards
	        twoMoreCards = false;
	        cardsToAdd = 0;
	    }
	    else if(fourMoreCards) {
	        if(!hasCard(currentPlayer, "+4")) {
	            int cardsAdded = 0;
	            for(int i=0; i<cardsToAdd; i++) {
	                CardView card = gameDeck.getAndDelLastCard();
	                if(card == null)
	                    break;

	                if(currentPlayer instanceof ComputerPlayer)
	                    card.setCardVisible(false);
	                else
	                    card.setCardVisible(true); // Make sure cards are visible for human players

	                card.setGame(this); // Ensure the card is properly connected to the game
	                deck.addCard(card);
	                cardsAdded++;
	            }

	            deck.setLastLength(deck.getLength());
	            skipTurn = true;
	        }
	        // Reset these flags after drawing cards
	        fourMoreCards = false;
	        cardsToAdd = 0;
	    }
	    
	    if(skipTurn) {
	        // Refresh the deck to ensure proper display
	        deck.refreshDeck();
	        changeTurn(true);
	        
	        // If next player is AI, trigger their turn
	        if (getActualPlayer().isAI()) {
	            triggerAITurn();
	        }
	    }
}

	public Player getActualPlayer() {
		// Check if players array is null or empty to prevent crashes
		if (players == null || players.length == 0) {
			return null;
		}

		// Make sure PLAY_TURN is within bounds
		if (PLAY_TURN < 0 || PLAY_TURN >= players.length) {
			PLAY_TURN = 0;
		}

		return players[PLAY_TURN];
	}

	public DeckView getBinDeck()
	{
		return topCard;
	}

	public DeckView getGameDeck()
	{
		return gameDeck;
	}

	/**
	 * Get all players in the game
	 * @return array of players
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Handles a card play event when a card is double-clicked
	 */
	public void handleCardPlay() {
		Player currentPlayer = getActualPlayer();

		// Only proceed if it's a human player's turn
		if (currentPlayer.isPlayer()) {
			DeckView deck = currentPlayer.getDeck();

			// Get the selected card
			CardView selectedCard = deck.getSelectedCard();

			// Check if a card is selected
			if (selectedCard != null) {
				CardView topCard = getBinDeck().getFirstCard();

				// Verify the selected card is valid for play
				if (checkCards(selectedCard, topCard)) {
					// Mark the card as up for the dropToTop method
					selectedCard.setUp(true);

					// Refresh the deck to show animation
					deck.refreshDeck();

					// Short delay to show the animation
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// Ignore
					}

					// Check if playing this card will leave player with exactly one card
					boolean willHaveOneCard = (currentPlayer.getDeck().getLength() == 2);

					// Play the selected card
					dropToTop();

					// If player now has one card, immediately check for UNO
					if (willHaveOneCard || currentPlayer.getDeck().getLength() == 1) {
						// Force immediate check for UNO
						showUnoButton(currentPlayer);
					}

					// Check if the player has won
					if (currentPlayer.getDeck().getLength() == 0) {
						currentPlayer.setFinish(nextRank++);

						// Cancel any active UNO timer
						cancelUnoTimer();

						window.setRankingView(players);
						return;
					}

					// Move to next player
					changeTurn();
					penalityCards();

					// If next player is AI, trigger its turn
					if (getActualPlayer().isAI()) {
						triggerAITurn();
					}
				} else {
					// Invalid play - deselect the card
					selectedCard.setSelected(false);
					deck.refreshDeck();
				}
			}
		}
	}

	/**
	 * Immediately shows the UNO button for the given player
	 * This is a dedicated method to ensure the button appears
	 */
	private void showUnoButton(Player player) {
		// Safety checks
		if (player == null || unoButton == null) return;

		// Only show for human players
		if (!player.isPlayer()) return;

		DeckView deck = player.getDeck();
		if (deck == null) return;

		// Make sure player has exactly one card left
		if (deck.getLength() != 1) return;

		// Cancel any existing UNO timer
		cancelUnoTimer();

		// Reset the UNO declaration status
		unoDeclared = false;

		// Position the button consistently below the player's last card
		positionButtonBelowLastCard(player);

		// Show UNO notification message
		if (playTurn != null) {
			playTurn.setText("<html><font color='#FF8C00' size='+1'><b>" + player.getName() +
				" must declare UNO!</b></font></html>");
		}

		// Make sure the UNO button is visible and on top
		unoButton.setVisible(true);
		this.remove(unoButton);
		this.add(unoButton);
		this.setComponentZOrder(unoButton, 0);

		// Force repaints
		unoButton.revalidate();
		unoButton.repaint();
		this.validate();
		this.repaint();

		// Start the UNO timer
		startUnoTimer(player);
	}

	/**
	 * Positions the UNO button below the player's last card
	 */
	private void positionButtonBelowLastCard(Player player) {
		if (player == null || unoButton == null) return;

		DeckView deck = player.getDeck();
		if (deck == null) {
			// Default position
			unoButton.setBounds(WIDTH/2 - 60, HEIGHT - 80, 120, 50);
			return;
		}

		CardView lastCard = getLastVisibleCard(deck);

		// If we can't find the last card, use a default position
		if (lastCard == null) {
			unoButton.setBounds(WIDTH/2 - 60, HEIGHT - 80, 120, 50);
			return;
		}

		try {
			// Calculate position based on the last card
			int cardX = lastCard.getX();
			int cardY = lastCard.getY();
			int cardWidth = lastCard.getWidth();
			int cardHeight = lastCard.getHeight();

			// Convert to screen coordinates
			int buttonX = cardX + (cardWidth - 120) / 2;
			int buttonY = cardY + cardHeight + 10;

			// Get parent coordinates if available
			if (lastCard.getParent() != null) {
				buttonX += lastCard.getParent().getX();
				buttonY += lastCard.getParent().getY();
			}

			// Adjust for screen boundaries
			buttonX = Math.max(10, Math.min(WIDTH - 130, buttonX));
			buttonY = Math.max(10, Math.min(HEIGHT - 60, buttonY));

			// Set the button position
			unoButton.setBounds(buttonX, buttonY, 120, 50);
		} catch (Exception e) {
			// If anything goes wrong, use a safe default position
			unoButton.setBounds(WIDTH/2 - 60, HEIGHT - 80, 120, 50);
		}
	}

	/**
	 * Gets the last visible card in a deck
	 */
	private CardView getLastVisibleCard(DeckView deck) {
		if (deck == null) return null;

		CardView[] cards = deck.getCards();
		if (cards == null || cards.length == 0) return null;

		// Return the last card, but make sure it's not null
		for (int i = cards.length - 1; i >= 0; i--) {
			if (cards[i] != null) {
				return cards[i];
			}
		}

		// If all cards are null, return null
		return null;
	}

	/**
	 * Starts the 3-second UNO timer
	 */
	private void startUnoTimer(final Player player) {
		// Cancel any existing timer first
		cancelUnoTimer();

		// Create a new timer
		unoTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// If UNO wasn't declared within the time limit, penalize the player
				if (!unoDeclared) {
					SwingUtilities.invokeLater(() -> {
						penalizeForMissingUno(player);
					});
				}
				unoTimer.cancel();
				unoTimer = null;
			}
		};

		// Schedule the timer to run after UNO_TIMER_DURATION
		unoTimer.schedule(task, UNO_TIMER_DURATION);
	}

	/**
	 * Cancels the active UNO timer if one exists
	 */
	private void cancelUnoTimer() {
		if (unoTimer != null) {
			unoTimer.cancel();
			unoTimer = null;
		}
	}

	/**
	 * Handles the player's UNO declaration
	 */
	private void declareUno() {
		// Mark UNO as declared
		unoDeclared = true;

		// Hide the UNO button
		unoButton.setVisible(false);

		// Cancel the timer
		cancelUnoTimer();

		// Show a confirmation message
		playTurn.setText("<html><font color='green' size='+1'><b>" +
			getActualPlayer().getName() + " declared UNO!</b></font></html>");

		// Refresh the game panel to ensure visual updates
		this.validate();
		this.repaint();
	}

	/**
	 * Penalizes a player for not declaring UNO by adding 2 cards to their deck
	 */
	private void penalizeForMissingUno(Player player) {
		// Safety checks
		if (player == null || gameDeck == null || unoButton == null) {
			return;
		}

		DeckView deck = player.getDeck();
		if (deck == null) {
			return;
		}

		// Only penalize if the player still has 1 card (they might have drawn more already)
		if (deck.getLength() == 1) {
			// Add 2 cards as penalty
			for (int i = 0; i < 2; i++) {
				CardView card = gameDeck.getAndDelLastCard();
				if (card != null) {
					card.setGame(this);
					deck.addCard(card);
				}
			}
			
			// Update the display
			deck.refreshDeck();
			
			// Show a penalty message in bold red
			if (playTurn != null) {
				playTurn.setText("<html><b><font color='red'>" + player.getName() + 
					" didn't say UNO! +2 cards penalty</font></b></html>");
			}
		}
		
		// Hide the UNO button
		unoButton.setVisible(false);
	}
	
	/**
	 * Triggers the AI player's turn logic
	 */
	private void triggerAITurn() {
		final Player aiPlayer = getActualPlayer();
		
		// Safety check to make sure we have a valid AI player
		if (aiPlayer == null || !aiPlayer.isAI()) {
			return;
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					ComputerPlayer bot = (ComputerPlayer)aiPlayer;
					
					boolean played = bot.play();
					
					if (!played) {
						// If bot can't play, draw a card
						dropCard();
						// Check if the drawn card can be played
						played = bot.play();
					}
					
					try {
						Thread.sleep(ComputerPlayer.sleepTime);
					} catch (InterruptedException e1) {
						// Ignore
					}
					
					// Check if bot will have one card after playing
					boolean willHaveOneCard = bot.getDeck().getLength() == 2 && played;
					
					dropToTop();
					
					// Bots automatically declare UNO
					if (bot.getDeck().getLength() == 1) {
						playTurn.setText(bot.getName() + " says UNO!");
					}
					
					// Check if the AI has won
					if (bot.getDeck().getLength() == 0) {
						bot.setFinish(nextRank++);
						window.setRankingView(players);
						return;
					}
					
					changeTurn();
					penalityCards();
					
					// If next player is also AI, trigger its turn
					Player nextPlayer = getActualPlayer();
					if (nextPlayer != null && nextPlayer.isAI()) {
						triggerAITurn();
					}
				} catch (Exception e) {
					// Log error but don't crash
					System.err.println("Error in AI turn: " + e.getMessage());
				}
			}
		}).start();
	}

private void handleChangeTurn() {
    Player currentPlayer = getActualPlayer();
    if (currentPlayer != null && currentPlayer.isPlayer()) {
        changeTurnButton.setVisible(false); // Hide the button
        changeTurn(true); // Force turn change
        penalityCards(); // Check for any penalties
        
        // If next player is AI, trigger their turn
        if (getActualPlayer().isAI()) {
            triggerAITurn();
        }
    }
}
}