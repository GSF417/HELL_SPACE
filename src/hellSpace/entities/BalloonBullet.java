package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class BalloonBullet implements Bullet {

	private static final int WIDTH = 4, HEIGHT = 4, TEAM = 1;
	private static final double SPEED = 3;
	
	private double x, y;
	private boolean dead, grazed;
	
	public static BalloonBullet create(double x, double y) {
		BalloonBullet b = new BalloonBullet();
		b.x = x;
		b.y = y;
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
		y += SPEED;
		collide(entityList);
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.red);
		g.fillOval((int) x, (int) y, WIDTH, HEIGHT);
	}

}
