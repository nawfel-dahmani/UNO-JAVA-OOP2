package model;

import framework.CardView;
import framework.DeckView;
import framework.NimoGame;

public class Player 
{
	protected static int nextNb = 1;

	private NimoGame board;
	private DeckView deck;
	protected String name = "Player";
	private int rank = -1;
	
	public Player(NimoGame board)
	{
		this.name += " "+(nextNb++);
		this.board = board;
		CardView[] deck = DeckView.getSomeCards(7, board.getGameDeck(), true);
		this.deck = new DeckView(7, deck, 'B', true);
	}
	
	public Player(NimoGame board, char position)
	{
		this.name += " "+(nextNb++);
		this.board = board;
		CardView[] deck = DeckView.getSomeCards(7, board.getGameDeck(), true);
		this.deck = new DeckView(7, deck, position, true);
	}
	
	public Player(NimoGame board, char position, boolean visible)
	{
		this.name += " "+(nextNb++);
		this.board = board;
		CardView[] deck = DeckView.getSomeCards(7, board.getGameDeck(), visible);
		this.deck = new DeckView(7, deck, position, visible);
	}
	
	public boolean canPlay() {
		if(hasFinished())
			return false;
		CardView binCard = board.getBinDeck().getFirstCard();
		CardView[] cards = deck.getCards();
		if(cards == null || cards.length == 0)
			return false;
		for(CardView c : cards)
			if(board.checkCards(c, binCard))
				return true;
		return false;
	}

	public DeckView getDeck()
	{
		return this.deck;
	}

	public NimoGame getBoard() 
	{
		return board;
	}

	public boolean hasFinished() 
	{
		return rank != -1;
	}

	public boolean isPlayer()
	{
		return getClass().equals(Player.class);
	}

	public boolean isAI()
	{
		return (this instanceof ComputerPlayer);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setFinish(int rank) {
		setRank(rank);
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public int getRank()
	{
		return rank;
	}

	public static void resetPlayerNumbers()
	{
		nextNb = 1;
	}

}
