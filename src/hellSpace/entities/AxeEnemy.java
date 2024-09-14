package hellSpace.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import hellSpace.main.Game;
import hellSpace.main.Soundtrack;
import hellSpace.main.Soundtrack.ClipCollection;

public class AxeEnemy implements Entity {

	private static final int WIDTH = 48, HEIGHT = 32, TEAM = 1, MAX_HP = 8, BULLET_INTERVAL = 1200,
							I_FRAMES = 175, SCORE = 800;
	public static final double SPEED = 2.25;
	
	// Graphics
	private static final BufferedImage sprite = Game.acquireImage("axe_enemy.png");
	private static final BufferedImage spriteInv = Game.acquireImage("axe_enemy_inv.png");
	private ClipCollection explosion;
	
	// Position
	private double x, y;
	private double targetX, targetY;
	private int HP, inv_frames, bullet_delay;
	
	// Enemy control
	private boolean dead;
	private LinkedList<Bullet> bulletBuffer = new LinkedList<Bullet>();

	public static AxeEnemy create(int x, int y) {
		AxeEnemy e = new AxeEnemy();
		e.explosion = Soundtrack.load("explosion.wav", 1);
		e.x = x;
		e.y = y;
		e.dead = false;
		e.HP = MAX_HP;
		e.bullet_delay = 0;
		e.retarget();
		return e;
	}
	
	@Override
	public Bullet getLastBullet() {
		return bulletBuffer.pop();
	}
	
	private void deathAttack() {
		for (int i = 0; i < 2; i++) {
			bulletBuffer.add(LaserBullet.create(this.x + WIDTH / 2, this.y + HEIGHT, 0 + Math.PI * i, 1500));
		}
	}
	
	private void fireBullet() {
		for (int i = 0; i < 4; i++) {
			bulletBuffer.add(LaserBullet.create(this.x + WIDTH / 2, this.y + HEIGHT, 0 + Math.PI/2 * i, 1500));
		}
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
	
	private void retarget() {
		targetX = Game.rng.nextInt(Game.WIDTH - 250) + 200;
		targetY = Game.rng.nextInt(Game.HEIGHT - 150) + 100;
	}

	@Override
	public void tick() {
		if (bullet_delay < BULLET_INTERVAL) {
			bullet_delay++;
		}
		if (inv_frames > 0) {
			inv_frames--;
		}
		if (x < targetX - 1) {
			x += SPEED;
		}
		else if (x > targetX + 1) {
			x -= SPEED;
		}
		else if (y < targetY - 1) {
			y += SPEED;
		}
		else if (y > targetY + 1) {
			y -= SPEED;
		}
		else {
			if (bullet_delay > BULLET_INTERVAL / 2) {
				fireBullet();
				bullet_delay -= BULLET_INTERVAL / 2;
			}
			retarget();
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
