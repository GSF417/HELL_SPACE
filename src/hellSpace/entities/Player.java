package hellSpace.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import hellSpace.main.Game;
import hellSpace.main.Soundtrack;

public class Player implements Entity {

	private static final int WIDTH = 48, HEIGHT = 32, TEAM = 0, I_FRAMES = 300, MAX_HP = 4, SHOOT_COOLDOWN = 30,
							GAME_BOUNDARY = 16;
	private static final double SPEED = 4, CTRL_SPEED = 2;
	
	private int grazeScore;
	
	private double x, y;
	
	private int HP;
	private int inv_frames;
	private int bulletCD;
	private boolean dead, cancelMovement;
	private LinkedList<Bullet> bulletBuffer = new LinkedList<Bullet>();
	
	// Graphics
	private static final BufferedImage sprite = Game.acquireImage("hero.png");
	private static final BufferedImage spriteInv = Game.acquireImage("hero_inv.png");
	
	public static Player create(int x, int y) {
		Player p = new Player();
		p.x = x;
		p.y = y;
		p.HP = MAX_HP;
		p.dead = false;
		p.inv_frames = 0;
		p.bulletCD = 0;
		return p;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public int getScore() {
		return 0;
	}
	
	public int getTeam() {
		return TEAM;
	}
	
	public int getWidth() {
		return WIDTH;
	}
	
	public int getHeight() {
		return HEIGHT;
	}
	
	public int getHP() {
		return HP;
	}
	
	public int getGrazeScore() {
		return grazeScore;
	}
	
	public void beingPulled(double sourceX, double sourceY) {
		cancelMovement = true;
		if (sourceX > this.x) {
			x++;
		}
		else if (sourceX < this.x){
			x--;
		}
		if (sourceY > this.y) {
			y++;
		}
		else if (sourceY > this.y) {
			y--;
		}
	}
	
	public void finishedPulling() {
		cancelMovement = false;
	}

	public void recoverHP(int quant) {
		HP += quant;
		if (HP > MAX_HP) HP = MAX_HP;
	}
	
	public void sufferDamage() {
		System.out.println("Player hit!");
		Soundtrack.hurtSound.play();
		inv_frames = I_FRAMES;
		HP--;
		if (HP <= 0) {
			dead = true;
			System.out.println("Player destroyed!");
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
		if (bulletCD > 0) {
			bulletCD--;
		}
	}
	
	public void move(int xDir, int yDir, boolean control) {
		if (cancelMovement) return;
		double speed = SPEED;
		if (control) {
			speed = CTRL_SPEED;
		}
		if (xDir != 0) {
			this.x += speed * xDir;
			if (this.x <= GAME_BOUNDARY) {
				this.x = GAME_BOUNDARY;
			}
			else if (this.x + WIDTH >= Game.WIDTH - GAME_BOUNDARY) {
				this.x = Game.WIDTH - GAME_BOUNDARY - WIDTH;
			}
		}
		if (yDir != 0) {
			this.y += speed * yDir;
			if (this.y + HEIGHT >= Game.HEIGHT) {
				this.y = Game.HEIGHT - HEIGHT;
			}
			else if (this.y <= GAME_BOUNDARY) {
				this.y = GAME_BOUNDARY;
			}
		}
	}
	
	private void addBulletsToBuffer() {
		if (HP == 1) {
			bulletBuffer.add(PlayerHomingBullet.create(this.x + WIDTH * 2 / 3, this.y + HEIGHT / 2, Math.PI / 2));
			bulletBuffer.add(PlayerHomingBullet.create(this.x + WIDTH / 3, this.y + HEIGHT / 2, Math.PI / 2));
			bulletBuffer.add(PlayerAngularBullet.create(this.x + WIDTH, this.y + HEIGHT * 2 / 3, Math.PI * 7 / 4, Math.PI * 5 / 4));
			bulletBuffer.add(PlayerAngularBullet.create(this.x, this.y + HEIGHT * 2 / 3, Math.PI * 5 / 4, Math.PI * 7 / 4));
		}
		if (HP == 2) {
			bulletBuffer.add(PlayerHomingBullet.create(this.x + WIDTH / 2, this.y + HEIGHT / 2, Math.PI / 2));
		}
		if (HP <= 3) {
			bulletBuffer.add(PlayerAngularBullet.create(this.x + WIDTH * 4 / 5, this.y, Math.PI * 7 / 4, Math.PI * 5 / 4));
			bulletBuffer.add(PlayerAngularBullet.create(this.x + WIDTH / 5, this.y, Math.PI * 5 / 4, Math.PI * 7 / 4));
		}
		bulletBuffer.add(PlayerSimpleBullet.create(this.x + WIDTH / 2 + WIDTH / 4, this.y, Math.PI * 3 / 2));
		bulletBuffer.add(PlayerSimpleBullet.create(this.x + WIDTH / 2 - WIDTH / 4, this.y, Math.PI * 3 / 2));
	}
	
	public void shoot(boolean toShoot) {
		if (toShoot && bulletCD == 0) {
			Soundtrack.laserShoot.play();
			addBulletsToBuffer();
			bulletCD = SHOOT_COOLDOWN;
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
	
	public boolean bufferStillHasBullets() {
		return (bulletBuffer.size() > 0) ? true : false;
	}

	@Override
	public Bullet getLastBullet() {
		return bulletBuffer.pop();
	}
	
	boolean lineCheckLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

		// calculate the distance to intersection point
		double uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
		double uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
			
		// if uA and uB are between 0-1, lines are colliding
		if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
			return true;
		}
		return false;
	}
	
	public CollisionType lineCollision(double x1, double y1, double x2, double y2, boolean grazed) {
		if (!grazed) {
			boolean left = lineCheckLine(x1, y1, x2, y2, this.x, this.y, this.x, this.y+HEIGHT);
			boolean right = lineCheckLine(x1, y1, x2, y2, this.x+WIDTH, this.y, this.x+WIDTH, this.y+HEIGHT);
			boolean top = lineCheckLine(x1, y1, x2, y2, this.x, this.y, this.x+WIDTH, this.y);
			boolean bottom = lineCheckLine(x1, y1, x2, y2, this.x, this.y+HEIGHT, this.x+WIDTH, this.y+HEIGHT);
			if (left | right | top | bottom) {
				grazeScore++;
				return CollisionType.GRAZED;
			}
		}
		if (inv_frames > 0) {
			return CollisionType.NO_COLLISION;
		}
		boolean left = lineCheckLine(x1, y1, x2, y2, this.x+WIDTH/4, this.y+HEIGHT/4, this.x+WIDTH/4, this.y+HEIGHT*3/4);
		boolean right = lineCheckLine(x1, y1, x2, y2, this.x+WIDTH*3/4, this.y+HEIGHT/4, this.x+WIDTH*3/4, this.y+HEIGHT*3/4);
		boolean top = lineCheckLine(x1, y1, x2, y2, this.x+WIDTH/4, this.y+HEIGHT/4, this.x+WIDTH*3/4, this.y+HEIGHT/4);
		boolean bottom = lineCheckLine(x1, y1, x2, y2, this.x+WIDTH/4, this.y+HEIGHT*3/4, this.x+WIDTH*3/4, this.y+HEIGHT*3/4);
		if (left | right | top | bottom) {
			grazeScore++;
			return CollisionType.COLLISION_WITH_ENEMY;
		}
		return CollisionType.NO_COLLISION;
	}

	@Override
	public CollisionType isColliding(double x, double y, int width, int height, int team, boolean grazed) {
		if (team == TEAM) {
			return CollisionType.NO_COLLISION;
		}
		if (!grazed) {
			if ((x + width > this.x && x < this.x + WIDTH) &&
				(y + height > this.y && y < this.y + HEIGHT)) {
				grazeScore += 1;
				return CollisionType.GRAZED;
			}
		}
		// Player hitbox is only half of its width and height, to make the game easier
		if (x + width < this.x + WIDTH / 4 || x > this.x + WIDTH * 3 / 4) {
			return CollisionType.NO_COLLISION;
		}
		if (y + height < this.y + HEIGHT / 4 || y > this.y + HEIGHT * 3 / 4) {
			return CollisionType.NO_COLLISION;
		}
		if (inv_frames > 0) {
			return CollisionType.NO_COLLISION;
		}
		return CollisionType.COLLISION_WITH_ENEMY;
	}

	public CollisionType powerupPickup(double x, double y, int width, int height) {
		// Player hitbox is only half of its width and height, to make the game easier
		if (x + width < this.x || x > this.x + WIDTH) {
			return CollisionType.NO_COLLISION;
		}
		if (y + height < this.y || y > this.y + HEIGHT) {
			return CollisionType.NO_COLLISION;
		}
		return CollisionType.COLLISION_WITH_ENEMY;
	}

}

