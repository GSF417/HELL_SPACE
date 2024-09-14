package hellSpace.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import hellSpace.main.Game;
import hellSpace.main.Soundtrack;
import hellSpace.main.Soundtrack.ClipCollection;

public class VoidEnemy implements Entity{
	
	private static int WIDTH = 32, HEIGHT = 32, TEAM = 1, MAX_HP = 3, I_FRAMES = 400, SCORE = 400, BULLET_INTERVAL = 120;
	private static double SPEED = 2;
	
	// Graphics
	private static final BufferedImage sprite = Game.acquireImage("voidroach_enemy.png");
	private static final BufferedImage spriteInv = Game.acquireImage("voidroach_enemy_inv.png");
	private ClipCollection explosion;
	
	private int x, y;
	private int HP, inv_frames;
	
	// Enemy control
	private int bulletCooldown;
	private boolean dead;
	private LinkedList<Bullet> bulletBuffer = new LinkedList<Bullet>();

	public static VoidEnemy create(int x, int y) {
		VoidEnemy e = new VoidEnemy();
		e.explosion = Soundtrack.load("explosion.wav", 1);
		e.x = x;
		e.y = y;
		e.HP = MAX_HP;
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

	@Override
	public Bullet getLastBullet() {
		return bulletBuffer.pop();
	}
	
	private void deathAttack() {
		bulletBuffer.add(VoidField.create(this.x + WIDTH / 2 - VoidField.WIDTH / 2,
											this.y + HEIGHT / 2 - VoidField.HEIGHT / 2));
	}
	
	private void fireBullet() {
		for (int i = 0; i < 8; i++) {
			bulletBuffer.add(VoidBullet.create(x + WIDTH / 2, y + HEIGHT / 2, Math.PI/2 + Math.PI / 4 * i));
		}
	}
	
	@Override
	public void tick() {
		if (inv_frames > 0) {
			inv_frames--;
		}

		double targetX = Game.player.getX() + Game.player.getWidth() / 2;
		double targetY = Game.player.getY() + Game.player.getHeight() / 2;
		double selfX = this.x + WIDTH / 2;
		double selfY = this.y + HEIGHT / 2;
		double range = Math.sqrt(Math.pow(targetX - selfX, 2) + Math.pow(targetY - selfY, 2));
		if (range > 144) {
			if (selfX < targetX) {
				x += SPEED;
			}
			else if (selfX > targetX) {
				x -= SPEED;
			}
			
			if (selfY < targetY) {
				y += SPEED;
			}
			else if (selfY > targetY) {
				y -= SPEED;
			}
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
