package hellSpace.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import hellSpace.entities.*;

public class Game extends Canvas implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7910014136086615761L;
	
	// Thread handling
	private boolean isRunning;
	private Thread thread;
	
	// Graphics handling
	public static final int WIDTH = 1080, HEIGHT = 720;
	private JFrame gameFrame;
	private JPanel inputPanel;
	private Graphics2D graphics;
	private BufferedImage image;
	
	// Game objects
	public List<Entity> entityList;
	public List<Entity> deadEntities;
	public List<Bullet> bulletList;
	public List<Bullet> lostBullets;
	public List<Powerup> powerupList;
	public List<Powerup> pickedPowerups;
	public static Player player;
	private int enemies, enemiesSpawned;
	private Screentype screenType;
	private int inputDelay, spawnDelay;
	private static final int SCORE_SCREEN_DELAY = 200, GRAZE_POINTS = 50, LIFE_POINTS = 2500, TOTAL_ENEMIES = 150, SPAWN_INTERVAL = 1500;
	
	// Player control
	private int playerMoveX, playerMoveY;
	private boolean playerShoot;
	private boolean playerSpeedControl;
	private boolean UP_PRESSED, DOWN_PRESSED, LEFT_PRESSED, RIGHT_PRESSED, SHOOT_PRESSED, CTRL_PRESSED;

	// Extra
	public static Random rng;
	private int score;
	private BufferedImage lifeIcon;
	private BufferedImage logo;
	
	public static BufferedImage acquireImage(String fileName) {
		BufferedImage image = null;
		Path imagePath = FileSystems.getDefault().getPath("res", fileName);
		try {
			image = ImageIO.read(imagePath.toFile());
		} catch (IOException er) {
			er.printStackTrace();
		}
		return image;
	}
	
	private void spawnWave() {
		EnemyWave ew = EnemyWave.createRandomWave();
		while (!ew.isFinished()) {
			Entity e = ew.getNextEnemy();
			if (e != null) {
				entityList.add(e);
				enemies++;
				enemiesSpawned++;
			}
		}
	}
	
	private void playerControl() {
		if (UP_PRESSED && DOWN_PRESSED || !UP_PRESSED && !DOWN_PRESSED) {
			playerMoveY = 0;
		}
		else if (UP_PRESSED) {
			playerMoveY = -1;
		}
		else if (DOWN_PRESSED) {
			playerMoveY = 1;
		}
		
		if (LEFT_PRESSED && RIGHT_PRESSED || !LEFT_PRESSED && !RIGHT_PRESSED) {
			playerMoveX = 0;
		}
		else if (LEFT_PRESSED) {
			playerMoveX = -1;
		}
		else if (RIGHT_PRESSED) {
			playerMoveX = 1;
		}
		
		if (SHOOT_PRESSED) {
			playerShoot = true;
		}
		else {
			playerShoot = false;
		}
		
		if (CTRL_PRESSED) {
			playerSpeedControl = true;
		}
		else {
			playerSpeedControl = false;
		}
	}
	
	public void screenInit() {
		// Add Keybindings
		inputPanel = new JPanel();
		InputMap im = inputPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = inputPanel.getActionMap();
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "UP_PRESSED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK, false), "UP_PRESSED");
		am.put("UP_PRESSED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				UP_PRESSED = true;
			}
			
		});
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "UP_RELEASED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK, true), "UP_RELEASED");
		am.put("UP_RELEASED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				UP_PRESSED = false;
			}
			
		});
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "DOWN_PRESSED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK, false), "DOWN_PRESSED");
		am.put("DOWN_PRESSED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				DOWN_PRESSED = true;
			}
			
		});
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "DOWN_RELEASED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK, true), "DOWN_RELEASED");
		am.put("DOWN_RELEASED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				DOWN_PRESSED = false;
			}
			
		});
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "LEFT_PRESSED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK, false), "LEFT_PRESSED");
		am.put("LEFT_PRESSED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				LEFT_PRESSED = true;
			}
			
		});
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "LEFT_RELEASED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK, true), "LEFT_RELEASED");
		am.put("LEFT_RELEASED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				LEFT_PRESSED = false;
			}
			
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "RIGHT_PRESSED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK, false), "RIGHT_PRESSED");
		am.put("RIGHT_PRESSED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				RIGHT_PRESSED = true;
			}
			
		});
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "RIGHT_RELEASED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK, true), "RIGHT_RELEASED");
		am.put("RIGHT_RELEASED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				RIGHT_PRESSED = false;
			}
			
		});
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, false), "SHOOT_PRESSED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, false), "SHOOT_PRESSED");
		am.put("SHOOT_PRESSED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				SHOOT_PRESSED = true;
			}
			
		});
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, true), "SHOOT_RELEASED");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, true), "SHOOT_RELEASED");
		am.put("SHOOT_RELEASED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				SHOOT_PRESSED = false;
			}
			
		});
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.CTRL_DOWN_MASK, 0, false), "CTRL_PRESSED");
		am.put("CTRL_PRESSED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				CTRL_PRESSED = true;
			}
			
		});
		im.put(KeyStroke.getKeyStroke(KeyEvent.CTRL_DOWN_MASK, 0, true), "CTRL_RELEASED");
		am.put("CTRL_RELEASED", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				CTRL_PRESSED = false;
			}
			
		});
		
		// Create JFrame
		Path iconPath = FileSystems.getDefault().getPath("res", "hero.png");
		Path logoPath = FileSystems.getDefault().getPath("res", "logo.png");
		BufferedImage iconImage = null;
		try {
			iconImage = ImageIO.read(iconPath.toFile());
			logo = ImageIO.read(logoPath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		gameFrame = new JFrame("Hell Space");
		gameFrame.add(inputPanel);
		gameFrame.setIconImage(iconImage);
		gameFrame.add(this);
		gameFrame.setResizable(false);
		gameFrame.pack();
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setVisible(true);
		lifeIcon = iconImage;
	}
	
	public void initialize() {
		score = 0;
		
		entityList = new ArrayList<Entity>();
		deadEntities = new ArrayList<Entity>();
		bulletList = new ArrayList<Bullet>();
		lostBullets = new ArrayList<Bullet>();
		powerupList = new ArrayList<Powerup>();
		pickedPowerups = new ArrayList<Powerup>();
		screenType = Screentype.GAME;
		inputDelay = SCORE_SCREEN_DELAY;
		
		player = Player.create(WIDTH/2, 680);
		entityList.add(player);
		enemies = 0;
		enemiesSpawned = 0;
		
		EnemyWave.defineWaves();
		spawnWave();
		spawnDelay = 0;
		
		// Create entities
		// Barriers
		Soundtrack.bgm.loop();
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		screenInit();
		// Graphics init
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		
		// Game variables
		rng = new Random();
		screenType = Screentype.INIT;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void drawUI(Graphics2D g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, WIDTH, HEIGHT / 20);
		g.setColor(Color.RED);
		g.setFont(new Font("Verdana", Font.PLAIN, 16));
		for (int i = 0; i < Game.player.getHP(); i++) {
			//g.drawImage(lifeIcon, 20 + 50 * i, 20, null);
			g.drawImage(lifeIcon, 10 + 40 * i, 10, 30 + 40 * i, 30, 0, 0, lifeIcon.getWidth(), lifeIcon.getHeight(), null);
		}
		
		g.setColor(Color.RED);
		g.setFont(new Font("Verdana", Font.PLAIN, 16));
		g.drawString("Remaining Enemies: "+(TOTAL_ENEMIES-(enemiesSpawned-enemies)), 700, 20);
	}
	
	public void tick() {
		spawnDelay++;
		if ((spawnDelay >= SPAWN_INTERVAL && enemiesSpawned <= TOTAL_ENEMIES) || enemies == 0) {
			spawnDelay = 0;
			spawnWave();
		}
		
		// First, clean all dead entities
		for (int i = 0; i < deadEntities.size(); i++) {
			Entity e = deadEntities.get(i);
			entityList.remove(e);
			if (!(e instanceof Player)) {
				enemies--;
				score += e.getScore();
				if ((enemiesSpawned - enemies) % 15 == 0) {
					Powerup p = LifeOrb.create(e.getX(), e.getY());
					powerupList.add(p);
				}
			}
		}
		
		// Now clear all lost bullets
		for (int i = 0; i < lostBullets.size(); i++) {
			Bullet b = lostBullets.get(i);
			bulletList.remove(b);
		}
		
		// Now clear all picked powerups
		for (int i = 0; i < pickedPowerups.size(); i++) {
			Powerup p = pickedPowerups.get(i);
			powerupList.remove(p);
		}
		
		// Reset both lists
		deadEntities.clear();
		lostBullets.clear();
		pickedPowerups.clear();
		
		// Controlling the player
		player.move(playerMoveX, playerMoveY, playerSpeedControl);
		player.shoot(playerShoot);
		
		// Now for all living entities, execute tick, render and check if they are dead
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			if (e.bufferStillHasBullets()) {
				while(e.bufferStillHasBullets()) {
					bulletList.add(e.getLastBullet());
				}
			}
			if (e.isDead()) {
				deadEntities.add(e);
				continue;
			}
			e.tick();
			e.render(graphics);
		}
		// Now process all bullets currently in the field.
		for (int i = 0; i < bulletList.size(); i++) {
			Bullet b = bulletList.get(i);
			if (b.isDead()) {
				lostBullets.add(b);
				continue;
			}
			b.tick(entityList);
			b.render(graphics);
			Bullet n = b.getLastBullet();
			if (n != null) {
				bulletList.add(n);
			}
		}
		for (int i = 0; i < powerupList.size(); i++) {
			Powerup p = powerupList.get(i);
			if (p.wasPicked()) {
				pickedPowerups.add(p);
				continue;
			}
			p.tick();
			p.render(graphics);
		}
		// Check Victory / Defeat conditions
		if (enemies == 0 && enemiesSpawned >= TOTAL_ENEMIES) {
			Soundtrack.bgm.stop();
			screenType = Screentype.VICTORY;
			inputDelay = SCORE_SCREEN_DELAY;
		}
		if (player.isDead()) {
			Soundtrack.bgm.stop();
			screenType = Screentype.DEFEAT;
			inputDelay = SCORE_SCREEN_DELAY;
		}
		drawUI(graphics);
	}
	
	private void initGame() {
		graphics.setFont(new Font("Verdana", Font.PLAIN, 24));
		graphics.setColor(Color.WHITE);
		
		graphics.drawImage(logo, 250, 80, null);
		int pivotX = 450;
		graphics.drawString("Fearsome aliens threaten the world!", 50, pivotX);
		graphics.drawString("Use your ship to destroy their attack waves. Arrow keys to move. Z to shoot.", 50, pivotX+50);
		graphics.drawString("You become stronger the closer you are to destruction!", 50, pivotX+100);
		graphics.drawString("But your enemies carry a surprise every time you destroy them...", 50, pivotX+150);
		
		if (inputDelay > 0) {
			inputDelay--;
			graphics.setColor(Color.GREEN);
			graphics.drawString("Wait...", 50, pivotX+200);
		}
		else {
			graphics.setColor(Color.GREEN);
			graphics.drawString("Press Z to start!", 50, pivotX+200);
		}
		if (playerShoot && inputDelay <= 0) {
			initialize();
		}
	}
	
	private void scoreScreen() {
		
		graphics.setFont(new Font("Verdana", Font.PLAIN, 32));
		if (screenType == Screentype.VICTORY) {
			graphics.setColor(Color.CYAN);
			graphics.drawString("VICTORY", 50, 50);
		}
		else if (screenType == Screentype.DEFEAT) {
			graphics.setColor(Color.RED);
			graphics.drawString("DEFEAT", 50, 50);
		}
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Verdana", Font.PLAIN, 32));
		graphics.drawString("Score", 50, 150);
		graphics.drawString("Defeated Enemies: "+score, 50, 200);
		graphics.drawString("Grazes: "+player.getGrazeScore()+" x "+GRAZE_POINTS+" = "+player.getGrazeScore()*GRAZE_POINTS,
							50, 250);
		graphics.drawString("Remaining Lives: "+player.getHP()+" x "+LIFE_POINTS+" = "+player.getHP()*LIFE_POINTS,
							50, 300);
		graphics.setColor(Color.YELLOW);
		graphics.drawString("Total: "+(score+(player.getGrazeScore()*GRAZE_POINTS)+(player.getHP()*LIFE_POINTS)),
							50, 350);
		
		if (inputDelay > 0) {
			inputDelay--;
			graphics.setColor(Color.GREEN);
			graphics.drawString("Wait...", 50, 400);
		}
		else {
			graphics.setColor(Color.GREEN);
			graphics.drawString("Press Z to play again.", 50, 400);
		}
		if (playerShoot && inputDelay <= 0) {
			initialize();
		}
	}
	
	private void gameStateMachine() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}
		graphics = (Graphics2D) image.getGraphics();
		// Background
		graphics.setColor(new Color(0, 0, 0));
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		
		playerControl();
		switch (screenType) {
			case INIT:
				initGame();
				break;
			case GAME:
				tick();
				break;
			case VICTORY:
				scoreScreen();
				break;
			case DEFEAT:
				scoreScreen();
				break;
		}
		graphics.dispose();
		graphics = (Graphics2D) bs.getDrawGraphics();
		graphics.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		bs.show();
	}
	
	@Override
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000/amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				gameStateMachine();
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("===");
				System.out.println("FPS: " +frames);
				frames = 0;
				timer+= 1000;
			}
		}
		stop();
	}

	
}