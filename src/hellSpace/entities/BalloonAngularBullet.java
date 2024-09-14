package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class BalloonAngularBullet implements Bullet {

	private static int WIDTH = 12, HEIGHT = 12, TEAM = 1;
	private static double SPEED = 4;
	
	private double x, y, angle;
	private boolean dead, grazed;
	
	public static BalloonAngularBullet create(double x, double y, double angle) {
		BalloonAngularBullet b = new BalloonAngularBullet();
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
		g.setColor(Color.red);
		g.fillOval((int) x, (int) y, WIDTH, HEIGHT);
		g.setColor(Color.yellow);
		g.fillOval((int) (x + WIDTH / 4), (int) (y + HEIGHT / 4), WIDTH / 2, HEIGHT / 2);
	}

}
