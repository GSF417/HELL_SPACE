package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class GliderBullet implements Bullet {
	
	private static final int WIDTH = 6, HEIGHT = 2, TEAM = 1;
	private static final double SPEED = 4;
	
	private double x, y;
	private boolean dead, grazed, rightDir;
	
	public static GliderBullet create(double x, double y) {
		GliderBullet b = new GliderBullet();
		b.x = x;
		b.y = y;
		double targetX = Game.player.getX();
		if (targetX > b.x) b.rightDir = true;
		else b.rightDir = false;
		return b;
	}
	
	@Override
	public Bullet getLastBullet() {
		return null;
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	public void collide(List<Entity> entityList) {
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			CollisionType col = e.isColliding(x, y, WIDTH, HEIGHT, TEAM, grazed);
			if (col == CollisionType.NO_COLLISION || col == CollisionType.COLLISION_WITH_ALLY) {
				continue;
			}
			else if(col == CollisionType.COLLISION_WITH_BARRIER) {
				dead = true;
			}
			else if (col == CollisionType.COLLISION_WITH_ENEMY) {
				e.sufferDamage();
				dead = true;
			}
			else if (col == CollisionType.GRAZED) {
				grazed = true;
			}
		}
	}
	
	@Override
	public void tick(List<Entity> entityList) {
		if (x < 0 || x > Game.WIDTH ||
			y < 0 || y > Game.HEIGHT) {
			dead = true;
		}
		if (rightDir) x += SPEED;
		else x -= SPEED;
		collide(entityList);
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.green);
		g.fillRect((int) x, (int) y, WIDTH, HEIGHT);
	}
}
