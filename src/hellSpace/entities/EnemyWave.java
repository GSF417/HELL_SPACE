package hellSpace.entities;

import java.util.LinkedList;

import hellSpace.main.Game;

public class EnemyWave {

	private static int WIDTH = Game.WIDTH;
	
	// 0 - None, 1 - Balloon, 2 - Glider, 3 - Axe, 4 - Void
	private int[] enemyClass = new int[10];
	private int[] enemyPos = new int[20];
	private LinkedList<Entity> enemyBuffer = new LinkedList<Entity>();
	private static EnemyWave[] templateWaves = new EnemyWave[5];
	
	public static void defineWaves() {
		//Wave 1 - 2 Gliders + 8 Balloons
		EnemyWave ew = new EnemyWave();;
		ew.enemyClass[0] = 2;
		ew.enemyClass[9] = 2;
		for (int i = 1; i < 9; i++) {
			ew.enemyClass[i] = 1;
		}
		for (int i = 0; i < 10; i++) {
			ew.enemyPos[2*i] = WIDTH * i / 10;
			ew.enemyPos[2*i + 1] = 0 + Game.rng.nextInt(25);
		}
		templateWaves[0] = ew;
		//Wave 2 - 2 Voids + 2 Axes + 6 Balloons
		ew = new EnemyWave();;
		ew.enemyClass[0] = 4;
		ew.enemyClass[4] = 3;
		ew.enemyClass[5] = 3;
		ew.enemyClass[9] = 4;
		for (int i = 1; i < 4; i++) {
			ew.enemyClass[i] = 1;
		}
		for (int i = 6; i < 9; i++) {
			ew.enemyClass[i] = 1;
		}
		for (int i = 0; i < 10; i++) {
			ew.enemyPos[2*i] = WIDTH * i / 10;
			ew.enemyPos[2*i + 1] = 0 + Game.rng.nextInt(25);
		}
		templateWaves[1] = ew;
		//Wave 3 - 2 Voids + 4 Axes + 4 Gliders
		ew = new EnemyWave();;
		ew.enemyClass[0] = 2;
		for (int i = 1; i < 3; i++) {
			ew.enemyClass[i] = 3;
		}
		ew.enemyClass[3] = 2;
		ew.enemyClass[4] = 4;
		ew.enemyClass[5] = 4;
		ew.enemyClass[6] = 2;
		for (int i = 7; i < 9; i++) {
			ew.enemyClass[i] = 3;
		}
		ew.enemyClass[9] = 2;
		for (int i = 0; i < 10; i++) {
			ew.enemyPos[2*i] = WIDTH * i / 10;
			ew.enemyPos[2*i + 1] = 0 + Game.rng.nextInt(25);
		}
		templateWaves[2] = ew;
		//Wave 3 - 2 Voids + 4 Balloons + 4 Gliders
		ew = new EnemyWave();;
		ew.enemyClass[0] = 4;
		for (int i = 1; i < 9; i++) {
			if (i % 2 == 0) ew.enemyClass[i] = 2;
			else ew.enemyClass[i] = 1;
		}
		ew.enemyClass[9] = 4;
		for (int i = 0; i < 10; i++) {
			ew.enemyPos[2*i] = WIDTH * i / 10;
			ew.enemyPos[2*i + 1] = -50 + Game.rng.nextInt(50);
		}
		templateWaves[3] = ew;
	}
	
	public static Entity createEnemy(int type, int posX, int posY) {
		Entity e = null;
		switch (type) {
			case 1:
				e = BalloonEnemy.create(posX, posY);
				break;
			case 2:
				e = GliderEnemy.create(posX, posY);
				break;
			case 3:
				e = AxeEnemy.create(posX, posY);
				break;
			case 4:
				e = VoidEnemy.create(posX, posY);
				break;
		}
		return e;
	}
	
	public static EnemyWave createRandomWave() {
		EnemyWave ew = new EnemyWave();
		int wavePicked = Game.rng.nextInt(4);
		EnemyWave templateWave = templateWaves[wavePicked];
		for (int i = 0; i < 10; i++) {
			Entity e = createEnemy(templateWave.enemyClass[i], templateWave.enemyPos[2*i], templateWave.enemyPos[2*i + 1]);
			ew.enemyBuffer.add(e);
		}
		return ew;
	}
	
	public Entity getNextEnemy() {
		return enemyBuffer.pop();
	}
	
	public boolean isFinished() {
		return (enemyBuffer.size() <= 0) ? true : false;
	}
}
