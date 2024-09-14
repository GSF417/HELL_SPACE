package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class VoidBullet implements Bullet {

	private static int WIDTH = 2, HEIGHT = 2, TEAM = 1, LASTS_FOR = 200;
	private static final double SPEED = 1.5;
	
	private int duration;
	private double x, y, angle;
	private boolean dead, grazed;
	
	public static VoidBullet create(double x, double y, double angle) {
		VoidBullet b = new VoidBullet();
		b.x = x;
		b.y = y;
		b.angle = angle;
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
		duration++;
		if (duration > LASTS_FOR) {
			dead = true;
			return;
		}
		if (x < 0 || x > Game.WIDTH ||
			y < 0 || y > Game.HEIGHT) {
			dead = true;
		}
		y += SPEED * Math.sin(angle);
		x += SPEED * Math.cos(angle);
		collide(entityList);
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.magenta);
		g.fillOval((int) x, (int) y, WIDTH, HEIGHT);
	}
}
