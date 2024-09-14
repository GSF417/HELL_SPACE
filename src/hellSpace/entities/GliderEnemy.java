package hellSpace.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import hellSpace.main.Game;
import hellSpace.main.Soundtrack;
import hellSpace.main.Soundtrack.ClipCollection;

public class GliderEnemy implements Entity {

	private static final int WIDTH = 32, HEIGHT = 32, TEAM = 1, I_FRAMES = 125, MAX_HP = 4, BULLET_INTERVAL = 40, SCORE = 600;
	private static final double SPEED = 4;
	
	// Graphics
	private static final BufferedImage sprite = Game.acquireImage("glider_enemy.png");
	private static final BufferedImage spriteInv = Game.acquireImage("glider_enemy_inv.png");
	private ClipCollection explosion;
	
	private double x, y;
	private int HP, inv_frames;
	
	private boolean dead, goingRight, goingUp;
	private int bulletCooldown;
	private LinkedList<Bullet> bulletBuffer = new LinkedList<Bullet>();
	
	public static GliderEnemy create(double x, double y) {
		GliderEnemy e = new GliderEnemy();
		e.explosion = Soundtrack.load("explosion.wav", 1);
		e.x = x;
		e.y = y;
		e.HP = MAX_HP;
		if (x < Game.WIDTH / 2) {
			e.goingRight = true;
		}
		return e;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}
	
	@Override
	public int getScore() {
		return SCORE;
	}

	@Override
	public int getTeam() {
		return TEAM;
	}

	private void deathAttack() {
		double targetX = Game.player.getX() + Game.player.getWidth() / 2;
		double targetY = Game.player.getY() + Game.player.getHeight() / 2;
		double selfX = this.x + WIDTH / 2;
		double selfY = this.y + HEIGHT / 2;
		double dot = targetX*selfX + targetY*selfY;
		double det = targetX*selfY - targetY*selfX;
		double angle = Math.atan2(det, dot);
		if (angle < 0) angle += Math.PI;
		System.out.println(targetX+" "+targetY+" "+selfX+" "+selfY+" "+angle);
		double den = (Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2)) * (Math.sqrt(Math.pow(selfX, 2) + Math.pow(selfY, 2))) );
		double cos =  dot / den;
		System.out.println(cos);
		System.out.println(Math.acos(cos));
		for (int i = 0; i < 3; i++) {
			bulletBuffer.add(LaserBullet.create(selfX, selfY, angle + Math.PI * 2 / 3 * i, 1000));
		}
	}
	
	private void fireBullet() {
		bulletBuffer.add(GliderBullet.create(this.x + WIDTH / 2, this.y + HEIGHT));
	}
	
	@Override
	public void tick() {
		if (inv_frames > 0) {
			inv_frames--;
		}
		if (!goingUp) {
			y += SPEED;
		}
		else {
			y -= SPEED;
		}
		if (y >= Game.HEIGHT) {
			if (goingRight) {
				x += 100;
			}
			else {
				x -= 100;
			}
			goingUp = true;
		}
		else if (y <= 0) {
			if (goingRight) {
				x += 100;
			}
			else {
				x -= 100;
			}
			goingUp = false;
		}
		if (x >= Game.WIDTH) {
			x -= 100;
			goingRight = false;
		}
		else if (x <= 0) {
			x += 100;
			goingRight = true;
		}
		bulletCooldown--;
		if (bulletCooldown <= 0) {
			bulletCooldown = BULLET_INTERVAL;
			fireBullet();
		}
	}

	@Override
	public void render(Graphics2D g) {
		int height = HEIGHT;
		if (goingUp) {
			height = -height;
		}
		if (inv_frames > 0 && inv_frames % 3 == 0){
			g.drawImage(spriteInv, (int) x, (int) y, (int) x+WIDTH, (int) y+height, 0, 0, WIDTH, HEIGHT, null);
		}
		else {
			g.drawImage(sprite, (int) x, (int) y, (int) x+WIDTH, (int) y+height, 0, 0, WIDTH, HEIGHT, null);
		}
	}

	@Override
	public Bullet getLastBullet() {
		return bulletBuffer.pop();
	}

	@Override
	public boolean bufferStillHasBullets() {
		return (bulletBuffer.size() > 0) ? true : false;
	}

	@Override
	public void sufferDamage() {
		inv_frames = I_FRAMES;
		HP--;
		if (HP <= 0) {
			explosion.play();
			dead = true;
			deathAttack();
		}
	}

	@Override
	public boolean isDead() {
		return dead;
	}
	
	@Override
	public boolean isInvincible() {
		return inv_frames > 0;
	}

	@Override
	public CollisionType isColliding(double x, double y, int width, int height, int team, boolean grazed) {
		if (team == TEAM) {
			return CollisionType.NO_COLLISION;
		}
		if (x + width < this.x || x > this.x + WIDTH) {
			return CollisionType.NO_COLLISION;
		}
		if (y + height < this.y || y > this.y + HEIGHT) {
			return CollisionType.NO_COLLISION;
		}
		if (inv_frames > 0) {
			return CollisionType.NO_COLLISION;
		}
		return CollisionType.COLLISION_WITH_ENEMY;
	}
}
