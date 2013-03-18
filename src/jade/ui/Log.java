package jade.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.ScrollPane;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class Log {
	protected static JPanel logPanel;
	protected static JFrame logFrame;
	protected static int fontHeight;
	protected static ScrollPane scrollpane;
	protected static JTextPane lblMessages;
	
	public static JPanel getLogPanel(){
		if(logPanel == null)
			init();
		return logPanel;
	}
	
	public static Frame getLogFrame(){
		if(logFrame == null)
			init();
		return logFrame;
	}

	private static void init() {
		try {
			logPanel = new JPanel();
			fontHeight = 16;
			logPanel.setPreferredSize(new Dimension(1000,fontHeight*10-2));
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"));
			logPanel.setFont(font.deriveFont(Font.PLAIN, fontHeight));
			logPanel.setBackground(Color.black);
			logPanel.setForeground(Color.white);
			logPanel.setFocusable(false);
			logPanel.setLayout(new BorderLayout());
			lblMessages = new JTextPane();
			scrollpane = new ScrollPane();
			lblMessages.setBackground(Color.black);
			lblMessages.setForeground(Color.white);
			scrollpane.add(lblMessages);
			logPanel.add(scrollpane, BorderLayout.CENTER);
			logPanel.setVisible(true);
			logFrame = new JFrame("Log");
			logFrame.add(logPanel);
			logPanel.setForeground(Color.white);
			logFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			logFrame.pack();
			logFrame.setFocusable(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void showLogFrame(boolean show){
		getLogFrame().setFocusableWindowState(false);
		getLogFrame().setVisible(show);
		getLogFrame().setFocusable(true);
	}
	
	public static void addMessage(String text){
		try {
			lblMessages.getDocument().insertString(lblMessages.getDocument().getLength(), text + "\n", null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scrollpane.setScrollPosition(scrollpane.getScrollPosition().x, Integer.MAX_VALUE);
	}
}
