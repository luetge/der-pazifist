package pazi;

import jade.ui.Terminal;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Display {
	
    protected static final Map<Character, Color> COLORS;  
    
    static {  
      Map<Character, Color> m = new HashMap<Character, Color>();  
      m.put('r', Color.red);    
      m.put('w',Color.white);    
      COLORS = Collections.unmodifiableMap(m);  
    }  
    
    public static void printStartScreen(Terminal term){
    	try {
			printFileToScreen(term, "res/start");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void printEndScreen(Terminal term){
    	try {
			printFileToScreen(term, "res/end");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public static void printFileToScreen(Terminal term, String sPath) throws IOException{
		synchronized(term){
			term.clearBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(sPath));
			String sStart = reader.readLine();
			int i, j=1;
			Color c = Color.white;
			while(sStart != null){
				int iCorrection = 0;
				for(i=0;i<sStart.length();i++){
					if(sStart.charAt(i)=='¥'){
						c = COLORS.get(sStart.charAt(i+1));
						iCorrection += 2;
						i++;
					} else		
						term.bufferChar(i-iCorrection, j, ColoredChar.create(sStart.charAt(i), c));
				}
				j++;
				sStart = reader.readLine();
			}
			reader.close();
			term.refreshScreen();
		}
	}

}
