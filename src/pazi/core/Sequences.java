package pazi.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.newdawn.slick.util.ResourceLoader;

public class Sequences {
	protected static HashMap<String, String> sequences;
	
	/**
	 * Lesen aus Datei. Format: schluesselwort:(x1,y1)->(x2,y2)->...
	 */
	protected static void init() {
		BufferedReader reader;
		sequences = new HashMap<String, String>();
		try {
			reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream("res/sequences"), "UTF-8"));
			while(reader.ready())
				addSequence(reader.readLine());
			reader.close();
		} catch (IOException e) { }
	}
		
	private static void addSequence(String readLine) {
		if(readLine == null || !readLine.contains(":") || readLine.startsWith("//"))
			return;
		sequences.put(readLine.substring(0, readLine.indexOf(':')), readLine.substring(readLine.indexOf(':') + 1));
	}

	public static String getSequence(String keyword) {
		if(sequences == null)
			init();
		return sequences.get(keyword);
	}

}
