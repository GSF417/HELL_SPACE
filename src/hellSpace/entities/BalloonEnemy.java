package hellSpace.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import hellSpace.main.Game;
import hellSpace.main.Soundtrack;
import hellSpace.main.Soundtrack.ClipCollection;

public class BalloonEnemy implements Entity {

	private static final int WIDTH = 48, HEIGHT = 32, TEAM = 1, MAX_HP = 4, BULLET_INTERVAL = 50,
							I_FRAMES = 75, SCORE = 200;
	public static final double SPEED = 1.25;
	
	// Graphics
	private static final BufferedImage sprite = Game.acquireImage("balloon_enemy.png");
	private static final BufferedImage spriteInv = Game.acquireImage("balloon_enemy_inv.png");
	private ClipCollection explosion;
	
	// Position
	private double x, y;
	private int HP, inv_frames;
	
	// Enemy control
	private int bulletCooldown;
	private boolean dead;
	private LinkedList<Bullet> bulletBuffer = new LinkedList<Bullet>();

	public static BalloonEnemy create(int x, int y) {
		BalloonEnemy e = new BalloonEnemy();
		e.explosion = Soundtrack.load("explosion.wav", 1);
		e.x = x;
		e.y = y;
		e.bulletCooldown = 100 + Game.rng.nextInt(90);
		e.dead = false;
		e.HP = MAX_HP;
		return e;
	}
	
	@Override
	public Bullet getLastBullet() {
		return bulletBuffer.pop();
	}
	
	private void deathAttack() {
		double targetX = Game.player.getX() + Game.player.getWidth() / 2;
		double targetY = Game.player.getY() + Game.player.getHeight() / 2;
		double selfX = this.x + WIDTH / 2;
		double selfY = this.y + HEIGHT / 2;
		double dot = targetX*selfX + targetY*selfY;
		double det = targetX*selfY - targetY*selfX;
		double angle = Math.atan2(det, dot);
		for (int i = 0; i < 8; i++) {
			bulletBuffer.add(BalloonAngularBullet.create(selfX, selfY, angle + Math.PI/2 + Math.PI / 4 * i));
		}
	}
	
	private void fireBullet() {
		bulletBuffer.add(BalloonBullet.create(this.x + WIDTH / 2, this.y + HEIGHT));
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
	public void tick() {
		if (inv_frames > 0) {
			inv_frames--;
		}
		y += SPEED;
		if (y >= Game.HEIGHT) {
			y = -100;
		}
		bulletCooldown--;
		if (bulletCooldown <= 0) {
			bulletCooldown = BULLET_INTERVAL;
			fireBullet();
		}
	}

	@Override
	public void render(Graphics2D g) {
		if (inv_frames > 0 && inv_frames % 3 == 0){
			g.drawImage(spriteInv, (int) x, (int) y, WIDTH, HEIGHT, null);
		}
		else {
			g.drawImage(sprite, (int) x, (int) y, WIDTH, HEIGHT, null);
		}
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

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}
	
	public int getScore() {
		return SCORE;
	}

	@Override
	public int getTeam() {
		return TEAM;
	}

	@Override
	public boolean bufferStillHasBullets() {
		return (bulletBuffer.size() > 0) ? true : false;
	}

}
