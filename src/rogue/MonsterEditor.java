package rogue;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import pazi.features.IFeature;

/**
 * Hilfsprogramm zum Bearbeiten von Monsterdateien.
 * 
 * Dateiformat: Je Zeile ein Monster 
 * Name;Zeichen;Fraktion;HP;MIN_DAMAGE;MAX_DAMAGE;Features(Komma separiert,Argumente in Klammern)
 * 
 * @author niklaswr
 * 
 */
public class MonsterEditor {

	private static final int FIELDS = 7;
	private static TextField txtPath, txtName, txtSymbol, txtFraktion, txtHP, txtMinD, txtMaxD;
	private static Label lblDisplay;
	private static TextComponent[] fields;
	private static List lstFeatures;
	private static HashMap<String, Integer> features = new HashMap();
	private static Button btnBrowse, btnLeft, btnRight, btnSave, btnNewMonster;
	private static ArrayList<String> monster = new ArrayList<String>();
	private static int current = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame("Monster-Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		Panel pnlTop = new Panel();
		Panel pnlBottom = new Panel();
		frame.add(pnlTop);
		frame.add(pnlBottom);
		frame.setMinimumSize(new Dimension(0, 400));

		// Erzeuge oberes Panel
		pnlTop.setLayout(new GridLayout(3, 3));
		pnlTop.add(new Label("Pfad:"));
		txtPath = new TextField();
		pnlTop.add(txtPath);
		btnBrowse = new Button("Durchsuchen");
		btnBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
					txtPath.setText(chooser.getSelectedFile().getAbsolutePath());
				load();
				updateDisplay();
			}
		});
		pnlTop.add(btnBrowse);
		btnLeft = new Button("<");
		btnLeft.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				createMonster();
				current--;				
				loadMonster();
				updateDisplay();
			}
		});
		pnlTop.add(btnLeft);
		btnRight = new Button(">");
		btnRight.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				createMonster();
				current++;
				loadMonster();
				updateDisplay();
			}
		});
		pnlTop.add(btnRight);
		btnSave = new Button("Speichern");
		pnlTop.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				createMonster();
				BufferedWriter out;
				try {
					out = new BufferedWriter(new FileWriter(txtPath.getText()));
					for (String s : monster){
							out.write(s);
							out.newLine();
					}
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}	
			}
		});
		lblDisplay = new Label();
		updateDisplay();
		pnlTop.add(lblDisplay);
		btnNewMonster = new Button("Neues Monster");
		btnNewMonster.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newMonster();
				current = monster.size() - 1;
			}
		});
		pnlTop.add(btnNewMonster);
		pnlTop.setMinimumSize(new Dimension(0, 60));
		pnlTop.setMaximumSize(new Dimension(1000000, 60));

		// Erzeuge unteres Panel
		pnlBottom.setLayout(new GridLayout(3, 4));
		pnlBottom.add(new Label("Name"));
		txtName = new TextField();
		pnlBottom.add(txtName);
		pnlBottom.add(new Label("Symbol"));
		txtSymbol = new TextField();
		pnlBottom.add(txtSymbol);
		pnlBottom.add(new Label("Fraktion"));
		txtFraktion = new TextField();
		pnlBottom.add(txtFraktion);
		pnlBottom.add(new Label("Hitpoints"));
		txtHP = new TextField();
		pnlBottom.add(txtHP);
		pnlBottom.add(new Label("Minimalschaden"));
		txtMinD = new TextField();
		pnlBottom.add(txtMinD);
		pnlBottom.add(new Label("Maximalschaden"));
		txtMaxD = new TextField();
		pnlBottom.add(txtMaxD);
		fields = new TextComponent[]{txtName, txtSymbol, txtFraktion, txtHP, txtMinD, txtMaxD};
		pnlBottom.setMinimumSize(new Dimension(0, 300));
		pnlBottom.setMaximumSize(new Dimension(100000, 300));
		lstFeatures = new List();
		addFeaturesToList();
		frame.add(lstFeatures);

		frame.setSize(800, 400);
		frame.setVisible(true);
	}

	protected static void createMonster() {
		String s = "";
		for(TextComponent comp : fields)
			s += comp.getText() + ";";
		for (String feat : lstFeatures.getSelectedItems())
			s += feat + ",";
		s = s.substring(0,s.length()-1);
		if (monster.size() == 0)
			monster.add(s);
		else
			monster.set(current, s);
	}
	
	protected static void updateDisplay(){
		lblDisplay.setText((current+1) + "/" + monster.size());
	}

	protected static void load() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(txtPath.getText())));
			while(reader.ready())
				monster.add(reader.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		current = monster.size()-1;
		loadMonster();
	}

	private static void loadMonster() {
		if(monster.size() == 0)
			newMonster();
		if(current >= monster.size())
			current = monster.size() - 1;
		if(current < 0)
			current = 0;
		String[] lst = monster.get(current).split(";");
		if(lst.length < FIELDS - 1) {
			monster.remove(current);
			loadMonster();
			return;
		}
		
		
		// deselect all items of list
		for (int i = 0; i < lstFeatures.getItemCount(); i++)
			lstFeatures.deselect(i);

		// select Features of current Monster in List
		if (lst.length == FIELDS){
			String[] tempFeatures = lst[FIELDS-1].split(",");
			for (String feat : tempFeatures)
				lstFeatures.select(features.get(feat));
		}
		
		for(int i = 0; i < FIELDS - 1; i++)
			fields[i].setText(lst[i]);
	}
	
	private static void newMonster() {
		monster.add("Dummy;X;Zombies;100;10;30;");
	}

	private static void addFeaturesToList() {
		lstFeatures.setMultipleMode(true);
		try {
			int i = 0;
			for(Class c : getClasses("pazi.features"))
				if(IFeature.class.isAssignableFrom(c) && c != IFeature.class){
					lstFeatures.add(c.getName().substring(14));
					features.put(c.getName().substring(14),i++);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//TODO inspired by http://dzone.com/snippets/get-all-classes-within-package
	private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		ArrayList<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements())
			dirs.add(new File(resources.nextElement().getFile()));
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs)
			classes.addAll(findClasses(directory, packageName));
		return classes.toArray(new Class[classes.size()]);
	}
	
	private static ArrayList<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		ArrayList<Class> classes = new ArrayList<Class>();
		if (!directory.exists())
			return classes;
		File[] files = directory.listFiles();
		for (File file : files)
			if (file.isDirectory()) 
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			else if (file.getName().endsWith(".class"))
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
		return classes;
	}

}
