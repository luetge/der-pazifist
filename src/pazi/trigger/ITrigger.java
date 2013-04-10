package pazi.trigger;

import pazi.core.World;

public interface ITrigger {
	public void setActivated(boolean activated);
	public boolean activated();
	void tick(World world);
}
