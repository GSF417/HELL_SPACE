package hellSpace.entities;

import java.awt.Graphics2D;

public interface Entity {

		public double getX();
		
		public double getY();
		
		public int getScore();
		
		public int getTeam();
	
		public void tick();
		
		public void render(Graphics2D g);
		
		public Bullet getLastBullet();
		
		public boolean bufferStillHasBullets();
		
		public void sufferDamage();
		
		public boolean isDead();
		
		public boolean isInvincible();
		
		public CollisionType isColliding(double x, double y, int width, int height, int team, boolean grazed);
}
