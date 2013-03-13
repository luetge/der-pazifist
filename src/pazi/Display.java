package pazi;

import jade.gen.map.AsciiMap;
import jade.ui.TiledTermPanel;

public class Display {
	
    public static void printStartScreen(TiledTermPanel term){
    	AsciiMap startscreen = new AsciiMap ("res/start");
    	term.clearBuffer();
    	startscreen.render(term,  0, 0);
    	term.refreshScreen();
    }
    
    public static void printEndScreen(TiledTermPanel term){
    	AsciiMap startscreen = new AsciiMap ("res/end");
    	term.clearBuffer();
    	startscreen.render(term,  0, 0);
    	term.refreshScreen();
    }
}
