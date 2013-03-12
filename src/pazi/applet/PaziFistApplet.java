package pazi.applet;

import java.applet.Applet;
import java.awt.HeadlessException;

import rogue.Rogue;

public class PaziFistApplet extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PaziFistApplet() throws HeadlessException {
		try {
        	Rogue rogue = new Rogue ();
        	rogue.run ();
        	rogue.finish ();
        
        	System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
