package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class PlayerHomingBullet implements Bullet {

	private static final int WIDTH = 5, HEIGHT = 5, MAX_DURATION = 250;
	private static final double SPEED = 8, ACCEL = 1.25;
	
	private double x, y, speedX, speedY;
	private int duration = MAX_DURATION;
	private boolean dead;
	
	private Entity target;
	
	public static PlayerHomingBullet create(double x, double y, double angle) {
		PlayerHomingBullet b = new PlayerHomingBullet();
		b.x = x;
		b.y = y;
		b.speedX = SPEED * Math.cos(angle);
		b.speedY = SPEED * Math.sin(angle);
		return b;
	}
	
	private void retarget(List<Entity> entityList) {
		double lowestDist = -1;
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			if (e.isInvincible()) continue;
			double distToE = Math.sqrt(Math.pow((e.getX() - this.x), 2) + Math.pow((e.getY() - this.y), 2));
			if (e.getTeam() != 0) {
				if (lowestDist == -1) {
					lowestDist = distToE;
					target = e;
				}
				else if (lowestDist > distToE){
					lowestDist = distToE;
					target = e;
				}
			}
		}
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
	
	private void homingEffect() {
		if (target.getX() > this.x) {
			this.speedX += ACCEL;
		}
		else {
			this.speedX -= ACCEL;
		}
		if (speedX > SPEED) {
			speedX = SPEED;
		}
		else if (speedX < -SPEED) {
			speedX = -SPEED;
		}
		
		if (target.getY() > this.y) {
			this.speedY += ACCEL;
		}
		else {
			this.speedY -= ACCEL;
		}
		if (speedY > SPEED) {
			speedY = SPEED;
		}
		else if (speedY < -SPEED) {
			speedY = -SPEED;
		}
	}
	
	@Override
	public void tick(List<Entity> entityList) {
		duration--;
		if (duration <= 0) {
			dead = true;
			return;
		}
		this.x += speedX;
		this.y += speedY;
		if (target == null) {
			retarget(entityList);
			if (target == null) return;
		}
		if (target.isDead() || target.isInvincible()) {
			target = null;
			retarget(entityList);
			if (target == null) return;
		}
		
		if (x < 0 || x > Game.WIDTH ||
			y < 0 || y > Game.HEIGHT + 100) {
			dead = true;
		}
		collide(entityList);
		homingEffect();
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.cyan);
		g.fillOval((int) x, (int) y, WIDTH, HEIGHT);
	}
}
