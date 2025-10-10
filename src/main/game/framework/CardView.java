package framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.BasicStroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.Player;

public class CardView extends JPanel
{
	private static final long serialVersionUID = 1L;

	private Image img;
	private boolean up = false, visible = true, selected = false;
	private String type = "NUMBER";
	private char color = 0;
	private int nb = -1, position = 0;
	public static int WIDTH = 85, HEIGHT = 135, COEFF_UP = HEIGHT/3;
	private NimoGame game;
	
	// Double-click tracking
	private long lastClickTime = 0;
	private static final int DOUBLE_CLICK_INTERVAL = 300; // milliseconds
	
	// Visual feedback for selection
	private static final Color SELECTION_COLOR = new Color(255, 215, 0); // Gold
	private static final int SELECTION_STROKE_WIDTH = 3;
	
	public CardView(Image img, String type)
	{
		super();
		this.img = img;
		this.type = type;
		setup();
	}
	
	public CardView(Image img, String type, char color)
	{
		super();
		this.img = img;
		this.type = type;
		this.color = color;
		setup();
	}
	
	public CardView(Image img, char color, int nb)
	{
		super();
		this.img = img;
		this.nb = nb;
		this.color = color;
		setup();
	}
	
	public void setup()
	{
		this.setBounds(0, 0, WIDTH, HEIGHT);
		this.setOpaque(false);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handleMousePress();
			}
			
			public void mouseEntered(MouseEvent e) {
				repaint();
			}
		});
	}
	
	private void handleMousePress() {
		if (!visible || game == null) {
			return; // Only interact with visible cards
		}
		
		Player currentPlayer = game.getActualPlayer();
		if (!currentPlayer.isPlayer()) {
			return; // Only human players can interact
		}
		
		// Check if this card belongs to the current player
		if (!belongsToPlayer(currentPlayer)) {
			return; // Only the current player can interact with their cards
		}
		
		long currentTime = System.currentTimeMillis();
		boolean isDoubleClick = (currentTime - lastClickTime < DOUBLE_CLICK_INTERVAL);
		lastClickTime = currentTime;
		
		if (isDoubleClick) {
			// Double click - try to play the selected card
			if (selected) {
				SwingUtilities.invokeLater(() -> game.handleCardPlay());
			}
		} else {
			// Single click - select this card and deselect others
			DeckView deck = currentPlayer.getDeck();
			if (deck != null) {
				deck.selectSingleCard(this);
			}
		}
	}
	
	private boolean belongsToPlayer(Player player) {
		if (player == null || game == null) return false;
		
		DeckView playerDeck = player.getDeck();
		if (playerDeck == null) return false;
		
		CardView[] cards = playerDeck.getCards();
		if (cards == null) return false;
		
		for (CardView card : cards) {
			if (card == this) {
				return true;
			}
		}
		return false;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		// Don't adjust the position here, let the DeckView handle positioning
		// Just set the selected state and update the appearance
		repaint();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	private void toggleCardSelection() {
		if (!up) {
			setLocation(getX(), getY() - COEFF_UP);
			up = true;
		} else {
			setLocation(getX(), getY() + COEFF_UP);
			up = false;
		}
	}
	
	public void setGame(NimoGame game) {
		this.game = game;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(img == null)
			return;
		BufferedImage image;
		int w = WIDTH, h = HEIGHT;
		
		if(visible)
			image = getBufferedImage(img, BufferedImage.TRANSLUCENT);
		else
			image = getBufferedImage(getBackCard(), BufferedImage.TRANSLUCENT);
		
		for(int i=0;i<position;i++)
			image = rotate(image, 90);
			
		if(position % 2 != 0)
		{
			w = HEIGHT;
			h = WIDTH;
			this.setSize(w, h);
		}
		
		g.drawImage(image, 0, 0, w, h, null);
		
		// Draw selection border if selected
		if (selected && visible) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(SELECTION_COLOR);
			g2d.setStroke(new BasicStroke(SELECTION_STROKE_WIDTH));
			g2d.drawRect(1, 1, w-2, h-2);
		}
	}
	
	public BufferedImage rotate(BufferedImage image, int deg)
	{
		final double rads = Math.toRadians(deg);
		final double sin = Math.abs(Math.sin(rads));
		final double cos = Math.abs(Math.cos(rads));
		final int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
		final int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
		final BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
		final AffineTransform at = new AffineTransform();
		at.translate(w / 2, h / 2);
		at.rotate(rads,0, 0);
		at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
		final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(image,rotatedImage);
		return rotatedImage;
	}
	
	public BufferedImage getBufferedImage(Image i, int type)
	{
		BufferedImage image = new BufferedImage(i.getWidth(null), i.getHeight(null), type);
		Graphics g2 = image.getGraphics();
		g2.drawImage(i, 0, 0, null);
		g2.dispose();
		
		return image;
	}
	
	public static Image getBackCard()
	{
		return NimoWindow.getImage("cards/backCard.png");
	}
	
	public void setCardVisible(boolean bool)
	{
		this.visible = bool;
	}

	public boolean isCardVisible()
	{
		return visible;
	}
	
	public int getNb() {
		return nb;
	}

	public char getColor() {
		return color;
	}

	public String getType() {
		return type;
	}
	
	public void setUp(boolean bool)
	{
		this.up = bool;
	}
	
	public boolean isUp()
	{
		return up;
	}
	
	public Image getImage()
	{
		return img;
	}

	public int getPosition() 
	{
		return position;
	}

	public void setPosition(int position) 
	{
		if(position > 3)
		{
			switch(position)
			{
			case 'L':
				position = 1;
				break;
			case 'U':
				position = 2;
				break;
			case 'R':
				position = 3;
				break;
			default:
				position = 0;
				break;
			}
		}
		else if(position < 0)
			position = 0;
			
		this.position = position;
	}
	
}
