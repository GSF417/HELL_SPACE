package hellSpace.entities;

import java.awt.Graphics2D;
import java.util.List;

public interface Bullet {

	public Bullet getLastBullet();
	
	public boolean isDead();
	
	public void tick(List<Entity> entityList);
	
	public void render(Graphics2D g);
}
