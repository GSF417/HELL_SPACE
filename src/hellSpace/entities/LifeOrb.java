package hellSpace.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import hellSpace.main.Game;

public class LifeOrb implements Powerup {

	private static final int WIDTH = 16, HEIGHT = 16;
	
	private double x, y;
	private boolean picked = false;
	
	// Graphics
	private static final BufferedImage sprite = Game.acquireImage("lifepack.png");
	
	public static LifeOrb create(double x, double y) {
		LifeOrb p = new LifeOrb();
		if (x > Game.WIDTH - 50) x = Game.WIDTH - 50;
		if (y > Game.HEIGHT - 50) y = Game.HEIGHT - 50;
		if (x < 50) x = 50;
		if (y < 50) y = 50;
		p.x = x;
		p.y = y;
		return p;
	}
	
	@Override
	public boolean wasPicked() {
		return picked;
	}
	
	private void checkIfPlayer() {
		Player e = Game.player;
		CollisionType col = e.powerupPickup(x, y, WIDTH, HEIGHT);
		if (col == CollisionType.COLLISION_WITH_ENEMY) {
			e.recoverHP(2);
			picked = true;
		}
	}

	public void tick() {
		checkIfPlayer();
	}
	
	public void render(Graphics2D g) {
		g.drawImage(sprite, (int) x, (int) y, null);
	}
	
}
