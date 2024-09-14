package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class LaserBullet implements Bullet {

	private static final int FRAMES_TILL_ACTIVE = 200, LASTS_FOR = 600;
	
	private int duration;
	private double x, y, angle, range;
	private boolean dead, grazed, collided;
	private Active status;
	
	private enum Active {
		INACTIVE,
		ACTIVE,
		USED
	}

	public static LaserBullet create(double x, double y, double angle, double range) {
		LaserBullet b = new LaserBullet();
		b.x = x;
		b.y = y;
		b.angle = angle;
		b.range = range;
		b.status = Active.INACTIVE;
		b.duration = 0;
		return b;
	}
	
	@Override
	public Bullet getLastBullet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDead() {
		return dead;
	}
	
	@Override
	public void tick(List<Entity> entityList) {
		duration++;
		if (duration > LASTS_FOR) {
			dead = true;
			return;
		}
		if (duration > FRAMES_TILL_ACTIVE && status != Active.USED) {
			status = Active.ACTIVE;
		}
		if (!collided && status == Active.ACTIVE) {
			Player p = Game.player;
			CollisionType col = p.lineCollision(x, y, x + range * Math.cos(angle), y + range * Math.sin(angle), grazed);
			if (col == CollisionType.COLLISION_WITH_ENEMY) {
				p.sufferDamage();
				status = Active.USED;
			}
			else if (col == CollisionType.GRAZED) {
				grazed = true;
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		if (status == Active.ACTIVE) {
			g.setColor(Color.GREEN);
		}
		else {
			g.setColor(Color.GRAY);
		}
		g.fillOval((int) x - 3, (int) y - 3, 6, 6);
		g.drawLine((int) x, (int) y, (int) (x + range * Math.cos(angle) + Math.PI/2), (int) (y + range * Math.sin(angle) + Math.PI/2));
	}

}
