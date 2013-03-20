package jade.ui;

import jade.util.Guard;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import pazi.items.Inventory;
import rogue.creature.Player;

public class Backpack {
	protected static JPanel bpPanel;
	protected static JFrame bpFrame;
	protected static int fontHeight;
	protected static JList lstItems;
	protected static JTextPane lblDescription;
	protected static ScrollPane scrollpane, scrollpaneList;
	protected static Inventory inventory;
	
	public static JPanel getBPPanel(){
		if(bpPanel == null)
			init();
		return bpPanel;
	}
	
	public static Frame getBPFrame(){
		if(bpFrame == null)
			init();
		return bpFrame;
	}

	private static void init() {
		try {
			bpPanel = new JPanel();
			fontHeight = 16;
			bpPanel.setPreferredSize(new Dimension(1000,fontHeight*10-2));
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"));
			bpPanel.setFont(font.deriveFont(Font.PLAIN, fontHeight));
			bpPanel.setBackground(Color.black);
			bpPanel.setForeground(Color.white);
			bpPanel.setFocusable(false);
			bpPanel.setLayout(new BorderLayout());
			lstItems = new JList();
			lstItems.setBackground(Color.black);
			lstItems.setForeground(Color.white);
			lstItems.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {
					if(e.getClickCount() == 1)
						showDescription();
					else
						useItem();
				}
				public void mouseClicked(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
			});
			scrollpaneList = new ScrollPane();
			scrollpaneList.add(lstItems);
			bpPanel.add(scrollpaneList, BorderLayout.CENTER);
			lblDescription = new JTextPane();
			scrollpane = new ScrollPane();
			lblDescription.setBackground(Color.black);
			lblDescription.setForeground(Color.white);
			scrollpane.add(lblDescription);
			bpPanel.add(scrollpane, BorderLayout.SOUTH);
			lblDescription.setText("Beschreibung:");
			bpPanel.setVisible(true);
			bpFrame = new JFrame("Rucksack");
			bpFrame.add(bpPanel);
			bpPanel.setForeground(Color.white);
			bpFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			bpFrame.pack();
			bpFrame.setFocusable(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static void useItem() {
		if(lstItems.getSelectedIndex() == -1)
			return;
		Guard.validateArgument(Player.class.isAssignableFrom(inventory.getOwner().getClass()));
		Guard.validateArgument(inventory.getItems().size() > lstItems.getSelectedIndex());
		
		((Player)inventory.getOwner()).useItem(inventory.getItems().get(lstItems.getSelectedIndex()));
		sendDeadAcuteKey();
	}
	
	protected static void sendDeadAcuteKey(){
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_F10);
			robot.keyRelease(KeyEvent.VK_F10);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	protected static void showDescription() {
		if(lstItems.getSelectedIndex() == -1)
			return;
		lblDescription.setText("Beschreibung:\n\n" + inventory.getItems().get(lstItems.getSelectedIndex()).getDescription());		
	}

	public static void showBPFrame(boolean show){
		getBPFrame().setFocusableWindowState(false);
		getBPFrame().setVisible(show);
		getBPFrame().setFocusable(true);
	}
	
	public static void setInventory(Inventory inventory){
		Backpack.inventory = inventory;
	}
	
	public static void updateInventory() {
		Guard.argumentIsNotNull(inventory);
		lstItems.setListData(inventory.getItems().toArray());
		lblDescription.setText("Beschreibung:");
	}
	
}
