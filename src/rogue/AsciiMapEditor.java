package rogue;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

import jade.ui.TiledTermPanel;
import jade.gen.map.AsciiMap;

public class AsciiMapEditor implements DocumentListener {
	
	private TiledTermPanel term;
	private JTextArea textarea;
	
	public AsciiMapEditor ()
	{
		term = new TiledTermPanel(256,256,TiledTermPanel.DEFAULT_TILESIZE);
		textarea = new JTextArea();
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"));
	        textarea.setFont(font.deriveFont(Font.PLAIN, term.tileHeight()));
		} catch(IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}
		
		JScrollPane tascroll = new JScrollPane(textarea);
		
		JScrollPane termscroll = new JScrollPane(term.panel());
		termscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		termscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JFrame frame = new JFrame("AsciiMapEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		c.setLayout (new GridLayout(0,2));
		c.add (tascroll);
		c.add (termscroll);
		frame.setSize(800,400);
		frame.setVisible(true);
		
		textarea.getDocument().addDocumentListener(this);
	}
	
	public void changedUpdate(DocumentEvent e)
	{
		updateTermPanel();
	}
	
	public void removeUpdate(DocumentEvent e)
	{
		updateTermPanel();
	}
	
	public void insertUpdate(DocumentEvent e)
	{
		updateTermPanel();
	}
	
	public void updateTermPanel()
	{
		try {
		String content;
		content = textarea.getDocument().getText(0, textarea.getDocument().getLength());
		AsciiMap asciimap;
		try {
		 asciimap = AsciiMap.createFromString (content);
		} catch (Exception e) {
	        term.clearBuffer();
	        term.bufferString(0,  0, "Invalid input");
	        term.refreshScreen();
			return;
		}
        term.clearBuffer();
		asciimap.render (term);
        term.refreshScreen();
        } catch(BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AsciiMapEditor asciimapeditor = new AsciiMapEditor ();
		

	}

}
