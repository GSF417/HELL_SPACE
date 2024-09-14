package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class PlayerAngularBullet implements Bullet {

	private static final int WIDTH = 2, HEIGHT = 2, SHIFT_INTERVAL = 5;
	private static final double SPEED = 8;
	
	private double x, y, angle, angleShift;
	private double angleDelta;
	private int untilNextShift;
	private boolean dead;
	
	public static PlayerAngularBullet create(double x, double y, double angle, double angleShift) {
		PlayerAngularBullet b = new PlayerAngularBullet();
		b.x = x;
		b.y = y;
		b.angle = angle;
		b.angleShift = angleShift;
		b.angleDelta = Math.abs(angle - angleShift);
		if (angleShift < angle) {
			b.angleDelta = -b.angleDelta;
		}
		b.angleDelta = b.angleDelta / 10;
		b.untilNextShift = SHIFT_INTERVAL;
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
		untilNextShift--;
		if (untilNextShift <= 0 && Math.abs(angle - angleShift) > 0.2) {
			untilNextShift = SHIFT_INTERVAL;
			angle += angleDelta;
		}
		if (x < -150 || x > Game.WIDTH + 150 ||
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
