package pazi.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.newdawn.slick.util.ResourceLoader;

import pazi.creature.Creature;
import pazi.creature.Monster;
import pazi.items.Item;
import pazi.weapons.IMeleeWeapon;
import pazi.weapons.IRangedCombatWeapon;


public class HUD {
	private static class MyLabel extends JLabel {
		private static final long serialVersionUID = -4347093470689854347L;

		MyLabel(String str)
		{
			super(str);
			setForeground(Color.white);
		}
	}
	protected static JLabel lblHP, lblFaith, lblRage, lblLevel, lblGold, lblXP;
	protected static JTextArea taMonsters, taEquip;
	protected static int fontHeight;
	protected static JFrame hud;
	
	public static void init() {
		try {
			hud = new JFrame("HUD");
			hud.setPreferredSize(new Dimension(200,100));
			hud.setMaximumSize(new Dimension(200,100000));
			hud.setMinimumSize(new Dimension(200,0));
			hud.setSize(new Dimension(200,100));
			fontHeight = 16;
			Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/DejaVuSansMono.ttf"));
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
			taMonsters = new JTextArea();
			JScrollPane scrollpane = new JScrollPane(taMonsters);
			taMonsters.setBackground(Color.black);
			taMonsters.setForeground(Color.white);
			scrollpane.getViewport().setBackground(Color.black);
			scrollpane.setBorder(null);
			scrollpane.setForeground(Color.black);
			hud.add(scrollpane, BorderLayout.CENTER);
			hud.setFocusable(false);
			hud.setFocusableWindowState(false);
			taEquip = new JTextArea();
			taEquip.setBackground(Color.black);
			taEquip.setForeground(Color.white);
			hud.add(taEquip, BorderLayout.SOUTH);
			hud.setIconImage(View.getIcon());
			hud.setVisible(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static JFrame getFrame(){
		return hud;
	}
	
	public static void setVisible(boolean visible){
		hud.setVisible(visible);
	}
	
	private static void addLabels(JPanel pnl) {
		lblHP = new MyLabel("100/100");
		lblFaith = new MyLabel("100%");
		lblRage = new MyLabel("?%");
		lblXP = new MyLabel("0");
		lblLevel = new MyLabel("1");
		lblGold = new MyLabel("0");
		pnl.add(new MyLabel("HP:"));
		pnl.add(lblHP);
		pnl.add(new MyLabel("Faith:"));
		pnl.add(lblFaith);
		pnl.add(new MyLabel("Rage:"));
		pnl.add(lblRage);
		pnl.add(new MyLabel("Erfahrung:"));
		pnl.add(lblXP);	
		pnl.add(new MyLabel("Level:"));
		pnl.add(lblLevel);
		pnl.add(new MyLabel("Gold:"));
		pnl.add(lblGold);
	}

	public static void setHP(int hp,int maxHp){
		lblHP.setText(hp + "/" + maxHp);
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

	public static void setXP(int xp){
		lblXP.setText("" + xp);
	}

	public static void setGold(int amount){
		lblGold.setText("" +amount);
	}
	
	public static void setMonsters(Iterable<Monster> monsters) {
		taMonsters.setText("");
		for(Monster monster: monsters)
			taMonsters.append("\n" + monster.getName() + " (" + monster.getHP() + " HP)");
	}
	
	public static void setWeaponLbl(IMeleeWeapon melee, IRangedCombatWeapon rcWeapon) {
		String meleeName, rcWeaponName;
		if (melee == null)
			meleeName = "Keine Nahkampfwaffe";
		else
			meleeName = melee.getName();
		if (rcWeapon == null)
			rcWeaponName = "Keine Fernkampfwaffe";
		else
			rcWeaponName = rcWeapon.getName();
		taEquip.setText("Deine Waffenwahl: " + "\n" + meleeName + "\n" + rcWeaponName);
	}
}
