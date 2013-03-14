package jade.core;

public class Dialog {
	public Dialog () {
		
	}
	public void tick (World world)
	{
		System.out.println("Dialog!");
		world.setActiveDialog(null);
	}

}
