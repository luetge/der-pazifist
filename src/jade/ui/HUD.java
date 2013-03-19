package jade.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import rogue.creature.Creature;

public class HUD {
	protected static Label lblHP, lblAgil, lblFaith, lblRage, lblLevel, lblGold;
	protected static JTextPane lblCreatures;
	protected static int fontHeight;
	protected static Panel hud;
	
	private static void init() {
		try {
			hud = new Panel();
			hud.setPreferredSize(new Dimension(200,100));
			hud.setMaximumSize(new Dimension(200,100000));
			hud.setMinimumSize(new Dimension(200,0));
			fontHeight = 16;
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"));
			hud.setFont(font.deriveFont(Font.PLAIN, fontHeight));
			hud.setBackground(Color.black);
			hud.setForeground(Color.white);
			hud.setFocusable(false);
			JPanel pnlStats = new JPanel();
			pnlStats.setBackground(Color.black);
			pnlStats.setForeground(Color.white);
			hud.setLayout(new BorderLayout());
			pnlStats.setLayout(new GridLayout(7,2));
			hud.add(pnlStats, BorderLayout.NORTH);
			addLabels(pnlStats);
			JPanel pnlCreatures = new JPanel();
			lblCreatures = new JTextPane();
			JScrollPane scrollpane = new JScrollPane(lblCreatures);
			lblCreatures.setBackground(Color.black);
			lblCreatures.setForeground(Color.white);
			scrollpane.getViewport().setBackground(Color.black);
			scrollpane.setBorder(null);
//			scrollpane.add(lblCreatures);
			scrollpane.setForeground(Color.black);
			hud.add(scrollpane, BorderLayout.CENTER);
			hud.setVisible(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Panel getPanel(){
		if(hud == null)
			init();
		return hud;
	}
	
	public static void setVisible(boolean visible){
		hud.setVisible(visible);
		hud.getParent().validate();
	}
	
	private static void addLabels(JPanel pnl) {
		lblHP = new Label("100/100");
		lblAgil = new Label("0%");
		lblFaith = new Label("100%");
		lblRage = new Label("?%");
		lblLevel = new Label("1");
		lblGold = new Label("0");
		pnl.add(new Label("HP:"));
		pnl.add(lblHP);
		pnl.add(new Label("Agil:"));
		pnl.add(lblAgil);
		pnl.add(new Label("Faith:"));
		pnl.add(lblFaith);
		pnl.add(new Label("Rage:"));
		pnl.add(lblRage);
		pnl.add(new Label("Level:"));
		pnl.add(lblLevel);	
		pnl.add(new Label("Gold:"));
		pnl.add(lblGold);	
	}

	public static void setHP(int hp,int maxHp){
		lblHP.setText(hp + "/" + maxHp);
	}
	
	public static void setAgil(int agil){
		lblAgil.setText(agil + "%");
	}
	
	public static void setFaith(int faith){
		lblFaith.setText(faith + "%");
	}
	
	public static void setRage(int rage){
		lblRage.setText(rage + "%");
	}
	
	public static void setLevel(int level){
		lblLevel.setText("" + level);
	}
	
	public static void setGold(int amount){
		lblGold.setText("" +amount);
	}
	
	public static void setCreatures(Iterable<Creature> creatures) {
		lblCreatures.setText("");
		for(Creature creature : creatures)
			lblCreatures.setText(lblCreatures.getText() + "\n" + creature.getName() + " (" + creature.getHP() + " HP)");
	}
}
