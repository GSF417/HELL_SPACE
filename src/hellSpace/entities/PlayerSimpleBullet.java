package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class PlayerSimpleBullet implements Bullet{

	private static final int WIDTH = 2, HEIGHT = 3;
	private static final double SPEED = 8;
	
	private double x, y;
	private double angle;
	private boolean dead;
	
	public static PlayerSimpleBullet create(double x, double y, double angle) {
		PlayerSimpleBullet b = new PlayerSimpleBullet();
		b.x = x;
		b.y = y;
		b.angle = angle;
		return b;
	}

	public void collide(List<Entity> entityList) {
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			CollisionType col = e.isColliding(x, y, WIDTH, HEIGHT, 0, false);
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
		}
	}
	
	@Override
	public Bullet getLastBullet() {
		return null;
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public void tick(List<Entity> entityList) {
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
		g.setColor(Color.cyan);
		g.fillRect((int) x, (int) y, WIDTH, HEIGHT);
	}
}
