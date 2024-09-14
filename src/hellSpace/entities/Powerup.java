package hellSpace.entities;

import java.awt.Graphics2D;

public interface Powerup {

	public boolean wasPicked();
	
	public void tick();
	
	public void render(Graphics2D g);
}
