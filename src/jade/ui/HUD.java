package jade.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.io.FileInputStream;

import javax.swing.JPanel;

public class HUD {
	protected static Label lblHP, lblAgil, lblFaith, lblRage, lblLevel, lblGold;
	protected static int fontHeight;
	protected static Panel hud;
	
	private static void init() {
		try {
			hud = new Panel();
			hud.setPreferredSize(new Dimension(100,100));
			hud.setMaximumSize(new Dimension(100,100000));
			hud.setMinimumSize(new Dimension(100,0));
			fontHeight = 16;
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"));
			hud.setFont(font.deriveFont(Font.PLAIN, fontHeight));
			hud.setBackground(Color.black);
			hud.setForeground(Color.white);
			hud.setFocusable(false);
			JPanel pnl = new JPanel();
			pnl.setBackground(Color.black);
			pnl.setForeground(Color.white);
			hud.setLayout(new BorderLayout());
			pnl.setLayout(new GridLayout(6,2));
			hud.add(pnl, BorderLayout.NORTH);
			addLabels(pnl);
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
		lblHP = new Label("100%");
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

	public static void setHP(int hp){
		lblHP.setText(hp + "%");
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
}
