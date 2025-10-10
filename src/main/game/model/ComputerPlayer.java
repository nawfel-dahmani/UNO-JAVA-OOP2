package model;
import java.util.ArrayList;
import java.util.List;
import framework.CardView;
import framework.NimoGame;
public class ComputerPlayer extends Player
{
	public static int sleepTime = 1000;
	public ComputerPlayer(NimoGame board, char position)
	{
		super(board, position, false);
		this.name = "Bot "+(nextNb-1);
		
		// Ensure the bot's deck is properly positioned
		this.getDeck().refreshDeck();
	}
	
	public boolean play()
	{
		// Reset any currently selected cards
		resetSelection();
		
		CardView[] cards = this.getDeck().getCards();
		CardView binCard = this.getBoard().getBinDeck().getFirstCard();
		
		if (cards == null || cards.length == 0) {
			return false;
		}
		
		// Try to find special cards first (better strategy)
		for(int i=0; i<cards.length; i++) {
			CardView card = cards[i];
			if (isSpecialCard(card) && this.getBoard().checkCards(card, binCard)) {
				// Select card but keep it hidden
				card.setUp(true);
				card.setCardVisible(false);
				this.getDeck().refreshDeck();
				return true;
			}
		}
		
		// Then look for any playable card
		for(int i=0; i<cards.length; i++)
		{
			if(this.getBoard().checkCards(cards[i], binCard))
			{
				// Select card but keep it hidden
				cards[i].setUp(true);
				cards[i].setCardVisible(false);
				this.getDeck().refreshDeck();
				return true;
			}
		}
		
		// No playable card found
		return false;
	}
	
	private boolean isSpecialCard(CardView card) {
		String type = card.getType();
		return type.equals("+2") || type.equals("+4") || 
			   type.equals("forbidden") || type.equals("sens");
	}
	
	private void resetSelection() {
		// Reset any selected cards
		this.getDeck().deselectAllCards();
		
		// Also make sure no cards are up
		CardView[] cards = this.getDeck().getCards();
		if (cards != null) {
			for (CardView card : cards) {
				card.setUp(false);
			}
			this.getDeck().refreshDeck();
		}
	}
	
	public static List<CardView> getStackCards(int cardIndex, CardView[] cards)
	{
		List<CardView> list = new ArrayList<>();
		list.add(cards[cardIndex]);
		
		for(int j=0;j<cards.length;j++)
		{
			if(NimoGame.canStackCards(cards[cardIndex], cards[j]))
				if(cardIndex != j)
					list.add(cards[j]);
		}	
		
		return list;
	}
}