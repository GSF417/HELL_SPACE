package hellSpace.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import hellSpace.main.Game;

public class VoidField implements Bullet {

	public static int WIDTH = 256, HEIGHT = 256, LASTS_FOR = 60;
	
	private int x, y;
	private int duration;
	private boolean dead;
	
	public static VoidField create(int x, int y) {
		VoidField b = new VoidField();
		b.x = x;
		b.y = y;
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

	@Override
	public void tick(List<Entity> entityList) {
		duration++;
		if (duration > LASTS_FOR) {
			dead = true;
			Game.player.finishedPulling();
			return;
		}
		double targetX = Game.player.getX() + Game.player.getWidth() / 2;
		double targetY = Game.player.getY() + Game.player.getHeight() / 2;
		double selfX = this.x + WIDTH / 2;
		double selfY = this.y + HEIGHT / 2;
		double range = Math.sqrt(Math.pow(targetX - selfX, 2) + Math.pow(targetY - selfY, 2));
		if (range < WIDTH / 2) {
			Game.player.beingPulled(selfX, selfY);
		}
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.MAGENTA);
		g.drawOval(x, y, WIDTH, HEIGHT);
	}

}
