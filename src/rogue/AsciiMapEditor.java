package rogue;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import jade.gen.map.AsciiMap;
import jade.ui.View;

public class AsciiMapEditor implements DocumentListener, WindowListener {
	
	private JTextArea textarea;
	private JFrame frame;
	
	private boolean changed;
	
	void setChanged (boolean changed)
	{
		this.changed = changed;
		if (changed)
			frame.setTitle("AsciiMapEditor*");
		else
			frame.setTitle("AsciiMapEditor");
	}
	
	boolean getChanged ()
	{
		return changed;
	}
	
	public void load ()
	{
		JFileChooser chooser = new JFileChooser("res/");
		int returnval = chooser.showOpenDialog(frame);
		if (returnval == JFileChooser.APPROVE_OPTION)
		{
			try {
			BufferedReader reader = new BufferedReader (new FileReader (chooser.getSelectedFile()));
			String content = new String();
			String str;
			while ((str = reader.readLine ()) != null)
			{
				content += str + "\n";
			}
			reader.close();
			textarea.setText(content);
			setChanged (false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void save ()
	{
		JFileChooser chooser = new JFileChooser("res/");
		int returnval = chooser.showSaveDialog(frame);
		if (returnval == JFileChooser.APPROVE_OPTION)
		{
			try {
			if (chooser.getSelectedFile().exists())
			{
				int n = JOptionPane.showConfirmDialog(frame, "Die gewählte Datei existert bereits. Wollen Sie die Datei wirklich überschreiben?",
						"Wirklich überschreiben?", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION)
					return;
			}
				
			BufferedWriter writer = new BufferedWriter (new FileWriter (chooser.getSelectedFile()));
			writer.write(textarea.getText());
			writer.close();
			setChanged (false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void quit ()
	{
		if (!getChanged())
		{
			System.exit(0);
		}
		else
		{
			int n = JOptionPane.showConfirmDialog(frame, "Der Inhalt wurde geändert, aber nicht gespeichert. Wirklich beenden?",
					"Wirklich beenden?", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION)
				System.exit(0);
		}
	}

	public AsciiMapEditor ()
	{
		View view;
		View.create ("AsciiMapEditor view", 128, 48, 10, 16);
		view = View.get();
		view.loadTiles("res/tiles");
		textarea = new JTextArea();
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"));
	        textarea.setFont(font.deriveFont(Font.PLAIN, view.tileHeight()));
		} catch(IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}
		
		frame = new JFrame("AsciiMapEditor");
		frame.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);
		frame.add(textarea);
		setChanged (false);

		view.getFrame().setFocusableWindowState(false);
		view.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		MenuBar menubar = new MenuBar ();
		Menu menu = new Menu ("File");
		menubar.add (menu);
		frame.setMenuBar (menubar);
		MenuItem menuitem = new MenuItem ("Laden");
		menu.add(menuitem);
		menuitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load ();
			}
		});
		menuitem = new MenuItem ("Speichern");
		menuitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save ();
			}
		});
		menu.add(menuitem);
		menu.addSeparator();
		menuitem = new MenuItem ("Beenden");
		menuitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				quit ();
			}
		});
		menu.add(menuitem);

		frame.setSize(800,400);
		frame.setVisible(true);

		textarea.getDocument().addDocumentListener(this);
	}
	
	void run()
	{
		while (frame.isEnabled())
		{
			updateTermPanel();
		}
	}
	
	public void changedUpdate(DocumentEvent e)
	{
		setChanged (true);
	}
	
	public void removeUpdate(DocumentEvent e)
	{
		setChanged (true);
	}
	
	public void insertUpdate(DocumentEvent e)
	{
		setChanged (true);
	}
	
	public void updateTermPanel()
	{
		View view = View.get();
		try {
		String content;
		content = textarea.getDocument().getText(0, textarea.getDocument().getLength());
		AsciiMap asciimap;
		try {
		 asciimap = AsciiMap.createFromString (content);
		} catch (Exception e) {
			view.clear();
			view.drawString(0, 0, 0, "Invalid input", Color.red);
			view.update();
			return;
		}
		view.clear();
		asciimap.render (view, 0, 0);
		view.update();
        } catch(BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		AsciiMapEditor asciimapeditor = new AsciiMapEditor ();
		
		asciimapeditor.run();

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {

		// TODO Auto-generated method stub
		quit();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
